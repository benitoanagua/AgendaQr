package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
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
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStroke

/**
 * Etiqueta persistente del campo con el marcador de opcionalidad: UN solo
 * patrón discreto (texto terciario junto al label), nunca "(opcional)"
 * incrustado en cada etiqueta.
 */
@Composable
private fun AqInputLabel(
    label: String,
    optionalLabel: String?,
    enabled: Boolean,
    labelColor: Color = AqColor.TextPrimary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        Text(
            label,
            style = AqTextStyles.Label,
            color = if (!enabled) AqColor.OnDisabled else labelColor,
            modifier = Modifier.weight(1f, fill = false),
        )
        optionalLabel?.let {
            Text(
                it,
                style = AqTextStyles.Caption,
                color = AqColor.TextTertiary,
            )
        }
    }
}

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
    /** Marcador discreto de opcionalidad junto a la etiqueta
     *  (copy del llamador, p. ej. "Opcional"). */
    optionalLabel: String? = null,
    /** Longitud máxima permitida. Cuando se informa, se muestra el contador
     *  "n/max" accesible y el campo pasa a error al excederlo (ADR-0033). */
    maxLength: Int? = null,
    /** Solicitante de foco opcional: lo usa [AqFormFocus] para llevar el
     *  foco al primer campo con error al intentar enviar. */
    focusRequester: FocusRequester? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val currentLength = value.length
    val lengthExceeded = maxLength != null && currentLength > maxLength
    // El error propio del llamador tiene prioridad; si no hay uno, el
    // exceso de longitud se informa como error.
    val effectiveIsError = isError || lengthExceeded
    val effectiveErrorMessage = when {
        isError && errorMessage != null -> errorMessage
        lengthExceeded -> "Se excedió el límite de $maxLength caracteres"
        else -> null
    }
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqInputLabel(label, optionalLabel, enabled)
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
                        effectiveIsError -> AqColor.Danger
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
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    .semantics {
                        contentDescription = label
                        if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = singleLine,
                minLines = minLines,
                textStyle = AqTextStyles.Body.copy(
                    color = when {
                        !enabled -> AqColor.OnDisabled
                        effectiveIsError -> AqColor.Danger
                        else -> AqColor.TextPrimary
                    },
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (effectiveIsError) AqColor.Danger else AqColor.Focus),
                // El campo mide al menos ControlMinSize, pero BasicTextField
                // ancla el texto arriba: se centra verticalmente dentro de
                // esa altura mínima para que el texto no quede pegado arriba.
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .padding(vertical = AqSpacing.Sm),
                        contentAlignment = Alignment.CenterStart,
                    ) { innerTextField() }
                },
            )
            trailing?.invoke()
        }
        // Contador "n/max" accesible (se anuncia al acercarse al límite).
        maxLength?.let { limit ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "$currentLength / $limit",
                    modifier = Modifier.semantics {
                        contentDescription = "Longitud $currentLength de $limit"
                    },
                    style = AqTextStyles.Support,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextTertiary,
                )
            }
        }
        when {
            // El error informa con icono + texto, nunca solo por color;
            // liveRegion cortés — el lector lo anuncia al aparecer.
            effectiveIsError && effectiveErrorMessage != null -> {
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
                        effectiveErrorMessage,
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
    /** Marcador discreto de opcionalidad junto a la etiqueta
     *  (copy del llamador, p. ej. "Opcional"). */
    optionalLabel: String? = null,
    /** Longitud máxima permitida. Cuando se informa, se muestra el contador
     *  "n/max" accesible y el campo pasa a error al excederlo (ADR-0033). */
    maxLength: Int? = null,
    /** Solicitante de foco opcional: lo usa [AqFormFocus] para llevar el
     *  foco al primer campo con error al intentar enviar. */
    focusRequester: FocusRequester? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val currentLength = value.length
    val lengthExceeded = maxLength != null && currentLength > maxLength
    val effectiveIsError = isError || lengthExceeded
    val effectiveErrorMessage = when {
        isError && errorMessage != null -> errorMessage
        lengthExceeded -> "Se excedió el límite de $maxLength caracteres"
        else -> null
    }
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqInputLabel(label, optionalLabel, enabled)
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
                        effectiveIsError -> AqColor.Danger
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
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    .semantics {
                        contentDescription = label
                        if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = false,
                minLines = minLines,
                maxLines = maxLines,
                textStyle = AqTextStyles.Body.copy(
                    color = when {
                        !enabled -> AqColor.OnDisabled
                        effectiveIsError -> AqColor.Danger
                        else -> AqColor.TextPrimary
                    },
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (effectiveIsError) AqColor.Danger else AqColor.Focus),
                // El campo mide al menos ControlMinSize, pero BasicTextField
                // ancla el texto arriba: se centra verticalmente dentro de
                // esa altura mínima para que el texto no quede pegado arriba.
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .padding(vertical = AqSpacing.Sm),
                        contentAlignment = Alignment.CenterStart,
                    ) { innerTextField() }
                },
            )
            trailing?.invoke()
        }
        // Contador "n/max" accesible (se anuncia al acercarse al límite).
        maxLength?.let { limit ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "$currentLength / $limit",
                    modifier = Modifier.semantics {
                        contentDescription = "Longitud $currentLength de $limit"
                    },
                    style = AqTextStyles.Support,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextTertiary,
                )
            }
        }
        when {
            // El error informa con icono + texto, nunca solo por color;
            // liveRegion cortés — el lector lo anuncia al aparecer.
            effectiveIsError && effectiveErrorMessage != null -> {
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
                        effectiveErrorMessage,
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
    /** Marcador discreto de opcionalidad junto a la etiqueta
     *  (copy del llamador, p. ej. "Opcional"). */
    optionalLabel: String? = null,
    /** Solicitante de foco opcional: lo usa [AqFormFocus] para llevar el
     *  foco al primer campo con error al intentar enviar. */
    focusRequester: FocusRequester? = null,
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AqAmountField: onClear necesita clearLabel (copy del llamador)"
    }
    val contentColor = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled
    Column(
        modifier = modifier.fillMaxWidth().fieldSemantics(state),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqInputLabel(label, optionalLabel, state.enabled, labelColor = AqColor.TextSecondary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            currencySymbol?.let {
                Box(
                    modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                    // El símbolo de moneda debe alinearse verticalmente al
                    // centro con el importe, no anclado al fondo (evita
                    // desalineación visual entre símbolo y cifra).
                    contentAlignment = Alignment.Center,
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
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
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

/**
 * Entrada de texto sobre [MoonTextInput] / [MoonFormTextInput]
 * (`moon_design` 1.1.0, `lib/src/widgets/text_input`).
 *
 * Etiqueta persistente sobre la caja, borde de 1 dp que pasa a color de
 * foco o de error, radio squircle interactivo y slot para ícono leading y
 * trailing. El error se expresa con ícono + texto (nunca solo color).
 *
 * Tamaños de Moon: sm (32 dp), md (40 dp), lg (48 dp), xl (56 dp), con su
 * relleno horizontal y radio propios. El área táctil se garantiza en 48 dp
 * (D-02) aunque el tamaño visual sea menor.
 */
@Composable
fun AqTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    size: AqTextInputSize = AqTextInputSize.Md,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    optionalLabel: String? = null,
    maxLength: Int? = null,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val currentLength = value.length
    val lengthExceeded = maxLength != null && currentLength > maxLength
    val effectiveIsError = isError || lengthExceeded
    val effectiveErrorMessage = when {
        isError && errorMessage != null -> errorMessage
        lengthExceeded -> "Se excedió el límite de $maxLength caracteres"
        else -> null
    }
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        pressed -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    val textColor = when {
        !enabled -> AqColor.OnDisabled
        effectiveIsError -> AqColor.Danger
        else -> AqColor.TextPrimary
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        AqInputLabel(label, optionalLabel, enabled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = size.touchTarget)
                .background(AqColor.InputSurface, size.corner)
                .border(AqStroke.Default, borderColor, size.corner)
                .padding(horizontal = size.horizontalPadding, vertical = AqSpacing.None),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
        ) {
            leadingIcon?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = when {
                        effectiveIsError -> AqColor.Danger
                        focused -> AqColor.Focus
                        else -> AqColor.TextTertiary
                    },
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = size.touchTarget)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    .semantics {
                        contentDescription = label
                        if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = true,
                textStyle = AqTextStyles.Body.copy(color = textColor),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (effectiveIsError) AqColor.Danger else AqColor.Focus),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = AqSize.Xs),
                        contentAlignment = Alignment.CenterStart,
                    ) { innerTextField() }
                },
            )
            trailing?.invoke()
        }
        maxLength?.let { limit ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "$currentLength / $limit",
                    modifier = Modifier.semantics { contentDescription = "Longitud $currentLength de $limit" },
                    style = AqTextStyles.Support,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextTertiary,
                )
            }
        }
        when {
            effectiveIsError && effectiveErrorMessage != null -> {
                Row(
                    modifier = Modifier.testTag("aq-field-support"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
                ) {
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.Cancel,
                        contentDescription = null,
                        tint = AqColor.Danger,
                    )
                    Text(
                        effectiveErrorMessage,
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

/** Tamaños de la entrada de texto de Moon (`MoonTextInput`). */
enum class AqTextInputSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val corner: androidx.compose.ui.graphics.Shape,
    val touchTarget: Dp,
) {
    Sm(
        height = AqSize.Sm,
        horizontalPadding = AqSize.X4s,
        corner = AqSquircles.InteractiveXs,
        touchTarget = AqMetrics.ControlMinSize,
    ),
    Md(
        height = AqSize.Md,
        horizontalPadding = AqSize.X3s,
        corner = AqSquircles.InteractiveSm,
        touchTarget = AqMetrics.ControlMinSize,
    ),
    Lg(
        height = AqSize.Lg,
        horizontalPadding = AqSize.X3s,
        corner = AqSquircles.InteractiveSm,
        touchTarget = AqMetrics.ControlMinSize,
    ),
    Xl(
        height = AqSize.Xl,
        horizontalPadding = AqSize.Xs,
        corner = AqSquircles.InteractiveMd,
        touchTarget = AqMetrics.ControlMinSize,
    );
}

/**
 * Entrada de texto multilínea sobre [MoonTextArea]
 * (`moon_design` 1.1.0, `lib/src/widgets/text_area`). La caja crece con el
 * contenido y la escala de fuente; objetivo táctil de 48 dp.
 */
@Composable
fun AqTextArea(
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
    size: AqTextInputSize = AqTextInputSize.Md,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    optionalLabel: String? = null,
    maxLength: Int? = null,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val currentLength = value.length
    val lengthExceeded = maxLength != null && currentLength > maxLength
    val effectiveIsError = isError || lengthExceeded
    val effectiveErrorMessage = when {
        isError && errorMessage != null -> errorMessage
        lengthExceeded -> "Se excedió el límite de $maxLength caracteres"
        else -> null
    }
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.FunctionalBorder
    }
    val textColor = when {
        !enabled -> AqColor.OnDisabled
        effectiveIsError -> AqColor.Danger
        else -> AqColor.TextPrimary
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        AqInputLabel(label, optionalLabel, enabled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = size.touchTarget)
                .background(AqColor.InputSurface, size.corner)
                .border(AqStroke.Default, borderColor, size.corner)
                .padding(horizontal = size.horizontalPadding, vertical = AqSpacing.None),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
        ) {
            leadingIcon?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = when {
                        effectiveIsError -> AqColor.Danger
                        focused -> AqColor.Focus
                        else -> AqColor.TextTertiary
                    },
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = size.height)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    .semantics {
                        contentDescription = label
                        if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                    },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = false,
                minLines = minLines,
                maxLines = maxLines,
                textStyle = AqTextStyles.Body.copy(color = textColor),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (effectiveIsError) AqColor.Danger else AqColor.Focus),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = AqSize.Xs),
                        contentAlignment = Alignment.CenterStart,
                    ) { innerTextField() }
                },
            )
            trailing?.invoke()
        }
        when {
            effectiveIsError && effectiveErrorMessage != null -> {
                Row(
                    modifier = Modifier.testTag("aq-field-support"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
                ) {
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.Cancel,
                        contentDescription = null,
                        tint = AqColor.Danger,
                    )
                    Text(
                        effectiveErrorMessage,
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
 * Mensaje de error sobre [MoonErrorMessage]
 * (`moon_design` 1.1.0, `lib/src/widgets/common/error_message_widgets.dart`).
 * Ícono de tono + texto (el estado nunca se comunica solo por color).
 */
@Composable
fun AqErrorMessage(
    message: String,
    tone: com.agendaqr.core.ui.components.AqTone = com.agendaqr.core.ui.components.AqTone.Danger,
    visible: Boolean = true,
) {
    if (!visible || message.isBlank()) return
    Row(
        modifier = Modifier.testTag("aq-field-support"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
    ) {
        tone.iconVector?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                tint = tone.content(),
            )
        }
        Text(
            message,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            style = AqTextStyles.Support,
            color = tone.content(),
        )
    }
}

/**
 * Grupo de entradas de texto sobre [MoonTextInputGroup]
 * (`moon_design` 1.1.0, `lib/src/widgets/text_input_group`). Orientación
 * horizontal o vertical; cada campo conserva su caja, etiqueta y estado de
 * error independiente.
 */
@Composable
fun AqTextInputGroup(
    fields: List<AqTextInputGroupField>,
    orientation: AqTextInputGroupOrientation = AqTextInputGroupOrientation.Vertical,
) {
    when (orientation) {
        AqTextInputGroupOrientation.Vertical -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AqSize.Lg)) {
                fields.forEach { field ->
                    AqTextInput(
                        label = field.label,
                        value = field.value,
                        onValueChange = field.onValueChange,
                        enabled = field.enabled,
                        isError = field.isError,
                        errorMessage = field.errorMessage,
                        helperMessage = field.helperMessage,
                        size = field.size,
                        leadingIcon = field.leadingIcon,
                        trailing = field.trailing,
                        optionalLabel = field.optionalLabel,
                        maxLength = field.maxLength,
                        focusRequester = field.focusRequester,
                    )
                }
            }
        }
        AqTextInputGroupOrientation.Horizontal -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AqSize.Lg),
            ) {
                fields.forEach { field ->
                    AqTextInput(
                        label = field.label,
                        value = field.value,
                        onValueChange = field.onValueChange,
                        enabled = field.enabled,
                        isError = field.isError,
                        errorMessage = field.errorMessage,
                        helperMessage = field.helperMessage,
                        size = field.size,
                        leadingIcon = field.leadingIcon,
                        trailing = field.trailing,
                        optionalLabel = field.optionalLabel,
                        maxLength = field.maxLength,
                        focusRequester = field.focusRequester,
                    )
                        .let { Box(modifier = Modifier.weight(1f)) { it } }
                }
            }
        }
    }
}

data class AqTextInputGroupField(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val helperMessage: String? = null,
    val size: AqTextInputSize = AqTextInputSize.Md,
    val leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val trailing: (@Composable () -> Unit)? = null,
    val optionalLabel: String? = null,
    val maxLength: Int? = null,
    val focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
)

enum class AqTextInputGroupOrientation { Vertical, Horizontal }
