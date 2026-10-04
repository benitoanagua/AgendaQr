/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Muestra el estado de sincronización por elemento (T6).
 */
package com.agendaqr.destinations.presentation

/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Estado de sincronización por elemento (T6).
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
internal fun OperationDetailScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.Center) {
            XauxaEmptyState(
                title = AppStrings.OperacionNoEncontrada,
                subtitle = AppStrings.PudoHaberSidoEliminada,
                actionLabel = AppStrings.Volver,
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            XauxaHeading(text = AppStrings.Detalle, size = XauxaType.Display, fontWeight = FontWeight.Bold)
            XauxaTextAction(label = AppStrings.Volver, onClick = { viewModel.onAction(OperationAction.Back) })
        }
        XauxaStatusBanner(
            operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt) + " · " +
                operation.amount.orEmpty().ifBlank { "sin monto" }
        )
        // T6: estado de sincronización del elemento (texto, no solo color).
        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            XauxaTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }
        operation.personOrEntity?.let { XauxaText(AppStrings.PersonaOEntidad + it) }
        operation.currency?.let { XauxaText(AppStrings.Moneda + it) }
        operation.destinationId?.let { XauxaText(AppStrings.Destino + it) }
        operation.concept?.let { XauxaText(AppStrings.Concepto + it) }
        operation.note?.let { XauxaText(AppStrings.Nota2 + it, color = XauxaColor.TextSecondary) }

        XauxaSection("Comprobantes") {
            if (state.operationComprobantes.isEmpty()) {
                XauxaText(AppStrings.SinComprobanteAdjunto, color = XauxaColor.TextSecondary)
                XauxaTextAction(label = AppStrings.AdjuntarComprobanteAhora, onClick = { viewModel.openUnassociatedForOperation(operation.id) })
            } else {
                for (receipt in state.operationComprobantes) {
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) }) {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg)) {
                            XauxaText(AppStrings.Comprobante2 + receiptProvenanceLabel(receipt.provenance), fontWeight = FontWeight.SemiBold)
                            XauxaText(formatDate(receipt.createdAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                            XauxaTextAction(
                                label = AppStrings.Desasociar,
                                onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                            )
                        }
                    }
                }
            }
        }
        XauxaSecondaryButton(label = AppStrings.Compartir, onClick = { shareOperation(operation) })
        XauxaSecondaryButton(label = AppStrings.Editar, onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) })
        XauxaTextAction(label = AppStrings.EliminarOperacion, onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarOperacion,
            message = AppStrings.EstaAccionNoSePuede,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

