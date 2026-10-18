package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType

/**
 * Entrada de formulario con etiqueta persistente y divisor inferior.
 *
 * - El error se expresa con icono + texto debajo del campo (nunca solo
 *   color) y se asocia también a la semántica del campo editable.
 * - La obligatoriedad la expresa el llamador con copy (texto de apoyo o
 *   error al enviar), no con asterisco decorativo.
 * - El slot [trailing] vive dentro del campo (p. ej. mostrar/ocultar
 *   contraseña); el glifo y su copy son del llamador.
 */
@Composable
fun AmTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    /** Glifo decorativo del concepto del campo (comercio, nota,
     *  moneda…). El label sigue siendo la señal. */
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        Text(
            label,
            style = AmTextStyles.Label,
            color = if (!enabled) AmColor.OnDisabled else AmColor.TextPrimary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                .padding(horizontal = AmSpacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            leadingIcon?.let { glyph ->
                AmIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = when {
                        isError -> AmColor.Danger
                        focused -> AmColor.Focus
                        else -> AmColor.TextTertiary
                    },
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                    .semantics {
                        contentDescription = label
                        if (isError && errorMessage != null) error(errorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = singleLine,
                minLines = minLines,
                textStyle = AmTextStyles.Body.copy(
                    color = when {
                        !enabled -> AmColor.OnDisabled
                        isError -> AmColor.Danger
                        else -> AmColor.TextPrimary
                    },
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (isError) AmColor.Danger else AmColor.Focus),
            )
            trailing?.invoke()
        }
        HorizontalDivider(
            thickness = AmMetrics.DividerHeight,
            color = when {
                isError -> AmColor.Danger
                focused -> AmColor.Focus
                else -> AmColor.Divider
            },
        )
        when {
            // El error informa con icono + texto, nunca solo por color;
            // liveRegion cortés — el lector lo anuncia al aparecer.
            isError && errorMessage != null -> {
                Row(
                    modifier = Modifier.padding(horizontal = AmSpacing.Lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                ) {
                    AmIcon(
                        imageVector = AmIcons.Cancel,
                        contentDescription = null,
                        tint = AmColor.Danger,
                    )
                    Text(
                        errorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        style = AmTextStyles.Support,
                        color = AmColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                Text(
                    helperMessage,
                    modifier = Modifier.padding(horizontal = AmSpacing.Lg),
                    style = AmTextStyles.Support,
                    color = AmColor.TextSecondary,
                )
            }
        }
    }
}

/**
 * Campo de búsqueda (SearchBar de la referencia): relleno box
 * (`SurfaceSecondary`), PÍLDORA (Anexo A: inputs/buscador 20+, radio
 * completo), glifo de búsqueda y limpieza opcional etiquetada; SIN
 * etiqueta flotante visible — el nombre accesible va por semántica
 * ([label] como contentDescription) y la pista visible es el
 * placeholder; en foco, borde de acento de 1 dp (excepción E-04). La
 * entrada real de búsqueda de producto (S04). La altura mínima de 48 dp
 * conserva el objetivo táctil (la referencia mide 32 px; desviación
 * documentada).
 */
@Composable
fun AmSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** copy desde el llamador (sin defaults hardcodeados). */
    label: String,
    placeholder: String,
    onClear: (() -> Unit)? = null,
    /** nombre accesible de la acción de limpiar, copy del llamador. */
    clearLabel: String = "",
    /** Teclado y acción IME según el dato (el llamador decide). */
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    /** Foco al entrar cuando el flujo lo justifica (p. ej. destino
     * Buscar): abre el teclado sin tocar dos veces. */
    autoFocus: Boolean = false,
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AmSearchField: onClear necesita clearLabel (copy del llamador)"
    }
    val focusRequester = remember { FocusRequester() }
    if (autoFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .focusRequester(focusRequester)
            .semantics { contentDescription = label },
        interactionSource = interactionSource,
        textStyle = AmTextStyles.Body.copy(color = AmColor.TextPrimary),
        cursorBrush = SolidColor(AmColor.Focus),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AmMetrics.FieldCompactHeight)
                        .background(AmColor.SurfaceSecondary, AmShapes.Pill)
                        .border(
                            AmMetrics.Border,
                            if (focused) AmColor.Focus else AmColor.Transparent,
                            AmShapes.Pill,
                        )
                        .padding(horizontal = AmSpacing.Md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    AmIcon(
                        imageVector = AmIcons.Search,
                        contentDescription = null,
                        tint = AmColor.TextTertiary,
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                style = AmTextStyles.Body,
                                color = AmColor.TextPlaceholder,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (onClear != null && value.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            AmIcon(
                                imageVector = AmIcons.Cancel,
                                contentDescription = clearLabel,
                                tint = AmColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        },
    )
}

/**
 * Disparador de búsqueda: la BARRA que abre la pantalla de búsqueda (S01)
 * sin campo editable — la entrada real vive en S04. Un ÚNICO nodo
 * semántico con rol Button y nombre accesible; la indicación de pulsación
 * es la estándar del tema.
 */
@Composable
fun AmSearchTrigger(
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
    ) {
        // El campo decorativo queda fuera del árbol de accesibilidad: el
        // control es un solo botón con nombre accesible (la superposición).
        Box(Modifier.clearAndSetSemantics {}) {
            AmSearchField(
                value = "",
                onValueChange = {},
                label = label,
                placeholder = placeholder,
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .clickable(
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
 * Entrada de importe: cifra hero del Anexo A (30 sp/800, tabular) con
 * símbolo opcional de moneda más pequeño (14 sp/700), acción de borrado ⌫
 * etiquetada y guía hairline — la altura la fija el contenido con un
 * objetivo táctil de 48 dp en el borrado. El valor sigue siendo texto
 * libre del dominio; nada se formatea ni valida aquí.
 */
@Composable
fun AmAmountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** Símbolo mostrado a la izquierda (copy del llamador; p. ej. "$"). */
    currencySymbol: String? = null,
    /** Borrado del importe; su descripción accesible es [clearLabel]. */
    onClear: (() -> Unit)? = null,
    clearLabel: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AmAmountField: onClear necesita clearLabel (copy del llamador)"
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        Text(label, style = AmTextStyles.Label, color = AmColor.TextSecondary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            currencySymbol?.let {
                Text(
                    it,
                    style = AmTextStyles.AmountSymbol,
                    color = AmColor.TextPrimary,
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                    .semantics { contentDescription = label },
                textStyle = AmTextStyles.Amount.copy(color = AmColor.TextPrimary),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AmColor.Focus),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) { inner() }
                },
            )
            if (onClear != null) {
                AmIconButton(
                    contentDescription = clearLabel,
                    onClick = onClear,
                ) {
                    AmIcon(
                        imageVector = AmIcons.Erase,
                        contentDescription = null,
                        tint = AmColor.TextTertiary,
                    )
                }
            }
        }
        HorizontalDivider(thickness = AmMetrics.DividerHeight, color = AmColor.Divider)
    }
}
