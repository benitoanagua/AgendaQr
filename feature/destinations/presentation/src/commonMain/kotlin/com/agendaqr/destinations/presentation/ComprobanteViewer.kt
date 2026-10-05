/**
 * Visor de comprobante (diálogo): contenido, compartir y eliminación con
 * confirmación. Estado de sincronización por elemento (T6).
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun ComprobanteViewerDialog(
    state: OperationsUiState,
    receipt: Comprobante,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarComprobante,
            message = AppStrings.operationKeptOnReceiptDelete,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.DeleteComprobante(receipt.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
        return
    }
    val bytes = state.openedComprobanteBytes
    XauxaDialog(
        title = AppStrings.Comprobante,
        confirmLabel = AppStrings.Cerrar,
        onConfirm = { viewModel.onAction(OperationAction.CloseComprobante) },
        dismissLabel = AppStrings.Eliminar,
        onDismiss = { showDeleteConfirm = true },
        // R1: Back del sistema / toque fuera CIERRA el visor; "Eliminar"
        // solo se alcanza por su botón explícito (§3: Back ≠ destructivo).
        onDismissRequest = { viewModel.onAction(OperationAction.CloseComprobante) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaText(formatDate(receipt.createdAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                XauxaText(AppStrings.Origen + receiptProvenanceLabel(receipt.provenance), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                // T6: estado de sincronización del comprobante, con texto.
                ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                when {
                    state.isLoadingComprobante -> XauxaLoading(message = AppStrings.AbriendoComprobante)
                    bytes != null -> {
                        ComprobantePreview(bytes, receipt.mimeType)
                        XauxaSecondaryButton(
                            label = AppStrings.Compartir,
                            onClick = { shareComprobante(bytes, receipt.extension ?: "bin", receipt.mimeType) },
                        )
                    }
                    else -> XauxaText(AppStrings.NoSePudoAbrirEl, size = XauxaType.Label, color = XauxaColor.TextSecondary)
                }
            }
        },
    )
}
