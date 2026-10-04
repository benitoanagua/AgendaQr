package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Entradas Xauxa: texto etiquetado, búsqueda y fila de ajuste.
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

/** Entrada de texto etiquetada con estado de error. Textarea con singleLine=false. */
@Composable
fun XauxaTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    isRequired: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    val visibleLabel = if (isRequired) "$label *" else label
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            label = {
                Text(
                    visibleLabel,
                    fontSize = XauxaType.Label,
                    fontWeight = FontWeight.SemiBold,
                    color = XauxaColor.TextPrimary,
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            supportingText = when {
                isError && errorMessage != null -> {
                    { Text(errorMessage, fontSize = XauxaType.Caption, color = XauxaColor.Danger) }
                }
                helperMessage != null -> {
                    { Text(helperMessage, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary) }
                }
                else -> null
            },
            shape = RectangleShape,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = XauxaColor.Surface,
                unfocusedContainerColor = XauxaColor.Surface,
                disabledContainerColor = XauxaColor.Surface2,
                errorContainerColor = XauxaColor.Surface,
                focusedIndicatorColor = XauxaColor.Brand,
                unfocusedIndicatorColor = XauxaColor.Border,
                errorIndicatorColor = XauxaColor.Danger,
                focusedTextColor = XauxaColor.TextPrimary,
                unfocusedTextColor = XauxaColor.TextPrimary,
                errorTextColor = XauxaColor.TextPrimary,
            ),
        )
    }
}

/** Barra de búsqueda: entrada de una línea con limpieza opcional. */
@Composable
fun XauxaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Buscar",
    placeholder: String = "Buscar",
    onClear: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        label = { Text(label, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary) },
        placeholder = { Text(placeholder, fontSize = XauxaType.Body, color = XauxaColor.TextTertiary) },
        singleLine = true,
        shape = RectangleShape,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
        trailingIcon = if (onClear != null && value.isNotEmpty()) {
            { XauxaTextAction(label = "Limpiar", onClick = onClear) }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = XauxaColor.Surface,
            unfocusedContainerColor = XauxaColor.Surface,
            focusedIndicatorColor = XauxaColor.Brand,
            unfocusedIndicatorColor = XauxaColor.Border,
            focusedTextColor = XauxaColor.TextPrimary,
            unfocusedTextColor = XauxaColor.TextPrimary,
        ),
    )
}

/**
 * Fila de ajuste con toggle cuadrado (Selector + Toggle). checked==null la
 * convierte en fila de navegación. Toda la fila es el objetivo táctil.
 */
@Composable
fun XauxaSettingRow(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val action: (() -> Unit)? = when {
        onCheckedChange != null && checked != null -> ({ onCheckedChange(!checked) })
        else -> onClick
    }
    val clickableModifier = if (action == null) modifier else modifier
        .semantics {
            if (checked != null && onCheckedChange != null) {
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            }
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            role = if (checked != null && onCheckedChange != null) Role.Switch else Role.Button,
            onClick = action,
        )
        .focusable(interactionSource = interaction)
    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .xauxaFocusRing(interaction)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Column(modifier = Modifier.weight(XauxaToneWeight)) {
            Text(title, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary)
            Text(description, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
        }
        if (checked != null) {
            Box(
                modifier = Modifier.size(width = XauxaMetrics.ControlMinSize, height = XauxaSpacing.Xxxl)
                    .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
                    .background(if (checked) XauxaColor.Brand else XauxaColor.Surface2)
                    .padding(XauxaSpacing.Xs),
                contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                // Pastilla del toggle: OnBrand preserva el blanco en light
                // y corrige el contraste en dark (White fijo fallaba el
                // par con Brand en dark). El estado apagado
                // (Surface2 + White en light) queda
                // pendiente de referencia oficial de Xauxa: no se inventa color.
                Box(
                    modifier = Modifier.size(XauxaSpacing.Xl)
                        .background(if (checked) XauxaColor.OnBrand else XauxaColor.White),
                )
            }
        }
    }
}
