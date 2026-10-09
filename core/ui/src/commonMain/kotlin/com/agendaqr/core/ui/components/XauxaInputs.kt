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
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.liveRegion
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

/** Entrada de texto etiquetada con estado de error. Textarea con singleLine=false.
 *
 * V1.1 (M8): campo de relleno plano (`Surface2`, el surfaceContainer del
 * esquema), etiqueta FIJA pequeña encima (no flotante), borde de 2 dp con
 * acento SOLO en foco o error; sin outline Material en reposo. El error se
 * expresa con icono + texto debajo del campo (§11: nunca solo color).
 */
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
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val visibleLabel = if (isRequired) "$label *" else label
    // M9: el borde existe solo cuando aporta estado (foco o error).
    val borderColor = when {
        isError -> XauxaColor.Danger
        focused -> XauxaColor.Brand
        else -> null
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
    ) {
        XauxaText(
            text = visibleLabel,
            size = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    // Fase 2: deshabilitado distinguible — Surface3 (más
                    // oscuro que el Surface2 de reposo), texto TextTertiary.
                    if (!enabled) XauxaColor.Surface3 else XauxaColor.Surface2,
                    RectangleShape,
                )
                .then(
                    if (borderColor != null) {
                        Modifier.border(XauxaMetrics.Focus, borderColor, RectangleShape)
                    } else {
                        Modifier
                    },
                ),
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
                    // Fase 2: asociación etiqueta-campo (la etiqueta fija
                    // es visual; el campo la expone como nombre accesible).
                    .semantics { contentDescription = visibleLabel },
                enabled = enabled,
                readOnly = readOnly,
                isError = isError,
                singleLine = singleLine,
                minLines = minLines,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                shape = RectangleShape,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = XauxaColor.Surface2,
                    unfocusedContainerColor = XauxaColor.Surface2,
                    disabledContainerColor = XauxaColor.Surface2,
                    errorContainerColor = XauxaColor.Surface2,
                    // Sin indicador Material: el borde de 2 dp externo es la
                    // única señal de foco/error (M8).
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = XauxaColor.TextPrimary,
                    unfocusedTextColor = XauxaColor.TextPrimary,
                    errorTextColor = XauxaColor.TextPrimary,
                    // Fase 2: readOnly se distingue por texto secundario
                    // (el campo sigue legible pero no editable); disabled
                    // hereda el Surface3 del contenedor + TextTertiary.
                    disabledTextColor = XauxaColor.TextTertiary,
                ),
            )
        }
        // §11/M8: el error informa con icono + texto, nunca solo por color.
        // Fase 2: liveRegion cortés — el error se anuncia cuando aparece.
        when {
            isError && errorMessage != null -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
                ) {
                    XauxaIcon(
                        imageVector = XauxaIcons.Cancel,
                        contentDescription = null,
                        tint = XauxaColor.Danger,
                    )
                    // liveRegion en el propio texto: el lector anuncia el
                    // error cuando aparece (§11); el icono lo acompaña
                    // para quien ve.
                    Text(
                        errorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        fontSize = XauxaType.Caption,
                        color = XauxaColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                Text(helperMessage, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            }
        }
    }
}

/** Barra de búsqueda: entrada de una línea con limpieza opcional.
 *
 * V1.1 (M8): mismo lenguaje de campo plano — relleno `Surface2`, etiqueta
 * fija encima, borde de 2 dp solo en foco; icono de búsqueda del set
 * Lucide como glifo decorativo (el nombre accesible lo da la etiqueta).
 */
@Composable
fun XauxaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** Fase 4: copy desde el llamador (sin defaults hardcodeados). */
    label: String,
    placeholder: String,
    onClear: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
    ) {
        XauxaText(
            text = label,
            size = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(XauxaColor.Surface2, RectangleShape)
                .then(
                    if (focused) {
                        Modifier.border(XauxaMetrics.Focus, XauxaColor.Brand, RectangleShape)
                    } else {
                        Modifier
                    },
                ),
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
                placeholder = { Text(placeholder, fontSize = XauxaType.Body, color = XauxaColor.TextTertiary) },
                singleLine = true,
                interactionSource = interactionSource,
                shape = RectangleShape,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
                leadingIcon = { XauxaIcon(imageVector = XauxaIcons.Search, contentDescription = null) },
                trailingIcon = if (onClear != null && value.isNotEmpty()) {
                    { XauxaTextAction(label = "Limpiar", onClick = onClear) }
                } else {
                    null
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = XauxaColor.Surface2,
                    unfocusedContainerColor = XauxaColor.Surface2,
                    disabledContainerColor = XauxaColor.Surface2,
                    errorContainerColor = XauxaColor.Surface2,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = XauxaColor.TextPrimary,
                    unfocusedTextColor = XauxaColor.TextPrimary,
                ),
            )
        }
    }
}

/**
 * Disparador de búsqueda (Fase 4): la BARRA que abre la pantalla de
 * búsqueda (S01) sin campo editable — la entrada real vive en S04. Un
 * ÚNICO nodo semántico con rol Button y nombre accesible (antes el campo
 * decorativo exponía su editable-node al lector de pantalla): el feature
 * nunca compone clickable crudo para esto (gate Fase 4).
 */
@Composable
fun XauxaSearchTrigger(
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        XauxaSearchBar(
            value = "",
            onValueChange = {},
            label = label,
            placeholder = placeholder,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClickLabel = label,
                    onClick = onClick,
                )
                // clearAndSetSemantics: el campo decorativo queda fuera del
                // árbol; el control es un solo botón con nombre accesible.
                .clearAndSetSemantics {
                    contentDescription = label
                    role = Role.Button
                    onClick(label = label) {
                        onClick()
                        true
                    }
                },
        )
    }
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
            // V1.1 (M9): la fila no lleva borde de reposo; la separación es
            // por espacio. El borde queda reservado al foco
            // (xauxaFocusRing) y al track del toggle (control funcional).
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
                // Fase 1: la pastilla apagada (White sobre Surface2,
                // 1.12:1) pasa a BorderControl: >= 3:1 contra el track en
                // claro y oscuro (XauxaSchemeTest).
                Box(
                    modifier = Modifier.size(XauxaSpacing.Xl)
                        .background(if (checked) XauxaColor.OnBrand else XauxaColor.BorderControl),
                )
            }
        }
    }
}
