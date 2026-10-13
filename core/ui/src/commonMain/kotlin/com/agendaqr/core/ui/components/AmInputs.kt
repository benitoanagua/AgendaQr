package com.agendaqr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "AmSearchField: onClear necesita clearLabel (copy del llamador)"
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
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
        shape = AmShapes.Medium,
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
            unfocusedContainerColor = AmColor.SurfaceSecondary,
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
