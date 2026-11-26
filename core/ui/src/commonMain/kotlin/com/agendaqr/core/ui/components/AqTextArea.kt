package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles
import androidx.compose.ui.draw.alpha
import com.agendaqr.core.ui.theme.AqStateAlpha

/**
 * Área de texto multilínea sobre [MoonTextArea] (`moon_design` 1.1.0,
 * `widgets/text_area` → `MoonFormTextInput`).
 *
 * Moon define una única configuración (sin tamaños ni etiqueta externa):
 * - Caja de altura fija ([height], por defecto `sizes.md` = 40 dp): fondo
 *   `goku`→[AqColor.Background], borde `beerus`→[AqColor.Border] inactivo /
 *   `piccolo`→[AqColor.Focus] activo / `chichi`→[AqColor.Danger] en error,
 *   radio `interactiveSm`, relleno `x2s` (16 dp) en los cuatro lados
 *   (`textPadding: EdgeInsets.all(x2s)`), texto `body.text16` (16 regular)
 *   alineado arriba.
 * - [placeholder] actúa como `hintText` dentro de la caja; [label] se
 *   conserva solo como nombre accesible y fallback de hint (Moon no tiene
 *   etiqueta persistente en `MoonTextArea`).
 * - Helper/error debajo con `helperPadding` de Moon (`top: x4s` = 8 dp, sin
 *   laterales): [AqErrorMessage] (icono + texto) o texto helper.
 * - El texto crece dentro de la caja fija; `minLines`/`maxLines` se pasan al
 *   campo. Deshabilitado = opacidad 0.6. Todo el copy llega del llamador.
 * - [borderless]: dentro de [AqTextInputGroup] la caja no dibuja fondo ni
 *   borde (los pinta el grupo).
 */
@Composable
fun AqTextArea(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    /** Alto de la caja (Moon `height`); por defecto `sizes.md` = 40 dp. */
    height: Dp = AqSize.Md,
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
    val idleHint = placeholder ?: label
    val accessibleName = label ?: placeholder.orEmpty()
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .alpha(if (enabled) 1f else AqStateAlpha.Disabled)
                .then(
                    if (borderless) {
                        Modifier
                    } else {
                        Modifier
                            .background(AqColor.Background, shape)
                            .border(AqMetrics.Border, borderColor, shape)
                    },
                )
                .testTag("aq-field-touch-target")
                .semantics {
                    contentDescription = accessibleName
                    if (effectiveIsError && effectiveErrorMessage != null) error(effectiveErrorMessage)
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalAlignment = Alignment.Top,
            ) {
                leadingIcon?.let { glyph ->
                    AqIcon(
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
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxSize()
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopStart,
                            ) {
                                innerTextField()
                                if (value.isEmpty() && idleHint != null) {
                                    AqText(
                                        text = idleHint,
                                        size = AqType.BodyMd,
                                        color = AqColor.TextTertiary,
                                    )
                                }
                            }
                        },
                    )
                }
                trailing?.let {
                    it()
                }
            }
        }
        maxLength?.let { limit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AqSize.X4s),
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
        // `helperPadding` de Moon: solo `top: x4s` (8 dp), sin laterales.
        when {
            effectiveIsError && effectiveErrorMessage != null -> {
                AqErrorMessage(
                    message = effectiveErrorMessage,
                    modifier = Modifier
                        .padding(top = AqSize.X4s)
                        .testTag("aq-field-support"),
                )
            }
            helperMessage != null -> {
                AqText(
                    text = helperMessage,
                    modifier = Modifier
                        .padding(top = AqSize.X4s)
                        .semantics { contentDescription = helperMessage }
                        .testTag("aq-field-support"),
                    size = AqType.BodyXs,
                    color = AqColor.TextSecondary,
                )
            }
        }
    }
}
