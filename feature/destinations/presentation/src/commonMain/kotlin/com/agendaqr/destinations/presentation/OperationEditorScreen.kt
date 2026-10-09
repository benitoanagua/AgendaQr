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

/**
 * Selector de tipo de actividad (S07): dos chips con estado seleccionado
 * visible (no dos primarios compitiendo, §1/§11).
 */
@Composable
internal fun OperationTypeSelector(
    type: OperationType,
    onSelect: (OperationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        XauxaFilterChip(
            label = AppStrings.Pago,
            selected = type == OperationType.PAGO,
            onClick = { onSelect(OperationType.PAGO) },
        )
        XauxaFilterChip(
            label = AppStrings.Cobro,
            selected = type == OperationType.COBRO,
            onClick = { onSelect(OperationType.COBRO) },
        )
    }
}

@Composable
internal fun OperationEditorScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    existing: Operation?,
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
) {
    val editorKey = existing?.id ?: "new"
    var type by rememberSaveable(editorKey) { mutableStateOf(existing?.type ?: OperationType.PAGO) }
    var amount by rememberSaveable(editorKey) { mutableStateOf(existing?.amount.orEmpty()) }
    var currency by rememberSaveable(editorKey) { mutableStateOf(existing?.currency.orEmpty()) }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    var dateText by rememberSaveable(editorKey) { mutableStateOf(formatDate(existing?.occurredAt ?: nowMillis())) }
    // S08: selección de contexto con semántica explícita — Cancelar no toca
    // la selección; Quitar contexto es la única vía de limpiarla.
    var contextSelection by rememberSaveable(editorKey, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing?.contextId))
    }
    // U3/ADR-0003: crear contexto desde S08 lo deja seleccionado.
    androidx.compose.runtime.LaunchedEffect(justCreatedContextId) {
        justCreatedContextId?.let { id -> contextSelection = contextSelection.select(id) }
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
        destinationId = existing?.destinationId,
        concept = concept.trim().takeIf(String::isNotBlank),
        note = note.trim().takeIf(String::isNotBlank),
        contextId = contextSelection.contextId,
    )

    // V1.1 (M7/T7): Guardar vive como acción principal de la app bar
    // inferior (nunca recortada al final del scroll) y Volver pasa a la
    // flecha de la barra (sin desbordar a dos líneas).
    Column(
        Modifier.fillMaxSize().statusBarsPadding().imePadding(),
    ) {
        Column(
            Modifier.fillMaxWidth().weight(1f).padding(horizontal = XauxaSpacing.ScreenMargin)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
        XauxaPageTitle(
            text = if (existing == null) AppStrings.RegistrarOperacion else AppStrings.EditarOperacion,
        )
        // Tipo con semántica de selección (no dos botones primarios
        // compitiendo): Guardar queda como única acción principal (§1).
        OperationTypeSelector(
            type = type,
            onSelect = { type = it },
        )
        XauxaTextInput(label = AppStrings.MontoOpcional, value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(
            label = AppStrings.Fecha,
            value = dateText,
            onValueChange = { dateText = it },
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            isError = dateError,
            errorMessage = if (dateError) AppStrings.FormatoFecha else null,
        )
        XauxaTextInput(label = AppStrings.MonedaOpcional, value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
        XauxaTextInput(label = AppStrings.PersonaOEntidadOpcional, value = person, onValueChange = { person = it }, modifier = Modifier.fillMaxWidth())
        XauxaSecondaryButton(
            label = contextSelection.contextId?.let { id -> AppStrings.ParaContextoElegido + (state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico) }
                ?: AppStrings.ParaElegirContexto,
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
                onAction = {
                    viewModel.onAction(
                        if (err.action == ErrorAction.Retry) OperationAction.RetryFailed
                        else OperationAction.ClearError,
                    )
                },
                onDismiss = { viewModel.onAction(OperationAction.ClearError) },
            )
        }
        }

        XauxaAppBar(
            modifier = Modifier.navigationBarsPadding(),
            actions = listOf(
                XauxaAppBarAction(
                    label = if (saving) {
                        AppStrings.Guardando
                    } else if (existing == null) {
                        AppStrings.Guardar
                    } else {
                        AppStrings.GuardarCambios
                    },
                    icon = XauxaIcons.Save,
                    primary = true,
                    enabled = !saving,
                    onClick = {
                        submitted = true
                        val at = occurredAt ?: return@XauxaAppBarAction
                        val candidate = buildCandidate(at)
                        if (existing == null) {
                            viewModel.onAction(
                                OperationAction.SaveNew(
                                    type, at, amount, currency, person, null, concept, note, contextSelection.contextId, candidate.id,
                                ),
                            )
                        } else if (candidate.hasSensitiveChangesComparedTo(existing) && state.operationComprobantes.isNotEmpty()) {
                            showSensitiveConfirm = true
                        } else {
                            viewModel.onAction(OperationAction.Update(candidate))
                        }
                    },
                ),
            ),
            onBack = { viewModel.onAction(OperationAction.Back) },
            backLabel = AppStrings.Volver,
        )
    }

    if (contextSelection.pickerOpen) {
        ContextPickerDialog(
            contexts = state.contexts,
            selection = contextSelection,
            onSelect = { id -> contextSelection = contextSelection.select(id) },
            onRemove = { contextSelection = contextSelection.removeContext() },
            onCancel = { contextSelection = contextSelection.cancelPicker() },
            onCreateContext = onCreateContext,
            justCreatedContextId = justCreatedContextId,
        )
    }

    if (showSensitiveConfirm) {
        XauxaDialog(
            title = AppStrings.changeOperationWithReceipts,
            message = AppStrings.sensitiveChangeWarning,
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

