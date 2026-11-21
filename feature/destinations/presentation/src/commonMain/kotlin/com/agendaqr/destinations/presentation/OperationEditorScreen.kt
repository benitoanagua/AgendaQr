/**
 * S07 — Registrar/Editar operación: draft con rememberSaveable, selector
 * de contexto (S08, semántica explícita) y confirmación de cambio sensible.
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqMultilineTextField
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Selector de tipo de actividad (S07): control
 * segmentado con glifo direccional — Pago activo en acento, Cobro activo
 * en success (la opción activa comunica el significado del segmento).
 * Mutuamente excluyente por semántica de radio.
 */
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
    // Error en vivo tras la primera edición: sin afirmaciones
    // falsas de validación.
    var amountEdited by rememberSaveable(editorKey) { mutableStateOf(false) }
    val formFocus = rememberAqFormFocus(FieldAmount)
    val initialCurrency = existing?.currency.orEmpty()
        .ifBlank { mostRecentCurrency(state.operations) }
    var currency by rememberSaveable(editorKey) { mutableStateOf(initialCurrency) }
    var currencyCustomOpen by rememberSaveable(editorKey) {
        mutableStateOf(initialCurrency.isNotBlank() && initialCurrency != AppStrings.MonedaBob && initialCurrency != AppStrings.MonedaUsd)
    }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    // Divulgación progresiva: concepto y nota plegados salvo
    // que el registro ya los tenga.
    var showDetails by rememberSaveable(editorKey) {
        mutableStateOf(existing?.concept?.isNotBlank() == true || existing?.note?.isNotBlank() == true)
    }
    // Momento con hora: milisegundos de época, sin texto
    // libre. El valor por defecto de una actividad nueva es ahora, con
    // hora; los datos antiguos a medianoche se muestran sin hora y no se
    // reescriben salvo que el usuario elija.
    val initialOccurredAtMillis = rememberSaveable(editorKey) { existing?.occurredAt ?: nowMillis() }
    var occurredAtMillis by rememberSaveable(editorKey, initialOccurredAtMillis) {
        mutableStateOf(initialOccurredAtMillis)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateUtc by remember { mutableStateOf<Long?>(null) }
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
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val saving = state.isSavingOperation
    val amountInvalid = amountEdited && !isValidAmount(amount)

    // El editor reporta si tiene cambios; `back()` con cambios
    // muestra la confirmación de descarte en vez de navegar.
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
        id = existing?.id ?: newEntityId("operation"),
        type = type,
        occurredAt = at,
        createdAt = existing?.createdAt ?: nowMillis(),
        updatedAt = existing?.updatedAt ?: nowMillis(),
        amount = canonizeAmount(amount).trim().takeIf(String::isNotBlank),
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
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = if (existing == null) AppStrings.RegistrarOperacion else AppStrings.EditarOperacion,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        // Grupos funcionales (tipo · datos · contexto · detalle) separados
        // a 16 dp; dentro de cada grupo la distancia es menor.
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSpacing.Lg),
        bottomAction = {
            AqBottomAction(
                label = if (saving) {
                    AppStrings.Guardando
                } else if (existing == null) {
                    AppStrings.Guardar
                } else {
                    AppStrings.GuardarCambios
                },
                onClick = {
                    // Sin importe válido no se guarda; el error en
                    // vivo ya es visible (o aparece ahora).
                    if (!isValidAmount(amount) || amount.length > FieldLimits.Importe) {
                        amountEdited = true
                        // Foco al primer (único) campo con error.
                        formFocus.focusFirstError(setOf(FieldAmount))
                        return@AqBottomAction
                    }
                    // G2: límites de dominio — no se guarda si algún campo
                    // excede su tope (el input ya muestra el error).
                    if (person.length > FieldLimits.PersonaOEntidad
                        || concept.length > FieldLimits.Concepto
                        || note.length > FieldLimits.Nota) {
                        return@AqBottomAction
                    }
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
                },
                enabled = !saving,
                isLoading = saving,
            )
        },
    ) {
        // Error recuperable ARRIBA, visible sin scroll, junto al resto de
        // estados de la pantalla (§10).
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { viewModel.onAction(OperationAction.RetryFailed) },
                onClear = { viewModel.onAction(OperationAction.ClearError) },
            )
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        // 1) HERO — importe grande con su moneda; el tipo (segmentado) y
        //    la moneda lo califican sin competir con Guardar.
        AqAmountField(
            label = AppStrings.LabelMonto,
            optionalLabel = AppStrings.Opcional,
            focusRequester = formFocus.requesterFor(FieldAmount),
            value = amount,
            onValueChange = {
                amount = it
                amountEdited = true
            },
            onClear = { amount = "" },
            clearLabel = AppStrings.BorrarImporte,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
            ),
            // La molécula del campo es dueña del mensaje de error
            // y de su separación (4 dp), no la pantalla.
            state = AqFieldState(
                isError = amountInvalid,
                errorMessage = if (amountInvalid) AppStrings.ImporteInvalido else null,
            ),
        )
        OperationTypeSelector(
            type = type,
            onSelect = { type = it },
        )
        AqFormField(label = AppStrings.LabelMoneda) {
            // Elección única de moneda: control segmentado (patrón de
            // enum de formulario); el chip "Otra…" muestra el código
            // elegido (ARS, EUR…) en la propia etiqueta.
            val otherLabel = if (currencyCustomOpen && currency.isNotBlank()) {
                currency.trim().uppercase()
            } else {
                AppStrings.MonedaOtra
            }
            AqSegmentedControl(
                options = listOf(
                    AqSegmentedOption(AppStrings.MonedaBob),
                    AqSegmentedOption(AppStrings.MonedaUsd),
                    AqSegmentedOption(otherLabel),
                ),
                selectedIndex = when {
                    currency == AppStrings.MonedaBob -> 0
                    currency == AppStrings.MonedaUsd -> 1
                    currencyCustomOpen -> 2
                    else -> -1
                },
                onSelect = { index ->
                    when (index) {
                        0 -> {
                            currency = AppStrings.MonedaBob
                            currencyCustomOpen = false
                        }
                        1 -> {
                            currency = AppStrings.MonedaUsd
                            currencyCustomOpen = false
                        }
                        2 -> {
                            if (currency == AppStrings.MonedaBob || currency == AppStrings.MonedaUsd) {
                                currency = ""
                            }
                            currencyCustomOpen = true
                        }
                    }
                },
            )
            if (currencyCustomOpen) {
                AqTextField(
                    label = AppStrings.CodigoMoneda,
                    value = currency,
                    onValueChange = { currency = it },
                    leadingIcon = com.agendaqr.core.ui.components.AqIcons.Wallet,
                )
            }
        }
        // 2) SECUNDARIOS — fecha con atajos acoplados; persona/entidad y
        //    contexto bajo UN encabezado, cada campo con su label único.
        AqFormSection(AppStrings.FechaYHora) {
            val dateValue = (relativeDayLabel(occurredAtMillis, nowMillis())?.let { it + " · " } ?: "") +
                formatDateTime(occurredAtMillis)
            AqListRow(
                title = dateValue,
                subtitle = AppStrings.Fecha,
                onClick = { showDatePicker = true },
                leading = {
                    AqIconBadge(
                        icon = com.agendaqr.core.ui.components.AqIcons.Calendar,
                        tone = AqTone.Info,
                    )
                },
                showChevron = true,
            )
            // Atajos Hoy/Ayer acoplados al campo: control segmentado de
            // elección única; -1 = fecha distinta (ningún segmento activo).
            val quickDay = when (relativeDayLabel(occurredAtMillis, nowMillis())) {
                AppStrings.Hoy -> 0
                AppStrings.Ayer -> 1
                else -> -1
            }
            AqSegmentedControl(
                options = listOf(
                    AqSegmentedOption(AppStrings.Hoy),
                    AqSegmentedOption(AppStrings.Ayer),
                ),
                selectedIndex = quickDay,
                onSelect = { index ->
                    occurredAtMillis = when (index) {
                        0 -> nowMillis()
                        1 -> minusCivilDays(nowMillis(), 1)
                        else -> occurredAtMillis
                    }
                },
            )
        }
        AqFormSection(AppStrings.DetallesDeLaActividad) {
            AqTextField(
                label = AppStrings.LabelPersonaOEntidad,
                optionalLabel = AppStrings.Opcional,
                value = person,
                onValueChange = { person = it },
                maxLength = FieldLimits.PersonaOEntidad,
                leadingIcon = com.agendaqr.core.ui.components.AqIcons.Store,
            )
            val counterparts = remember(state.operations) { recentCounterparts(state.operations) }
            if (counterparts.isNotEmpty()) {
                // Sugerencias de contrapartes: la activa es la que ya
                // coincide con el campo (selección inequívoca).
                AqChipGroup(
                    options = counterparts.map { AqChipOption(it) },
                    selectedIndex = counterparts.indexOf(person.trim()).takeIf { it >= 0 } ?: -1,
                    onSelect = { index -> person = counterparts[index] },
                    horizontalScroll = true,
                )
            }
            AqListBody {
                AqListRow(
                    title = contextSelection.contextId?.let { id ->
                        AppStrings.ParaContextoElegido +
                            (state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
                    } ?: AppStrings.ParaElegirContexto,
                    subtitle = AppStrings.PuedesSinContexto,
                    onClick = { contextSelection = contextSelection.openPicker() },
                    leading = {
                        AqIconBadge(
                            icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                            tone = AqTone.Info,
                        )
                    },
                    showChevron = true,
                )
            }
        }
        // 3) Divulgación progresiva (ADR-0028): concepto y nota
        //    plegados en un panel expandible; solo el encabezado alterna.
        AqExpansionPanel(
            title = AppStrings.MasDetalles,
            expanded = showDetails,
            onToggle = { showDetails = !showDetails },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
            ) {
                AqTextField(
                    label = AppStrings.LabelConcepto,
                    optionalLabel = AppStrings.Opcional,
                    value = concept,
                    onValueChange = { concept = it },
                    maxLength = FieldLimits.Concepto,
                    leadingIcon = com.agendaqr.core.ui.components.AqIcons.NoteEdit,
                )
                AqMultilineTextField(
                    label = AppStrings.Nota,
                    optionalLabel = AppStrings.Opcional,
                    value = note,
                    onValueChange = { note = it },
                    maxLength = FieldLimits.Nota,
                )
            }
        }
        // 4) Destructiva SOLO en edición, al final y separada del resto:
        //    confirmación con el diálogo estándar antes de eliminar.
        if (existing != null) {
            AqDangerButton(
                label = AppStrings.EliminarOperacion,
                onClick = { showDeleteConfirm = true },
                size = AqButtonSize.Medium,
            )
        }
    }

    if (state.discardPrompt) {
        // Salir con cambios pide confirmación; el foco inicial
        // cae en la opción conservadora (destructivo).
        AqDialog(
            title = AppStrings.DescartarCambios,
            message = AppStrings.DescartarCambiosMensaje,
            confirmLabel = AppStrings.Descartar,
            destructive = true,
            onConfirm = { viewModel.onAction(OperationAction.DiscardEditor) },
            dismissLabel = AppStrings.SeguirEditando,
            onDismiss = { viewModel.onAction(OperationAction.DismissDiscardPrompt) },
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

    // Selectores de fecha y hora: la fecha elegida abre la
    // hora; cancelar en cualquiera conserva el valor anterior.
    if (showDatePicker) {
        AqDatePickerDialog(
            selectedDateUtc = utcDayMillis(occurredAtMillis),
            onConfirm = { utc ->
                pendingDateUtc = utc
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false },
            confirmLabel = AppStrings.Elegir,
            dismissLabel = AppStrings.Cancelar,
            title = AppStrings.Fecha,
            monthNames = AppStrings.MonthNames,
            weekdays = AppStrings.WeekdaysShort,
            previousMonthLabel = AppStrings.MesAnterior,
            nextMonthLabel = AppStrings.MesSiguiente,
        )
    }
    if (showTimePicker) {
        val current = Instant.fromEpochMilliseconds(occurredAtMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        AqTimePickerDialog(
            initialHour = current.hour,
            initialMinute = current.minute,
            onConfirm = { hour, minute ->
                occurredAtMillis = combineDateAndTime(
                    pendingDateUtc ?: utcDayMillis(occurredAtMillis),
                    hour,
                    minute,
                )
                pendingDateUtc = null
                showTimePicker = false
            },
            onDismiss = {
                pendingDateUtc = null
                showTimePicker = false
            },
            confirmLabel = AppStrings.Elegir,
            dismissLabel = AppStrings.Cancelar,
            title = AppStrings.Hora,
            hourLabel = AppStrings.Hora,
            minuteLabel = AppStrings.Minutos,
            invalidTimeMessage = AppStrings.HoraInvalida,
        )
    }

    if (showDeleteConfirm && existing != null) {
        // Eliminación desde el editor (solo edición): mismo diálogo
        // destructivo estándar que el detalle; el VM navega atrás al
        // confirmar con éxito.
        AqDialog(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onAction(OperationAction.Delete(existing.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }

    if (showSensitiveConfirm) {
        AqDialog(
            title = AppStrings.changeOperationWithReceipts,
            message = AppStrings.sensitiveChangeWarning,
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
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showSensitiveConfirm = false },
        )
    }
}

private const val FieldAmount = "amount"
