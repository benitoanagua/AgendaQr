package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
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
                .background(AqColor.InputSurface, AqShapes.Medium)
                .border(AqMetrics.Border, borderColor, AqShapes.Medium)
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
        modifier = modifier.fieldSemantics(state),
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
        modifier = modifier.fieldSemantics(state),
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

/** Estado indeterminado derivado de una selección parcial (select-all). */
fun AqTriState(checkedIds: Set<String>, options: List<AqChoiceOption>): ToggleableState =
    when {
        checkedIds.isEmpty() -> ToggleableState.Off
        checkedIds.containsAll(options.map { it.id }) -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }
