/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Muestra el estado de sincronización por elemento .
 */
package com.agendaqr.destinations.presentation

/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Estado de sincronización por elemento .
 */
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
internal fun OperationDetailScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        AmScreenScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AmEmptyState(
                title = AppStrings.OperacionNoEncontrada,
                subtitle = AppStrings.PudoHaberSidoEliminada,
                actionLabel = AppStrings.Volver,
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    AmFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    // V1.4 (§12): detalle con título y flecha atrás en la barra superior;
    // Compartir y Editar como acciones visibles (Editar es la principal),
    // Eliminar en el menú "…" (destructiva, no a un toque).
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.Detalle,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
                actions = listOf(
                    AmTopBarAction(label = AppStrings.Compartir, onClick = { shareOperation(operation) }),
                    AmTopBarAction(
                        label = AppStrings.Editar,
                        onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) },
                    ),
                ),
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.EliminarOperacion, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
            AmStatusBanner(
                operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt) + " · " +
                    operation.amount.orEmpty().ifBlank { AppStrings.SinMonto }
            )
        // estado de sincronización del elemento (texto, no solo color).
        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            AmTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }
        if (operationSyncStatus == ElementSyncStatus.Dead) {
            // U4: el drain omite los errores permanentes; reintentar no
            // puede recuperarlos. El dato está a salvo localmente.
            AmText(
                text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                color = AmColor.TextSecondary,
            )
        }
        operation.personOrEntity?.let { AmText(AppStrings.PersonaOEntidad + it) }
        operation.currency?.let { AmText(AppStrings.Moneda + it) }
        operation.concept?.let { AmText(AppStrings.Concepto + it) }
        operation.note?.let { AmText(AppStrings.Nota2 + it, color = AmColor.TextSecondary) }

        AmSection(AppStrings.SeccionComprobantes) {
            if (state.operationComprobantes.isEmpty()) {
                AmText(AppStrings.SinComprobanteAdjunto, color = AmColor.TextSecondary)
                AmTextAction(label = AppStrings.attachReceiptNow, onClick = { viewModel.openUnassociatedForOperation(operation.id) })
            } else {
                for (receipt in state.operationComprobantes) {
                    // §12 V1.4: fila ListItem; "Desasociar" como acción de fila.
                    AmListRow(
                        title = AppStrings.Comprobante2 + receiptProvenanceLabel(receipt.provenance),
                        subtitle = formatDate(receipt.createdAt),
                        onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) },
                        meta = {
                            AmTextAction(
                                label = AppStrings.Desasociar,
                                onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                            )
                        },
                    )
                }
            }
        }
        AmTextAction(label = AppStrings.EliminarOperacion, onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        AmDialog(
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

