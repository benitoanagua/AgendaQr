package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Entrada de texto multilínea sobre [MoonTextArea]
 * (`moon_design` 1.1.0, `widgets/text_area`).
 *
 * La caja crece en altura con el contenido y la escala de fuente (mínimo
 * 48 dp de objetivo táctil, sin altura fija que recorte). La etiqueta es
 * persistente y el error se expresa con ícono + texto bajo el campo
 * ([AqErrorMessage]), nunca solo color.
 *
 * - Altura por tamaño: sm 32 / md 40 / lg 48 / xl 56 dp ([AqSize]).
 * - Radios: InteractiveXs / InteractiveSm ([AqCorner]), recortados con
 *   [AqSquircles].
 * - Todo el copy llega del llamador.
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    optionalLabel: String? = null,
    maxLength: Int? = null,
    focusRequester: FocusRequester? = null,
    size: AqTextInputSize = AqTextInputSize.Md,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused = interactionSource.collectIsFocusedAsState().value
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
        enabled -> AqColor.FunctionalBorder
        else -> AqColor.OnDisabled.copy(alpha = 0.3f)
    }
    val shape = when (size) {
        AqTextInputSize.Xl -> AqSquircles.InteractiveMd
        else -> AqSquircles.InteractiveSm
    }
    val horizontalPadding = when (size) {
        AqTextInputSize.Xs -> AqSpacing.Xs
        AqTextInputSize.Sm -> AqSpacing.Md
        AqTextInputSize.Md -> AqSpacing.MdPlus
        AqTextInputSize.Lg -> AqSpacing.MdPlus
        AqTextInputSize.Xl -> AqSpacing.Lg
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X2s),
    ) {
        AqTextInputLabel(label, optionalLabel, enabled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .heightIn(min = size.height)
                .background(AqColor.InputSurface, shape)
                .border(AqMetrics.Border, borderColor, shape)
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.X2s),
        ) {
            leadingIcon?.let { glyph ->
                AqIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = when {
                        effectiveIsError -> AqColor.Danger
                        focused -> AqColor.Focus
                        enabled -> AqColor.TextTertiary
                        else -> AqColor.OnDisabled
                    },
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .then(
                        if (focusRequester != null) {
                            Modifier.focusRequester(focusRequester)
                        } else {
                            Modifier
                        },
                    )
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
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .padding(vertical = AqSize.X2s),
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
                    modifier = Modifier.semantics {
                        contentDescription = "Longitud $currentLength de $limit"
                    },
                    style = AqTextStyles.Support,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextTertiary,
                )
            }
        }
        when {
            effectiveIsError && effectiveErrorMessage != null -> {
                AqErrorMessage(
                    message = effectiveErrorMessage,
                    modifier = Modifier.testTag("aq-field-support"),
                )
            }
            helperMessage != null -> {
                AqHelperMessage(message = helperMessage, modifier = Modifier.testTag("aq-field-support"))
            }
        }
    }
}

/** Etiqueta persistente del área de texto con el marcador de opcionalidad. */
@Composable
private fun AqTextInputLabel(
    label: String,
    optionalLabel: String?,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X2s),
    ) {
        Text(
            text = label,
            style = AqTextStyles.Label,
            color = if (!enabled) AqColor.OnDisabled else AqColor.TextPrimary,
            modifier = Modifier.weight(1f, fill = false),
        )
        optionalLabel?.let {
            Text(
                text = it,
                style = AqTextStyles.Caption,
                color = AqColor.TextTertiary,
            )
        }
    }
}
