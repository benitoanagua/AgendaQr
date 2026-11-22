package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

/**
 * G4: nodo de la lista agrupada (mes / día / operación).
 * Permite iterar con `items()` sobre una única lista plana y renderizar
 * encabezados de mes y día como secciones, conservando la agrupación
 * visual (mes → día → operaciones).
 */
private sealed class ListItem {
    data class Mes(val group: MonthlyGroup) : ListItem()
    data class Dia(val group: DailyGroup) : ListItem()
    data class Op(val operation: Operation) : ListItem()
}

/**
 * Fila de actividad dentro de la lista agrupada (G4). Función separada
 * para evitar anidación excesiva de lambdas.
 */
@Composable
private fun OperationRow(
    operation: Operation,
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup,
    selectedIds: Set<String>,
    selectionMode: Boolean,
    onToggleSelection: (String) -> Unit,
    onDelete: (Operation) -> Unit,
) {
    val syncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
    val amountText = operationAmountText(operation)
    val amountColor = if (operation.type == OperationType.COBRO) {
        AqColor.Success
    } else {
        AqColor.Danger
    }
    val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
    val showContextOrSync = contextName != null || syncStatus != ElementSyncStatus.Synced

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
            onAction = { onDelete(operation) },
        ),
    )

    AqSwipeActions(actions = swipeActions, enabled = !selectionMode) {
        AqMoonListRow(
            modifier = Modifier.fillMaxWidth(),
            title = operationTitle(operation),
            subtitle = operation.concept?.takeIf { it.isNotBlank() },
            endMeta = formatDate(operation.occurredAt),
            leading = {
                if (selectionMode) {
                    AqCheckbox(
                        label = "",
                        checked = selectedIds.contains(operation.id),
                        onCheckedChange = { checked ->
                            onToggleSelection(operation.id)
                        },
                    )
                } else {
                    AqMoonIconBadge(
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
                                AqMoonTonalTag(text = it)
                            }
                            if (syncStatus != ElementSyncStatus.Synced) {
                                AqMoonTonalTag(text = syncStatus.label))
                            }
                        }
                    }
                }
            },
            onClick = {
                if (selectionMode) {
                    onToggleSelection(operation.id)
                } else {
                    viewModel.onAction(OperationAction.Open(operation.id))
                }
            },
            showDivider = true,
        )
    }
}

/**
 * G4: lista agrupada por mes → día con encabezados de sección y balance
 * neto mensual por moneda, virtualizada con LazyColumn (N03).
 *
 * Renderiza su propio `LazyColumn`; el llamador la coloca dentro del
 * contenedor de scroll de la pantalla (p. ej. `AqPullToRefresh` +
 * `AqMoonListBody`).
 */
@Composable
fun OperationListGrouped(
    monthlyGroups: List<MonthlyGroup>,
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup,
    selectedIds: Set<String>,
    selectionMode: Boolean,
    onToggleSelection: (String) -> Unit,
    onDelete: (Operation) -> Unit,
) {
    val flatItems: List<ListItem> = buildList {
        monthlyGroups.forEach { monthGroup ->
            add(ListItem.Mes(monthGroup))
            monthGroup.days.forEach { dayGroup ->
                add(ListItem.Dia(dayGroup))
                dayGroup.operations.forEach { operation -> add(ListItem.Op(operation)) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(flatItems, key = {
            when (it) {
                is ListItem.Mes -> "mes-" + it.group.monthKey
                is ListItem.Dia -> "dia-" + it.group.dayKey
                is ListItem.Op -> "op-" + it.operation.id
            }
        }) { item ->
            when (item) {
                is ListItem.Mes -> {
                    val balanceText = item.group.balanceByCurrency.map { (currency, value) ->
                        currency + " " + formatBalance(value)
                    }.joinToString("  ")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AqSectionHeader(
                            text = item.group.monthLabel,
                            inset = false,
                            modifier = Modifier.weight(1f),
                        )
                        balanceText.takeIf { it.isNotBlank() }?.let { balance ->
                            AqMoonTonalTag(text = balance)
                        }
                    }
                }
                is ListItem.Dia -> {
                    AqSectionHeader(
                        text = item.group.dayLabel,
                        inset = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                is ListItem.Op -> {
                    OperationRow(
                        operation = item.operation,
                        state = state,
                        viewModel = viewModel,
                        syncLookup = syncLookup,
                        selectedIds = selectedIds,
                        selectionMode = selectionMode,
                        onToggleSelection = onToggleSelection,
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}
