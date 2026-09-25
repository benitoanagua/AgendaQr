package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*

@Composable
fun OperationsScreen(state: OperationsUiState, viewModel: OperationsViewModel, onBack: () -> Unit) {
    when (state.route) {
        OperationRoute.List -> OperationListScreen(state, viewModel, onBack)
        OperationRoute.Unassociated -> UnassociatedScreen(state, viewModel)
        OperationRoute.New -> NewOperationScreen(state, viewModel)
        is OperationRoute.Detail -> OperationDetailScreen(state, viewModel)
    }
    state.pendingIncoming?.let { incoming ->
        val duplicate = state.pendingDuplicates.isNotEmpty()
        XauxaDialog(
            onDismissRequest = { viewModel.onAction(OperationAction.ClearIncoming) },
            title = { Text(if (duplicate) "Comprobante duplicado" else "Comprobante recibido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    if (duplicate) {
                        Text("Ya existe un comprobante igual. No se guardará otra copia.")
                    } else {
                        Text("Se guardará en tu bandeja de respaldos sin asociar.")
                    }
                    Text("Archivo: " + incoming.extension, color = XauxaColor.TextSecondary)
                }
            },
            confirmButton = {
                XauxaPrimaryButton(
                    if (duplicate) "CERRAR" else if (state.isSavingReceipt) "Guardando…" else "LISTO / OK",
                    { viewModel.onAction(OperationAction.ClearIncoming.takeIf { duplicate } ?: OperationAction.SaveIncoming(false)) },
                    enabled = !state.isSavingReceipt,
                )
            },
            dismissButton = if (!duplicate) {
                {
                    XauxaTextAction(label = "ASOCIAR AHORA (Opcional)", onClick = {
                        viewModel.onAction(OperationAction.SaveIncoming(true))
                    })
                }
            } else {
                null
            },
        )
    }
}

@Composable
private fun OperationListScreen(state: OperationsUiState, viewModel: OperationsViewModel, onBack: () -> Unit = {}) {
    val operations = viewModel.visibleOperations()
    var visibleCount by remember(operations.size) { mutableStateOf(50) }
    val paged = operations.take(visibleCount)
    Column(Modifier.fillMaxSize().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Operaciones", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaTextAction(label = "Destinos", onClick = onBack)
                XauxaPrimaryButton(label = "Nuevo", onClick = { viewModel.onAction(OperationAction.New) })
            }
        }
        XauxaTextInput(state.query, { viewModel.onAction(OperationAction.Search(it)) }, Modifier.fillMaxWidth(), label = { Text("Buscar") }, singleLine = true)
        if (state.unassociated.isNotEmpty()) {
            XauxaStatusBanner("Comprobantes sin asociar: " + state.unassociated.size)
            XauxaSecondaryButton(label = "Ver bandeja de respaldos", onClick = { viewModel.onAction(OperationAction.OpenUnassociated) })
        }
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        if (operations.isEmpty()) {
            XauxaEmptyState(
                title = if (state.query.isBlank()) "Aún no hay operaciones" else "No hay coincidencias",
                actionLabel = if (state.query.isBlank()) "Registrar operación" else "Limpiar búsqueda",
                onAction = {
                    if (state.query.isBlank()) viewModel.onAction(OperationAction.New)
                    else viewModel.onAction(OperationAction.Search(""))
                }
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(paged, key = { it.id }) { operation ->
                    XauxaTile(onClick = { viewModel.onAction(OperationAction.Open(operation.id)) }) {
                        Row(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                Text(formatDate(operation.occurredAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                                Text(operation.type.name, fontWeight = FontWeight.SemiBold,
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
                        XauxaSecondaryButton(
                            label = "Cargar más (${operations.size - paged.size} restantes)",
                            onClick = { visibleCount = (visibleCount + 50).coerceAtMost(operations.size) },
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
    Column(Modifier.fillMaxSize().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Comprobantes sin asociar", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        if (state.unassociated.isEmpty()) {
            XauxaEmptyState(title = "No hay comprobantes sin asociar", actionLabel = "Volver", onAction = { viewModel.onAction(OperationAction.Back) })
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.unassociated, key = { it.id }) { receipt ->
                    XauxaTile {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                            Text("Comprobante recibido", fontWeight = FontWeight.SemiBold)
                            Text(formatDate(receipt.createdAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                                XauxaPrimaryButton(label = "ASOCIAR A OPERACIÓN EXISTENTE", onClick = { selectedReceipt = receipt })
                                XauxaSecondaryButton(label = "CREAR NUEVA OPERACIÓN CON ESTO", onClick = {
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
        val candidateOperations = suggestion?.operationIds.orEmpty()
            .mapNotNull { id -> state.operations.firstOrNull { it.id == id } }
        XauxaDialog(
            onDismissRequest = { selectedReceipt = null },
            title = { Text(
                when (suggestion?.kind) {
                    ReceiptMatchKind.SINGLE -> "Parece corresponder a"
                    ReceiptMatchKind.MULTIPLE -> "¿A cuál corresponde?"
                    ReceiptMatchKind.NONE, null -> "No encontramos coincidencia"
                }
            ) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    when {
                        suggestion == null -> Text("Estamos analizando el comprobante.")
                        suggestion.kind == ReceiptMatchKind.NONE ->
                            Text("No encontramos una operación con señales suficientes para asociarlo automáticamente. Puedes guardarlo sin asociar.")
                        candidateOperations.isEmpty() ->
                            Text("Las operaciones candidatas ya no están disponibles. Puedes volver atrás y revisar el comprobante.")
                        else -> candidateOperations.forEach { operation ->
                            XauxaTile(onClick = {
                                viewModel.onAction(OperationAction.Associate(receipt.id, operation.id))
                                selectedReceipt = null
                            }) {
                                Row(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(operation.type.name + " · " + formatDate(operation.occurredAt))
                                    Text(operation.amount.orEmpty().ifBlank { "—" })
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { XauxaTextAction(label = "CANCELAR", onClick = { selectedReceipt = null }) },
        )
    }
}

@Composable
private fun NewOperationScreen(state: OperationsUiState, viewModel: OperationsViewModel) {
    var type by remember { mutableStateOf(OperationType.PAGO) }
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var person by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedContextId by remember { mutableStateOf<String?>(null) }
    val draftOperationId = remember { newEntityId("operation") }
    var showContextPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Registrar operación", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            if (type == OperationType.PAGO) XauxaPrimaryButton(label = "PAGO", onClick = { type = OperationType.PAGO })
            else XauxaSecondaryButton(label = "PAGO", onClick = { type = OperationType.PAGO })
            if (type == OperationType.COBRO) XauxaPrimaryButton(label = "COBRO", onClick = { type = OperationType.COBRO })
            else XauxaSecondaryButton(label = "COBRO", onClick = { type = OperationType.COBRO })
        }
        XauxaTextInput(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text("Monto (opcional)") }, singleLine = true)
        XauxaTextInput(currency, { currency = it }, Modifier.fillMaxWidth(), label = { Text("Moneda (opcional)") }, singleLine = true)
        XauxaTextInput(person, { person = it }, Modifier.fillMaxWidth(), label = { Text("Persona o entidad (opcional)") }, singleLine = true)
        XauxaTextInput(destination, { destination = it }, Modifier.fillMaxWidth(), label = { Text("Destino QR (opcional)") }, singleLine = true)
        XauxaSecondaryButton(label = selectedContextId?.let { id -> "Para: " + (state.contexts.firstOrNull { it.id == id }?.name ?: "Contexto") } ?: "Para: elegir contexto (opcional)", onClick = { showContextPicker = true })
        XauxaTextInput(concept, { concept = it }, Modifier.fillMaxWidth(), label = { Text("Concepto (opcional)") }, singleLine = true)
        XauxaTextInput(note, { note = it }, Modifier.fillMaxWidth(), label = { Text("Nota (opcional)") }, singleLine = true)
        Text("Podrás adjuntar comprobantes más adelante", color = XauxaColor.TextSecondary, fontSize = XauxaType.Label)
        XauxaPrimaryButton(label = "Guardar", onClick = {
            viewModel.onAction(OperationAction.SaveNew(type, nowMillis(), amount, currency, person, destination, concept, note, selectedContextId, draftOperationId))
        })
    }
    if (showContextPicker) {
        XauxaDialog(
            onDismissRequest = { showContextPicker = false },
            title = { Text("¿A cuál corresponde?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    XauxaTextAction(label = "Sin contexto", onClick = { selectedContextId = null; showContextPicker = false })
                    state.contexts.forEach { context ->
                        XauxaTile(onClick = { selectedContextId = context.id; showContextPicker = false }) {
                            Text(context.name, modifier = Modifier.padding(XauxaSpacing.Lg))
                        }
                    }
                }
            },
            confirmButton = { XauxaTextAction(label = "CANCELAR", onClick = { showContextPicker = false }) },
        )
    }

}

@Composable
private fun OperationDetailScreen(state: OperationsUiState, viewModel: OperationsViewModel) {
    val operation = viewModel.selectedOperation() ?: return
    Column(Modifier.fillMaxSize().padding(XauxaSpacing.Xxl), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Detalle", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            XauxaTextAction(label = "Volver", onClick = { viewModel.onAction(OperationAction.Back) })
        }
        XauxaStatusBanner(
            operation.type.name + " · " + formatDate(operation.occurredAt) + " · " +
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
                XauxaTextAction(label = "ADJUNTAR COMPROBANTE AHORA", onClick = { viewModel.onAction(OperationAction.OpenUnassociated) })
            } else {
                for (receipt in state.operationComprobantes) {
                    XauxaTile {
                        Column(Modifier.fillMaxWidth().padding(XauxaSpacing.Lg)) {
                            Text("Comprobante " + receipt.provenance?.name.orEmpty(), fontWeight = FontWeight.SemiBold)
                            Text(formatDate(receipt.createdAt), fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                        }
                    }
                }
            }
        }
        XauxaSecondaryButton(label = "Compartir", onClick = { shareOperation(operation) })
        XauxaTextAction(label = "Eliminar operación", onClick = { viewModel.onAction(OperationAction.Delete(operation.id)) })
    }
}

private fun formatDate(millis: Long): String {
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
