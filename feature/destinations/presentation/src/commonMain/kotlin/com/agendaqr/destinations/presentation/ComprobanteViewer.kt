/**
 * Visor de comprobante (diálogo): contenido, compartir y eliminación con
 * confirmación. Estado de sincronización por elemento .
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
internal fun ComprobanteViewerDialog(
    state: OperationsUiState,
    receipt: Comprobante,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    AqFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    if (showDeleteConfirm) {
        AqDialog(
            title = AppStrings.EliminarComprobante,
            destructive = true,
            message = AppStrings.operationKeptOnReceiptDelete,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                viewModel.onAction(OperationAction.DeleteComprobante(receipt.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
        return
    }
    val bytes = state.openedComprobanteBytes
    AqDialog(
        title = AppStrings.Comprobante,
        confirmLabel = AppStrings.Cerrar,
        onConfirm = { viewModel.onAction(OperationAction.CloseComprobante) },
        dismissLabel = AppStrings.Eliminar,
        onDismiss = { showDeleteConfirm = true },
        // R1: Back del sistema / toque fuera CIERRA el visor; "Eliminar"
        // solo se alcanza por su botón explícito (§3: Back ≠ destructivo).
        onDismissRequest = { viewModel.onAction(OperationAction.CloseComprobante) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(formatDate(receipt.createdAt), size = AqType.Label, color = AqColor.TextSecondary)
                AqText(AppStrings.Origen + receiptProvenanceLabel(receipt.provenance), size = AqType.Label, color = AqColor.TextSecondary)
                // estado de sincronización del comprobante, con texto.
                ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                when {
                    state.isLoadingComprobante -> AqLoading(message = AppStrings.AbriendoComprobante)
                    bytes != null -> {
                        ComprobantePreview(bytes, receipt.mimeType)
                        AqSecondaryButton(
                            label = AppStrings.Compartir,
                            onClick = { shareComprobante(bytes, receipt.extension ?: "bin", receipt.mimeType) },
                        )
                    }
                    else -> AqText(AppStrings.NoSePudoAbrirEl, size = AqType.Label, color = AqColor.TextSecondary)
                }
            }
        },
    )
}
