package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel del flujo de operaciones (S07/…). El scope es el de la sesión
 * (T12): lo aporta el grafo y se cancela al cerrar sesión.
 */
class OperationsViewModel(
    observeOperations: ObserveOperationsUseCase,
    observeContexts: ObserveContextsUseCase,
    observeUnassociated: ObserveUnassociatedComprobantesUseCase,
    private val observeOperationComprobantes: ObserveOperationComprobantesUseCase,
    private val getOperation: GetOperationUseCase,
    private val saveOperation: SaveOperationUseCase,
    private val updateOperation: UpdateOperationUseCase,
    private val deleteOperation: DeleteOperationWithHistoryUseCase,
    private val associate: AssociateComprobanteToOperationUseCase,
    private val unassociate: UnassociateComprobanteUseCase,
    private val saveComprobante: SaveComprobanteUseCase,
    private val findDuplicates: FindDuplicateComprobantesUseCase,
    private val getComprobante: GetComprobanteUseCase,
    private val suggestReceiptAssociation: SuggestReceiptAssociationUseCase,
    private val deleteComprobante: DeleteComprobanteUseCase,
    private val comprobanteFiles: ComprobanteFileStore,
    /**
     * Scope de la sesión (T12): lo aporta el grafo de la app; se cancela
     * al cerrar sesión, junto con todos los colecciones del ViewModel.
     */
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(OperationsUiState())
    val state: StateFlow<OperationsUiState> = _state.asStateFlow()
    private var selectedOperationId: String? = null
    private var associationReturnOperationId: String? = null

    init {
        scope.launch { observeContexts().collect { contexts ->
            _state.update { it.copy(contexts = contexts) }
        } }
        scope.launch { observeOperations().collect { operations ->
            _state.update { it.copy(operations = operations.sortedByDescending { operation -> operation.occurredAt }) }
        } }
        scope.launch { observeUnassociated().collect { receipts ->
            val sorted = receipts.sortedByDescending { receipt -> receipt.createdAt }
            _state.update { it.copy(unassociated = sorted) }
            sorted.forEach { receipt ->
                scope.launch {
                    runCatching { suggestReceiptAssociation(receipt.id) }
                        .onSuccess { suggestion ->
                            _state.update { state ->
                                state.copy(receiptSuggestions = state.receiptSuggestions + (receipt.id to suggestion))
                            }
                        }
                        .onFailure { error ->
                            // Never leave the dialog stuck in "analizando":
                            // degrade to an explicit no-match so the user can
                            // still associate manually.
                            println("AGENDAQR suggestion failed for ${receipt.id}: $error")
                            _state.update { state ->
                                state.copy(
                                    receiptSuggestions = state.receiptSuggestions +
                                        (receipt.id to ReceiptAssociationSuggestion(
                                            kind = ReceiptMatchKind.NONE,
                                            receiptId = receipt.id,
                                            operationIds = emptyList(),
                                        )),
                                )
                            }
                        }
                }
            }
        } }
        scope.launch { observeIncomingComprobantes().collect { receiveIncoming(it) } }
    }

    /** Última operación fallida, para re-ejecutarla con RetryFailed. */
    private var retryBlock: (() -> Unit)? = null

    fun onAction(action: OperationAction) {
        // Un reintento consume el bloque; cualquier otra acción lo invalida.
        if (action != OperationAction.RetryFailed) retryBlock = null
        when (action) {
            is OperationAction.Search -> _state.update { it.copy(query = action.value) }
            OperationAction.New -> _state.update { it.copy(route = OperationRoute.New, error = null) }
            is OperationAction.Open -> open(action.id)
            is OperationAction.Edit -> {
                selectedOperationId = action.id
                _state.update { it.copy(route = OperationRoute.Edit(action.id), error = null) }
            }
            is OperationAction.Delete -> scope.launch {
                retryBlock = { onAction(action) }
                runCatching { deleteOperation(action.id) }.onFailure { showError(it, ErrorFlow.DeleteOperation) }.onSuccess { retryBlock = null; back() }
            }
            is OperationAction.Associate -> scope.launch {
                retryBlock = { onAction(action) }
                runCatching { associate(action.comprobanteId, action.operationId) }
                    .onFailure { showError(it, ErrorFlow.SaveReceipt) }
                    .onSuccess {
                        retryBlock = null
                        associationReturnOperationId?.let { operationId ->
                            selectedOperationId = operationId
                            associationReturnOperationId = null
                            _state.update { it.copy(route = OperationRoute.Detail(operationId), error = null) }
                        }
                    }
            }
            is OperationAction.Disassociate -> scope.launch {
                retryBlock = { onAction(action) }
                runCatching { unassociate(action.comprobanteId) }.onFailure { showError(it, ErrorFlow.SaveReceipt) }.onSuccess { retryBlock = null }
            }
            is OperationAction.CreateOperationFromReceipt -> {
                if (!state.value.isSavingOperation) createOperationFromReceipt(action.comprobanteId)
            }
            OperationAction.ClearIncoming -> _state.update { it.copy(pendingIncoming = null, pendingDuplicates = emptyList()) }
            is OperationAction.SaveIncoming -> saveIncoming(action.openInbox, action.allowDuplicate)
            OperationAction.DismissDuplicateWarning -> _state.update { it.copy(pendingDuplicates = emptyList()) }
            OperationAction.OpenUnassociated -> _state.update { it.copy(route = OperationRoute.Unassociated, error = null) }
            OperationAction.Back -> back()
            OperationAction.ClearError -> _state.update { it.copy(error = null) }
            OperationAction.RetryFailed -> retryBlock?.invoke()
            is OperationAction.OpenComprobante -> openComprobante(action.id)
            OperationAction.CloseComprobante -> _state.update { it.copy(openedComprobante = null, openedComprobanteBytes = null, isLoadingComprobante = false) }
            is OperationAction.DeleteComprobante -> scope.launch {
                retryBlock = { onAction(action) }
                runCatching {
                    val receipt = requireNotNull(
                        state.value.unassociated.firstOrNull { it.id == action.id }
                            ?: state.value.operationComprobantes.firstOrNull { it.id == action.id }
                            ?: state.value.openedComprobante?.takeIf { it.id == action.id },
                    ) { AppStrings.ComprobanteNoEncontrado }
                    deleteComprobante(receipt)
                }.onFailure { showError(it, ErrorFlow.DeleteReceipt) }.onSuccess {
                    retryBlock = null
                    _state.update {
                        it.copy(
                            openedComprobante = null,
                            openedComprobanteBytes = null,
                            isLoadingComprobante = false,
                        )
                    }
                }
            }
            is OperationAction.Update -> if (!state.value.isSavingOperation) update(action)
            is OperationAction.SaveNew -> if (!state.value.isSavingOperation) saveNew(action)
        }
    }

    fun visibleOperations(): List<Operation> {
        val query = state.value.query.trim()
        if (query.isBlank()) return state.value.operations
        return state.value.operations.filter { operation ->
            listOfNotNull(
                formatDate(operation.occurredAt),
                operationTypeLabel(operation.type),
                operation.amount,
                operation.currency,
                operation.personOrEntity,
                operation.concept,
                operation.note,
            ).any { it.contains(query, ignoreCase = true) }
        }
    }

    fun selectedOperation(): Operation? =
        selectedOperationId?.let { id -> state.value.operations.firstOrNull { it.id == id } }

    private fun open(id: String) {
        selectedOperationId = id
        _state.update { it.copy(route = OperationRoute.Detail(id), error = null, operationComprobantes = emptyList()) }
        scope.launch {
            if (getOperation(id) == null) {
                _state.update { it.copy(error = operationNotFound()) }
                return@launch
            }
            observeOperationComprobantes(id).collect { receipts ->
                _state.update { it.copy(operationComprobantes = receipts) }
            }
        }
    }

    fun receiptSuggestion(receiptId: String): ReceiptAssociationSuggestion? =
        state.value.receiptSuggestions[receiptId]

    private fun receiveIncoming(incoming: IncomingComprobante) {
        scope.launch {
            val duplicates = runCatching { findDuplicates(incoming.bytes) }.getOrDefault(emptyList())
            _state.update { it.copy(pendingIncoming = incoming, pendingDuplicates = duplicates, error = null) }
        }
    }

    private fun saveIncoming(openInbox: Boolean, allowDuplicate: Boolean) {
        val incoming = state.value.pendingIncoming ?: return
        if (state.value.pendingDuplicates.isNotEmpty() && !allowDuplicate) {
            _state.update {
                it.copy(
                    pendingIncoming = null,
                    pendingDuplicates = emptyList(),
                    error = duplicateReceiptGuardError(),
                )
            }
            return
        }
        scope.launch {
            _state.update { it.copy(isSavingReceipt = true) }
            retryBlock = { saveIncoming(openInbox, allowDuplicate) }
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
                    incoming.mimeType,
                )
            }.onFailure { showError(it, ErrorFlow.SaveReceipt) }.onSuccess {
                retryBlock = null
                _state.update { it.copy(pendingIncoming = null, pendingDuplicates = emptyList(), route = if (openInbox) OperationRoute.Unassociated else OperationRoute.List) }
            }
            _state.update { it.copy(isSavingReceipt = false) }
        }
    }

    private fun createOperationFromReceipt(comprobanteId: String) {
        _state.update { it.copy(isSavingOperation = true, error = null) }
        retryBlock = { createOperationFromReceipt(comprobanteId) }
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
            }.onFailure { showError(it, ErrorFlow.SaveReceipt) }.onSuccess {
                retryBlock = null
                selectedOperationId = operation.id
                _state.update { it.copy(route = OperationRoute.Detail(operation.id)) }
            }
            _state.update { it.copy(isSavingOperation = false) }
        }
    }

    private fun update(action: OperationAction.Update) {
        scope.launch {
            _state.update { it.copy(isSavingOperation = true, error = null) }
            retryBlock = { update(action) }
            runCatching { updateOperation(action.operation) }
                .onFailure { showError(it, ErrorFlow.SaveOperation) }
                .onSuccess {
                    retryBlock = null
                    selectedOperationId = action.operation.id
                    _state.update { it.copy(route = OperationRoute.Detail(action.operation.id)) }
                }
            _state.update { it.copy(isSavingOperation = false) }
        }
    }

    private fun saveNew(action: OperationAction.SaveNew) {
        scope.launch {
            _state.update { it.copy(isSavingOperation = true, error = null) }
            val operation = Operation(
                id = action.id ?: newEntityId("operation"),
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
            retryBlock = { saveNew(action) }
            runCatching { saveOperation(operation) }.onFailure { showError(it, ErrorFlow.SaveOperation) }.onSuccess {
                retryBlock = null
                selectedOperationId = operation.id
                _state.update { it.copy(route = OperationRoute.Detail(operation.id)) }
            }
            _state.update { it.copy(isSavingOperation = false) }
        }
    }

    fun openUnassociatedForOperation(operationId: String) {
        associationReturnOperationId = operationId
        selectedOperationId = operationId
        _state.update { it.copy(route = OperationRoute.Unassociated, error = null) }
    }

    private fun back() {
        val returnOperationId = associationReturnOperationId
        associationReturnOperationId = null
        val currentRoute = state.value.route
        if (returnOperationId != null) {
            selectedOperationId = returnOperationId
            _state.update { it.copy(route = OperationRoute.Detail(returnOperationId), error = null) }
        } else if (currentRoute is OperationRoute.Edit) {
            // Back desde edición vuelve al detalle que la originó (contrato:
            // "Back vuelve al estado anterior y conserva el draft cuando existe").
            selectedOperationId = currentRoute.id
            _state.update { it.copy(route = OperationRoute.Detail(currentRoute.id), error = null) }
        } else {
            selectedOperationId = null
            _state.update { it.copy(route = OperationRoute.List, operationComprobantes = emptyList(), error = null) }
        }
    }

    private fun openComprobante(id: String) {
        val knownReceipt = state.value.unassociated.firstOrNull { it.id == id }
            ?: state.value.operationComprobantes.firstOrNull { it.id == id }
        _state.update { it.copy(openedComprobante = knownReceipt, openedComprobanteBytes = null, isLoadingComprobante = true, error = null) }
        scope.launch {
            val receipt = knownReceipt ?: getComprobante(id)
            if (receipt == null) {
                _state.update { it.copy(openedComprobante = null, isLoadingComprobante = false, error = comprobanteNotFound()) }
                return@launch
            }
            _state.update { it.copy(openedComprobante = receipt) }
            val bytes = runCatching { comprobanteFiles.read(receipt.file) }.getOrNull()
            if (bytes == null) {
                _state.update { it.copy(isLoadingComprobante = false, error = userFacingError(IllegalStateException(), ErrorFlow.ComprobanteOpen)) }
            } else {
                _state.update { it.copy(openedComprobanteBytes = bytes, isLoadingComprobante = false) }
            }
        }
    }

    /**
     * Mapeo centralizado (T5): nunca `error.message` crudo; el estado lleva
     * qué pasó, qué pasó con los datos y la acción (spec §10).
     */
    private fun showError(error: Throwable, flow: ErrorFlow) {
        _state.update {
            it.copy(error = userFacingError(error, flow), isSavingReceipt = false, isSavingOperation = false)
        }
    }
}
