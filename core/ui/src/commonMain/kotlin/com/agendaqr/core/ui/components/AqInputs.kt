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
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqRadius
import com.agendaqr.core.ui.theme.AqSquircles

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
        AqText(
            text = label,
            size = AqType.Label,
            color = if (!enabled) AqColor.OnDisabled else labelColor,
            modifier = Modifier.weight(1f, fill = false),
        )
        optionalLabel?.let {
            AqText(
                text = it,
                size = AqType.CaptionSmall,
                color = AqColor.TextTertiary,
            )
        }
    }
}

/**
 * Entrada de texto multilínea justificada por los flujos de notas del
 * producto (S02/S07 edición de destino, S07 registro de operación).
 *
 * A diferencia de [AqTextInput], la caja crece en altura con el contenido
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
    optionalLabel: String? = null,
    maxLength: Int? = null,
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
                .background(AqColor.InputSurface, AqSquircles.InteractiveSm)
                .border(AqMetrics.Border, borderColor, AqSquircles.InteractiveSm)
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
        maxLength?.let { limit ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                AqText(
                    text = "$currentLength / $limit",
                    modifier = Modifier.semantics {
                        contentDescription = "Longitud $currentLength de $limit"
                    },
                    size = AqType.BodySmall,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextTertiary,
                )
            }
        }
        when {
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
                    AqText(
                        text = effectiveErrorMessage,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        size = AqType.BodySmall,
                        color = AqColor.Danger,
                    )
                }
            }
            helperMessage != null -> {
                AqText(
                    text = helperMessage,
                    modifier = Modifier.testTag("aq-field-support"),
                    size = AqType.BodySmall,
                    color = AqColor.TextSecondary,
                )
            }
        }
    }
}
