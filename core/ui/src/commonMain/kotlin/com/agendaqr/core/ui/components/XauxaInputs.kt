package com.agendaqr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Entradas Xauxa (spec §12 V1.2, ADR-0012): `OutlinedTextField` con
 * etiqueta, texto de apoyo y error contextual.
 *
 * - El borde en reposo cumple ≥ 3:1 (token `borderControl`,
 *   XauxaSchemeTest); el foco usa el acento y el error el tono danger.
 * - El error se expresa con icono + texto debajo del campo (§11: nunca
 *   solo color) y se asocia también a la semántica del campo editable.
 * - La obligatoriedad la expresa el llamador con copy (texto de apoyo o
 *   error al enviar), no con asterisco decorativo.
 * - El slot [trailing] vive dentro del campo (p. ej. mostrar/ocultar
 *   contraseña); el glifo y su copy son del llamador.
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
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
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
            colors = OutlinedTextFieldDefaults.colors(
                // Borde de reposo >= 3:1 (borderControl); foco en acento.
                unfocusedBorderColor = XauxaColor.BorderControl,
                focusedBorderColor = XauxaColor.Brand,
                errorBorderColor = XauxaColor.Danger,
                unfocusedLabelColor = XauxaColor.TextSecondary,
                focusedLabelColor = XauxaColor.BrandText,
                errorLabelColor = XauxaColor.Danger,
            ),
        )
        when {
            // §11: el error informa con icono + texto, nunca solo por
            // color; liveRegion cortés — el lector lo anuncia al aparecer.
            isError && errorMessage != null -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(XauxaSpacing.Xs),
                ) {
                    XauxaIcon(
                        imageVector = XauxaIcons.Cancel,
                        contentDescription = null,
                        tint = XauxaColor.Danger,
                    )
                    Text(
                        errorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        style = XauxaTextStyles.Support,
                        color = XauxaColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                Text(
                    helperMessage,
                    style = XauxaTextStyles.Support,
                    color = XauxaColor.TextSecondary,
                )
            }
        }
    }
}

/**
 * Barra de búsqueda: `OutlinedTextField` con glifo de búsqueda y limpieza
 * opcional etiquetada (contrato de entrada de §12 V1.2).
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
    /** nombre accesible de la acción de limpiar, copy del llamador. */
    clearLabel: String = "",
) {
    if (onClear != null) require(clearLabel.isNotBlank()) {
        "XauxaSearchBar: onClear necesita clearLabel (copy del llamador)"
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        placeholder = {
            Text(
                placeholder,
                style = XauxaTextStyles.Body,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        },
        singleLine = true,
        label = { Text(label) },
        leadingIcon = { XauxaIcon(imageVector = XauxaIcons.Search, contentDescription = null) },
        trailingIcon = if (onClear != null && value.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    XauxaIcon(
                        imageVector = XauxaIcons.Cancel,
                        contentDescription = clearLabel,
                        tint = XauxaColor.TextSecondary,
                    )
                }
            }
        } else {
            null
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = XauxaColor.BorderControl,
            focusedBorderColor = XauxaColor.Brand,
        ),
    )
}

/**
 * Disparador de búsqueda: la BARRA que abre la pantalla de búsqueda (S01)
 * sin campo editable — la entrada real vive en S04. Un ÚNICO nodo semántico
 * con rol Button y nombre accesible; la indicación de pulsación es el
 * ripple estándar del tema (§12 V1.2).
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
