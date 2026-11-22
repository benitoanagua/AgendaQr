package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/** Modelo inmutable de una opción de selección, sin estado interno. */
data class AqChoiceOption(
    val id: String,
    val label: String,
    val description: String? = null,
)

/** Estado visual compartido por los controles booleanos del formulario. */
data class AqFieldState(
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val helperMessage: String? = null,
)

internal fun Modifier.fieldSemantics(state: AqFieldState): Modifier =
    if (state.isError && state.errorMessage != null) {
        this.semantics { error(state.errorMessage) }
    } else {
        this
    }

@Composable
internal fun AqFieldMessages(state: AqFieldState) {
    val message = if (state.isError) state.errorMessage else state.helperMessage
    if (!message.isNullOrBlank()) {
        AqText(
            text = message,
            size = AqType.Caption,
            color = if (state.isError) AqColor.Danger else AqColor.TextSecondary,
        )
    }
}

/**
 * Select de una opción equivalente a `s-select`: la fila con valor
 * seleccionado abre un diálogo de opciones con [AqDialogList], reutilizando
 * el modal canónico y la semántica radio existente.
 */
@Composable
fun AqSelect(
    options: List<AqChoiceOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    label: String,
    dialogTitle: String,
    confirmLabel: String,
    dismissLabel: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    state: AqFieldState = AqFieldState(),
) {
    require(options.isNotEmpty()) { "AqSelect requiere al menos una opción" }
    var expanded by remember { mutableStateOf(false) }
    val selected = options.firstOrNull { it.id == selectedId }
    val borderColor = when {
        state.isError -> AqColor.Danger
        !state.enabled -> AqColor.Disabled
        else -> AqColor.FunctionalBorder
    }
    Column(
        modifier = modifier.fillMaxWidth().fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.Medium,
            color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .background(AqColor.InputSurface, AqSquircles.InteractiveSm)
                .border(AqMetrics.Border, borderColor, AqSquircles.InteractiveSm)
                .selectable(
                    selected = false,
                    enabled = state.enabled,
                    role = Role.DropdownList,
                    onClick = { expanded = true },
                )
                .padding(horizontal = AqSpacing.MdPlus, vertical = AqSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AqText(
                text = selected?.label ?: placeholder,
                size = AqType.Body,
                color = when {
                    !state.enabled -> AqColor.OnDisabled
                    selected == null -> AqColor.TextPlaceholder
                    else -> AqColor.TextPrimary
                },
                modifier = Modifier.weight(1f),
            )
            AqIcon(
                imageVector = AqIcons.ChevronRight,
                contentDescription = null,
                tint = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
            )
        }
        AqFieldMessages(state)
    }
    if (expanded) {
        AqDialog(
            title = dialogTitle,
            confirmLabel = confirmLabel,
            onConfirm = { expanded = false },
            dismissLabel = dismissLabel,
            onDismiss = { expanded = false },
            content = {
                AqDialogList {
                    options.forEach { option ->
                        AqRadioButton(
                            label = option.label,
                            description = option.description,
                            selected = option.id == selectedId,
                            onSelected = { onSelected(option.id) },
                        )
                    }
                }
            },
        )
    }
}

/** Casilla equivalente a `s-checkbox`, con target táctil de 48 dp. */
@Composable
fun AqCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = checked,
                enabled = state.enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AqMetrics.ControlMinSize),
        )
        Column(modifier = Modifier.padding(start = AqSpacing.Md).weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldMessages(state)
        }
    }
}

/** Opción radio equivalente a `s-choice-list` en modo selección única. */
@Composable
fun AqRadioButton(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = selected,
                enabled = state.enabled,
                role = Role.RadioButton,
                onClick = onSelected,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AqMetrics.ControlMinSize),
        )
        Column(modifier = Modifier.padding(start = AqSpacing.Md).weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldMessages(state)
        }
    }
}

/** Interruptor equivalente a `s-switch` para ajustes de efecto inmediato. */
@Composable
fun AqSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = checked,
                enabled = state.enabled,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldMessages(state)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AqMetrics.ControlMinSize),
        )
    }
}

/** Grupo de opciones con semántica de selección mutuamente excluyente. */
@Composable
fun AqChoiceGroup(
    label: String,
    options: List<AqChoiceOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state).selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.Medium,
            color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        // Los hijos solo heredan habilitación/error de borde; el mensaje lo
        // pinta UNA vez el grupo, no cada opción.
        val childState = state.copy(isError = false, errorMessage = null, helperMessage = null)
        options.forEach { option ->
            AqRadioButton(
                label = option.label,
                description = option.description,
                selected = option.id == selectedId,
                onSelected = { onSelected(option.id) },
                state = childState,
            )
        }
        AqFieldMessages(state)
    }
}

/** Grupo de casillas con estado derivado, sin estado interno. */
@Composable
fun AqCheckboxGroup(
    label: String,
    options: List<AqChoiceOption>,
    checkedIds: Set<String>,
    onToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state).selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.Medium,
            color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        val childState = state.copy(isError = false, errorMessage = null, helperMessage = null)
        options.forEach { option ->
            AqCheckbox(
                label = option.label,
                description = option.description,
                checked = option.id in checkedIds,
                onCheckedChange = { onToggle(option.id, it) },
                state = childState,
            )
        }
        AqFieldMessages(state)
    }
}

/**
 * Casilla de selección total con estado mixto derivado por el consumidor.
 * Se usa junto a [AqCheckboxGroup] cuando una lista permite seleccionar
 * todos sus elementos. El llamador decide si al pulsar un estado mixto
 * selecciona o limpia el conjunto.
 */
@Composable
fun AqSelectAllCheckbox(
    label: String,
    checkedState: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .fieldSemantics(state)
            .triStateToggleable(
                state = checkedState,
                enabled = state.enabled,
                role = Role.Checkbox,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TriStateCheckbox(
            state = checkedState,
            onClick = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AqMetrics.ControlMinSize),
        )
        Column(modifier = Modifier.padding(start = AqSpacing.Md).weight(1f)) {
            AqText(
                text = label,
                size = AqType.Body,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            )
            if (description != null) {
                AqText(
                    text = description,
                    size = AqType.Caption,
                    color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                )
            }
            AqFieldMessages(state)
        }
    }
}

// ---------------------------------------------------------------------------
// Jerarquía de formulario: sección con encabezado único + campo con label
// ---------------------------------------------------------------------------

/**
 * Sección de formulario con UN encabezado tipográfico ([AqSectionHeader],
 * semántica `heading`). El título nombra el GRUPO ("Detalles",
 * "Comprobante"), nunca repite el label de un campo hijo — la regla de
 * jerarquía del DS es "encabezado de sección ≠ etiqueta de campo".
 */
@Composable
fun AqFormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aq-form-section"),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        // En formulario el encabezado va al borde, sin la sangría de lista.
        AqSectionHeader(text = title, inset = false)
        Column(
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
            content = content,
        )
    }
}

/**
 * Campo de formulario con etiqueta persistente, marcador de opcionalidad
 * discreto ([optionalLabel], un solo patrón) y mensajes de ayuda/error
 * ([AqFieldMessages]). Para contenido compuesto (chips, fechas, filas);
 * los campos hoja usan su propio label + [optionalLabel].
 */
@Composable
fun AqFormField(
    label: String,
    modifier: Modifier = Modifier,
    optionalLabel: String? = null,
    state: AqFieldState = AqFieldState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            AqText(
                text = label,
                size = AqType.Label,
                fontWeight = AqWeight.Medium,
                color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
                modifier = Modifier.weight(1f, fill = false),
            )
            optionalLabel?.let {
                AqText(
                    text = it,
                    size = AqType.Caption,
                    color = AqColor.TextTertiary,
                )
            }
        }
        Column(content = content)
        AqFieldMessages(state)
    }
}

// ---------------------------------------------------------------------------
// ChipGroup — selección inequívoca de opciones en formulario o filtro
// ---------------------------------------------------------------------------

/** Una opción seleccionable de [AqChipGroup]. */
data class AqChipOption(
    val label: String,
    /** Conteo junto a la etiqueta; null = sin conteo. */
    val count: Int? = null,
    val enabled: Boolean = true,
)

/**
 * Grupo de chips seleccionables: conjunto compacto de opciones dentro de
 * una superficie (moneda, atajos de fecha, sugerencias). La selección NO
 * depende solo del color: el chip activo lleva check + relleno de acento
 * + borde reforzado — distinguible en claro, oscuro y para daltónicos.
 * Objetivo táctil de 48 dp; semántica `selectable` + `selected`.
 *
 * [horizontalScroll] = fila desplazable (opciones que no caben);
 * si no, los chips fluyen y saltan de línea. [allowDeselect] permite
 * volver a pulsar el activo para limpiarlo (`onSelect(-1)`).
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AqChipGroup(
    options: List<AqChipOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalScroll: Boolean = false,
    allowDeselect: Boolean = false,
) {
    require(options.isNotEmpty()) { "AqChipGroup requiere al menos una opción" }
    val onChipClick: (Int) -> Unit = { index ->
        onSelect(if (allowDeselect && index == selectedIndex) -1 else index)
    }
    if (horizontalScroll) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            options.forEachIndexed { index, option ->
                AqChip(
                    option = option,
                    selected = index == selectedIndex,
                    onClick = { onChipClick(index) },
                )
            }
        }
    } else {
        androidx.compose.foundation.layout.FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            options.forEachIndexed { index, option ->
                AqChip(
                    option = option,
                    selected = index == selectedIndex,
                    onClick = { onChipClick(index) },
                )
            }
        }
    }
}

@Composable
private fun AqChip(
    option: AqChipOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val textColor = when {
        !option.enabled -> AqColor.OnDisabled
        selected -> AqColor.OnEmphasisFill
        else -> AqColor.TextPrimary
    }
    val containerColor = when {
        !option.enabled -> AqColor.SurfaceSecondary
        selected -> AqColor.EmphasisFill
        else -> AqColor.Surface
    }
    val borderColor = when {
        selected && option.enabled -> AqColor.EmphasisBorder
        else -> AqColor.Border
    }
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .clip(AqSquircles.InteractiveSm)
            .background(containerColor, AqSquircles.InteractiveSm)
            .border(AqMetrics.Border, borderColor, AqSquircles.InteractiveSm)
            .selectable(
                selected = selected,
                enabled = option.enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .semantics { this.selected = selected }
            .padding(horizontal = AqSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            if (selected) {
                // Indicador no-color: check + relleno + borde juntos.
                AqIcon(
                    imageVector = AqIcons.Check,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = textColor,
                )
            }
            AqText(
                text = option.label,
                size = AqType.Label,
                color = textColor,
            )
            option.count?.let { count ->
                // Count separado del label: aire explícito + caption en
                // TextSecondary (cuando no está seleccionado); el activo
                // hereda el color de texto para contraste sobre el fill.
                AqText(
                    text = count.toString(),
                    size = AqType.Caption,
                    color = when {
                        !option.enabled -> AqColor.OnDisabled
                        selected -> textColor
                        else -> AqColor.TextSecondary
                    },
                    modifier = Modifier.padding(start = AqSpacing.Xs),
                )
            }
        }
    }
}
