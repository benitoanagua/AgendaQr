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
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

/**
 * S07 — Lista de operaciones (`OperationRoute.List`).
 * Construido exclusivamente con componentes Moon.
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
    var selectedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarLote by remember { mutableStateOf(false) }
    val selectionMode = selectedIds.isNotEmpty()
    val selectAll = operations.isNotEmpty() && selectedIds.size == operations.size
    var typeFilter by rememberSaveable { mutableStateOf(0) }
    val pagosCount = operations.count { it.type == OperationType.PAGO }
    val cobrosCount = operations.count { it.type == OperationType.COBRO }
    val filtered = when (typeFilter) {
        1 -> operations.filter { it.type == OperationType.PAGO }
        2 -> operations.filter { it.type == OperationType.COBRO }
        else -> operations
    }
    val paged = filtered.take(visibleCount)
    val toastHost = appHosts?.toastHost

    AqScreen(
        title = AppStrings.Operaciones,
        topBar = if (selectionMode) {
            {
                AqSelectionToolbar(
                    closeLabel = AppStrings.CerrarSeleccion,
                    selectAllLabel = if (selectAll) AppStrings.SeleccionarNinguno else AppStrings.SeleccionarTodo,
                    selectedCountLabel = "${selectedIds.size} ${AppStrings.Seleccionados}",
                    selectedCount = selectedIds.size,
                    totalCount = operations.size,
                    selectAllChecked = selectAll,
                    onSelectAll = {
                        selectedIds = if (selectAll) emptySet() else operations.map { it.id }.toSet()
                    },
                    onClose = { selectedIds = emptySet() },
                )
            }
        } else {
            null
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { name -> navChrome?.onSelect?.invoke(name); Unit },
        statusContent = appHosts?.status,
        bottomAction = if (selectionMode) {
            {
                AqButton(
                    label = AppStrings.EliminarOperacion + " (${selectedIds.size})",
                    onClick = { confirmarEliminarLote = true },
                    enabled = selectedIds.isNotEmpty(),
                    variant = AqButtonVariant.Danger,
                )
            }
        } else {
            null
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqTextInput(
                label = AppStrings.Buscar,
                value = state.query,
                onValueChange = { viewModel.onAction(OperationAction.Search(it)) },
            )
            AqSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Todas, count = operations.size),
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Pago, icon = AqIcons.Expense, count = pagosCount),
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = AppStrings.Cobro, icon = AqIcons.Income,
                        activeTone = AqTone.Success, count = cobrosCount),
                ),
                selectedIndex = typeFilter,
                onSelect = { typeFilter = it },
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.unassociated.isNotEmpty()) {
                AqButton(
                    label = AppStrings.ComprobantesSueltos + " (" + state.unassociated.size + ")",
                    onClick = { viewModel.onAction(OperationAction.OpenUnassociated) },
                    icon = AqIcons.Inbox,
                    variant = AqButtonVariant.Text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AqSectionTitle(text = AppStrings.Operaciones)
            if (state.isLoadingOperations) {
                AqSkeletonBlock(lines = 4)
            } else if (filtered.isEmpty()) {
                AqEmptyPlaceholder(
                    icon = AqIcons.Register,
                    title = AppStrings.AunNoHayOperaciones,
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    paged.forEachIndexed { index, operation ->
                        AqMenuItem(
                            title = listOfNotNull(
                                when (operation.type) {
                                    OperationType.PAGO -> AppStrings.Pago
                                    OperationType.COBRO -> AppStrings.Cobro
                                },
                                operation.amount,
                                operation.currency,
                                operation.personOrEntity,
                            ).joinToString(" · "),
                            subtitle = formatDate(operation.occurredAt),
                            onClick = { viewModel.onAction(OperationAction.Open(operation.id)) },
                            leading = {
                                AqIcon(
                                    imageVector = AqIcons.Attachment,
                                    contentDescription = null,
                                )
                            },
                            trailing = {
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
                            },
                            showDivider = index < paged.lastIndex,
                        )
                        if (index < paged.lastIndex) AqHairline()
                    }
                }
                if (paged.size < filtered.size) {
                    com.agendaqr.core.ui.components.AqLoadMore(
                        onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(filtered.size) },
                        loadMoreLabel = AppStrings.CargarMas,
                        loadingLabel = AppStrings.Cargando,
                        endLabel = AppStrings.NoHayMasElementos,
                    )
                }
            }
        }
    }

    operationToDelete?.let { op ->
        AqModal(
            title = AppStrings.EliminarOperacion,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                operationToDelete = null
                viewModel.onAction(OperationAction.Delete(op.id))
            },
            onDismissRequest = { operationToDelete = null },
            content = { },
        )
    }

    if (confirmarEliminarLote) {
        val plural = if (selectedIds.size == 1) AppStrings.OperacionSingular else AppStrings.OperacionesPlural
        AqModal(
            title = AppStrings.EliminarOperacion,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                selectedIds.toList().forEach { id ->
                    viewModel.onAction(OperationAction.Delete(id))
                }
                selectedIds = emptySet()
                confirmarEliminarLote = false
            },
            onDismissRequest = { confirmarEliminarLote = false },
            content = {
                AqText(
                    text = AppStrings.irreversibleActionWarning + "\n" + selectedIds.size + " " + plural,
                    color = AqColor.TextSecondary,
                )
            },
        )
    }
}
