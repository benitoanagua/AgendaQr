/**
 * S07 — Lista de operaciones, bandeja de comprobantes sin asociar y el
 * dispatcher de la superficie (diálogo de recibido y visor).
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

import androidx.compose.foundation.layout.*
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*

@Composable
fun OperationsScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    onBack: () -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
) {
    when (state.route) {
        OperationRoute.List -> OperationListScreen(state, viewModel, onBack, syncLookup)
        OperationRoute.Unassociated -> UnassociatedScreen(state, viewModel, syncLookup)
        OperationRoute.New -> OperationEditorScreen(state, viewModel, existing = null)
        is OperationRoute.Edit -> OperationEditorScreen(state, viewModel, existing = viewModel.selectedOperation())
        is OperationRoute.Detail -> OperationDetailScreen(state, viewModel, syncLookup, onRetrySync)
    }
    state.pendingIncoming?.let { incoming ->
        val duplicate = state.pendingDuplicates.isNotEmpty()
        val saving = state.isSavingReceipt
        XauxaDialog(
            title = if (duplicate) "Comprobante duplicado" else "Comprobante recibido",
            message = if (duplicate) {
                "Parece que este comprobante ya está guardado.\nArchivo: " + incoming.extension
            } else {
                "Se guardará en tu bandeja de respaldos sin asociar.\nArchivo: " + incoming.extension
            },
            confirmLabel = if (duplicate) "Guardar de todos modos" else if (saving) "Guardando…" else "Guardar",
            onConfirm = {
                if (!saving) {
                    viewModel.onAction(
                        if (duplicate) OperationAction.SaveIncoming(
                            openInbox = false,
                            allowDuplicate = duplicate,
                        )
                        else OperationAction.SaveIncoming(false),
                    )
                }
            },
            dismissLabel = if (duplicate) "Ver existente" else "Asociar ahora",
            onDismiss = {
                if (duplicate) {
                    state.pendingDuplicates.firstOrNull()?.id?.let { id ->
                        viewModel.onAction(OperationAction.OpenComprobante(id))
                    }
                    viewModel.onAction(OperationAction.ClearIncoming)
                } else {
                    viewModel.onAction(OperationAction.SaveIncoming(true))
                }
            },
        )
    }
    state.openedComprobante?.let { receipt ->
        ComprobanteViewerDialog(state, receipt, viewModel, syncLookup)
    }
}

@Composable
internal fun OperationListScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    onBack: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    val paged = operations.take(visibleCount)
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            XauxaHeading(text = AppStrings.Operaciones, size = XauxaType.Display, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaTextAction(label = AppStrings.Destinos, onClick = onBack)
                XauxaPrimaryButton(label = AppStrings.Nuevo, onClick = { viewModel.onAction(OperationAction.New) })
            }
        }
        XauxaSearchBar(value = state.query, onValueChange = { viewModel.onAction(OperationAction.Search(it)) }, label = AppStrings.Buscar, placeholder = AppStrings.BuscarOperaciones, onClear = { viewModel.onAction(OperationAction.Search("")) })
        if (state.unassociated.isNotEmpty()) {
            XauxaStatusBanner(AppStrings.ComprobantesSinAsociar2 + state.unassociated.size)
            XauxaSecondaryButton(label = AppStrings.VerBandejaDeRespaldos, onClick = { viewModel.onAction(OperationAction.OpenUnassociated) })
        }
        state.error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = { viewModel.onAction(OperationAction.ClearError) },
                onDismiss = { viewModel.onAction(OperationAction.ClearError) },
            )
        }
        if (operations.isEmpty()) {
            XauxaEmptyState(
                title = if (state.query.isBlank()) "Aún no hay operaciones" else "No hay coincidencias",
                subtitle = if (state.query.isBlank()) "Registra tu primer pago o cobro." else "Prueba con otra búsqueda.",
                actionLabel = if (state.query.isBlank()) "Registrar operación" else "Limpiar búsqueda",
                onAction = {
                    if (state.query.isBlank()) viewModel.onAction(OperationAction.New)
                    else viewModel.onAction(OperationAction.Search(""))
                }
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(paged, key = { it.id }) { operation ->
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.Open(operation.id)) }) {
                        Row(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                XauxaText(formatDate(operation.occurredAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                                XauxaText(operationTypeLabel(operation.type), fontWeight = FontWeight.SemiBold,
                                    color = if (operation.type == OperationType.COBRO) XauxaColor.Success else XauxaColor.Brand)
                                // T6: estado de sincronización por elemento, con texto.
                                ElementSyncBadge(
                                    syncLookup.status(SyncResource.OPERATION, operation.id),
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                XauxaText(operation.amount.orEmpty().ifBlank { "—" }, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
                                operation.personOrEntity?.let { XauxaText(it, size = XauxaType.Label, color = XauxaColor.TextSecondary) }
                            }
                        }
                    }
                }
                if (paged.size < operations.size) {
                    item {
                        XauxaLoadMoreFooter(
                            onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(operations.size) },
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
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var selectedReceipt by remember { mutableStateOf<Comprobante?>(null) }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            XauxaHeading(text = AppStrings.ComprobantesSinAsociar, size = XauxaType.Display, fontWeight = FontWeight.Bold)
            XauxaTextAction(label = AppStrings.Volver, onClick = { viewModel.onAction(OperationAction.Back) })
        }
        if (state.unassociated.isEmpty()) {
            XauxaEmptyState(title = AppStrings.NoHayComprobantesSinAsociar, actionLabel = AppStrings.Volver, onAction = { viewModel.onAction(OperationAction.Back) })
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.unassociated, key = { it.id }) { receipt ->
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) }) {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                            XauxaText(AppStrings.ComprobanteRecibido, fontWeight = FontWeight.SemiBold)
                            XauxaText(formatDate(receipt.createdAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                            // T6: estado de sincronización por comprobante, con texto.
                            ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                            // Acciones apiladas: dos etiquetas largas no caben lado a lado en 360dp.
                            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                XauxaPrimaryButton(label = AppStrings.AsociarAOperacionExistente, onClick = { selectedReceipt = receipt })
                                XauxaSecondaryButton(label = AppStrings.CrearNuevaOperacionConEsto, onClick = {
                                    viewModel.onAction(OperationAction.CreateOperationFromReceipt(receipt.id))
                                })
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
        XauxaDialog(
            title = when (suggestion?.kind) {
                ReceiptMatchKind.SINGLE -> "Parece corresponder a"
                ReceiptMatchKind.MULTIPLE -> "¿A cuál corresponde?"
                ReceiptMatchKind.NONE, null -> "No encontramos coincidencia"
            },
            confirmLabel = AppStrings.Cancelar,
            onConfirm = { selectedReceipt = null },
            onDismiss = { selectedReceipt = null },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    when {
                        suggestion == null -> XauxaText(AppStrings.EstamosAnalizandoElComprobante, color = XauxaColor.TextSecondary)
                        suggestion.kind == ReceiptMatchKind.NONE ->
                            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                XauxaText(
                                    AppStrings.NoEncontramosUnaCoincidenciaAutomatica,
                                    color = XauxaColor.TextSecondary,
                                )
                                if (candidateOperations.isEmpty()) {
                                    XauxaText(AppStrings.NoHayOperacionesDisponibles, color = XauxaColor.TextSecondary)
                                } else {
                                    candidateOperations.forEach { operation ->
                                        XauxaListRow(
                                            title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
                                            subtitle = operation.amount.orEmpty().ifBlank { "—" },
                                            onClick = {
                                                viewModel.onAction(OperationAction.Associate(receipt.id, operation.id))
                                                selectedReceipt = null
                                            },
                                        )
                                    }
                                }
                            }
                        candidateOperations.isEmpty() ->
                            XauxaText(
                                AppStrings.LasOperacionesCandidatasYaNo,
                                color = XauxaColor.TextSecondary,
                            )
                        else -> candidateOperations.forEach { operation ->
                            XauxaListRow(
                                title = operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt),
                                subtitle = operation.amount.orEmpty().ifBlank { "—" },
                                onClick = {
                                    viewModel.onAction(OperationAction.Associate(receipt.id, operation.id))
                                    selectedReceipt = null
                                },
                            )
                        }
                    }
                }
            },
        )
    }
}

