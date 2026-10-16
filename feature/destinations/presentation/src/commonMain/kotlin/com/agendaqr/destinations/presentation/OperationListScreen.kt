/**
 * S07 — Lista de operaciones, bandeja de comprobantes sin asociar y el
 * dispatcher de la superficie (diálogo de recibido y visor).
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmSegmentedControl
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
    // Pestañas cápsula (referencia Stitch): Todas · Pago · Cobro con
    // CONTEO real; "Comprobantes sueltos" es acción separada, no filtro.
    var typeFilter by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(0) }
    val pagosCount = operations.count { it.type == OperationType.PAGO }
    val cobrosCount = operations.count { it.type == OperationType.COBRO }
    val filtered = when (typeFilter) {
        1 -> operations.filter { it.type == OperationType.PAGO }
        2 -> operations.filter { it.type == OperationType.COBRO }
        else -> operations
    }
    val paged = filtered.take(visibleCount)

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
        // Grupo de entrada: búsqueda + control segmentado (8 dp).
        // SegmentedControl (referencia Stitch): 3 opciones con badges de conteo.
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSearchField(
                value = state.query,
                onValueChange = { viewModel.onAction(OperationAction.Search(it)) },
                label = AppStrings.Buscar,
                placeholder = AppStrings.BuscarOperaciones,
                onClear = { viewModel.onAction(OperationAction.Search("")) },
                clearLabel = AppStrings.Limpiar
            )
            AmSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Todas,
                        count = operations.size,
                    ),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Pago,
                        icon = AmIcons.Expense,
                        count = pagosCount,
                    ),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Cobro,
                        icon = AmIcons.Income,
                        activeTone = AmTone.Success,
                        count = cobrosCount,
                    ),
                ),
                selectedIndex = typeFilter,
                onSelect = { typeFilter = it },
                modifier = Modifier.fillMaxWidth(),
            )
            // "Comprobantes sueltos" no es un filtro de tipo: es navegación
            // a la bandeja. Se muestra como botón secundario de ancho completo.
            if (state.unassociated.isNotEmpty()) {
                AmSecondaryButton(
                    label = AppStrings.ComprobantesSueltos + " (" + state.unassociated.size + ")",
                    onClick = { viewModel.onAction(OperationAction.OpenUnassociated) },
                    icon = AmIcons.Inbox,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Barra de mes con reaseguro local: mes del registro más reciente
        // + conteo + píldora de respaldo. Vive en su propia tarjeta.
        if (filtered.isNotEmpty()) {
            AmListBody {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Sm),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                    ) {
                        AmIcon(
                            imageVector = AmIcons.Calendar,
                            contentDescription = null,
                            size = AmMetrics.IconSizeCompact,
                            tint = AmColor.AccentText,
                        )
                        Column {
                            AmText(
                                monthYearLabel(filtered.first().occurredAt),
                                size = AmType.BodySmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            AmText(
                                filtered.size.toString() + " " + AppStrings.RegistrosLocales,
                                color = AmColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        }

        // Error persistente.
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

        if (filtered.isEmpty()) {
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
            // Lista agrupada (referencia Stitch: Card mode / ListBody):
            // AmListBody + AmListRow con separadores hairline.
            // Cada fila: insignia direccional + título/concepto + importe
            // coloreado con signo + metadato "ocurrido · Reg:" + tags.
            AmListBody(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                Column {
                    paged.forEachIndexed { index, operation ->
                        val syncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
                        val amountText = operationAmountText(operation)
                        val amountColor = if (operation.type == OperationType.COBRO) {
                            AmColor.Success
                        } else {
                            AmColor.Danger
                        }
                        val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
                        val showContextOrSync = contextName != null || syncStatus != ElementSyncStatus.Synced
                        val isLast = index == paged.lastIndex && paged.size >= filtered.size

                        AmListRow(
                            modifier = Modifier.fillMaxWidth(),
                            title = operationTitle(operation),
                            subtitle = operation.concept?.takeIf { it.isNotBlank() },
                            leading = {
                                AmIconBadge(
                                    icon = operationTypeIcon(operation.type),
                                    tone = operationTypeTone(operation.type),
                                )
                            },
                            trailing = {
                                amountText?.let {
                                    AmText(
                                        it,
                                        size = AmType.SectionHeader,
                                        fontWeight = FontWeight.SemiBold,
                                        color = amountColor,
                                    )
                                }
                            },
                            meta = {
                                Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                                    ) {
                                        AmIcon(
                                            imageVector = AmIcons.Clock,
                                            contentDescription = null,
                                            size = AmMetrics.IconSizeCompact,
                                            tint = AmColor.TextTertiary,
                                        )
                                        AmText(
                                            formatDate(operation.occurredAt) + " · " + AppStrings.RegPrefijo + formatTime(operation.createdAt),
                                            color = AmColor.TextTertiary,
                                            size = AmType.Caption,
                                        )
                                    }
                                    if (showContextOrSync) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                                            contextName?.let {
                                                AmTonalTag(text = it, tone = AmTone.Neutral)
                                            }
                                            if (syncStatus != ElementSyncStatus.Synced) {
                                                AmTonalTag(text = syncStatus.label, tone = syncStatus.tone())
                                            }
                                        }
                                    }
                                }
                            },
                            onClick = { viewModel.onAction(OperationAction.Open(operation.id)) },
                            showDivider = !isLast,
                        )
                    }
                }
            }

            // Cargar más (fuera del AmListBody para no tener separador extra).
            if (paged.size < filtered.size) {
                AmLoadMoreFooter(
                    modifier = Modifier.fillMaxWidth().padding(top = AmSpacing.Sm),
                    onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(filtered.size) },
                    loadMoreLabel = AppStrings.CargarMas,
                    loadingLabel = AppStrings.Cargando,
                    endLabel = AppStrings.NoHayMasElementos,
                )
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
                        // U-15: una sola primaria por pantalla — la fila
                        // entera abre el flujo de asociación (diálogo con
                        // candidatas); "Crear actividad desde esto" es
                        // acción de texto. Sin botones primarios por fila.
                        AmListBody {
                            AmListRow(
                                title = AppStrings.receiptReceivedTitle,
                                subtitle = formatDate(receipt.createdAt),
                                onClick = { selectedReceipt = receipt },
                                meta = {
                                    Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                                        ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                                        AmTextAction(label = AppStrings.createOperationFromThis, onClick = {
                                            viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                        })
                                    }
                                },
                            )
                        }
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

