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
import androidx.compose.ui.platform.testTag
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
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Entrada de formulario con etiqueta persistente y caja de campo.
 *
 * - La etiqueta se alinea al borde izquierdo de la caja (4 dp de
 *   separación vertical). La caja usa `input-bg-surface`, borde de 1 dp
 *   `functionalBorder` (foco/error lo sustituyen), radio Medium y 12 dp de
 *   relleno horizontal interno; conserva el objetivo táctil de 48 dp.
 * - El error se expresa con icono + texto debajo del campo (nunca solo
 *   color) y se asocia también a la semántica del campo editable.
 * - La obligatoriedad la expresa el llamador con copy (texto de apoyo o
 *   error al enviar), no con asterisco decorativo.
 * - El slot [trailing] vive dentro del campo (p. ej. mostrar/ocultar
 *   contraseña); el glifo y su copy son del llamador.
 */
@Composable
fun AqTextField(
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
    val borderColor = when {
        isError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Text(
            label,
            style = AqTextStyles.Label,
            color = if (!enabled) AqColor.OnDisabled else AqColor.TextPrimary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .background(AqColor.InputSurface, AqShapes.Medium)
                .border(AqMetrics.Border, borderColor, AqShapes.Medium)
                .padding(horizontal = AqSpacing.MdPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            leadingIcon?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = when {
                        isError -> AqColor.Danger
                        focused -> AqColor.Focus
                        else -> AqColor.TextTertiary
                    },
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .semantics {
                        contentDescription = label
                        if (isError && errorMessage != null) error(errorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = singleLine,
                minLines = minLines,
                textStyle = AqTextStyles.Body.copy(
                    color = when {
                        !enabled -> AqColor.OnDisabled
                        isError -> AqColor.Danger
                        else -> AqColor.TextPrimary
                    },
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (isError) AqColor.Danger else AqColor.Focus),
            )
            trailing?.invoke()
        }
        when {
            // El error informa con icono + texto, nunca solo por color;
            // liveRegion cortés — el lector lo anuncia al aparecer.
            isError && errorMessage != null -> {
                Row(
                    modifier = Modifier.testTag("aq-field-support"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    AqIcon(
                        imageVector = AqIcons.Cancel,
                        contentDescription = null,
                        tint = AqColor.Danger,
                    )
                    Text(
                        errorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        style = AqTextStyles.Support,
                        color = AqColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                Text(
                    helperMessage,
                    modifier = Modifier.testTag("aq-field-support"),
                    style = AqTextStyles.Support,
                    color = AqColor.TextSecondary,
                )
            }
        }
    }
}

/**
 * Entrada de texto multilínea justificada por los flujos de notas del
 * producto (S02/S07 edición de destino, S07 registro de operación).
 *
 * A diferencia de [AqTextField], la caja crece en altura con el contenido
 * y la escala de fuente (mínimo 48 dp de objetivo táctil, sin altura fija
 * que recorte). La etiqueta es persistente y el error se expresa con
 * icono + texto bajo el campo, nunca solo color.
 */
@Composable
fun AqMultilineTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        isError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Text(
            label,
            style = AqTextStyles.Label,
            color = if (!enabled) AqColor.OnDisabled else AqColor.TextPrimary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .background(AqColor.InputSurface, AqShapes.Medium)
                .border(AqMetrics.Border, borderColor, AqShapes.Medium)
                .padding(horizontal = AqSpacing.MdPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            leadingIcon?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = when {
                        isError -> AqColor.Danger
                        focused -> AqColor.Focus
                        else -> AqColor.TextTertiary
                    },
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .semantics {
                        contentDescription = label
                        if (isError && errorMessage != null) error(errorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = false,
                minLines = minLines,
                maxLines = maxLines,
                textStyle = AqTextStyles.Body.copy(
                    color = when {
                        !enabled -> AqColor.OnDisabled
                        isError -> AqColor.Danger
                        else -> AqColor.TextPrimary
                    },
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (isError) AqColor.Danger else AqColor.Focus),
            )
            trailing?.invoke()
        }
        when {
            isError && errorMessage != null -> {
                Row(
                    modifier = Modifier.testTag("aq-field-support"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    AqIcon(
                        imageVector = AqIcons.Cancel,
                        contentDescription = null,
                        tint = AqColor.Danger,
                    )
                    Text(
                        errorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        style = AqTextStyles.Support,
                        color = AqColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                Text(
                    helperMessage,
                    modifier = Modifier.testTag("aq-field-support"),
                    style = AqTextStyles.Support,
                    color = AqColor.TextSecondary,
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
fun AqSearchField(
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
    /** Cuando falso, el campo no es editable y se muestra deshabilitado. */
    enabled: Boolean = true,
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AqSearchField: onClear necesita clearLabel (copy del llamador)"
    }
    val focusRequester = remember { FocusRequester() }
    if (autoFocus && enabled) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textColor = if (enabled) AqColor.TextPrimary else AqColor.OnDisabled
    val placeholderColor = if (enabled) AqColor.TextPlaceholder else AqColor.OnDisabled
    val borderColor = when {
        !enabled -> AqColor.OnDisabled.copy(alpha = 0.3f)
        focused -> AqColor.Focus
        else -> AqColor.Transparent
    }
    val iconTint = if (enabled) AqColor.TextTertiary else AqColor.OnDisabled
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .focusRequester(focusRequester)
            .semantics { contentDescription = label },
        interactionSource = interactionSource,
        enabled = enabled,
        textStyle = AqTextStyles.Body.copy(color = textColor),
        cursorBrush = SolidColor(if (enabled) AqColor.Focus else AqColor.OnDisabled),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AqMetrics.FieldCompactHeight)
                        .background(AqColor.SurfaceSecondary, AqShapes.Pill)
                        .border(
                            AqMetrics.Border,
                            borderColor,
                            AqShapes.Pill,
                        )
                        .padding(horizontal = AqSpacing.MdPlus),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    AqIcon(
                        imageVector = AqIcons.Search,
                        contentDescription = null,
                        tint = iconTint,
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                style = AqTextStyles.Body,
                                color = placeholderColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (enabled && onClear != null && value.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            AqIcon(
                                imageVector = AqIcons.Cancel,
                                contentDescription = clearLabel,
                                tint = AqColor.TextSecondary,
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
fun AqSearchTrigger(
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
    ) {
        // El campo decorativo queda fuera del árbol de accesibilidad: el
        // control es un solo botón con nombre accesible (la superposición).
        Box(Modifier.clearAndSetSemantics {}) {
            AqSearchField(
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
fun AqAmountField(
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
    /** Estado compartido de campo (error/ayuda/deshabilitado): la molécula
     *  es dueña del mensaje y de su separación, no la pantalla. */
    state: AqFieldState = AqFieldState(),
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AqAmountField: onClear necesita clearLabel (copy del llamador)"
    }
    val contentColor = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled
    Column(
        modifier = modifier.fillMaxWidth().fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Text(
            label,
            style = AqTextStyles.Label,
            color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            currencySymbol?.let {
                Box(
                    modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Text(
                        it,
                        style = AqTextStyles.AmountSymbol,
                        color = contentColor,
                    )
                }
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .semantics { contentDescription = label },
                enabled = state.enabled,
                textStyle = AqTextStyles.Amount.copy(color = contentColor),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AqColor.Focus),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) { inner() }
                },
            )
            if (onClear != null) {
                AqIconButton(
                    contentDescription = clearLabel,
                    onClick = onClear,
                ) {
                    AqIcon(
                        imageVector = AqIcons.Erase,
                        contentDescription = null,
                        tint = if (state.enabled) AqColor.TextTertiary else AqColor.OnDisabled,
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = AqMetrics.DividerHeight,
            color = if (state.isError) AqColor.Danger else AqColor.Divider,
        )
        AqFieldMessages(state)
    }
}
