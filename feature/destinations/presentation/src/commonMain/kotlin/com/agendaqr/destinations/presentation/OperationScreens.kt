package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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

@Composable
fun OperationsScreen(state: OperationsUiState, viewModel: OperationsViewModel, onBack: () -> Unit) {
    when (state.route) {
        OperationRoute.List -> OperationListScreen(state, viewModel, onBack)
        OperationRoute.Unassociated -> UnassociatedScreen(state, viewModel)
        OperationRoute.New -> OperationEditorScreen(state, viewModel, existing = null)
        is OperationRoute.Edit -> OperationEditorScreen(state, viewModel, existing = viewModel.selectedOperation())
        is OperationRoute.Detail -> OperationDetailScreen(state, viewModel)
    }
    state.pendingIncoming?.let { incoming ->
        val duplicate = state.pendingDuplicates.isNotEmpty()
        val saving = state.isSavingReceipt
        XauxaDialog(
            title = if (duplicate) "Comprobante duplicado" else "Comprobante recibido",
            message = if (duplicate) {
                "Ya existe un comprobante igual. Puedes verlo o guardar otra copia.\nArchivo: " + incoming.extension
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
        ComprobanteViewerDialog(state, receipt, viewModel)
    }
}

@Composable
private fun OperationListScreen(state: OperationsUiState, viewModel: OperationsViewModel, onBack: () -> Unit = {}) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    val paged = operations.take(visibleCount)
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Operaciones", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaTextAction(label = "Destinos", onClick = onBack)
                XauxaPrimaryButton(label = "Nuevo", onClick = { viewModel.onAction(OperationAction.New) })
            }
        }
        XauxaSearchBar(value = state.query, onValueChange = { viewModel.onAction(OperationAction.Search(it)) }, label = "Buscar", placeholder = "Buscar operaciones", onClear = { viewModel.onAction(OperationAction.Search("")) })
        if (state.unassociated.isNotEmpty()) {
            XauxaStatusBanner("Comprobantes sin asociar: " + state.unassociated.size)
            XauxaSecondaryButton(label = "Ver bandeja de respaldos", onClick = { viewModel.openUnassociatedForOperation(operation.id) })
        }
        state.error?.let { XauxaStatusBanner(it, tone = XauxaTone.Danger, onDismiss = { viewModel.onAction(OperationAction.ClearError) }) }
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
                                Text(formatDate(operation.occurredAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                                Text(operationTypeLabel(operation.type), fontWeight = FontWeight.SemiBold,
                                    color = if (operation.type == OperationType.COBRO) XauxaColor.Success else XauxaColor.Brand)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(operation.amount.orEmpty().ifBlank { "—" }, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
                                operation.personOrEntity?.let { Text(it, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary) }
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
private fun UnassociatedScreen(state: OperationsUiState, viewModel: OperationsViewModel) {
    var selectedReceipt by remember { mutableStateOf<Comprobante?>(null) }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Comprobantes sin asociar", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        if (state.unassociated.isEmpty()) {
            XauxaEmptyState(title = "No hay comprobantes sin asociar", actionLabel = "Volver", onAction = { viewModel.onAction(OperationAction.Back) })
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.unassociated, key = { it.id }) { receipt ->
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) }) {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                            Text("Comprobante recibido", fontWeight = FontWeight.SemiBold)
                            Text(formatDate(receipt.createdAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                            // Acciones apiladas: dos etiquetas largas no caben lado a lado en 360dp.
                            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                XauxaPrimaryButton(label = "Asociar a operación existente", onClick = { selectedReceipt = receipt })
                                XauxaSecondaryButton(label = "Crear nueva operación con esto", onClick = {
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
            confirmLabel = "Cancelar",
            onConfirm = { selectedReceipt = null },
            onDismiss = { selectedReceipt = null },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    when {
                        suggestion == null -> Text("Estamos analizando el comprobante.", color = XauxaColor.TextSecondary)
                        suggestion.kind == ReceiptMatchKind.NONE ->
                            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                Text(
                                    "No encontramos una coincidencia automática. Puedes elegir manualmente una operación.",
                                    color = XauxaColor.TextSecondary,
                                )
                                if (candidateOperations.isEmpty()) {
                                    Text("No hay operaciones disponibles.", color = XauxaColor.TextSecondary)
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
                            Text(
                                "Las operaciones candidatas ya no están disponibles.",
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
    var selectedContextId by rememberSaveable(editorKey) { mutableStateOf(existing?.contextId) }
    var showContextPicker by remember { mutableStateOf(false) }
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
        contextId = selectedContextId,
    )

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl)
            .imePadding().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (existing == null) "Registrar operación" else "Editar operación",
                modifier = Modifier.semantics { heading() },
                fontSize = XauxaType.Display,
                fontWeight = FontWeight.Bold,
                color = XauxaColor.TextPrimary,
            )
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            if (type == OperationType.PAGO) XauxaPrimaryButton(label = "Pago", onClick = { type = OperationType.PAGO })
            else XauxaSecondaryButton(label = "Pago", onClick = { type = OperationType.PAGO })
            if (type == OperationType.COBRO) XauxaPrimaryButton(label = "Cobro", onClick = { type = OperationType.COBRO })
            else XauxaSecondaryButton(label = "Cobro", onClick = { type = OperationType.COBRO })
        }
        XauxaTextInput(label = "Monto (opcional)", value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(
            label = "Fecha",
            value = dateText,
            onValueChange = { dateText = it },
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            isError = dateError,
            errorMessage = if (dateError) "Usa el formato dd/mm/aaaa" else null,
        )
        XauxaTextInput(label = "Moneda (opcional)", value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Persona o entidad (opcional)", value = person, onValueChange = { person = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Destino QR (opcional)", value = destination, onValueChange = { destination = it }, modifier = Modifier.fillMaxWidth())
        XauxaSecondaryButton(
            label = selectedContextId?.let { id -> "Para: " + (state.contexts.firstOrNull { it.id == id }?.name ?: "Contexto") }
                ?: "Para: elegir contexto (opcional)",
            onClick = { showContextPicker = true },
        )
        XauxaTextInput(label = "Concepto (opcional)", value = concept, onValueChange = { concept = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Nota (opcional)", value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth())
        if (existing == null) {
            Text("Podrás adjuntar comprobantes más adelante", color = XauxaColor.TextSecondary, fontSize = XauxaType.Label)
        }
        state.error?.let { XauxaStatusBanner(it, tone = XauxaTone.Danger, onDismiss = { viewModel.onAction(OperationAction.ClearError) }) }
        XauxaPrimaryButton(
            label = if (existing == null) "Guardar" else "Guardar cambios",
            onClick = {
                submitted = true
                val at = occurredAt ?: return@XauxaPrimaryButton
                val candidate = buildCandidate(at)
                if (existing == null) {
                    viewModel.onAction(
                        OperationAction.SaveNew(
                            type, at, amount, currency, person, destination, concept, note, selectedContextId, candidate.id,
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

    if (showContextPicker) {
        XauxaDialog(
            title = "Seleccionar contexto",
            confirmLabel = "Cerrar",
            onConfirm = { showContextPicker = false },
            dismissLabel = "Sin contexto",
            onDismiss = { selectedContextId = null; showContextPicker = false },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    state.contexts.forEach { context ->
                        XauxaListRow(
                            title = context.name,
                            onClick = {
                                selectedContextId = context.id
                                showContextPicker = false
                            },
                        )
                    }
                }
            },
        )
    }

    if (showSensitiveConfirm) {
        XauxaDialog(
            title = "Cambiar operación con comprobantes",
            message = "Esta operación tiene comprobantes. El comprobante no será modificado.",
            confirmLabel = "Guardar cambio",
            onConfirm = {
                showSensitiveConfirm = false
                occurredAt?.let { viewModel.onAction(OperationAction.Update(buildCandidate(it), confirmedSensitiveChange = true)) }
            },
            dismissLabel = "Cancelar",
            onDismiss = { showSensitiveConfirm = false },
        )
    }
}

@Composable
private fun OperationDetailScreen(state: OperationsUiState, viewModel: OperationsViewModel) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.Center) {
            XauxaEmptyState(
                title = "Operación no encontrada",
                subtitle = "Pudo haber sido eliminada.",
                actionLabel = "Volver",
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Detalle", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        XauxaStatusBanner(
            operationTypeLabel(operation.type) + " · " + formatDate(operation.occurredAt) + " · " +
                operation.amount.orEmpty().ifBlank { "sin monto" }
        )
        operation.personOrEntity?.let { Text("Persona o entidad: " + it) }
        operation.currency?.let { Text("Moneda: " + it) }
        operation.destinationId?.let { Text("Destino: " + it) }
        operation.concept?.let { Text("Concepto: " + it) }
        operation.note?.let { Text("Nota: " + it, color = XauxaColor.TextSecondary) }

        XauxaSection("Comprobantes") {
            if (state.operationComprobantes.isEmpty()) {
                Text("Sin comprobante adjunto", color = XauxaColor.TextSecondary)
                XauxaTextAction(label = "Adjuntar comprobante ahora", onClick = { viewModel.onAction(OperationAction.OpenUnassociated) })
            } else {
                for (receipt in state.operationComprobantes) {
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) }) {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg)) {
                            Text("Comprobante · " + receiptProvenanceLabel(receipt.provenance), fontWeight = FontWeight.SemiBold)
                            Text(formatDate(receipt.createdAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                            XauxaTextAction(
                                label = "Desasociar",
                                onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                            )
                        }
                    }
                }
            }
        }
        XauxaSecondaryButton(label = "Compartir", onClick = { shareOperation(operation) })
        XauxaSecondaryButton(label = "Editar", onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) })
        XauxaTextAction(label = "Eliminar operación", onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = "Eliminar operación",
            message = "Esta acción no se puede deshacer.",
            confirmLabel = "Eliminar",
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            dismissLabel = "Cancelar",
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

internal fun receiptProvenanceLabel(provenance: ReceiptProvenance?): String = when (provenance) {\n    ReceiptProvenance.ENVIADO -> "Enviado"\n    ReceiptProvenance.RECIBIDO -> "Recibido"\n    ReceiptProvenance.DESCONOCIDO, null -> "Origen desconocido"\n}\n\ninternal fun formatDate(millis: Long): String {
    val z = millis / 86_400_000L + 719468
    val era = if (z >= 0) z / 146097 else (z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val day = doy - (153 * mp + 2) / 5 + 1
    val month = mp + if (mp < 10) 3 else -9
    val year = y + if (month <= 2) 1 else 0
    fun two(value: Long) = if (value < 10) "0" + value else value.toString()
    return two(day) + "/" + two(month) + "/" + year
}

/** Inversa estricta de [formatDate]: solo acepta dd/mm/aaaa reales. */
internal fun parseDate(text: String): Long? {
    val parts = text.trim().split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    if (year < 1900 || year > 2100 || month !in 1..12 || day !in 1..31) return null
    var y = year
    if (month <= 2) y -= 1
    val era = if (y >= 0) y / 400 else (y - 399) / 400
    val yoe = y - era * 400
    val mp = if (month > 2) month - 3 else month + 9
    val doy = (153 * mp + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    val millis = (era * 146097 + doe - 719468) * 86_400_000L
    // Round-trip: rechaza 31/02, 30/02, 31/04 y demás combinaciones inexistentes.
    fun two(value: Int) = if (value < 10) "0" + value else value.toString()
    if (formatDate(millis) != two(day) + "/" + two(month) + "/" + year) return null
    return millis
}


private fun operationTypeLabel(type: OperationType): String = when (type) {
    OperationType.PAGO -> "Pago"
    OperationType.COBRO -> "Cobro"
}

@Composable
private fun ComprobanteViewerDialog(state: OperationsUiState, receipt: Comprobante, viewModel: OperationsViewModel) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = "Eliminar comprobante",
            message = "La operación asociada se conservará.",
            confirmLabel = "Eliminar",
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.DeleteComprobante(receipt.id))
            },
            dismissLabel = "Cancelar",
            onDismiss = { showDeleteConfirm = false },
        )
        return
    }
    val bytes = state.openedComprobanteBytes
    XauxaDialog(
        title = "Comprobante",
        confirmLabel = "Cerrar",
        onConfirm = { viewModel.onAction(OperationAction.CloseComprobante) },
        dismissLabel = "Eliminar",
        onDismiss = { showDeleteConfirm = true },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                Text(formatDate(receipt.createdAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                receipt.provenance?.let { Text("Origen: " + it.name, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary) }
                when {
                    state.isLoadingComprobante -> XauxaLoading(message = "Abriendo comprobante…")
                    bytes != null -> {
                        ComprobantePreview(bytes, receipt.mimeType)
                        XauxaSecondaryButton(
                            label = "Compartir",
                            onClick = { shareComprobante(bytes, receipt.extension ?: "bin", receipt.mimeType) },
                        )
                    }
                    else -> Text("No se pudo abrir el comprobante", fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                }
            }
        },
    )
}
