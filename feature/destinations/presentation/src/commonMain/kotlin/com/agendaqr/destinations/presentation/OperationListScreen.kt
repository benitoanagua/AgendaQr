/**
 * S07 — Lista de operaciones, bandeja de comprobantes sin asociar y el
 * dispatcher de la superficie (diálogo de recibido y visor).
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
fun OperationsScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
    /** U3/ADR-0003: crear contexto desde S08 sin perder el draft. */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
) {
    when (state.route) {
        OperationRoute.List -> OperationListScreen(state, viewModel, navChrome, appHosts, syncLookup)
        OperationRoute.Unassociated -> UnassociatedScreen(state, viewModel, appHosts, syncLookup)
        OperationRoute.New -> OperationEditorScreen(state, viewModel, existing = null, appHosts = appHosts, onCreateContext = onCreateContext, justCreatedContextId = justCreatedContextId)
        is OperationRoute.Edit -> OperationEditorScreen(state, viewModel, existing = viewModel.selectedOperation(), appHosts = appHosts, onCreateContext = onCreateContext, justCreatedContextId = justCreatedContextId)
        is OperationRoute.Detail -> OperationDetailScreen(state, viewModel, appHosts, syncLookup, onRetrySync)
    }
    state.pendingIncoming?.let { incoming ->
        val duplicate = state.pendingDuplicates.isNotEmpty()
        val saving = state.isSavingReceipt
        AmDialog(
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

@Composable
internal fun OperationListScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    val paged = operations.take(visibleCount)
    // V1.4 (§12): Actividades es destino raíz — barra superior con título,
    // navegación inferior y FAB extendido "Registrar" (la acción de
    // creación vive en el FAB, no en la barra).
    AmScreenScaffold(
        topBar = {
            AmTopBar(title = AppStrings.Operaciones)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        floatingActionButton = {
            AmExtendedFab(
                label = AppStrings.RegistrarOperacion,
                icon = AmIcons.Add,
                onClick = { viewModel.onAction(OperationAction.New) },
            )
        },
    ) {
            AmSearchField(value = state.query, onValueChange = { viewModel.onAction(OperationAction.Search(it)) }, label = AppStrings.Buscar, placeholder = AppStrings.BuscarOperaciones, onClear = { viewModel.onAction(OperationAction.Search("")) }, clearLabel = AppStrings.Limpiar)
        if (state.unassociated.isNotEmpty()) {
            AmStatusBanner(unassociatedReceiptsLabel(state.unassociated.size))
            AmSecondaryButton(label = AppStrings.VerBandejaDeRespaldos, onClick = { viewModel.onAction(OperationAction.OpenUnassociated) })
        }
        state.error?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
                actionLabel = err.action.label,
                onAction = {
                    viewModel.onAction(
                        if (err.action == ErrorAction.Retry) OperationAction.RetryFailed
                        else OperationAction.ClearError,
                    )
                },
                onDismiss = { viewModel.onAction(OperationAction.ClearError) },
            )
        }
        if (operations.isEmpty()) {
            AmEmptyState(
                icon = AmIcons.Register,
                title = if (state.query.isBlank()) AppStrings.AunNoHayOperaciones else AppStrings.NoHayCoincidencias,
                subtitle = if (state.query.isBlank()) AppStrings.registerFirstPaymentHint else AppStrings.tryAnotherSearchHint,
                actionLabel = if (state.query.isBlank()) AppStrings.RegistrarOperacion else AppStrings.LimpiarBusqueda,
                onAction = {
                    if (state.query.isBlank()) viewModel.onAction(OperationAction.New)
                    else viewModel.onAction(OperationAction.Search(""))
                }
            )
        } else {
            LazyColumn(
                // Sin tarjeta contenedora: las filas viven sobre el fondo
                // de página y la agrupación la dan los hairlines (modo
                // List por defecto de la referencia).
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                itemsIndexed(paged, key = { _, operation -> operation.id }) { index, operation ->
                    // Fila ListItem convencional con el contenido a la
                    // derecha; el estado de sincronización con texto.
                    AmListRow(
                        title = listOfNotNull(
                            operationTypeLabel(operation.type),
                            operation.amount.orEmpty().ifBlank { AppStrings.SinMontoCorto },
                        ).joinToString(" · "),
                        subtitle = formatDate(operation.occurredAt) +
                            (operation.personOrEntity?.let { " · $it" } ?: ""),
                        onClick = { viewModel.onAction(OperationAction.Open(operation.id)) },
                        meta = {
                            ElementSyncBadge(
                                syncLookup.status(SyncResource.OPERATION, operation.id),
                            )
                        },
                        showDivider = index < paged.lastIndex,
                    )
                }
                if (paged.size < operations.size) {
                    item {
                        AmLoadMoreFooter(
                            onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(operations.size) },
                            loadMoreLabel = AppStrings.CargarMas,
                            loadingLabel = AppStrings.Cargando,
                            endLabel = AppStrings.NoHayMasElementos,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun UnassociatedScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var selectedReceipt by remember { mutableStateOf<Comprobante?>(null) }
    // V1.4 (§12): bandeja con título y flecha atrás en la barra superior
    // (no es destino raíz: no lleva navegación inferior).
    AmScreenScaffold(
        topBar = {
            AmTopBar(
                title = AppStrings.ComprobantesSinAsociar,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
            if (state.unassociated.isEmpty()) {
                AmEmptyState(icon = AmIcons.Inbox, title = AppStrings.noUnassociatedReceipts)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    items(state.unassociated, key = { it.id }) { receipt ->
                        // §12 V1.4: fila ListItem; las acciones de fila viven
                        // apiladas bajo el contenido (dos etiquetas largas no
                        // caben lado a lado en 360 dp).
                        AmListRow(
                            title = AppStrings.receiptReceivedTitle,
                            subtitle = formatDate(receipt.createdAt),
                            onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) },
                            meta = {
                                Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                                    ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                                    AmPrimaryButton(label = AppStrings.associateToExistingOperation, onClick = { selectedReceipt = receipt })
                                    AmSecondaryButton(label = AppStrings.createOperationFromThis, onClick = {
                                        viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                    })
                                }
                            },
                        )
                    }
                }
            }
        }
    selectedReceipt?.let { receipt ->
        val suggestion = viewModel.receiptSuggestion(receipt.id)
        val suggestedOperations = suggestion?.operationIds.orEmpty()
            .mapNotNull { id -> state.operations.firstOrNull { it.id == id } }
        val candidateOperations = suggestedOperations.ifEmpty { state.operations }
        // Selector de operación: elige de la lista en el propio diálogo
        // (la acción principal es elegir; Cancelar y Back son salidas).
        AmDialog(
            title = when (suggestion?.kind) {
                ReceiptMatchKind.SINGLE -> AppStrings.singleMatchTitle
                ReceiptMatchKind.MULTIPLE -> AppStrings.ACualCorresponde
                ReceiptMatchKind.NONE, null -> AppStrings.noMatchTitle
            },
            confirmLabel = AppStrings.Cancelar,
            confirmAsText = true,
            onConfirm = { selectedReceipt = null },
            onDismiss = { selectedReceipt = null },
            content = {
                // Con muchas operaciones candidatas la lista queda
                // alcanzable por scroll (accesibilidad).
                AmDialogList {
                    when {
                        suggestion == null -> AmText(AppStrings.analyzingReceipt, color = AmColor.TextSecondary)
                        suggestion.kind == ReceiptMatchKind.NONE ->
                            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                                AmText(
                                    AppStrings.noAutomaticMatch,
                                    color = AmColor.TextSecondary,
                                )
                                if (candidateOperations.isEmpty()) {
                                    AmText(AppStrings.noOperationsAvailable, color = AmColor.TextSecondary)
                                } else {
                                    candidateOperations.forEachIndexed { index, operation ->
                                        AmListRow(
                                            title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
                                            subtitle = operation.amount.orEmpty().ifBlank { AppStrings.SinMontoCorto },
                                            onClick = {
                                                viewModel.onAction(OperationAction.Associate(receipt.id, operation.id))
                                                selectedReceipt = null
                                            },
                                            showDivider = index < candidateOperations.lastIndex,
                                        )
                                    }
                                }
                            }
                        candidateOperations.isEmpty() ->
                            AmText(
                                AppStrings.LasOperacionesCandidatasYaNo,
                                color = AmColor.TextSecondary,
                            )
                        else -> candidateOperations.forEachIndexed { index, operation ->
                            AmListRow(
                                title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
                                subtitle = operation.amount.orEmpty().ifBlank { AppStrings.SinMontoCorto },
                                onClick = {
                                    viewModel.onAction(OperationAction.Associate(receipt.id, operation.id))
                                    selectedReceipt = null
                                },
                                showDivider = index < candidateOperations.lastIndex,
                            )
                        }
                    }
                }
            },
        )
    }
}

