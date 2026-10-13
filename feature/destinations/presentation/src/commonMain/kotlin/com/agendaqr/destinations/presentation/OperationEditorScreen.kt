/**
 * S07 — Registrar/Editar operación: draft con rememberSaveable, selector
 * de contexto (S08, semántica explícita) y confirmación de cambio sensible.
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*

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
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
        AmFilterChip(
            label = AppStrings.Pago,
            selected = type == OperationType.PAGO,
            onClick = { onSelect(OperationType.PAGO) },
        )
        AmFilterChip(
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
    appHosts: AppHosts? = null,
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

    // V1.4 (§12): "Registrar actividad"/"Editar actividad" en la barra
    // superior; Guardar es el botón principal FIJO abajo (acción real de
    // guardado), nunca recortado por el scroll.
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = if (existing == null) AppStrings.RegistrarOperacion else AppStrings.EditarOperacion,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = {
            AmPrimaryButton(
                label = if (saving) {
                    AppStrings.Guardando
                } else if (existing == null) {
                    AppStrings.Guardar
                } else {
                    AppStrings.GuardarCambios
                },
                onClick = {
                    submitted = true
                    val at = occurredAt ?: return@AmPrimaryButton
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
                enabled = !saving,
                isLoading = saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = AmSpacing.ScreenMargin, vertical = AmSpacing.Sm),
            )
        },
    ) {
        // Tipo con semántica de selección (no dos botones primarios
        // compitiendo): Guardar queda como única acción principal (§1).
        OperationTypeSelector(
            type = type,
            onSelect = { type = it },
        )
        AmTextField(label = AppStrings.MontoOpcional, value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        AmTextField(
            label = AppStrings.Fecha,
            value = dateText,
            onValueChange = { dateText = it },
            modifier = Modifier.fillMaxWidth(),
            isError = dateError,
            errorMessage = if (dateError) AppStrings.FormatoFecha else null,
        )
        AmTextField(label = AppStrings.MonedaOpcional, value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
        AmTextField(label = AppStrings.PersonaOEntidadOpcional, value = person, onValueChange = { person = it }, modifier = Modifier.fillMaxWidth())
        AmSecondaryButton(
            label = contextSelection.contextId?.let { id -> AppStrings.ParaContextoElegido + (state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico) }
                ?: AppStrings.ParaElegirContexto,
            onClick = { contextSelection = contextSelection.openPicker() },
        )
        AmTextField(label = AppStrings.ConceptoOpcional, value = concept, onValueChange = { concept = it }, modifier = Modifier.fillMaxWidth())
        AmTextField(label = AppStrings.NotaOpcional, value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth())
        if (existing == null) {
            AmText(AppStrings.PodrasAdjuntarComprobantesMasAdelante, color = AmColor.TextSecondary, size = AmType.Label)
        }
        state.error?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
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
        AmDialog(
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

