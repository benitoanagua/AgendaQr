/**
 * S07 — Lista de operaciones, bandeja de comprobantes sin asociar y el
 * dispatcher de la superficie (diálogo de recibido y visor).
 *
 * La superficie se divide en [OperationListScreen] (listado, en
 * OperationListContent.kt), [UnassociatedScreen] (bandeja, en
 * UnassociatedScreen.kt) y el editor/detalle en sus propios archivos.
 * Este archivo conserva únicamente el dispatcher y los diálogos de
 * comprobante entrante / visor. Sin cambios de comportamiento.
 */
package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
fun OperationsScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
    /** D8: resolución del nombre del destino QR vinculado a una actividad. */
    destinationsViewModel: DestinationsViewModel? = null,
    /** U3/ADR-0003: crear contexto desde S08 sin perder el draft. */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
) {
    when (state.route) {
        OperationRoute.List -> OperationListScreen(state, viewModel, navChrome, appHosts, syncLookup)
        OperationRoute.Unassociated -> UnassociatedScreen(state, viewModel, appHosts, syncLookup)
        OperationRoute.New -> OperationEditorScreen(state, viewModel, existing = null, appHosts = appHosts, onCreateContext = onCreateContext, justCreatedContextId = justCreatedContextId)
        is OperationRoute.Edit -> OperationEditorScreen(state, viewModel, existing = viewModel.selectedOperation(), appHosts = appHosts, onCreateContext = onCreateContext, justCreatedContextId = justCreatedContextId)
        is OperationRoute.Detail -> OperationDetailScreen(
            state, viewModel, appHosts, syncLookup, onRetrySync,
            destinationsViewModel = destinationsViewModel,
        )
    }
    state.pendingIncoming?.let { incoming ->
        val duplicate = state.pendingDuplicates.isNotEmpty()
        val saving = state.isSavingReceipt
        AqDialog(
            title = if (duplicate)  AppStrings.ComprobanteDuplicado else AppStrings.receiptReceivedTitle,
            message = if (duplicate) {
                AppStrings.duplicateReceiptWarningPrefix + incoming.extension
            } else {
                AppStrings.incomingReceiptExplanationPrefix + incoming.extension
            },
            // S10: ASOCIAR es la acción primaria y "Guardar sin asociar"
            // la secundaria (orden de la spec; ambos resultados legítimos).
            // El caso duplicado conserva su contrato (RF-14) intacto.
            confirmLabel = if (duplicate) AppStrings.GuardarDeTodosModos else if (saving) AppStrings.Guardando else AppStrings.AsociarAhora,
            onConfirm = {
                if (!saving) {
                    viewModel.onAction(
                        if (duplicate) OperationAction.SaveIncoming(
                            openInbox = false,
                            allowDuplicate = duplicate,
                        )
                        else OperationAction.SaveIncoming(true),
                    )
                }
            },
            dismissLabel = if (duplicate) AppStrings.VerExistente else AppStrings.Guardar,
            onDismiss = {
                if (duplicate) {
                    state.pendingDuplicates.firstOrNull()?.id?.let { id ->
                        viewModel.onAction(OperationAction.OpenComprobante(id))
                    }
                    viewModel.onAction(OperationAction.ClearIncoming)
                } else {
                    viewModel.onAction(OperationAction.SaveIncoming(false))
                }
            },
            // R1: Back del sistema / toque fuera ABANDONA el comprobante
            // entrante sin guardar nada (§3 "Cancelar abandona la intención
            // actual"); "Asociar ahora"/"Ver existente" requieren su botón.
            onDismissRequest = { viewModel.onAction(OperationAction.ClearIncoming) },
        )
    }
    state.openedComprobante?.let { receipt ->
        ComprobanteViewerDialog(state, receipt, viewModel, appHosts, syncLookup)
    }
}
