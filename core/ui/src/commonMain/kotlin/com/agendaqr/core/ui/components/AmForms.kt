package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
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
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.AmWeight

/** Modelo inmutable de una opción de selección, sin estado interno. */
data class AmChoiceOption(
    val id: String,
    val label: String,
    val description: String? = null,
)

/** Estado visual compartido por los controles booleanos del formulario. */
data class AmFieldState(
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val helperMessage: String? = null,
)

private fun Modifier.fieldSemantics(state: AmFieldState): Modifier =
    if (state.isError && state.errorMessage != null) {
        this.semantics { error(state.errorMessage) }
    } else {
        this
    }

@Composable
private fun AmFieldMessages(state: AmFieldState) {
    val message = if (state.isError) state.errorMessage else state.helperMessage
    if (!message.isNullOrBlank()) {
        AmText(
            text = message,
            size = AmType.Caption,
            color = if (state.isError) AmColor.Danger else AmColor.TextSecondary,
        )
    }
}

/**
 * Select de una opción equivalente a `s-select`: la fila con valor
 * seleccionado abre un diálogo de opciones con [AmDialogList], reutilizando
 * el modal canónico y la semántica radio existente.
 */
@Composable
fun AmSelect(
    options: List<AmChoiceOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    label: String,
    dialogTitle: String,
    confirmLabel: String,
    dismissLabel: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    state: AmFieldState = AmFieldState(),
) {
    require(options.isNotEmpty()) { "AmSelect requiere al menos una opción" }
    var expanded by remember { mutableStateOf(false) }
    val selected = options.firstOrNull { it.id == selectedId }
    Column(modifier = modifier.fillMaxWidth().fieldSemantics(state)) {
        AmText(text = label, size = AmType.Label, fontWeight = AmWeight.Medium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                .background(AmColor.Surface, AmShapes.Small)
                .selectable(
                    selected = false,
                    enabled = state.enabled,
                    role = Role.DropdownList,
                    onClick = { expanded = true },
                )
                .padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AmText(
                text = selected?.label ?: placeholder,
                size = AmType.Body,
                color = if (selected == null) AmColor.TextPlaceholder else AmColor.TextPrimary,
                modifier = Modifier.weight(1f),
            )
            AmIcon(imageVector = AmIcons.ChevronRight, contentDescription = null)
        }
        AmFieldMessages(state)
    }
    if (expanded) {
        AmDialog(
            title = dialogTitle,
            confirmLabel = confirmLabel,
            onConfirm = { expanded = false },
            dismissLabel = dismissLabel,
            onDismiss = { expanded = false },
            content = {
                AmDialogList {
                    options.forEach { option ->
                        AmRadioButton(
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
fun AmCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AmFieldState = AmFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = checked,
                enabled = state.enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(vertical = AmSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AmMetrics.ControlMinSize),
        )
        Column(modifier = Modifier.padding(start = AmSpacing.Md).weight(1f)) {
            AmText(text = label, size = AmType.Body, fontWeight = AmWeight.Medium)
            if (description != null) {
                AmText(text = description, size = AmType.Caption, color = AmColor.TextSecondary)
            }
            AmFieldMessages(state)
        }
    }
}

/** Opción radio equivalente a `s-choice-list` en modo selección única. */
@Composable
fun AmRadioButton(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    state: AmFieldState = AmFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = selected,
                enabled = state.enabled,
                role = Role.RadioButton,
                onClick = onSelected,
            )
            .padding(vertical = AmSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AmMetrics.ControlMinSize),
        )
        Column(modifier = Modifier.padding(start = AmSpacing.Md).weight(1f)) {
            AmText(text = label, size = AmType.Body, fontWeight = AmWeight.Medium)
            if (description != null) {
                AmText(text = description, size = AmType.Caption, color = AmColor.TextSecondary)
            }
            AmFieldMessages(state)
        }
    }
}

/** Interruptor equivalente a `s-switch` para ajustes de efecto inmediato. */
@Composable
fun AmSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AmFieldState = AmFieldState(),
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .fieldSemantics(state)
            .selectable(
                selected = checked,
                enabled = state.enabled,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(vertical = AmSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AmText(text = label, size = AmType.Body, fontWeight = AmWeight.Medium)
            if (description != null) {
                AmText(text = description, size = AmType.Caption, color = AmColor.TextSecondary)
            }
            AmFieldMessages(state)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = state.enabled,
            modifier = Modifier.clearAndSetSemantics { }.size(AmMetrics.ControlMinSize),
        )
    }
}

/** Grupo de opciones con semántica de selección mutuamente excluyente. */
@Composable
fun AmChoiceGroup(
    label: String,
    options: List<AmChoiceOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: AmFieldState = AmFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
    ) {
        AmText(text = label, size = AmType.Label, fontWeight = AmWeight.Medium)
        options.forEach { option ->
            AmRadioButton(
                label = option.label,
                description = option.description,
                selected = option.id == selectedId,
                onSelected = { onSelected(option.id) },
                state = state,
            )
        }
        AmFieldMessages(state)
    }
}

/** Grupo de casillas con estado derivado, sin estado interno. */
@Composable
fun AmCheckboxGroup(
    label: String,
    options: List<AmChoiceOption>,
    checkedIds: Set<String>,
    onToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AmFieldState = AmFieldState(),
) {
    Column(
        modifier = modifier.fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
    ) {
        AmText(text = label, size = AmType.Label, fontWeight = AmWeight.Medium)
        options.forEach { option ->
            AmCheckbox(
                label = option.label,
                description = option.description,
                checked = option.id in checkedIds,
                onCheckedChange = { onToggle(option.id, it) },
                state = state,
            )
        }
        AmFieldMessages(state)
    }
}

/** Estado indeterminado derivado de una selección parcial (select-all). */
fun AmTriState(checkedIds: Set<String>, options: List<AmChoiceOption>): ToggleableState =
    when {
        checkedIds.isEmpty() -> ToggleableState.Off
        checkedIds.containsAll(options.map { it.id }) -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }
