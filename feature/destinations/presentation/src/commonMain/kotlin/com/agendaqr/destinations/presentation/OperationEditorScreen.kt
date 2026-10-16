/**
 * S07 — Registrar/Editar operación: draft con rememberSaveable, selector
 * de contexto (S08, semántica explícita) y confirmación de cambio sensible.
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.domain.*
import kotlinx.datetime.Instant
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
    AmSegmentedControl(
        options = listOf(
            com.agendaqr.core.ui.components.AmSegmentedOption(
                label = AppStrings.Pago,
                icon = com.agendaqr.core.ui.components.AmIcons.Expense,
            ),
            com.agendaqr.core.ui.components.AmSegmentedOption(
                label = AppStrings.Cobro,
                icon = com.agendaqr.core.ui.components.AmIcons.Income,
                activeTone = com.agendaqr.core.ui.components.AmTone.Success,
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
    // Error en vivo tras la primera edición (U-07, D6): sin afirmaciones
    // falsas de validación.
    var amountEdited by rememberSaveable(editorKey) { mutableStateOf(false) }
    val initialCurrency = existing?.currency.orEmpty()
        .ifBlank { mostRecentCurrency(state.operations) }
    var currency by rememberSaveable(editorKey) { mutableStateOf(initialCurrency) }
    var currencyCustomOpen by rememberSaveable(editorKey) {
        mutableStateOf(initialCurrency.isNotBlank() && initialCurrency != AppStrings.MonedaBob && initialCurrency != AppStrings.MonedaUsd)
    }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    // Divulgación progresiva (U-06, D5): concepto y nota plegados salvo
    // que el registro ya los tenga.
    var showDetails by rememberSaveable(editorKey) {
        mutableStateOf(existing?.concept?.isNotBlank() == true || existing?.note?.isNotBlank() == true)
    }
    // Momento con hora (U-03, D2): milisegundos de época, sin texto
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
    val saving = state.isSavingOperation
    val amountInvalid = amountEdited && !isValidAmount(amount)

    // U-08/D7: el editor reporta si tiene cambios; `back()` con cambios
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
        // Grupos funcionales (tipo · datos · contexto · detalle) separados
        // a 20 dp; dentro de cada grupo la distancia es menor.
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AmSpacing.Xl),
        bottomAction = {
            AmBottomAction(
                label = if (saving) {
                    AppStrings.Guardando
                } else if (existing == null) {
                    AppStrings.Guardar
                } else {
                    AppStrings.GuardarCambios
                },
                onClick = {
                    // U-07: sin importe válido no se guarda; el error en
                    // vivo ya es visible (o aparece ahora).
                    if (!isValidAmount(amount)) {
                        amountEdited = true
                        return@AmBottomAction
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
            AmStatusBanner(
                message = err.display(),
                tone = AmTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        // Tipo con semántica de selección (segmentado, no dos botones
        // primarios compitiendo): Guardar queda como única acción
        // principal (§1).
        OperationTypeSelector(
            type = type,
            onSelect = { type = it },
        )
        // Importe + moneda (U-07, D6): entrada grande con borrado y error
        AmListBody {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AmSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmAmountField(
                    label = AppStrings.MontoOpcional,
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
                )
                if (amountInvalid) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                    ) {
                        AmIcon(
                            imageVector = com.agendaqr.core.ui.components.AmIcons.Cancel,
                            contentDescription = null,
                            size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                            tint = AmColor.Danger,
                        )
                        AmText(AppStrings.ImporteInvalido, color = AmColor.Danger)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                    AmFilterChip(
                        label = AppStrings.MonedaBob,
                        selected = currency == AppStrings.MonedaBob,
                        onClick = {
                            currency = AppStrings.MonedaBob
                            currencyCustomOpen = false
                        },
                    )
                    AmFilterChip(
                        label = AppStrings.MonedaUsd,
                        selected = currency == AppStrings.MonedaUsd,
                        onClick = {
                            currency = AppStrings.MonedaUsd
                            currencyCustomOpen = false
                        },
                    )
                    AmFilterChip(
                        label = AppStrings.MonedaOtra,
                        selected = currencyCustomOpen,
                        onClick = {
                            if (currency == AppStrings.MonedaBob || currency == AppStrings.MonedaUsd) {
                                currency = ""
                            }
                            currencyCustomOpen = true
                        },
                    )
                }
                if (currencyCustomOpen) {
                    AmTextField(
                        label = AppStrings.LabelMoneda,
                        value = currency,
                        onValueChange = { currency = it },
                        leadingIcon = com.agendaqr.core.ui.components.AmIcons.Wallet,
                    )
                }
            }
        }
        // Momento con fecha y hora (U-03, D2): fila de selección con el
        // valor ("Hoy · 16/10/2026 14:30") que abre los selectores, más
        // atajos Hoy/Ayer. Sin texto libre: la hora es opcional solo para
        // datos antiguos (medianoche = sin hora, se muestra sin hora).
        AmListBody {
            val dateValue = (relativeDayLabel(occurredAtMillis, nowMillis())?.let { it + " · " } ?: "") +
                formatDateTime(occurredAtMillis)
            AmListRow(
                title = dateValue,
                subtitle = AppStrings.Fecha,
                onClick = { showDatePicker = true },
                leading = {
                    AmIconBadge(
                        icon = com.agendaqr.core.ui.components.AmIcons.Calendar,
                        tone = AmTone.Info,
                    )
                },
                showChevron = true,
            )
            AmHairline()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Sm),
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmFilterChip(
                    label = AppStrings.Hoy,
                    selected = false,
                    onClick = { occurredAtMillis = nowMillis() },
                )
                AmFilterChip(
                    label = AppStrings.Ayer,
                    selected = false,
                    onClick = { occurredAtMillis = minusCivilDays(nowMillis(), 1) },
                )
            }
        }
        // Persona o entidad (U-05): campo + sugerencias de las últimas
        // contrapartes (derivadas del estado, sin persistencia nueva).
        AmListBody {
            AmTextField(label = AppStrings.PersonaOEntidadOpcional, value = person, onValueChange = { person = it }, leadingIcon = com.agendaqr.core.ui.components.AmIcons.Store, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
            val counterparts = remember(state.operations) { recentCounterparts(state.operations) }
            if (counterparts.isNotEmpty()) {
                AmHairline()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Sm)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    counterparts.forEach { suggestion ->
                        AmFilterChip(
                            label = suggestion,
                            selected = false,
                            onClick = { person = suggestion },
                        )
                    }
                }
            }
        }
        // Contexto (S08): fila de selección con chevron; Cancelar nunca
        // destruye el draft (semántica del selector intacta).
        AmListBody {
            AmListRow(
                title = contextSelection.contextId?.let { id ->
                    AppStrings.ParaContextoElegido +
                        (state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
                } ?: AppStrings.ParaElegirContexto,
                subtitle = AppStrings.PuedesSinContexto,
                onClick = { contextSelection = contextSelection.openPicker() },
                leading = {
                    AmIconBadge(
                        icon = com.agendaqr.core.ui.components.AmIcons.QrCode,
                        tone = AmTone.Info,
                    )
                },
                showChevron = true,
            )
        }
        // Más detalles (U-06, D5): concepto y nota plegados salvo que el
        // registro ya los tenga; el contexto vive en su propia tarjeta
        // de selección (S08).
        AmListBody {
            AmListRow(
                title = AppStrings.MasDetalles,
                onClick = { showDetails = !showDetails },
                showChevron = true,
            )
            if (showDetails) {
                AmHairline()
                AmTextField(label = AppStrings.ConceptoOpcional, value = concept, onValueChange = { concept = it }, leadingIcon = com.agendaqr.core.ui.components.AmIcons.NoteEdit, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
                AmHairline()
                AmTextField(label = AppStrings.NotaOpcional, value = note, onValueChange = { note = it }, singleLine = false, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
            }
        }
    }

    if (state.discardPrompt) {
        // U-08/D7: salir con cambios pide confirmación; el foco inicial
        // cae en la opción conservadora (destructivo).
        AmDialog(
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

    // Selectores de fecha y hora (U-03, D2): la fecha elegida abre la
    // hora; cancelar en cualquiera conserva el valor anterior.
    if (showDatePicker) {
        AmDatePickerDialog(
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
        )
    }
    if (showTimePicker) {
        val current = Instant.fromEpochMilliseconds(occurredAtMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        AmTimePickerDialog(
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
        )
    }

    if (showSensitiveConfirm) {
        AmDialog(
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

