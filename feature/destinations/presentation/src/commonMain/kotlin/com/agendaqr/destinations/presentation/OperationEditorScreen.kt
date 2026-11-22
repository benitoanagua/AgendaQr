package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAccordion
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAmountInput
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqFieldState
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextArea
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.rememberAqFormFocus
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.*
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun OperationTypeSelector(
    type: OperationType,
    onSelect: (OperationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    AqSegmentedControl(
        options = listOf(
            com.agendaqr.core.ui.components.AqSegmentedOption(
                label = AppStrings.Pago,
                icon = com.agendaqr.core.ui.components.AqIcons.Expense,
            ),
            com.agendaqr.core.ui.components.AqSegmentedOption(
                label = AppStrings.Cobro,
                icon = com.agendaqr.core.ui.components.AqIcons.Income,
                activeTone = com.agendaqr.core.ui.components.AqTone.Success,
            ),
        ),
        selectedIndex = if (type == OperationType.PAGO) 0 else 1,
        onSelect = { onSelect(if (it == 0) OperationType.PAGO else OperationType.COBRO) },
        modifier = modifier,
    )
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
    var amountEdited by rememberSaveable(editorKey) { mutableStateOf(false) }
    val formFocus = rememberAqFormFocus("amount")
    val initialCurrency = existing?.currency.orEmpty()
        .ifBlank { mostRecentCurrency(state.operations) }
    var currency by rememberSaveable(editorKey) { mutableStateOf(initialCurrency) }
    var currencyCustomOpen by rememberSaveable(editorKey) {
        mutableStateOf(initialCurrency.isNotBlank() && initialCurrency != AppStrings.MonedaBob && initialCurrency != AppStrings.MonedaUsd)
    }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    var showDetails by rememberSaveable(editorKey) {
        mutableStateOf(existing?.concept?.isNotBlank() == true || existing?.note?.isNotBlank() == true)
    }
    val initialOccurredAtMillis = rememberSaveable(editorKey) { existing?.occurredAt ?: com.agendaqr.destinations.domain.nowMillis() }
    var occurredAtMillis by rememberSaveable(editorKey, initialOccurredAtMillis) {
        mutableStateOf(initialOccurredAtMillis)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateUtc by remember { mutableStateOf<Long?>(null) }
    var contextSelection by rememberSaveable(editorKey, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing?.contextId))
    }
    androidx.compose.runtime.LaunchedEffect(justCreatedContextId) {
        justCreatedContextId?.let { id -> contextSelection = contextSelection.select(id) }
    }
    var showSensitiveConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val saving = state.isSavingOperation
    val amountInvalid = amountEdited && !isValidAmount(amount)

    val dirty = type != (existing?.type ?: OperationType.PAGO) ||
        amount != existing?.amount.orEmpty() ||
        currency != initialCurrency ||
        person != existing?.personOrEntity.orEmpty() ||
        concept != existing?.concept.orEmpty() ||
        note != existing?.note.orEmpty() ||
        occurredAtMillis != initialOccurredAtMillis ||
        contextSelection.contextId != existing?.contextId
    LaunchedEffect(dirty) {
        viewModel.onAction(OperationAction.EditorDirtyChanged(dirty))
    }

    fun buildCandidate(at: Long): Operation = Operation(
        id = existing?.id ?: com.agendaqr.destinations.domain.newEntityId("operation"),
        type = type,
        occurredAt = at,
        createdAt = existing?.createdAt ?: com.agendaqr.destinations.domain.nowMillis(),
        updatedAt = existing?.updatedAt ?: com.agendaqr.destinations.domain.nowMillis(),
        amount = canonizeAmount(amount).trim().takeIf(String::isNotBlank),
        currency = currency.trim().takeIf(String::isNotBlank),
        personOrEntity = person.trim().takeIf(String::isNotBlank),
        destinationId = existing?.destinationId,
        concept = concept.trim().takeIf(String::isNotBlank),
        note = note.trim().takeIf(String::isNotBlank),
        contextId = contextSelection.contextId,
    )

    val toastHost = appHosts?.toastHost
    AqScreen(
        title = if (existing == null) AppStrings.RegistrarOperacion else AppStrings.EditarOperacion,
        onBack = { viewModel.onAction(OperationAction.Back) },
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AqButton(
                    label = if (saving) AppStrings.Guardando else if (existing == null) AppStrings.Guardar else AppStrings.GuardarCambios,
                    onClick = {
                        if (isValidAmount(amount) && amount.length <= FieldLimits.Importe
                            && person.length <= FieldLimits.PersonaOEntidad
                            && concept.length <= FieldLimits.Concepto
                            && note.length <= FieldLimits.Nota) {
                            amountEdited = false
                            val at = occurredAtMillis
                            val candidate = buildCandidate(at)
                            if (existing == null) {
                                viewModel.onAction(
                                    OperationAction.SaveNew(
                                        type, at, canonizeAmount(amount), currency, person, null, concept, note, contextSelection.contextId, candidate.id,
                                    ),
                                )
                            } else if (candidate.hasSensitiveChangesComparedTo(existing) && state.operationComprobantes.isNotEmpty()) {
                                showSensitiveConfirm = true
                            } else {
                                viewModel.onAction(OperationAction.Update(candidate))
                            }
                        } else {
                            amountEdited = true
                            formFocus.focusFirstError(setOf("amount"))
                        }
                    },
                    enabled = !saving,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                )
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { viewModel.onAction(OperationAction.RetryFailed) },
                onClear = { viewModel.onAction(OperationAction.ClearError) },
            )
            AqAlert(
                tone = AqTone.Danger,
                message = err.dataStatus,
                dismissLabel = bannerDismiss?.label ?: AppStrings.Cerrar,
                onDismiss = bannerDismiss?.onClick ?: { },
            )
        }
        AqAmountInput(
            label = AppStrings.LabelMonto,
            value = amount,
            onValueChange = { amount = it; amountEdited = true },
            currencySymbol = currency,
            isError = amountInvalid,
            errorMessage = if (amountInvalid) AppStrings.ImporteInvalido else null,
        )
        OperationTypeSelector(type = type, onSelect = { type = it })
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqText(
                text = AppStrings.LabelMoneda,
                size = AqType.Label,
                color = AqColor.TextPrimary,
            )
            AqSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.MonedaBob),
                    com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.MonedaUsd),
                    com.agendaqr.core.ui.components.AqSegmentedOption(
                        label = if (currencyCustomOpen && currency.isNotBlank()) currency.trim().uppercase() else AppStrings.MonedaOtra,
                    ),
                ),
                selectedIndex = when {
                    currency == AppStrings.MonedaBob -> 0
                    currency == AppStrings.MonedaUsd -> 1
                    currencyCustomOpen -> 2
                    else -> -1
                },
                onSelect = { index ->
                    when (index) {
                        0 -> { currency = AppStrings.MonedaBob; currencyCustomOpen = false }
                        1 -> { currency = AppStrings.MonedaUsd; currencyCustomOpen = false }
                        2 -> {
                            if (currency == AppStrings.MonedaBob || currency == AppStrings.MonedaUsd) { currency = "" }
                            currencyCustomOpen = true
                        }
                    }
                },
            )
            if (currencyCustomOpen) {
                AqTextInput(
                    label = AppStrings.CodigoMoneda,
                    value = currency,
                    onValueChange = { currency = it },
                    leadingIcon = com.agendaqr.core.ui.components.AqIcons.Wallet,
                )
            }
        }
        AqSectionTitle(text = AppStrings.FechaYHora)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            AqMenuItem(
                title = (relativeDayLabel(occurredAtMillis, com.agendaqr.destinations.domain.nowMillis())?.let { it + " · " } ?: "") + formatDateTime(occurredAtMillis),
                subtitle = AppStrings.Fecha,
                onClick = { showDatePicker = true },
                leading = {
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.Calendar,
                        contentDescription = null,
                        tint = AqColor.TextSecondary,
                    )
                },
            )
            val quickDay = when (relativeDayLabel(occurredAtMillis, com.agendaqr.destinations.domain.nowMillis())) {
                AppStrings.Hoy -> 0
                AppStrings.Ayer -> 1
                else -> -1
            }
            AqSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.Hoy),
                    com.agendaqr.core.ui.components.AqSegmentedOption(AppStrings.Ayer),
                ),
                selectedIndex = quickDay,
                onSelect = { index ->
                    occurredAtMillis = when (index) {
                        0 -> com.agendaqr.destinations.domain.nowMillis()
                        1 -> minusCivilDays(com.agendaqr.destinations.domain.nowMillis(), 1)
                        else -> occurredAtMillis
                    }
                },
            )
        }
        AqSectionTitle(text = AppStrings.DetallesDeLaActividad)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqTextInput(
                label = AppStrings.LabelPersonaOEntidad,
                value = person,
                onValueChange = { person = it },
                maxLength = FieldLimits.PersonaOEntidad,
                leadingIcon = com.agendaqr.core.ui.components.AqIcons.Store,
            )
            val counterparts = remember(state.operations) { recentCounterparts(state.operations) }
            if (counterparts.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    counterparts.forEachIndexed { index, c ->
                        AqChip(
                            text = c,
                            selected = index == counterparts.indexOf(person.trim()).takeIf { it >= 0 } ?: -1,
                            onClick = { person = c },
                        )
                    }
                }
            }
            AqMenuItem(
                title = contextSelection.contextId?.let { id ->
                    AppStrings.ParaContextoElegido + (state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
                } ?: AppStrings.ParaElegirContexto,
                subtitle = AppStrings.PuedesSinContexto,
                onClick = { contextSelection = contextSelection.openPicker() },
                leading = {
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode,
                        contentDescription = null,
                        tint = AqColor.TextSecondary,
                    )
                },
            )
        }
        if (showDetails) {
            AqAccordion(
                title = AppStrings.MasDetalles,
                expanded = showDetails,
                onToggle = { showDetails = !showDetails },
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                    ) {
                        AqTextInput(
                            label = AppStrings.LabelConcepto,
                            value = concept,
                            onValueChange = { concept = it },
                            maxLength = FieldLimits.Concepto,
                            leadingIcon = com.agendaqr.core.ui.components.AqIcons.NoteEdit,
                        )
                        AqTextArea(
                            label = AppStrings.Nota,
                            value = note,
                            onValueChange = { note = it },
                            maxLength = FieldLimits.Nota,
                        )
                    }
                },
            )
        }
        if (existing != null) {
            AqButton(
                label = AppStrings.EliminarOperacion,
                onClick = { showDeleteConfirm = true },
                variant = AqButtonVariant.Danger,
                size = AqButtonSize.Medium,
            )
        }
    }

    if (state.discardPrompt) {
        AqModal(
            title = AppStrings.DescartarCambios,
            dismissLabel = AppStrings.SeguirEditando,
            confirmLabel = AppStrings.Descartar,
            destructive = true,
            onConfirm = { viewModel.onAction(OperationAction.DiscardEditor) },
            onDismissRequest = { viewModel.onAction(OperationAction.DismissDiscardPrompt) },
            content = { },
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

    if (showDatePicker) {
        AqModal(
            title = AppStrings.Fecha,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Elegir,
            onConfirm = {
                pendingDateUtc = null
                showDatePicker = false
                showTimePicker = true
            },
            onDismissRequest = { showDatePicker = false },
            content = {
                AqTextInput(
                    label = AppStrings.Fecha,
                    value = formatDate(occurredAtMillis),
                    onValueChange = {},
                )
            },
        )
    }

    if (showTimePicker) {
        val current = Instant.fromEpochMilliseconds(occurredAtMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        AqModal(
            title = AppStrings.Hora,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Elegir,
            onConfirm = {
                occurredAtMillis = combineDateAndTime(
                    pendingDateUtc ?: utcDayMillis(occurredAtMillis),
                    current.hour,
                    current.minute,
                )
                pendingDateUtc = null
                showTimePicker = false
            },
            onDismissRequest = {
                pendingDateUtc = null
                showTimePicker = false
            },
            content = {
                AqTextInput(
                    label = AppStrings.Hora,
                    value = "${current.hour}:${current.minute}",
                    onValueChange = {},
                )
            },
        )
    }

    if (showDeleteConfirm && existing != null) {
        AqModal(
            title = AppStrings.EliminarOperacion,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.Delete(existing.id))
            },
            onDismissRequest = { showDeleteConfirm = false },
            content = { },
        )
    }

    if (showSensitiveConfirm) {
        AqModal(
            title = AppStrings.changeOperationWithReceipts,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.GuardarCambio,
            onConfirm = {
                showSensitiveConfirm = false
                viewModel.onAction(
                    OperationAction.Update(
                        buildCandidate(occurredAtMillis),
                        confirmedSensitiveChange = true,
                    ),
                )
            },
            onDismissRequest = { showSensitiveConfirm = false },
            content = { },
        )
    }
}

private const val FieldAmount = "amount"
