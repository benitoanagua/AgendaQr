package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
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
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*
import com.agendaqr.destinations.data.SyncResource
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

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
private fun OperationListScreen(
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
private fun UnassociatedScreen(
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

@Composable
private fun OperationEditorScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    existing: Operation?,
) {
    val editorKey = existing?.id ?: "new"
    var type by rememberSaveable(editorKey) { mutableStateOf(existing?.type ?: OperationType.PAGO) }
    var amount by rememberSaveable(editorKey) { mutableStateOf(existing?.amount.orEmpty()) }
    var currency by rememberSaveable(editorKey) { mutableStateOf(existing?.currency.orEmpty()) }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var destination by rememberSaveable(editorKey) { mutableStateOf(existing?.destinationId.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    var dateText by rememberSaveable(editorKey) { mutableStateOf(formatDate(existing?.occurredAt ?: nowMillis())) }
    // S08: selección de contexto con semántica explícita — Cancelar no toca
    // la selección; Quitar contexto es la única vía de limpiarla.
    var contextSelection by rememberSaveable(editorKey, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing?.contextId))
    }
    var showSensitiveConfirm by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    val saving = state.isSavingOperation
    val occurredAt = parseDate(dateText)
    val dateError = submitted && occurredAt == null

    fun buildCandidate(at: Long): Operation = Operation(
        id = existing?.id ?: newEntityId("operation"),
        type = type,
        occurredAt = at,
        createdAt = existing?.createdAt ?: nowMillis(),
        updatedAt = existing?.updatedAt ?: nowMillis(),
        amount = amount.trim().takeIf(String::isNotBlank),
        currency = currency.trim().takeIf(String::isNotBlank),
        personOrEntity = person.trim().takeIf(String::isNotBlank),
        destinationId = destination.trim().takeIf(String::isNotBlank),
        concept = concept.trim().takeIf(String::isNotBlank),
        note = note.trim().takeIf(String::isNotBlank),
        contextId = contextSelection.contextId,
    )

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl)
            .imePadding().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            XauxaHeading(
                text = if (existing == null) "Registrar operación" else "Editar operación",
                size = XauxaType.Display,
                fontWeight = FontWeight.Bold,
            )
            XauxaTextAction(label = AppStrings.Volver, onClick = { viewModel.onAction(OperationAction.Back) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            if (type == OperationType.PAGO) XauxaPrimaryButton(label = AppStrings.Pago, onClick = { type = OperationType.PAGO })
            else XauxaSecondaryButton(label = AppStrings.Pago, onClick = { type = OperationType.PAGO })
            if (type == OperationType.COBRO) XauxaPrimaryButton(label = AppStrings.Cobro, onClick = { type = OperationType.COBRO })
            else XauxaSecondaryButton(label = AppStrings.Cobro, onClick = { type = OperationType.COBRO })
        }
        XauxaTextInput(label = AppStrings.MontoOpcional, value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(
            label = AppStrings.Fecha,
            value = dateText,
            onValueChange = { dateText = it },
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            isError = dateError,
            errorMessage = if (dateError) "Usa el formato dd/mm/aaaa" else null,
        )
        XauxaTextInput(label = AppStrings.MonedaOpcional, value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = AppStrings.PersonaOEntidadOpcional, value = person, onValueChange = { person = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = AppStrings.DestinoQrOpcional, value = destination, onValueChange = { destination = it }, modifier = Modifier.fillMaxWidth())
        XauxaSecondaryButton(
            label = contextSelection.contextId?.let { id -> "Para: " + (state.contexts.firstOrNull { it.id == id }?.name ?: "Contexto") }
                ?: "Para: elegir contexto (opcional)",
            onClick = { contextSelection = contextSelection.openPicker() },
        )
        XauxaTextInput(label = AppStrings.ConceptoOpcional, value = concept, onValueChange = { concept = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = AppStrings.NotaOpcional, value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth())
        if (existing == null) {
            XauxaText(AppStrings.PodrasAdjuntarComprobantesMasAdelante, color = XauxaColor.TextSecondary, size = XauxaType.Label)
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
        XauxaPrimaryButton(
            label = if (existing == null) "Guardar" else "Guardar cambios",
            onClick = {
                submitted = true
                val at = occurredAt ?: return@XauxaPrimaryButton
                val candidate = buildCandidate(at)
                if (existing == null) {
                    viewModel.onAction(
                        OperationAction.SaveNew(
                            type, at, amount, currency, person, destination, concept, note, contextSelection.contextId, candidate.id,
                        ),
                    )
                } else if (candidate.hasSensitiveChangesComparedTo(existing) && state.operationComprobantes.isNotEmpty()) {
                    showSensitiveConfirm = true
                } else {
                    viewModel.onAction(OperationAction.Update(candidate))
                }
            },
            enabled = !saving,
            isLoading = saving,
        )
    }

    if (contextSelection.pickerOpen) {
        ContextPickerDialog(
            contexts = state.contexts,
            selection = contextSelection,
            onSelect = { id -> contextSelection = contextSelection.select(id) },
            onRemove = { contextSelection = contextSelection.removeContext() },
            onCancel = { contextSelection = contextSelection.cancelPicker() },
        )
    }

    if (showSensitiveConfirm) {
        XauxaDialog(
            title = AppStrings.CambiarOperacionConComprobantes,
            message = AppStrings.EstaOperacionTieneComprobantesEl,
            confirmLabel = AppStrings.GuardarCambio,
            onConfirm = {
                showSensitiveConfirm = false
                occurredAt?.let { viewModel.onAction(OperationAction.Update(buildCandidate(it), confirmedSensitiveChange = true)) }
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showSensitiveConfirm = false },
        )
    }
}

@Composable
private fun OperationDetailScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.Center) {
            XauxaEmptyState(
                title = AppStrings.OperacionNoEncontrada,
                subtitle = AppStrings.PudoHaberSidoEliminada,
                actionLabel = AppStrings.Volver,
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            XauxaHeading(text = AppStrings.Detalle, size = XauxaType.Display, fontWeight = FontWeight.Bold)
            XauxaTextAction(label = AppStrings.Volver, onClick = { viewModel.onAction(OperationAction.Back) })
        }
        XauxaStatusBanner(
            operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt) + " · " +
                operation.amount.orEmpty().ifBlank { "sin monto" }
        )
        // T6: estado de sincronización del elemento (texto, no solo color).
        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            XauxaTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }
        operation.personOrEntity?.let { XauxaText(AppStrings.PersonaOEntidad + it) }
        operation.currency?.let { XauxaText(AppStrings.Moneda + it) }
        operation.destinationId?.let { XauxaText(AppStrings.Destino + it) }
        operation.concept?.let { XauxaText(AppStrings.Concepto + it) }
        operation.note?.let { XauxaText(AppStrings.Nota2 + it, color = XauxaColor.TextSecondary) }

        XauxaSection("Comprobantes") {
            if (state.operationComprobantes.isEmpty()) {
                XauxaText(AppStrings.SinComprobanteAdjunto, color = XauxaColor.TextSecondary)
                XauxaTextAction(label = AppStrings.AdjuntarComprobanteAhora, onClick = { viewModel.openUnassociatedForOperation(operation.id) })
            } else {
                for (receipt in state.operationComprobantes) {
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) }) {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg)) {
                            XauxaText(AppStrings.Comprobante2 + receiptProvenanceLabel(receipt.provenance), fontWeight = FontWeight.SemiBold)
                            XauxaText(formatDate(receipt.createdAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                            XauxaTextAction(
                                label = AppStrings.Desasociar,
                                onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                            )
                        }
                    }
                }
            }
        }
        XauxaSecondaryButton(label = AppStrings.Compartir, onClick = { shareOperation(operation) })
        XauxaSecondaryButton(label = AppStrings.Editar, onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) })
        XauxaTextAction(label = AppStrings.EliminarOperacion, onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarOperacion,
            message = AppStrings.EstaAccionNoSePuede,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

internal fun receiptProvenanceLabel(provenance: ReceiptProvenance?): String = when (provenance) {
    ReceiptProvenance.ENVIADO -> "Enviado"
    ReceiptProvenance.RECIBIDO -> "Recibido"
    ReceiptProvenance.DESCONOCIDO, null -> "Origen desconocido"
}

/**
 * Fecha civil dd/mm/aaaa de [millis] en [timeZone] (por defecto, la del
 * dispositivo).
 *
 * Se calcula en la zona local (no UTC): con la zona del público objetivo
 * (p. ej. UTC-4) la medianoche UTC ya pertenece al día siguiente por la
 * tarde, y el editor proponía "mañana" como fecha por defecto.
 */
internal fun formatDate(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
    fun two(value: Int) = if (value < 10) "0" + value else value.toString()
    return two(date.dayOfMonth) + "/" + two(date.monthNumber) + "/" + date.year
}

/** Inversa estricta de [formatDate]: solo acepta dd/mm/aaaa reales. */
internal fun parseDate(text: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long? {
    val parts = text.trim().split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    if (year < 1900 || year > 2100 || month !in 1..12 || day !in 1..31) return null
    // LocalDate rechaza 31/02, 29/02 en año no bisiesto, etc.
    val date = runCatching { LocalDate(year, month, day) }.getOrNull() ?: return null
    // Medianoche local de esa fecha civil: mismo valor que vería el usuario.
    return date.atStartOfDayIn(timeZone).toEpochMilliseconds()
}


internal fun operationTypeLabel(type: OperationType): String = when (type) {
    OperationType.PAGO -> "Pago"
    OperationType.COBRO -> "Cobro"
}

@Composable
private fun ComprobanteViewerDialog(
    state: OperationsUiState,
    receipt: Comprobante,
    viewModel: OperationsViewModel,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarComprobante,
            message = AppStrings.LaOperacionAsociadaSeConservara,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.DeleteComprobante(receipt.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
        return
    }
    val bytes = state.openedComprobanteBytes
    XauxaDialog(
        title = AppStrings.Comprobante,
        confirmLabel = AppStrings.Cerrar,
        onConfirm = { viewModel.onAction(OperationAction.CloseComprobante) },
        dismissLabel = AppStrings.Eliminar,
        onDismiss = { showDeleteConfirm = true },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaText(formatDate(receipt.createdAt), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                XauxaText(AppStrings.Origen + receiptProvenanceLabel(receipt.provenance), size = XauxaType.Label, color = XauxaColor.TextSecondary)
                // T6: estado de sincronización del comprobante, con texto.
                ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                when {
                    state.isLoadingComprobante -> XauxaLoading(message = AppStrings.AbriendoComprobante)
                    bytes != null -> {
                        ComprobantePreview(bytes, receipt.mimeType)
                        XauxaSecondaryButton(
                            label = AppStrings.Compartir,
                            onClick = { shareComprobante(bytes, receipt.extension ?: "bin", receipt.mimeType) },
                        )
                    }
                    else -> XauxaText(AppStrings.NoSePudoAbrirEl, size = XauxaType.Label, color = XauxaColor.TextSecondary)
                }
            }
        },
    )
}
