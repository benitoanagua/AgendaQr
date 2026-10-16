/**
 * S07 — Registrar/Editar operación: draft con rememberSaveable, selector
 * de contexto (S08, semántica explícita) y confirmación de cambio sensible.
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
 * Selector de tipo de actividad (S07, referencia Stitch): control
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
    var currency by rememberSaveable(editorKey) { mutableStateOf(existing?.currency.orEmpty()) }
    var person by rememberSaveable(editorKey) { mutableStateOf(existing?.personOrEntity.orEmpty()) }
    var concept by rememberSaveable(editorKey) { mutableStateOf(existing?.concept.orEmpty()) }
    var note by rememberSaveable(editorKey) { mutableStateOf(existing?.note.orEmpty()) }
    // Momento con hora (U-03, D2): milisegundos de época, sin texto
    // libre. El valor por defecto de una actividad nueva es ahora, con
    // hora; los datos antiguos a medianoche se muestran sin hora y no se
    // reescriben salvo que el usuario elija.
    var occurredAtMillis by rememberSaveable(editorKey) { mutableStateOf(existing?.occurredAt ?: nowMillis()) }
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
                    val at = occurredAtMillis
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
            )
        },
    ) {
        // Error recuperable ARRIBA, visible sin scroll, junto al resto de
        // estados de la pantalla (§10).
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
        // Reaseguro local-first de la referencia S07: SIEMPRE cierto (la
        // actividad se guarda en el dispositivo) y el motor local real.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            com.agendaqr.core.ui.components.AmStatusPill(
                text = AppStrings.ModoLocal,
                inverse = true,
            )
            AmIcon(
                imageVector = com.agendaqr.core.ui.components.AmIcons.Database,
                contentDescription = null,
                size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                tint = AmColor.TextTertiary,
            )
            AmText(AppStrings.SqliteLocal, color = AmColor.TextTertiary)
        }
        // Tipo con semántica de selección (segmentado, no dos botones
        // primarios compitiendo): Guardar queda como única acción
        // principal (§1).
        OperationTypeSelector(
            type = type,
            onSelect = { type = it },
        )
        // Requisito mínimo honesto (referencia MCP): tipo y momento son
        // los únicos obligatorios del dominio; el resto es opcional.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmColor.SurfaceContainerLow, com.agendaqr.core.ui.theme.AmShapes.Large)
                .padding(AmSpacing.Sm),
            verticalAlignment = androidx.compose.ui.Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            AmIcon(
                imageVector = com.agendaqr.core.ui.components.AmIcons.Info,
                contentDescription = null,
                size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                tint = AmColor.AccentText,
            )
            AmText(
                AppStrings.RequisitoMinimo,
                color = AmColor.TextSecondary,
            )
        }
        // Tarjeta de importe (referencia S07): entrada grande con
        // borrado ⌫ y ayuda de validación.
        AmListBody {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AmSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmAmountField(
                    label = AppStrings.MontoOpcional,
                    value = amount,
                    onValueChange = { amount = it },
                    onClear = { amount = "" },
                    clearLabel = AppStrings.BorrarImporte,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                    ),
                )
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                ) {
                    AmIcon(
                        imageVector = com.agendaqr.core.ui.components.AmIcons.CircleCheck,
                        contentDescription = null,
                        size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                        tint = AmColor.Success,
                    )
                    AmText(AppStrings.MontoHint, color = AmColor.TextTertiary)
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
        // Tarjeta de datos opcionales (referencia S07): persona, moneda,
        // concepto y nota con glifos de la referencia; el contexto vive en
        // su propia tarjeta de selección (S08).
        AmListBody {
            AmTextField(label = AppStrings.PersonaOEntidadOpcional, value = person, onValueChange = { person = it }, leadingIcon = com.agendaqr.core.ui.components.AmIcons.Store, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
            AmHairline()
            AmTextField(label = AppStrings.MonedaOpcional, value = currency, onValueChange = { currency = it }, leadingIcon = com.agendaqr.core.ui.components.AmIcons.Wallet, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
            AmHairline()
            AmTextField(label = AppStrings.ConceptoOpcional, value = concept, onValueChange = { concept = it }, leadingIcon = com.agendaqr.core.ui.components.AmIcons.NoteEdit, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
            AmHairline()
            AmTextField(label = AppStrings.NotaOpcional, value = note, onValueChange = { note = it }, singleLine = false, modifier = Modifier.padding(horizontal = AmSpacing.Lg))
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
        if (existing == null) {
            // RF-10: los comprobantes se gestionan en el detalle (ADR-0015
            // §6); en el registro nuevo, la promesa es honesta y visible.
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            ) {
                AmIcon(
                    imageVector = com.agendaqr.core.ui.components.AmIcons.Attachment,
                    contentDescription = null,
                    size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                    tint = AmColor.TextTertiary,
                )
                AmText(AppStrings.PodrasAdjuntarComprobantesMasAdelante, color = AmColor.TextSecondary)
            }
        }
        // Texto de confianza bajo la acción (referencia S07/ADR-0008).
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            AmIcon(
                imageVector = com.agendaqr.core.ui.components.AmIcons.ShieldCheck,
                contentDescription = null,
                size = com.agendaqr.core.ui.theme.AmMetrics.IconSizeCompact,
                tint = AmColor.Success,
            )
            AmText(AppStrings.TuInformacionEstaGuardadaEnElDispositivo, color = AmColor.TextSecondary)
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

