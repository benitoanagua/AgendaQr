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
            title = if (duplicate) "Comprobante duplicado" else "Comprobante recibido",
            message = if (duplicate) {
                "Ya existe un comprobante igual. No se guardará otra copia.\nArchivo: " + incoming.extension
            } else {
                "Se guardará en tu bandeja de respaldos sin asociar.\nArchivo: " + incoming.extension
            },
            confirmLabel = if (duplicate) "CERRAR" else if (state.isSavingReceipt) "GUARDANDO…" else "LISTO / OK",
            onConfirm = {
                viewModel.onAction(
                    if (duplicate) OperationAction.ClearIncoming
                    else OperationAction.SaveIncoming(false),
                )
            },
            dismissLabel = if (duplicate) null else "ASOCIAR AHORA",
            onDismiss = {
                viewModel.onAction(
                    if (duplicate) OperationAction.ClearIncoming
                    else OperationAction.SaveIncoming(true),
                )
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
        XauxaSearchBar(value = state.query, onValueChange = { viewModel.onAction(OperationAction.Search(it)) }, label = "Buscar", placeholder = "Buscar operaciones", onClear = { viewModel.onAction(OperationAction.Search("")) })
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
        val candidateOperations = suggestion?.operationIds.orEmpty()
            .mapNotNull { id -> state.operations.firstOrNull { it.id == id } }
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
                            Text(
                                "No encontramos una operación con señales suficientes para asociarlo automáticamente. Puedes guardarlo sin asociar.",
                                color = XauxaColor.TextSecondary,
                            )
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
            if (type == OperationType.PAGO) XauxaPrimaryButton(label = "Pago", onClick = { type = OperationType.PAGO })
            else XauxaSecondaryButton(label = "Pago", onClick = { type = OperationType.PAGO })
            if (type == OperationType.COBRO) XauxaPrimaryButton(label = "Cobro", onClick = { type = OperationType.COBRO })
            else XauxaSecondaryButton(label = "Cobro", onClick = { type = OperationType.COBRO })
        }
        XauxaTextInput(label = "Monto (opcional)", value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Moneda (opcional)", value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Persona o entidad (opcional)", value = person, onValueChange = { person = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Destino QR (opcional)", value = destination, onValueChange = { destination = it }, modifier = Modifier.fillMaxWidth())
        XauxaSecondaryButton(label = selectedContextId?.let { id -> "Para: " + (state.contexts.firstOrNull { it.id == id }?.name ?: "Contexto") } ?: "Para: elegir contexto (opcional)", onClick = { showContextPicker = true })
        XauxaTextInput(label = "Concepto (opcional)", value = concept, onValueChange = { concept = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = "Nota (opcional)", value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth())
        Text("Podrás adjuntar comprobantes más adelante", color = XauxaColor.TextSecondary, fontSize = XauxaType.Label)
        XauxaPrimaryButton(label = "Guardar", onClick = {
            viewModel.onAction(OperationAction.SaveNew(type, nowMillis(), amount, currency, person, destination, concept, note, selectedContextId, draftOperationId))
        })
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


private fun operationTypeLabel(type: OperationType): String = when (type) {
    OperationType.PAGO -> "Pago"
    OperationType.COBRO -> "Cobro"
}
