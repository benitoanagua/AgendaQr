package com.agendaqr.core.ui.components

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.XauxaShape


/**
 * Entradas Xauxa: texto etiquetado y búsqueda.
 * (XauxaSettingRow fue retirado: sin pantalla de ajustes en V1 — auditoría de componentes.)
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
    /**
     * slot trailing DENTRO del campo (p. ej. el futuro
     * mostrar/ocultar contraseña). El DS ofrece el hueco; el glifo y su
     * copy son decisión de producto pendiente (ADR-0005, Puntos abiertos)
     * — el slot existe con su test, sin inventar el icono.
     */
    trailing: (@Composable () -> Unit)? = null,
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
                    // deshabilitado distinguible — Surface3 (más
                    // oscuro que el Surface2 de reposo), texto TextTertiary.
                    if (!enabled) XauxaColor.Surface3 else XauxaColor.Surface2,
                    XauxaShape,
                )
                .then(
                    if (borderColor != null) {
                        Modifier.border(XauxaMetrics.Focus, borderColor, XauxaShape)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
                    // asociación etiqueta-campo (la etiqueta fija
                    // es visual; el campo la expone como nombre accesible).
                    .semantics {
                        contentDescription = visibleLabel
                        // El error se asocia al nodo editable, no solo a un
                        // texto hermano; lectores de pantalla pueden anunciarlo
                        // al enfocar el campo.
                        if (isError && errorMessage != null) error(errorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                isError = isError,
                singleLine = singleLine,
                minLines = minLines,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                shape = XauxaShape,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = XauxaColor.Surface2,
                    unfocusedContainerColor = XauxaColor.Surface2,
                    disabledContainerColor = XauxaColor.Surface2,
                    errorContainerColor = XauxaColor.Surface2,
                    // Sin indicador Material: el borde de 2 dp externo es la
                    // única señal de foco/error (M8).
                    focusedIndicatorColor = XauxaColor.Transparent,
                    unfocusedIndicatorColor = XauxaColor.Transparent,
                    disabledIndicatorColor = XauxaColor.Transparent,
                    errorIndicatorColor = XauxaColor.Transparent,
                    focusedTextColor = XauxaColor.TextPrimary,
                    unfocusedTextColor = XauxaColor.TextPrimary,
                    errorTextColor = XauxaColor.TextPrimary,
                    // readOnly se distingue por texto secundario
                    // (el campo sigue legible pero no editable); disabled
                    // hereda el Surface3 del contenedor + TextTertiary.
                    disabledTextColor = XauxaColor.TextTertiary,
                ),
            )
            if (trailing != null) {
                Box(modifier = Modifier.padding(end = XauxaSpacing.Sm)) { trailing() }
            }
            }
        }
        // §11/M8: el error informa con icono + texto, nunca solo por color.
        // liveRegion cortés — el error se anuncia cuando aparece.
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
    /** copy desde el llamador (sin defaults hardcodeados). */
    label: String,
    placeholder: String,
    onClear: (() -> Unit)? = null,
    /** copy de la acción de limpiar, del llamador. */
    clearLabel: String = "",
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "XauxaSearchBar: onClear necesita clearLabel (copy del llamador)"
    }
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
                .background(XauxaColor.Surface2, XauxaShape)
                .then(
                    if (focused) {
                        Modifier.border(XauxaMetrics.Focus, XauxaColor.Brand, XauxaShape)
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
                shape = XauxaShape,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
                leadingIcon = { XauxaIcon(imageVector = XauxaIcons.Search, contentDescription = null) },
                trailingIcon = if (onClear != null && value.isNotEmpty()) {
                    { XauxaTextAction(label = clearLabel, onClick = onClear) }
                } else {
                    null
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = XauxaColor.Surface2,
                    unfocusedContainerColor = XauxaColor.Surface2,
                    disabledContainerColor = XauxaColor.Surface2,
                    errorContainerColor = XauxaColor.Surface2,
                    focusedIndicatorColor = XauxaColor.Transparent,
                    unfocusedIndicatorColor = XauxaColor.Transparent,
                    disabledIndicatorColor = XauxaColor.Transparent,
                    errorIndicatorColor = XauxaColor.Transparent,
                    focusedTextColor = XauxaColor.TextPrimary,
                    unfocusedTextColor = XauxaColor.TextPrimary,
                ),
            )
        }
    }
}

/**
 * Disparador de búsqueda la BARRA que abre la pantalla de
 * búsqueda (S01) sin campo editable — la entrada real vive en S04. Un
 * ÚNICO nodo semántico con rol Button y nombre accesible (antes el campo
 * decorativo exponía su editable-node al lector de pantalla): el feature
 * nunca compone clickable crudo para esto (gate del design system).
 */
@Composable
fun XauxaSearchTrigger(
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Capa transparente con su PROPIA fuente de interacción: la barra
    // decorativa de debajo nunca recibe el toque; la capa sí dibuja foco y
    // pulsación (contrato §7) y expone un único nodo de botón.
    val triggerInteraction = remember { MutableInteractionSource() }
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
                    interactionSource = triggerInteraction,
                    indication = null,
                    role = Role.Button,
                    onClickLabel = label,
                    onClick = onClick,
                )
                .xauxaFocusRing(triggerInteraction)
                .xauxaPressFeedback(triggerInteraction)
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

