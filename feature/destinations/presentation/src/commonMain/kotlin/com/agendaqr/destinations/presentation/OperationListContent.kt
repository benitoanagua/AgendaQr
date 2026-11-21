package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqCard
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqExtendedFab
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqLoadMoreFooter
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqSearchField
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelectionBar
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSkeleton
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTonalTag
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

/**
 * S07 — Lista de operaciones (`OperationRoute.List`).
 *
 * Contenido extraído de [OperationListScreen] para aislar la superficie de
 * listado de la bandeja de comprobantes sin asociar y del dispatcher de
 * la superficie. Sin cambios de comportamiento.
 */
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
    // G3: modo selección por lote (pulsación larga entra en modo;
    // rememberSaveable conserva la selección entre recreaciones).
    var selectedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarLote by remember { mutableStateOf(false) }
    val selectionMode = selectedIds.isNotEmpty()
    val selectAll = operations.isNotEmpty() && selectedIds.size == operations.size
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

    // G4: agrupación por mes/día con encabezados de sección y balance neto
    // mensual por moneda (domain.OperationGrouping, verificado con pruebas).
    val monthlyGroups = groupOperationsByMonthAndDay(paged)

    AqScreenScaffold(
        topBar = {
            if (selectionMode) {
                AqSelectionBar(
                    closeLabel = AppStrings.CerrarSeleccion,
                    selectAllLabel = if (selectAll) AppStrings.SeleccionarNinguno else AppStrings.SeleccionarTodo,
                    selectedCountLabel = "${selectedIds.size} ${AppStrings.Seleccionados}",
                    selectedCount = selectedIds.size,
                    totalCount = operations.size,
                    selectAllChecked = operations.isNotEmpty() && selectedIds.size == operations.size,
                    onSelectAll = {
                        selectedIds = if (operations.isNotEmpty() && selectedIds.size == operations.size) {
                            emptySet()
                        } else {
                            operations.map { it.id }.toSet()
                        }
                    },
                    onClose = { selectedIds = emptySet() },
                )
            } else {
                AqTopBar(title = AppStrings.Operaciones)
            }
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
        bottomAction = if (selectionMode) {
            {
                AqBottomAction(
                    label = AppStrings.EliminarOperacion + " (${selectedIds.size})",
                    onClick = { confirmarEliminarLote = true },
                    enabled = selectedIds.isNotEmpty(),
                )
            }
        } else {
            null
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
                    AqSegmentedOption(
                        label = AppStrings.Todas,
                        count = operations.size,
                    ),
                    AqSegmentedOption(
                        label = AppStrings.Pago,
                        icon = AqIcons.Expense,
                        count = pagosCount,
                    ),
                    AqSegmentedOption(
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
                enabled = !selectionMode,
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

                            AqSwipeActions(actions = swipeActions, enabled = !selectionMode) {
                                AqListRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = operationTitle(operation),
                                    subtitle = operation.concept?.takeIf { it.isNotBlank() },
                                    // Fecha real del registro: metadato corto en la
                                    // línea del título (patrón de lista de facturas);
                                    // la hora de registro queda con el resto del meta.
                                    endMeta = formatDate(operation.occurredAt),
                                    leading = {
                                        if (selectionMode) {
                                            AqCheckbox(
                                                label = "",
                                                checked = selectedIds.contains(operation.id),
                                                onCheckedChange = { checked ->
                                                    selectedIds = if (checked) {
                                                        selectedIds + operation.id
                                                    } else {
                                                        selectedIds - operation.id
                                                    }
                                                },
                                            )
                                        } else {
                                            AqIconBadge(
                                                icon = operationTypeIcon(operation.type),
                                                tone = operationTypeTone(operation.type),
                                            )
                                        }
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
                                    onClick = {
                                        if (selectionMode) {
                                            selectedIds = if (selectedIds.contains(operation.id)) {
                                                selectedIds - operation.id
                                            } else {
                                                selectedIds + operation.id
                                            }
                                        } else {
                                            viewModel.onAction(OperationAction.Open(operation.id))
                                        }
                                    },
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

    // G3: confirmación de eliminación por lote con el conteo.
    if (confirmarEliminarLote) {
        val plural = if (selectedIds.size == 1) AppStrings.OperacionSingular else AppStrings.OperacionesPlural
        AqDialog(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            message = AppStrings.irreversibleActionWarning + "\n" + selectedIds.size + " " + plural,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                selectedIds.toList().forEach { id ->
                    viewModel.onAction(OperationAction.Delete(id))
                }
                selectedIds = emptySet()
                confirmarEliminarLote = false
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { confirmarEliminarLote = false },
        )
    }
}
