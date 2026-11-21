package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.ComprobanteThumbnail
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqDialog
import com.agendaqr.core.ui.components.AqDialogList
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqSelectionBar
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTonalTag
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ReceiptMatchKind
import com.agendaqr.destinations.data.SyncResource

/**
 * S07 — Bandeja de comprobantes sin asociar (`OperationRoute.Unassociated`).
 *
 * Contrato congelado (docs/04-ux, inbox): acción primaria `—`, sin gestos
 * declarados, componentes `AqScreenScaffold`, `AqListRow`, `AqEmptyState`
 * y `AqDialogList`. La fila entera es tocable y abre el flujo de
 * asociación; «Crear nueva actividad con esto» es acción secundaria de
 * texto dentro de la celda. Todas las filas forman un único Cell-group
 * (spec §12). El chevron añade la affordance de «fila tocable» sin alterar el
 * contrato de navegación (no cambia ruta ni back stack).
 */
@Composable
internal fun UnassociatedScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var selectedReceipt by remember { mutableStateOf<Comprobante?>(null) }
    // G3: modo selección por lote en la bandeja.
    var selectedReceiptIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarRecibo by remember { mutableStateOf(false) }
    val selectionMode = selectedReceiptIds.isNotEmpty()
    // V1.4 (§12): bandeja con título y flecha atrás en la barra superior
    // (no es destino raíz: no lleva navegación inferior).
    AqScreenScaffold(
        topBar = {
            if (selectionMode) {
                val selectAll = state.unassociated.isNotEmpty() && selectedReceiptIds.size == state.unassociated.size
                AqSelectionBar(
                    closeLabel = AppStrings.CerrarSeleccion,
                    selectAllLabel = if (selectAll) AppStrings.SeleccionarNinguno else AppStrings.SeleccionarTodo,
                    selectedCountLabel = "${selectedReceiptIds.size} ${AppStrings.Seleccionados}",
                    selectedCount = selectedReceiptIds.size,
                    totalCount = state.unassociated.size,
                    selectAllChecked = selectAll,
                    onSelectAll = {
                        selectedReceiptIds = if (selectAll) emptySet() else state.unassociated.map { it.id }.toSet()
                    },
                    onClose = { selectedReceiptIds = emptySet() },
                )
            } else {
                AqTopBar(
                    title = AppStrings.ComprobantesSinAsociar,
                    onBack = { viewModel.onAction(OperationAction.Back) },
                    backLabel = AppStrings.Volver,
                )
            }
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (selectionMode) {
            {
                AqBottomAction(
                    label = AppStrings.EliminarComprobante + " (${selectedReceiptIds.size})",
                    onClick = { confirmarEliminarRecibo = true },
                    enabled = selectedReceiptIds.isNotEmpty(),
                )
            }
        } else {
            null
        },
    ) {
        if (state.unassociated.isEmpty()) {
            AqEmptyState(icon = AqIcons.Inbox, title = AppStrings.noUnassociatedReceipts)
        } else {
            // Un único Cell-group (spec §12: las tarjetas no envuelven cada
            // fila). La fila entera abre la asociación; «Crear nueva
            // actividad con esto» es la acción de texto de la celda, sin
            // botón tonal por fila.
            AqListBody(modifier = Modifier.fillMaxWidth().weight(1f)) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(state.unassociated, key = { _, it -> it.id }) { index, receipt ->
                        val isLast = index == state.unassociated.lastIndex
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AqListRow(
                                title = receipt.file.takeIf { it.isNotBlank() } ?: AppStrings.ElementoImportado,
                                subtitle = formatDate(receipt.createdAt),
                                onClick = {
                                    if (selectionMode) {
                                        selectedReceiptIds = if (selectedReceiptIds.contains(receipt.id)) {
                                            selectedReceiptIds - receipt.id
                                        } else {
                                            selectedReceiptIds + receipt.id
                                        }
                                    } else {
                                        selectedReceipt = receipt
                                    }
                                },
                                leading = {
                                    if (selectionMode) {
                                        AqCheckbox(
                                            label = "",
                                            checked = selectedReceiptIds.contains(receipt.id),
                                            onCheckedChange = { checked ->
                                                selectedReceiptIds = if (checked) {
                                                    selectedReceiptIds + receipt.id
                                                } else {
                                                    selectedReceiptIds - receipt.id
                                                }
                                            },
                                        )
                                    } else {
                                        ComprobanteThumbnail(
                                            file = receipt.file,
                                            mimeType = receipt.mimeType,
                                            contentDescription = if (receipt.mimeType?.contains("pdf", ignoreCase = true) == true) {
                                                AppStrings.PdfAdjuntoSinVistaPrevia
                                            } else {
                                                AppStrings.VistaPreviaNoDisponible
                                            },
                                        )
                                    }
                                },
                                meta = {
                                    ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                                },
                                titleMaxLines = 2,
                                showChevron = true,
                                showDivider = selectionMode && !isLast,
                            )
                            if (!selectionMode) {
                                AqTextAction(
                                    label = AppStrings.createOperationFromThis,
                                    icon = AqIcons.Add,
                                    onClick = {
                                        viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                    },
                                    modifier = Modifier.padding(start = AqSpacing.Sm, bottom = AqSpacing.Xs),
                                )
                                if (!isLast) AqHairline()
                            }
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
                                AqText(AppStrings.noAutomaticMatch, color = AqColor.TextSecondary)
                                if (candidateOperations.isEmpty()) {
                                    AqText(AppStrings.noOperationsAvailable, color = AqColor.TextSecondary)
                                } else {
                                    CandidateOperationRows(candidateOperations) { operationId ->
                                        viewModel.onAction(OperationAction.Associate(receipt.id, operationId))
                                        selectedReceipt = null
                                    }
                                }
                            }
                        candidateOperations.isEmpty() ->
                            AqText(AppStrings.LasOperacionesCandidatasYaNo, color = AqColor.TextSecondary)
                        else -> CandidateOperationRows(candidateOperations) { operationId ->
                            viewModel.onAction(OperationAction.Associate(receipt.id, operationId))
                            selectedReceipt = null
                        }
                    }
                }
            },
        )
    }

    // G3: confirmación de eliminación por lote en la bandeja.
    if (confirmarEliminarRecibo) {
        val plural = if (selectedReceiptIds.size == 1) AppStrings.ComprobanteSingular else AppStrings.ComprobantesPlural
        AqDialog(
            title = AppStrings.EliminarComprobante,
            destructive = true,
            message = AppStrings.irreversibleActionWarning + "\n" + selectedReceiptIds.size + " " + plural,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                selectedReceiptIds.toList().forEach { id ->
                    viewModel.onAction(OperationAction.DeleteComprobante(id))
                }
                selectedReceiptIds = emptySet()
                confirmarEliminarRecibo = false
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { confirmarEliminarRecibo = false },
        )
    }
}

/** Filas de operaciones candidatas del selector de asociación (una sola
 *  anatomía para el caso con y sin coincidencia automática). */
@Composable
private fun CandidateOperationRows(
    operations: List<com.agendaqr.destinations.domain.Operation>,
    onPick: (String) -> Unit,
) {
    operations.forEachIndexed { index, operation ->
        AqListRow(
            title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
            subtitle = operation.amount.orEmpty().ifBlank { AppStrings.SinMontoCorto },
            onClick = { onPick(operation.id) },
            showDivider = index < operations.lastIndex,
        )
    }
}
