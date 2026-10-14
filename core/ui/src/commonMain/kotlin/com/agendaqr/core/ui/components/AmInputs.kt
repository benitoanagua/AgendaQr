package com.agendaqr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
 * Entradas (patrón Form/List de la referencia): campo SIN borde, fondo
 * transparente, con etiqueta, texto de apoyo y error contextual. La guía
 * hairline inferior aparece en reposo (Divider), en foco (acento) y en
 * error (peligro); el error también con icono + texto debajo del campo.
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
    /** Glifo decorativo del concepto del campo (referencia Stitch:
     * comercio, nota, moneda…). El label sigue siendo la señal. */
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        // La ayuda/error vive bajo el campo con aire mínimo (4 dp) y
        // alineada al texto editable (padding horizontal del campo, 16 dp):
        // sin esto la ayuda parece de otro campo y el error toca el hairline.
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                // El error se asocia al nodo editable, no solo al texto
                // hermano; los lectores de pantalla lo anuncian al
                // enfocar el campo.
                .semantics {
                    if (isError && errorMessage != null) error(errorMessage)
                },
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            label = { Text(label) },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon?.let { glyph ->
                {
                    AmIcon(
                        imageVector = glyph,
                        contentDescription = null,
                        tint = if (isError) AmColor.Danger else AmColor.TextTertiary,
                    )
                }
            },
            trailingIcon = trailing?.let { slot -> { slot() } },
            shape = AmShapes.Medium,
            colors = TextFieldDefaults.colors(
                // Sin borde ni relleno (referencia): fondo transparente y
                // guía hairline — Divider en reposo, acento en foco.
                unfocusedContainerColor = AmColor.Transparent,
                focusedContainerColor = AmColor.Transparent,
                disabledContainerColor = AmColor.Transparent,
                errorContainerColor = AmColor.Transparent,
                unfocusedIndicatorColor = AmColor.Divider,
                focusedIndicatorColor = AmColor.Focus,
                disabledIndicatorColor = AmColor.Transparent,
                errorIndicatorColor = AmColor.Danger,
                unfocusedTextColor = AmColor.TextPrimary,
                focusedTextColor = AmColor.TextPrimary,
                disabledTextColor = AmColor.OnDisabled,
                errorTextColor = AmColor.Danger,
                cursorColor = AmColor.Focus,
                errorCursorColor = AmColor.Danger,
                selectionColors = null,
                unfocusedLabelColor = AmColor.TextSecondary,
                focusedLabelColor = AmColor.AccentText,
                disabledLabelColor = AmColor.OnDisabled,
                errorLabelColor = AmColor.Danger,
                unfocusedPlaceholderColor = AmColor.TextSecondary,
                focusedPlaceholderColor = AmColor.TextSecondary,
                disabledPlaceholderColor = AmColor.OnDisabled,
                errorPlaceholderColor = AmColor.Danger,
                unfocusedLeadingIconColor = AmColor.TextSecondary,
                focusedLeadingIconColor = AmColor.AccentText,
                errorLeadingIconColor = AmColor.Danger,
                unfocusedTrailingIconColor = AmColor.TextSecondary,
                focusedTrailingIconColor = AmColor.AccentText,
                errorTrailingIconColor = AmColor.Danger,
            ),
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
 * Campo de búsqueda (SearchBar de la referencia): relleno box, radio 6,
 * glifo de búsqueda y limpieza opcional etiquetada; activo con borde de
 * acento. La entrada real de búsqueda de producto (S04). La altura mínima
 * de 48 dp conserva el objetivo táctil (la referencia mide 32 px).
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
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .focusRequester(focusRequester),
        keyboardOptions = keyboardOptions,
        placeholder = {
            Text(
                placeholder,
                style = AmTextStyles.Body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        singleLine = true,
        label = { Text(label) },
        shape = AmShapes.ExtraLarge,
        leadingIcon = { AmIcon(imageVector = AmIcons.Search, contentDescription = null, tint = AmColor.TextTertiary) },
        trailingIcon = if (onClear != null && value.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    AmIcon(
                        imageVector = AmIcons.Cancel,
                        contentDescription = clearLabel,
                        tint = AmColor.TextSecondary,
                    )
                }
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            // Caja de búsqueda (referencia): `Surface` en reposo y en foco
            // (distinta del fondo en ambos temas); el foco lo marca el
            // indicador de acento, no el relleno.
            unfocusedContainerColor = AmColor.Surface,
            focusedContainerColor = AmColor.Surface,
            disabledContainerColor = AmColor.Transparent,
            unfocusedIndicatorColor = AmColor.Divider,
            focusedIndicatorColor = AmColor.Focus,
            disabledIndicatorColor = AmColor.Transparent,
            unfocusedLabelColor = AmColor.TextSecondary,
            focusedLabelColor = AmColor.AccentText,
            cursorColor = AmColor.Focus,
        ),
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
    Box(modifier = modifier) {
        AmSearchField(
            value = "",
            onValueChange = {},
            label = label,
            placeholder = placeholder,
        )
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
 * Entrada de importe (referencia Stitch S07): cifra grande (26 sp
 * SemiBold) con símbolo opcional de moneda, acción de borrado ⌫
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
        Text(label, style = AmTextStyles.BodySmall, color = AmColor.TextSecondary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            currencySymbol?.let {
                Text(
                    it,
                    style = AmTextStyles.Amount,
                    color = AmColor.AccentText,
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = AmType.Amount,
                    lineHeight = AmType.LineAmount,
                    fontFamily = AmType.FamilyUi,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = AmColor.TextPrimary,
                ),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AmColor.Focus),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                label,
                                style = AmTextStyles.Amount,
                                color = AmColor.TextTertiary,
                            )
                        }
                        inner()
                    }
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
        HorizontalDivider(thickness = AmMetrics.Border, color = AmColor.Divider)
    }
}
