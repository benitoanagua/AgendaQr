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
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AqExtendedFab
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.theme.*
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

@Composable
internal fun OperationListScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    var operationToDelete by remember { mutableStateOf<Operation?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    // Pestañas cápsula: Todas · Pago · Cobro con
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

    AqScreenScaffold(
        topBar = {
            AqTopBar(title = AppStrings.Operaciones)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        floatingActionButton = {
            AqExtendedFab(
                label = AppStrings.RegistrarOperacion,
                icon = AqIcons.Add,
                onClick = { viewModel.onAction(OperationAction.New) },
            )
        },
    ) {
        // Grupo de entrada: búsqueda + control segmentado (8 dp).
        // SegmentedControl: 3 opciones con badges de conteo.
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqSearchField(
                value = state.query,
                onValueChange = { viewModel.onAction(OperationAction.Search(it)) },
                label = AppStrings.Buscar,
                placeholder = AppStrings.BuscarOperaciones,
                onClear = { viewModel.onAction(OperationAction.Search("")) },
                clearLabel = AppStrings.Limpiar
            )
            AqSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Todas,
                        count = operations.size,
                    ),
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Pago,
                        icon = AqIcons.Expense,
                        count = pagosCount,
                    ),
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Cobro,
                        icon = AqIcons.Income,
                        activeTone = AqTone.Success,
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
                AqSecondaryButton(
                    label = AppStrings.ComprobantesSueltos + " (" + state.unassociated.size + ")",
                    onClick = { viewModel.onAction(OperationAction.OpenUnassociated) },
                    icon = AqIcons.Inbox,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Barra de mes con reaseguro local: mes del registro más reciente
        // + conteo + píldora de respaldo. Vive en su propia tarjeta.
        if (filtered.isNotEmpty()) {
            // La tarjeta (organismo) aporta superficie y relleno de sección;
            // la página no compensa con padding propio.
            AqCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                    ) {
                        AqIcon(
                            imageVector = AqIcons.Calendar,
                            contentDescription = null,
                            size = AqMetrics.IconSizeCompact,
                            tint = AqColor.EmphasisIcon,
                        )
                        Column {
                            AqText(
                                monthYearLabel(filtered.first().occurredAt),
                                size = AqType.BodySmall,
                                fontWeight = AqWeight.SemiBold,
                            )
                            AqText(
                                filtered.size.toString() + " " + AppStrings.RegistrosLocales,
                                color = AqColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        }

        // Error persistente.
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { viewModel.onAction(OperationAction.RetryFailed) },
                onClear = { viewModel.onAction(OperationAction.ClearError) },
            )
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }

        if (state.isLoadingOperations) {
            // Carga en frío de S07-list: esqueleto mientras llega la
            // primera emisión (contrato `loading` de S07-list, N02).
            AqSkeleton(lines = 6)
        } else if (filtered.isEmpty()) {
            AqEmptyState(
                icon = AqIcons.Register,
                title = if (state.query.isBlank()) AppStrings.AunNoHayOperaciones else AppStrings.NoHayCoincidencias,
                subtitle = if (state.query.isBlank()) AppStrings.registerFirstPaymentHint else AppStrings.tryAnotherSearchHint,
                // El FAB "Registrar" cubre la misma acción en la primera
                // ejecución: el vacío no repite el botón, solo apunta al FAB.
                actionLabel = if (state.query.isBlank()) null else AppStrings.LimpiarBusqueda,
                onAction = if (state.query.isBlank()) null else ({ viewModel.onAction(OperationAction.Search("")) })
            )
        } else {
            // Lista agrupada (Card mode / ListBody), virtualizada con
            // LazyColumn (N03): solo se componen las filas visible + búfer.
            AqPullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.onAction(OperationAction.Search(state.query)) },
                refreshingLabel = AppStrings.Cargando,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                AqListBody(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itemsIndexed(paged) { index, operation ->
                            val syncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
                            val amountText = operationAmountText(operation)
                            val amountColor = if (operation.type == OperationType.COBRO) {
                                AqColor.Success
                            } else {
                                AqColor.Danger
                            }
                            val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
                            val showContextOrSync = contextName != null || syncStatus != ElementSyncStatus.Synced
                            val isLast = index == paged.lastIndex && paged.size >= filtered.size

                            val swipeActions = listOf(
                                AqSwipeAction(
                                    label = AppStrings.Editar,
                                    icon = AqIcons.Edit,
                                    backgroundColor = AqColor.EmphasisFill,
                                    contentColor = AqColor.InverseOnSurface,
                                    onAction = { viewModel.onAction(OperationAction.Edit(operation.id)) },
                                ),
                                AqSwipeAction(
                                    label = AppStrings.Eliminar,
                                    icon = AqIcons.Trash,
                                    backgroundColor = AqColor.DangerFill,
                                    contentColor = AqColor.InverseOnSurface,
                                    onAction = { operationToDelete = operation },
                                ),
                            )

                            AqSwipeActions(actions = swipeActions) {
                                AqListRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = operationTitle(operation),
                                    subtitle = operation.concept?.takeIf { it.isNotBlank() },
                                    // Fecha real del registro: metadato corto en la
                                    // línea del título (patrón de lista de facturas);
                                    // la hora de registro queda con el resto del meta.
                                    endMeta = formatDate(operation.occurredAt),
                                    leading = {
                                        AqIconBadge(
                                            icon = operationTypeIcon(operation.type),
                                            tone = operationTypeTone(operation.type),
                                        )
                                    },
                                    trailing = {
                                        amountText?.let {
                                            AqText(
                                                it,
                                                size = AqType.SectionHeader,
                                                fontWeight = AqWeight.SemiBold,
                                                color = amountColor,
                                            )
                                        }
                                    },
                                    meta = {
                                        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                                            ) {
                                                AqIcon(
                                                    imageVector = AqIcons.Clock,
                                                    contentDescription = null,
                                                    size = AqMetrics.IconSizeCompact,
                                                    tint = AqColor.TextTertiary,
                                                )
                                                AqText(
                                                    AppStrings.RegPrefijo + formatTime(operation.createdAt),
                                                    color = AqColor.TextTertiary,
                                                    size = AqType.Caption,
                                                )
                                            }
                                            if (showContextOrSync) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                                                    contextName?.let {
                                                        AqTonalTag(text = it, tone = AqTone.Neutral)
                                                    }
                                                    if (syncStatus != ElementSyncStatus.Synced) {
                                                        AqTonalTag(text = syncStatus.label, tone = syncStatus.tone())
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
                }
            }

            // Cargar más (fuera del AqListBody para no tener separador extra).
            if (paged.size < filtered.size) {
                AqLoadMoreFooter(
                    modifier = Modifier.fillMaxWidth(),
                    onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(filtered.size) },
                    loadMoreLabel = AppStrings.CargarMas,
                    loadingLabel = AppStrings.Cargando,
                    endLabel = AppStrings.NoHayMasElementos,
                )
            }
        }
    }

    operationToDelete?.let { op ->
        AqDialog(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                viewModel.onAction(OperationAction.Delete(op.id))
                operationToDelete = null
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { operationToDelete = null },
        )
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
    AqScreenScaffold(
        topBar = {
            AqTopBar(
                title = AppStrings.ComprobantesSinAsociar,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
            if (state.unassociated.isEmpty()) {
                AqEmptyState(icon = AqIcons.Inbox, title = AppStrings.noUnassociatedReceipts)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
                ) {
                    items(state.unassociated, key = { it.id }) { receipt ->
                        // Una sola primaria por pantalla — la fila
                        // entera abre el flujo de asociación (diálogo con
                        // candidatas); "Crear actividad desde esto" es
                        // acción de texto. Sin botones primarios por fila.
                        AqListBody {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AqListRow(
                                    title = receipt.file.takeIf { it.isNotBlank() } ?: AppStrings.ElementoImportado,
                                    subtitle = formatDate(receipt.createdAt),
                                    onClick = { selectedReceipt = receipt },
                                    leading = {
                                        // Miniatura real del comprobante (imagen) o
                                        // representación de tipo (PDF / fallback).
                                        ComprobanteThumbnail(
                                            file = receipt.file,
                                            mimeType = receipt.mimeType,
                                        )
                                    },
                                    meta = {
                                        ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                                    },
                                    titleMaxLines = 2,
                                )
                                // CTA de fila con affordance real: píldora
                                // tonal compacta con target de 48 dp (no un
                                // enlace de texto sin superficie).
                                AqCompactAction(
                                    label = AppStrings.createOperationFromThis,
                                    icon = AqIcons.Add,
                                    onClick = {
                                        viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Sm),
                                )
                            }
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
        AqDialog(
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
                AqDialogList {
                    when {
                        suggestion == null -> AqText(AppStrings.analyzingReceipt, color = AqColor.TextSecondary)
                        suggestion.kind == ReceiptMatchKind.NONE ->
                            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                                AqText(
                                    AppStrings.noAutomaticMatch,
                                    color = AqColor.TextSecondary,
                                )
                                if (candidateOperations.isEmpty()) {
                                    AqText(AppStrings.noOperationsAvailable, color = AqColor.TextSecondary)
                                } else {
                                    candidateOperations.forEachIndexed { index, operation ->
                                        AqListRow(
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
                            AqText(
                                AppStrings.LasOperacionesCandidatasYaNo,
                                color = AqColor.TextSecondary,
                            )
                        else -> candidateOperations.forEachIndexed { index, operation ->
                            AqListRow(
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

