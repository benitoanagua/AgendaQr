package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class IncomingComprobante(
    val bytes: ByteArray,
    val mimeType: String,
    val extension: String,
)

expect fun observeIncomingComprobantes(): Flow<IncomingComprobante>

sealed interface OperationRoute {
    data object List : OperationRoute
    data class Detail(val id: String) : OperationRoute
    data object New : OperationRoute
    data object Unassociated : OperationRoute
}

data class OperationsUiState(
    val operations: List<Operation> = emptyList(),
    val unassociated: List<Comprobante> = emptyList(),
    val operationComprobantes: List<Comprobante> = emptyList(),
    val query: String = "",
    val route: OperationRoute = OperationRoute.List,
    val pendingIncoming: IncomingComprobante? = null,
    val pendingDuplicates: List<Comprobante> = emptyList(),
    val isSavingReceipt: Boolean = false,
    val error: String? = null,
)

sealed interface OperationAction {
    data class Search(val value: String) : OperationAction
    data object New : OperationAction
    data class Open(val id: String) : OperationAction
    data class Delete(val id: String) : OperationAction
    data class Associate(val comprobanteId: String, val operationId: String) : OperationAction
    data class CreateOperationFromReceipt(val comprobanteId: String) : OperationAction
    data object ClearIncoming : OperationAction
    data class SaveIncoming(val openInbox: Boolean = false) : OperationAction
    data object DismissDuplicateWarning : OperationAction
    data object OpenUnassociated : OperationAction
    data object Back : OperationAction
    data object ClearError : OperationAction
    data class SaveNew(
        val type: OperationType,
        val occurredAt: Long,
        val amount: String?,
        val currency: String?,
        val personOrEntity: String?,
        val destinationId: String?,
        val concept: String?,
        val note: String?,
        val contextId: String? = null,
    ) : OperationAction
}

class OperationsViewModel(
    observeOperations: ObserveOperationsUseCase,
    observeUnassociated: ObserveUnassociatedComprobantesUseCase,
    private val observeOperationComprobantes: ObserveOperationComprobantesUseCase,
    private val getOperation: GetOperationUseCase,
    private val saveOperation: SaveOperationUseCase,
    private val deleteOperation: DeleteOperationWithHistoryUseCase,
    private val associate: AssociateComprobanteToOperationUseCase,
    private val saveComprobante: SaveComprobanteUseCase,
    private val findDuplicates: FindDuplicateComprobantesUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(OperationsUiState())
    val state: StateFlow<OperationsUiState> = _state.asStateFlow()
    private var selectedOperationId: String? = null

    init {
        scope.launch { observeOperations().collect { operations ->
            _state.update { it.copy(operations = operations.sortedByDescending { operation -> operation.occurredAt }) }
        } }
        scope.launch { observeUnassociated().collect { receipts ->
            _state.update { it.copy(unassociated = receipts.sortedByDescending { receipt -> receipt.createdAt }) }
        } }
        scope.launch { observeIncomingComprobantes().collect { receiveIncoming(it) } }
    }

    fun onAction(action: OperationAction) {
        when (action) {
            is OperationAction.Search -> _state.update { it.copy(query = action.value) }
            OperationAction.New -> _state.update { it.copy(route = OperationRoute.New, error = null) }
            is OperationAction.Open -> open(action.id)
            is OperationAction.Delete -> scope.launch {
                runCatching { deleteOperation(action.id) }.onFailure(::showError).onSuccess { back() }
            }
            is OperationAction.Associate -> scope.launch {
                runCatching { associate(action.comprobanteId, action.operationId) }.onFailure(::showError)
            }
            is OperationAction.CreateOperationFromReceipt -> createOperationFromReceipt(action.comprobanteId)
            OperationAction.ClearIncoming -> _state.update { it.copy(pendingIncoming = null, pendingDuplicates = emptyList()) }
            is OperationAction.SaveIncoming -> saveIncoming(action.openInbox)
            OperationAction.DismissDuplicateWarning -> _state.update { it.copy(pendingDuplicates = emptyList()) }
            OperationAction.OpenUnassociated -> _state.update { it.copy(route = OperationRoute.Unassociated, error = null) }
            OperationAction.Back -> back()
            OperationAction.ClearError -> _state.update { it.copy(error = null) }
            is OperationAction.SaveNew -> saveNew(action)
        }
    }

    fun visibleOperations(): List<Operation> {
        val query = state.value.query.trim()
        if (query.isBlank()) return state.value.operations
        return state.value.operations.filter { operation ->
            listOfNotNull(operation.amount, operation.currency, operation.personOrEntity, operation.concept, operation.note)
                .any { it.contains(query, ignoreCase = true) }
        }
    }

    fun selectedOperation(): Operation? =
        selectedOperationId?.let { id -> state.value.operations.firstOrNull { it.id == id } }

    private fun open(id: String) {
        selectedOperationId = id
        _state.update { it.copy(route = OperationRoute.Detail(id), error = null, operationComprobantes = emptyList()) }
        scope.launch {
            if (getOperation(id) == null) {
                showError(IllegalArgumentException("Operation not found: $id"))
                return@launch
            }
            observeOperationComprobantes(id).collect { receipts ->
                _state.update { it.copy(operationComprobantes = receipts) }
            }
        }
    }

    private fun receiveIncoming(incoming: IncomingComprobante) {
        scope.launch {
            val duplicates = runCatching { findDuplicates(incoming.bytes) }.getOrDefault(emptyList())
            _state.update { it.copy(pendingIncoming = incoming, pendingDuplicates = duplicates, error = null) }
        }
    }

    private fun saveIncoming(openInbox: Boolean) {
        val incoming = state.value.pendingIncoming ?: return
        scope.launch {
            _state.update { it.copy(isSavingReceipt = true) }
            runCatching {
                val now = nowMillis()
                saveComprobante(
                    Comprobante(
                        id = newEntityId("comprobante"),
                        file = "",
                        createdAt = now,
                        updatedAt = now,
                        provenance = ReceiptProvenance.RECIBIDO,
                    ),
                    incoming.bytes,
                    incoming.extension,
                )
            }.onFailure(::showError).onSuccess {
                _state.update { it.copy(pendingIncoming = null, pendingDuplicates = emptyList(), route = if (openInbox) OperationRoute.Unassociated else OperationRoute.List) }
            }
            _state.update { it.copy(isSavingReceipt = false) }
        }
    }

    private fun createOperationFromReceipt(comprobanteId: String) {
        scope.launch {
            val now = nowMillis()
            val operation = Operation(
                id = newEntityId("operation"),
                type = OperationType.PAGO,
                occurredAt = now,
                createdAt = now,
            )
            runCatching {
                saveOperation(operation)
                associate(comprobanteId, operation.id)
            }.onFailure(::showError).onSuccess {
                selectedOperationId = operation.id
                _state.update { it.copy(route = OperationRoute.Detail(operation.id)) }
            }
        }
    }

    private fun saveNew(action: OperationAction.SaveNew) {
        scope.launch {
            val operation = Operation(
                id = newEntityId("operation"),
                type = action.type,
                occurredAt = action.occurredAt,
                createdAt = nowMillis(),
                amount = action.amount?.trim()?.takeIf(String::isNotBlank),
                currency = action.currency?.trim()?.takeIf(String::isNotBlank),
                personOrEntity = action.personOrEntity?.trim()?.takeIf(String::isNotBlank),
                destinationId = action.destinationId?.trim()?.takeIf(String::isNotBlank),
                concept = action.concept?.trim()?.takeIf(String::isNotBlank),
                note = action.note?.trim()?.takeIf(String::isNotBlank),
                contextId = action.contextId,
            )
            runCatching { saveOperation(operation) }.onFailure(::showError).onSuccess {
                selectedOperationId = operation.id
                _state.update { it.copy(route = OperationRoute.Detail(operation.id)) }
            }
        }
    }

    private fun back() {
        selectedOperationId = null
        _state.update { it.copy(route = OperationRoute.List, operationComprobantes = emptyList(), error = null) }
    }

    private fun showError(error: Throwable) {
        _state.update { it.copy(error = error.message ?: "No se pudo completar la operación", isSavingReceipt = false) }
    }
}
