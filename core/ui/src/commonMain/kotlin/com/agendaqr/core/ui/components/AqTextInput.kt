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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqTracking
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Entrada de texto sobre [MoonTextInput] / [MoonFormTextInput]
 * (`moon_design` 1.1.0, `widgets/text_input`).
 *
 * Anatomía Moon v1: la etiqueta flotante dentro de la caja solo existe en
 * tamaño [AqTextInputSize.Xl] (alto 56). En sm/md/lg la etiqueta es un
 * texto separado encima del campo (variante `Input Label` del spec:
 * 14/24 en sm, 16/24 en md/lg, separación 4/8). Con [hasFloatingLabel]
 * en xl la etiqueta flota a la esquina superior al enfocar o escribir
 * (estado activo = etiqueta pequeña arriba, inactivo = hint centrado).
 *
 * - Geometría por tamaño: sm 32 / md 40 / lg 48 / xl 56 dp
 *   ([AqTextInputSize]); relleno vertical 2/4/6/9 y horizontal 8/12/12/16
 *   ([AqLayout.InputPaddingVertical*]/[AqLayout.InputPaddingHorizontal*]).
 * - Texto: `body.textDefault` (14) en sm/md y `body.text16` (16) en lg/xl.
 * - Hint/flotante: color `helperTextColor`→[AqColor.TextTertiary]; el error
 *   tiñe borde (`chichi`→[AqColor.Danger]) y texto.
 * - Helper/error debajo con `helperPadding` de Moon (`top: x4s` = 8 dp, sin
 *   padding lateral): [AqErrorMessage] (icono + texto) o [AqHelperMessage].
 * - [borderless]: dentro de [AqTextInputGroup] la caja no dibuja fondo ni
 *   borde (Moon `MoonFormTextInput` transparente; el borde lo pinta el
 *   grupo).
 * - Todo el copy llega del llamador; [AqText] en lugar de Material Text.
 */
@Composable
fun AqTextInput(
    label: String? = null,
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
    /** Etiqueta flotante dentro de la caja (Moon `hasFloatingLabel`).
     *  Solo es efectiva en [AqTextInputSize.Xl]; en los demás tamaños la
     *  etiqueta se renderiza como texto separado encima del campo. */
    hasFloatingLabel: Boolean = true,
    /** `true` dentro de [AqTextInputGroup]: la caja no dibuja fondo ni borde
     *  (Moon `MoonFormTextInput` recibe `inactiveBorderColor` transparente y
     *  `backgroundColor` transparente; el borde lo pinta el grupo). */
    borderless: Boolean = false,
    visualTestTag: String = "aq-text-input-visual",
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
    // Moon text_input: inactivo `beerus`, activo/enfocado `piccolo`,
    // error `chichi` (chichi→AqColor.Danger).
    val borderColor = when {
        effectiveIsError -> AqColor.Danger
        focused -> AqColor.Focus
        else -> AqColor.Border
    }
    val shape: androidx.compose.ui.graphics.Shape = when (size) {
        AqTextInputSize.Sm -> AqSquircles.InteractiveXs
        else -> AqSquircles.InteractiveSm
    }
    val verticalPadding = when (size) {
        AqTextInputSize.Sm -> AqLayout.InputPaddingVerticalSm
        AqTextInputSize.Md -> AqLayout.InputPaddingVerticalMd
        AqTextInputSize.Lg -> AqLayout.InputPaddingVerticalLg
        AqTextInputSize.Xl -> AqLayout.InputPaddingVerticalXl
    }
    val horizontalPadding = when (size) {
        AqTextInputSize.Sm -> AqLayout.InputPaddingHorizontalSm
        AqTextInputSize.Md -> AqLayout.InputPaddingHorizontalMd
        AqTextInputSize.Lg -> AqLayout.InputPaddingHorizontalLg
        AqTextInputSize.Xl -> AqLayout.InputPaddingHorizontalXl
    }
    // Hueco interno entre el ícono de apoyo y el texto (Moon §3: 8 dp, xl 16).
    val innerGap = if (size == AqTextInputSize.Xl) AqSize.X2s else AqSize.X4s
    // Moon v1: la etiqueta flotante solo existe en xl; en sm/md/lg la
    // etiqueta es un texto separado encima del campo (`Input Label`).
    val floatingCapable = size == AqTextInputSize.Xl
    val floatingActive = hasFloatingLabel && floatingCapable &&
        !label.isNullOrBlank() && (focused || value.isNotEmpty())
    val topLabel = !label.isNullOrBlank() && !(hasFloatingLabel && floatingCapable)
    val idleHint = if (topLabel) placeholder else placeholder ?: label
    val accessibleName = label ?: placeholder.orEmpty()
    val fieldTextStyle = (if (size == AqTextInputSize.Lg || size == AqTextInputSize.Xl) {
        AqTextStyles.BodyMd
    } else {
        AqTextStyles.Body
    })

    Column(modifier = modifier.fillMaxWidth()) {
        if (topLabel) {
            // Etiqueta superior de Moon v1 (`Input Label` en sm/md/lg):
            // texto separado, 14/24 en sm y 16/24 en md/lg, separación 8
            // (4 en sm). Es visual: el nombre accesible del campo ya lo
            // porta `contentDescription` (misma regla que la flotante).
            Box(
                modifier = Modifier
                    .padding(
                        bottom = if (size == AqTextInputSize.Sm) {
                            AqSize.X5s
                        } else {
                            AqLayout.LabelToField
                        },
                    )
                    .testTag("aq-field-label"),
            ) {
                Text(
                    text = label.orEmpty(),
                    fontSize = if (size == AqTextInputSize.Sm) AqType.BodySm else AqType.BodyMd,
                    lineHeight = AqType.LineHeightBodyMd,
                    fontFamily = AqType.FamilyUi,
                    fontWeight = AqWeight.Regular,
                    letterSpacing = AqTracking.Normal,
                    color = AqColor.TextPrimary,
                    maxLines = 1,
                    modifier = Modifier.clearAndSetSemantics { },
                )
            }
        }
        // La caja visual es el campo completo de Moon: altura fija del
        // tamaño, sin target táctil extra.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size.height)
                .then(
                    if (borderless) {
                        Modifier
                    } else {
                        Modifier
                            .background(AqColor.Background, shape)
                            .border(AqMetrics.Border, borderColor, shape)
                    },
                )
                .testTag(visualTestTag),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(innerGap),
            ) {
                leadingIcon?.let { glyph ->
                    AqIcon(
                        imageVector = glyph,
                        contentDescription = null,
                        size = AqSize.Xs,
                        tint = when {
                            effectiveIsError -> AqColor.Danger
                            focused -> AqColor.Focus
                            enabled -> AqColor.TextTertiary
                            else -> AqColor.OnDisabled
                        },
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
                            // Android divide el campo editable en un nodo
                            // clicable (EditText) y nodos hijos; sin merge el
                            // nombre caería en un hijo no interactivo. Con
                            // mergeDescendants queda UN solo nodo accesible
                            // que porta nombre, valor editable y acciones.
                            .semantics {
                                contentDescription = accessibleName
                                text = AnnotatedString(accessibleName)
                                if (!enabled) disabled()
                                if (effectiveIsError && effectiveErrorMessage != null) {
                                    error(effectiveErrorMessage)
                                }
                            },
                        enabled = enabled,
                        readOnly = readOnly,
                        singleLine = singleLine,
                        minLines = minLines,
                        maxLines = maxLines,
                        textStyle = fieldTextStyle.copy(
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
                                // Con la etiqueta flotando el texto va abajo
                                // para no solapar la etiqueta superior, pero
                                // SIEMPRE alineado al inicio (BottomCenter lo
                                // centraba horizontalmente).
                                contentAlignment = if (floatingActive) {
                                    Alignment.BottomStart
                                } else {
                                    Alignment.CenterStart
                                },
                            ) {
                                // El contenido interno del campo emite un
                                // nodo de semántica hijo; mientras exista,
                                // Android desvía el contentDescription del
                                // editable a un nodo falso no interactivo.
                                // Se limpia para que el nombre quede en el
                                // propio campo (las acciones editable,
                                // SetText, selección y cursor viven en el
                                // nodo padre y no se ven afectadas).
                                Box(modifier = Modifier.clearAndSetSemantics { }) {
                                    innerTextField()
                                }
                            }
                        },
                    )
                    if (value.isEmpty() && idleHint != null && !floatingActive) {
                        // Placeholder como hermano del campo, no como hijo de
                        // su semántica: dentro del decorationBox se convierte
                        // en hijo del nodo editable y Android desvía el
                        // contentDescription a un nodo falso no interactivo,
                        // dejando el campo vacío sin nombre accesible.
                        AqText(
                            text = idleHint,
                            size = if (size == AqTextInputSize.Lg || size == AqTextInputSize.Xl) {
                                AqType.BodyMd
                            } else {
                                AqType.BodySm
                            },
                            color = AqColor.TextTertiary,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                    }
                    if (floatingActive) {
                        // Etiqueta flotante de Moon (`hasFloatingLabel`):
                        // flota arriba dentro de la caja cuando el campo
                        // tiene contenido o foco.
                        AqText(
                            text = label.orEmpty(),
                            size = AqType.BodyXs,
                            color = if (effectiveIsError) AqColor.Danger else AqColor.TextTertiary,
                            maxLines = 1,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = AqSize.X6s)
                                .clearAndSetSemantics { },
                        )
                    }
                }
                trailing?.invoke()
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
                AqHelperMessage(
                    message = helperMessage,
                    modifier = Modifier
                        .padding(top = AqSize.X4s)
                        .testTag("aq-field-support"),
                )
            }
        }
        maxLength?.let { limit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AqSize.X5s),
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
    }
}

/** Tamaños visuales de [AqTextInput] y [AqTextArea] (tabla §3 del catálogo Moon). */
enum class AqTextInputSize(
    val height: Dp,
) {
    Sm(height = AqSize.Sm),
    Md(height = AqSize.Md),
    Lg(height = AqSize.Lg),
    Xl(height = AqSize.Xl),
}
