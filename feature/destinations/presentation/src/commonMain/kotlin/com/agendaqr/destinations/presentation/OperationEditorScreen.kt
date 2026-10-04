/**
 * S07 — Registrar/Editar operación: draft con rememberSaveable, selector
 * de contexto (S08, semántica T4) y confirmación de cambio sensible.
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
import androidx.compose.ui.text.font.FontWeight
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

@Composable
internal fun OperationEditorScreen(
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

