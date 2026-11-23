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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Entrada de texto sobre [MoonTextInput] / [MoonFormTextInput]
 * (`moon_design` 1.1.0, `widgets/text_input`).
 *
 * Etiqueta persistente junto al borde superior de la caja, estado de foco,
 * error y deshabilitado resueltos con borde y color (nunca solo color),
 * ícono de apoyo a la izquierda y slot [trailing] a la derecha (p. ej.
 * mostrar/ocultar contraseña). El error se informa debajo con ícono +
 * texto ([AqErrorMessage]) y se asocia a la semántica del campo.
 *
 * Geometría Moon (§ TextInput):
 * - Altura visual por tamaño: xs 24 / sm 32 / md 40 / lg 48 / xl 56 dp
 *   ([AqSize]); el área táctil es ≥ 48 dp ([AqMetrics.ControlMinSize])
 *   mediante un wrapper de toque invisible centrado alrededor de la caja.
 * - Relleno vertical de la caja: xs 2 / sm 2 / md 4 / lg 6 / xl 9 dp
 *   ([AqLayout.InputPaddingVertical*]); relleno horizontal: xs 8 / sm 8 /
 *   md 12 / lg 12 / xl 16 dp ([AqLayout.InputPaddingHorizontal*]).
 * - Hueco etiqueta-caja: 8 dp ([AqLayout.LabelToField]); hueco
 *   etiqueta-optionalLabel: 8 dp ([AqSize.X4s]); hueco interno
 *   icono-texto: 8 dp (xl 16 dp).
 * - Helper/error y contador: padding start/end 12 dp y arriba 8 dp.
 * - Todo el copy llega del llamador; [AqText] en lugar de Material Text.
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
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    optionalLabel: String? = null,
    maxLength: Int? = null,
    focusRequester: FocusRequester? = null,
    placeholder: String? = null,
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
    val shape: androidx.compose.ui.graphics.Shape = when (size) {
        AqTextInputSize.Xs -> AqSquircles.InteractiveXs
        AqTextInputSize.Xl -> AqSquircles.InteractiveMd
        else -> AqSquircles.InteractiveSm
    }
    val verticalPadding = when (size) {
        AqTextInputSize.Xs -> AqLayout.InputPaddingVerticalXs
        AqTextInputSize.Sm -> AqLayout.InputPaddingVerticalSm
        AqTextInputSize.Md -> AqLayout.InputPaddingVerticalMd
        AqTextInputSize.Lg -> AqLayout.InputPaddingVerticalLg
        AqTextInputSize.Xl -> AqLayout.InputPaddingVerticalXl
    }
    val horizontalPadding = when (size) {
        AqTextInputSize.Xs -> AqLayout.InputPaddingHorizontalXs
        AqTextInputSize.Sm -> AqLayout.InputPaddingHorizontalSm
        AqTextInputSize.Md -> AqLayout.InputPaddingHorizontalMd
        AqTextInputSize.Lg -> AqLayout.InputPaddingHorizontalLg
        AqTextInputSize.Xl -> AqLayout.InputPaddingHorizontalXl
    }
    // Hueco interno entre el ícono de apoyo y el texto (Moon §3: 8 dp, xl 16).
    val innerGap = if (size == AqTextInputSize.Xl) AqSize.X2s else AqSize.X4s

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqLayout.LabelToField),
    ) {
        AqTextInputLabel(
            label = label,
            optionalLabel = optionalLabel,
            enabled = enabled,
            errorMessage = effectiveErrorMessage,
        )
        // El hit target mide al menos 48 dp; la caja visual conserva la
        // altura Moon seleccionada y se centra dentro del área táctil.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AqMetrics.ControlMinSize)
                .testTag("aq-field-touch-target"),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = size.height)
                    .background(AqColor.InputSurface, shape)
                    .border(AqMetrics.Border, borderColor, shape)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .testTag("aq-text-input-visual"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(innerGap),
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
                        .then(
                            if (focusRequester != null) {
                                Modifier.focusRequester(focusRequester)
                            } else {
                                Modifier
                            },
                        )
                        .semantics {
                            contentDescription = label
                            text = AnnotatedString(label)
                            if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                        },
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
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
                                .padding(vertical = verticalPadding),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            innerTextField()
                            if (value.isBlank() && placeholder != null) {
                                AqText(
                                    text = placeholder!!,
                                    size = AqType.Body,
                                    color = AqColor.TextTertiary,
                                    modifier = Modifier.padding(horizontal = horizontalPadding),
                                )
                            }
                        }
                    },
                )
                trailing?.invoke()
            }
        }
        maxLength?.let { limit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AqSize.X3s, vertical = AqSize.X5s),
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
                AqErrorMessage(
                    message = effectiveErrorMessage,
                    modifier = Modifier
                        .padding(horizontal = AqSize.X3s, vertical = AqSize.X4s)
                        .testTag("aq-field-support"),
                )
            }
            helperMessage != null -> {
                AqHelperMessage(
                    message = helperMessage,
                    modifier = Modifier
                        .padding(horizontal = AqSize.X3s, vertical = AqSize.X4s)
                        .testTag("aq-field-support"),
                )
            }
        }
    }
}

/** Tamaños visuales de [AqTextInput] y [AqTextArea] (tabla §3 del catálogo Moon). */
enum class AqTextInputSize(
    val height: Dp,
) {
    Xs(height = AqSize.Xs),
    Sm(height = AqSize.Sm),
    Md(height = AqSize.Md),
    Lg(height = AqSize.Lg),
    Xl(height = AqSize.Xl),
}

/** Etiqueta persistente del campo con el marcador de opcionalidad. */
@Composable
private fun AqTextInputLabel(
    label: String,
    optionalLabel: String?,
    enabled: Boolean,
    errorMessage: String? = null,
) {
    val fieldEnabled = enabled
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X4s),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.SemiBold,
            color = if (!fieldEnabled) AqColor.OnDisabled else AqColor.TextPrimary,
            modifier = Modifier
                .clearAndSetSemantics { }   // el label es visual; su nombre accesible vive en el campo (contentDescription)
                .fieldLabelSemantics(errorMessage),
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

/** Semántica de la etiqueta persistente de un campo: mensaje de error. */
private fun Modifier.fieldLabelSemantics(errorMessage: String?): Modifier =
    semantics {
        if (errorMessage != null) error(errorMessage)
    }
