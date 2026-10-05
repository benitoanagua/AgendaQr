package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.coroutines.CoroutineScope
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
    data class Edit(val id: String) : OperationRoute
    data object Unassociated : OperationRoute
}

data class OperationsUiState(
    val operations: List<Operation> = emptyList(),
    val contexts: List<Context> = emptyList(),
    val unassociated: List<Comprobante> = emptyList(),
    val operationComprobantes: List<Comprobante> = emptyList(),
    val query: String = "",
    val route: OperationRoute = OperationRoute.List,
    val pendingIncoming: IncomingComprobante? = null,
    val pendingDuplicates: List<Comprobante> = emptyList(),
    val isSavingReceipt: Boolean = false,
    val isSavingOperation: Boolean = false,
    val openedComprobante: Comprobante? = null,
    val openedComprobanteBytes: ByteArray? = null,
    val isLoadingComprobante: Boolean = false,
    val error: UserFacingError? = null,
    val receiptSuggestions: Map<String, ReceiptAssociationSuggestion> = emptyMap(),
)

sealed interface OperationAction {
    data class Search(val value: String) : OperationAction
    data object New : OperationAction
    data class Open(val id: String) : OperationAction
    data class Edit(val id: String) : OperationAction
    data class Delete(val id: String) : OperationAction
    data class Associate(val comprobanteId: String, val operationId: String) : OperationAction
    data class Disassociate(val comprobanteId: String) : OperationAction
    data class CreateOperationFromReceipt(val comprobanteId: String) : OperationAction
    data object ClearIncoming : OperationAction
    data class SaveIncoming(val openInbox: Boolean = false, val allowDuplicate: Boolean = false) : OperationAction
    data object DismissDuplicateWarning : OperationAction
    data object OpenUnassociated : OperationAction
    data object Back : OperationAction
    data object ClearError : OperationAction
    /**
     * Re-ejecuta la última operación fallida: el banner REINTENTAR
     * reintenta de verdad en vez de solo cerrar (§10).
     */
    data object RetryFailed : OperationAction
    data class OpenComprobante(val id: String) : OperationAction
    data object CloseComprobante : OperationAction
    data class DeleteComprobante(val id: String) : OperationAction
    data class Update(val operation: Operation, val confirmedSensitiveChange: Boolean = false) : OperationAction
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
        val id: String? = null,
    ) : OperationAction
}
