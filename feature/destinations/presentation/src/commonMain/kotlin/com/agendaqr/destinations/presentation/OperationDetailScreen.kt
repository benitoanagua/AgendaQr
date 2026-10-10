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
        XauxaScreenScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
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
    // Ronda 2 (Área F): anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    XauxaFeedbackEvent(event = deleteDoneEvent, message = AppStrings.Eliminado)
    XauxaScreenScaffold(
        scrollable = true,
        bottomBar = {
            // M7: Compartir y Editar viven en la barra de aplicación inferior;
            // Editar es la acción principal (bloque sólido de acento, M10).
            // Sus glifos siguen SIN icono aprobado (ADR-0005, puntos abiertos):
            // la etiqueta visible se mantiene (§11).
            XauxaAppBar(
                actions = listOf(
                    XauxaAppBarAction(AppStrings.Compartir, icon = null, onClick = { shareOperation(operation) }),
                    XauxaAppBarAction(AppStrings.Editar, icon = null, primary = true, onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) }),
                ),
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
    ) {
            // M6: título de página en display ligero (no XauxaHeading en
            // negrita); M7: Compartir/Editar viven en la barra inferior.
            XauxaPageTitle(text = AppStrings.Detalle)
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
        if (operationSyncStatus == ElementSyncStatus.Dead) {
            // U4: el drain omite los errores permanentes; reintentar no
            // puede recuperarlos. El dato está a salvo localmente.
            XauxaText(
                text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                color = XauxaColor.TextSecondary,
            )
        }
        operation.personOrEntity?.let { XauxaText(AppStrings.PersonaOEntidad + it) }
        operation.currency?.let { XauxaText(AppStrings.Moneda + it) }
        operation.concept?.let { XauxaText(AppStrings.Concepto + it) }
        operation.note?.let { XauxaText(AppStrings.Nota2 + it, color = XauxaColor.TextSecondary) }

        XauxaSection(AppStrings.SeccionComprobantes) {
            if (state.operationComprobantes.isEmpty()) {
                XauxaText(AppStrings.SinComprobanteAdjunto, color = XauxaColor.TextSecondary)
                XauxaTextAction(label = AppStrings.attachReceiptNow, onClick = { viewModel.openUnassociatedForOperation(operation.id) })
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
        XauxaTextAction(label = AppStrings.EliminarOperacion, onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

