package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.theme.AqMetrics

/**
 * Entrada de búsqueda sobre [AqTextInput] con lupa y borrado.
 * Sustituye a [AqSearchField] y [AqSearchTrigger].
 *
 * - [label]: etiqueta de accesibilidad (copy del llamador).
 * - [value] / [onValueChange]: texto de búsqueda.
 * - [placeholder]: texto de ayuda cuando está vacío (copy del llamador).
 * - [clearLabel] / [onClear]: botón de borrado en el slot trailing.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSearchInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    clearLabel: String? = null,
    onClear: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    AqTextInput(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        placeholder = placeholder,
        leadingIcon = AqIcons.Search,
        trailing = {
            if (value.isNotBlank() && onClear != null && clearLabel != null) {
                AqButton(
                    label = clearLabel!!,
                    onClick = onClear,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                    size = com.agendaqr.core.ui.components.AqButtonSize.Small,
                    icon = AqIcons.Erase,
                    modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
            }
        },
    )
}
