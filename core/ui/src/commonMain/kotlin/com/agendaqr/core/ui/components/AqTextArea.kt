package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles
import androidx.compose.ui.draw.alpha

/**
 * Área de texto multilínea sobre [MoonTextArea] (`moon_design` 1.1.0,
 * `widgets/text_area` + `text_area_theme.dart`).
 *
 * Moon define una única configuración (sin tamaños):
 * - Caja: fondo `goku`→[AqColor.Background], borde `beerus`→[AqColor.Border]
 *   inactivo / `piccolo`→[AqColor.Focus] activo / `chichi`→[AqColor.Danger]
 *   en error, radio `interactiveSm`, relleno `x2s` (16 dp) en los cuatro
 *   lados, texto `body.text16` (16 regular).
 * - Helper/error debajo: padding superior `x4s` (8 dp), laterales `x3s`
 *   (12 dp), `body.text12` en `trunks`→[AqColor.TextSecondary] o color de
 *   error. El error se expresa con ícono + texto ([AqErrorMessage]).
 * - La caja crece con el contenido (`minLines`/`maxLines`); el área táctil
 *   se garantiza con un wrapper de 48 dp ([AqMetrics.ControlMinSize]).
 * - [borderless]: dentro de [AqTextInputGroup] la caja no dibuja fondo ni
 *   borde (los pinta el grupo, como `MoonFormTextInput` anidado).
 * - Deshabilitado = opacidad 0.6. Todo el copy llega del llamador.
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
    borderless: Boolean = false,
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
    // Moon text_area: inactivo `beerus`, activo `piccolo`, error `chichi`.
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.Border
    }
    val shape = AqSquircles.InteractiveSm
    // `textPadding` de Moon: EdgeInsets.all(x2s).
    val contentPadding = AqSize.X2s
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        AqTextAreaLabel(label, optionalLabel, enabled)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AqMetrics.ControlMinSize)
                .alpha(if (enabled) 1f else AqStateAlpha.Disabled)
                .testTag("aq-field-touch-target")
                .semantics {
                    contentDescription = label
                    if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                },
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (borderless) {
                            Modifier
                        } else {
                            Modifier
                                .background(AqColor.Background, shape)
                                .border(AqMetrics.Border, borderColor, shape)
                        },
                    )
                    .padding(contentPadding),
                verticalAlignment = Alignment.Top,
            ) {
                leadingIcon?.let { glyph ->
                    com.agendaqr.core.ui.components.AqIcon(
                        imageVector = glyph,
                        contentDescription = null,
                        size = AqSize.Xs,
                        tint = when {
                            effectiveIsError -> AqColor.Danger
                            focused -> AqColor.Focus
                            enabled -> AqColor.TextSecondary
                            else -> AqColor.OnDisabled
                        },
                        modifier = Modifier.padding(top = AqSize.X5s),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (leadingIcon != null) AqSize.X4s else AqLayout.Zero)
                        .then(
                            if (focusRequester != null) {
                                Modifier.focusRequester(focusRequester)
                            } else {
                                Modifier
                            },
                        )
                        .semantics {
                            // El valor editado se anuncia como texto; el nombre
                            // accesible del campo vive en el wrapper.
                            text = AnnotatedString(value)
                        },
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = false,
                    minLines = minLines,
                    maxLines = maxLines,
                    textStyle = AqTextStyles.BodyMd.copy(
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
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopStart,
                        ) {
                            innerTextField()
                        }
                    },
                )
                trailing?.let {
                    it()
                }
            }
        }
        maxLength?.let { limit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AqSize.X3s),
                horizontalArrangement = Arrangement.End,
            ) {
                AqText(
                    text = "$currentLength / $limit",
                    modifier = Modifier.semantics {
                        contentDescription = "Longitud $currentLength de $limit"
                    },
                    size = AqType.BodyXs,
                    color = if (lengthExceeded) AqColor.Danger else AqColor.TextSecondary,
                )
            }
        }
        // `helperPadding` de Moon: top x4s, laterales x3s.
        when {
            effectiveIsError && effectiveErrorMessage != null -> {
                AqErrorMessage(
                    message = effectiveErrorMessage,
                    modifier = Modifier
                        .padding(
                            start = AqSize.X3s,
                            end = AqSize.X3s,
                            top = AqSize.X5s,
                        )
                        .testTag("aq-field-support"),
                )
            }
            helperMessage != null -> {
                AqText(
                    text = helperMessage,
                    modifier = Modifier
                        .padding(
                            start = AqSize.X3s,
                            end = AqSize.X3s,
                            top = AqSize.X5s,
                        )
                        .semantics { contentDescription = helperMessage }
                        .testTag("aq-field-support"),
                    size = AqType.BodyXs,
                    color = AqColor.TextSecondary,
                )
            }
        }
    }
}

/** Etiqueta persistente del área de texto con el marcador de opcionalidad. */
@Composable
private fun AqTextAreaLabel(
    label: String,
    optionalLabel: String?,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X4s),
    ) {
        AqText(
            text = label,
            size = AqType.Label,
            fontWeight = AqWeight.SemiBold,
            color = if (!enabled) AqColor.OnDisabled else AqColor.TextPrimary,
            modifier = Modifier.semantics { },
        )
        optionalLabel?.let {
            AqText(
                text = it,
                size = AqType.CaptionSmall,
                color = AqColor.TextSecondary,
            )
        }
    }
}
