package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

private sealed class ListItem {
    data class Mes(val group: MonthlyGroup) : ListItem()
    data class Dia(val group: DailyGroup) : ListItem()
    data class Op(val operation: Operation) : ListItem()
}

@Composable
internal fun OperationListGrouped(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    var selectedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarLote by remember { mutableStateOf(false) }
    val selectionMode = selectedIds.isNotEmpty()
    val toastHost = appHosts?.toastHost

    AqScreen(
        title = AppStrings.Operaciones,
        topBar = if (selectionMode) {
            {
                val selectAll = operations.isNotEmpty() && selectedIds.size == operations.size
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
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        if (state.isLoading) {
            AqSkeletonBlock(lines = 4)
        } else if (operations.isEmpty()) {
            AqEmptyPlaceholder(
                icon = AqIcons.Register,
                title = AppStrings.AunNoHayOperaciones,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                operations.take(visibleCount).forEachIndexed { index, operation ->
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
                            AqCheckbox(
                                label = "",
                                checked = selectedIds.contains(operation.id),
                                onCheckedChange = { checked ->
                                    selectedIds = if (checked) selectedIds + operation.id else selectedIds - operation.id
                                },
                            )
                        },
                        trailing = {
                            AqIcon(
                                imageVector = AqIcons.Attachment,
                                contentDescription = null,
                                tint = AqColor.TextSecondary,
                            )
                        },
                        showDivider = index < operations.take(visibleCount).lastIndex,
                    )
                    if (index < operations.take(visibleCount).lastIndex) {
                        AqHairline()
                    }
                }
            }
        }
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

@Composable
internal fun OperationListScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
) {
    OperationListGrouped(state = state, viewModel = viewModel, appHosts = appHosts)
}
