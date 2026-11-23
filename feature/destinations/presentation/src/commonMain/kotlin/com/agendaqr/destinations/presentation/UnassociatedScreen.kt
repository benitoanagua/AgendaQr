package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.ComprobanteThumbnail
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ReceiptMatchKind
import com.agendaqr.destinations.data.SyncResource

/**
 * S07 — Bandeja de comprobantes sin asociar (`OperationRoute.Unassociated`).
 *
 * Contrato congelado (docs/04-ux, inbox): acción primaria `—`, sin gestos
 * declarados. Ahora construido exclusivamente con componentes Moon:
 * [AqScreen] (scaffold), [AqAppBar] (vía título/atrás de [AqScreen]),
 * [AqSelectionToolbar] (modo selección G3), [AqMenuItem] (filas),
 * [AqEmptyPlaceholder] (vacío), [AqToastHost] (toasts), [AqModal] (diálogos)
 * y [AqButton] (acciones). Sin símbolos legacy ni wrappers `Aq*`.
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
    val toastHost = appHosts?.toastHost

    // V1.4 (§12): bandeja con título y flecha atrás en la barra superior
    // (no es destino raíz: no lleva navegación inferior).
    AqScreen(
        title = AppStrings.ComprobantesSinAsociar,
        onBack = { viewModel.onAction(OperationAction.Back) },
        backLabel = AppStrings.Volver,
        topBar = if (selectionMode) {
            {
                val selectAll = state.unassociated.isNotEmpty() && selectedReceiptIds.size == state.unassociated.size
                AqSelectionToolbar(
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
            }
        } else {
            null
        },
        // Acción principal fija inferior (dock): respeta navigationBars + IME.
        bottomAction = if (selectionMode) {
            {
                AqButton(
                    label = AppStrings.EliminarComprobante + " (" + selectedReceiptIds.size + ")",
                    onClick = { confirmarEliminarRecibo = true },
                    enabled = selectedReceiptIds.isNotEmpty(),
                    variant = AqButtonVariant.Danger,
                    size = AqButtonSize.Large,
                )
            }
        } else {
            null
        },
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        if (state.unassociated.isEmpty()) {
            AqEmptyPlaceholder(
                icon = AqIcons.Inbox,
                title = AppStrings.noUnassociatedReceipts,
            )
        } else {
            // Un único Cell-group (spec §12: las tarjetas no envuelven cada
            // fila). La fila entera abre la asociación; «Crear nueva
            // actividad con esto» es la acción de texto de la celda.
            Column(modifier = Modifier.fillMaxWidth().testTag("aq-list-body")) {
                state.unassociated.forEachIndexed { index, receipt ->
                    val isLast = index == state.unassociated.lastIndex
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AqMenuItem(
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
                            trailing = {
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (!selectionMode) {
                                        AqIcon(
                                            imageVector = AqIcons.ChevronRight,
                                            contentDescription = null,
                                            size = AqMetrics.IconSizeCompact,
                                            tint = AqColor.TextTertiary,
                                        )
                                    }
                                    ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                                }
                            },
                            titleMaxLines = 2,
                            showDivider = selectionMode && !isLast,
                        )
                        if (!selectionMode) {
                            AqButton(
                                label = AppStrings.createOperationFromThis,
                                onClick = {
                                    viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                },
                                variant = AqButtonVariant.Text,
                                icon = AqIcons.Add,
                                size = AqButtonSize.Small,
                                modifier = Modifier.padding(start = AqSpacing.Sm, bottom = AqSpacing.Xs),
                            )
                            if (!isLast) AqHairline()
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
        AqModal(
            show = true,
            title = when (suggestion?.kind) {
                ReceiptMatchKind.SINGLE -> AppStrings.singleMatchTitle
                ReceiptMatchKind.MULTIPLE -> AppStrings.ACualCorresponde
                ReceiptMatchKind.NONE, null -> AppStrings.noMatchTitle
            },
            dismissLabel = AppStrings.Cancelar,
            onDismissRequest = { selectedReceipt = null },
            content = {
                // Con muchas operaciones candidatas la lista queda
                // alcanzable por scroll (accesibilidad).
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
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
        AqModal(
            show = true,
            title = AppStrings.EliminarComprobante,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                selectedReceiptIds.toList().forEach { id ->
                    viewModel.onAction(OperationAction.DeleteComprobante(id))
                }
                selectedReceiptIds = emptySet()
                confirmarEliminarRecibo = false
            },
            destructive = true,
            onDismissRequest = { confirmarEliminarRecibo = false },
            content = {
                AqText(
                    text = AppStrings.irreversibleActionWarning + "\n" + selectedReceiptIds.size + " " + plural,
                    color = AqColor.TextSecondary,
                )
            },
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
        AqMenuItem(
            title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
            subtitle = operation.amount.orEmpty().ifBlank { AppStrings.SinMontoCorto },
            onClick = { onPick(operation.id) },
            showDivider = index < operations.lastIndex,
        )
    }
}
