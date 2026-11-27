package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Popup
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTracking
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.aqShadow

/**
 * Selector desplegable sobre la especificación Moon v1 (Figma «Select» /
 * `moon-v1-tokens-y-componentes.json`).
 *
 * El campo replica la anatomía de [AqTextInput] (mismas alturas por
 * [AqTextInputSize], mismo radio `interactiveXs`/`interactiveSm`, mismo
 * borde `beerus`/`piccolo`/`chichi`, misma etiqueta flotante y mensajes de
 * soporte), pero es de solo lectura: muestra la opción seleccionada o el
 * [placeholder] y un chevron que abre un [AqDropdown] con las opciones.
 *
 * - Alturas: sm 32 / md 40 / lg 48 / xl 56 dp; con etiqueta de entrada el
 *   marco completo crece a 60/72/80/88 dp (igual que `Text input` con
 *   `Input Label=True`).
 * - El estado activo (abierto) usa el borde de foco `piccolo`, como el
 *   foco de un input de Moon.
 * - Todo el copy llega del llamador; las opciones se renderizan con la
 *   anatomía de menú de Moon ([AqMenuItem] dentro de [AqDropdown]).
 */
@Composable
fun AqSelect(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    leadingIcon: ImageVector? = null,
    size: AqTextInputSize = AqTextInputSize.Md,
    hasFloatingLabel: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor = when {
        isError -> AqColor.Danger
        expanded -> AqColor.Focus
        else -> AqColor.Border
    }
    val shape = when (size) {
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
    val innerGap = if (size == AqTextInputSize.Xl) AqSize.X2s else AqSize.X4s
    // Moon v1: la etiqueta flotante solo existe en xl; en sm/md/lg es un
    // texto separado encima del campo (`Input Label`, igual que en
    // [AqTextInput]).
    val floatingCapable = size == AqTextInputSize.Xl
    val floatingActive = hasFloatingLabel && floatingCapable &&
        !label.isNullOrBlank() && (expanded || selectedIndex >= 0)
    val topLabel = !label.isNullOrBlank() && !(hasFloatingLabel && floatingCapable)
    val idleHint = if (topLabel) placeholder else placeholder ?: label
    val accessibleName = label ?: placeholder.orEmpty()
    val selectedLabel = options.getOrNull(selectedIndex)
    val effectiveErrorMessage = if (isError) errorMessage else null

    Column(modifier = modifier.fillMaxWidth()) {
        if (topLabel) {
            // Etiqueta superior de Moon v1 (`Input Label` en sm/md/lg).
            // Es visual: el nombre accesible del campo ya lo porta
            // `contentDescription` (misma regla que la flotante).
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
        AqDropdown(
            open = expanded && enabled,
            options = options,
            selected = selectedIndex,
            onOptionClick = {
                onSelected(it)
                expanded = false
            },
            anchor = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(size.height)
                        .clip(shape)
                        .background(AqColor.Background, shape)
                        .border(AqMetrics.Border, borderColor, shape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            role = Role.DropdownList,
                            onClick = { expanded = !expanded },
                        )
                        .semantics {
                            text = AnnotatedString(accessibleName)
                            contentDescription = accessibleName
                            selected = selectedIndex >= 0
                            if (!enabled) disabled()
                            if (effectiveErrorMessage != null) error(effectiveErrorMessage)
                        }
                        .testTag("aq-select-visual"),
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
                                    isError -> AqColor.Danger
                                    expanded -> AqColor.Focus
                                    enabled -> AqColor.TextTertiary
                                    else -> AqColor.OnDisabled
                                },
                            )
                        }
                        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                            if (selectedLabel != null) {
                                AqText(
                                    text = selectedLabel,
                                    size = if (size == AqTextInputSize.Lg || size == AqTextInputSize.Xl) {
                                        AqType.BodyMd
                                    } else {
                                        AqType.BodySm
                                    },
                                    color = if (enabled) AqColor.TextPrimary else AqColor.OnDisabled,
                                    maxLines = 1,
                                )
                            } else if (idleHint != null && !floatingActive) {
                                AqText(
                                    text = idleHint,
                                    size = if (size == AqTextInputSize.Lg || size == AqTextInputSize.Xl) {
                                        AqType.BodyMd
                                    } else {
                                        AqType.BodySm
                                    },
                                    color = AqColor.TextTertiary,
                                    maxLines = 1,
                                )
                            }
                            if (floatingActive) {
                                AqText(
                                    text = label.orEmpty(),
                                    size = AqType.BodyXs,
                                    color = if (isError) AqColor.Danger else AqColor.TextTertiary,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(top = AqSize.X6s)
                                        .clearAndSetSemantics { },
                                )
                            }
                        }
                        // Chevron de apertura: `ControlsChevronRight` de Moon
                        // rotado 90° (no existe variante `chevron-down` en el
                        // set 10.7.4).
                        AqIcon(
                            imageVector = AqIcons.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(90f),
                            size = AqSize.Xs,
                            tint = when {
                                !enabled -> AqColor.OnDisabled
                                expanded -> AqColor.Focus
                                else -> AqColor.TextTertiary
                            },
                        )
                    }
                }
            },
            content = {},
        )
        when {
            effectiveErrorMessage != null -> {
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
    }
}

/**
 * Selector desplegable de opciones sobre la especificación Moon v1 (Figma
 * «Combobox» / `moon-v1-tokens-y-componentes.json`): campo editable con
 * lista filtrada y selección simple o múltiple.
 *
 * - El campo es [AqTextInput] (misma geometría y estados que `Text input`);
 *   el desplegable usa la superficie de menú de Moon con una marca de
 *   selección por opción.
 * - [multiple] = `true` activa el «Selected counter» del Figma: un pie en el
 *   menú muestra cuántas opciones hay marcadas.
 * - La filtración es `contains` sin distinción de mayúsculas sobre [options]
 *   con el [query] actual; el llamador conserva la propiedad de los datos.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqCombobox(
    query: String,
    onQueryChange: (String) -> Unit,
    options: List<String>,
    selectedIndices: Set<Int>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperMessage: String? = null,
    multiple: Boolean = false,
    size: AqTextInputSize = AqTextInputSize.Md,
    hasFloatingLabel: Boolean = true,
    expanded: Boolean = true,
    onExpandedChange: (Boolean) -> Unit = {},
    counterText: ((Int) -> String)? = null,
    /** Nombre accesible del botón que limpia el filtro del desplegable. */
    clearLabel: String? = null,
) {
    val (anchorPosition, anchorModifier) = rememberAqAnchor()
    val maxWidth = aqAnchorMaxWidthDp(anchorPosition.centerXPx)
    val density = LocalDensity.current
    val gapPx = with(density) { AqAnchorGap.toPx().toInt() }
    val filtered = options.mapIndexedNotNull { index, option ->
        if (option.contains(query, ignoreCase = true)) index to option else null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box {
            Box(modifier = anchorModifier) {
                AqTextInput(
                    label = label,
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    isError = isError,
                    errorMessage = errorMessage,
                    helperMessage = null,
                    placeholder = placeholder,
                    size = size,
                    hasFloatingLabel = hasFloatingLabel,
                    trailing = {
                        if (query.isNotEmpty() && enabled) {
                            AqIcon(
                                imageVector = AqIcons.Erase,
                                contentDescription = null,
                                size = AqSize.Xs,
                                tint = AqColor.TextTertiary,
                                modifier = Modifier
                                    .semantics {
                                        if (clearLabel != null) {
                                            text = AnnotatedString(clearLabel)
                                            contentDescription = clearLabel
                                        } else {
                                            hideFromAccessibility()
                                        }
                                    }
                                    .clickable { onQueryChange("") },
                            )
                        }
                    },
                )
            }
            if (expanded && enabled) {
                Popup(
                    popupPositionProvider = AqCenteredAnchorPositionProvider(
                        gapPx = gapPx,
                        below = true,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = maxWidth)
                            .aqWrapIntrinsicWidth()
                            .testTag("aq-combobox-popup")
                            .aqShadow(AqShadow.Sm, AqSquircles.InteractiveMd)
                            .background(AqColor.Background, AqSquircles.InteractiveMd)
                            .padding(AqLayout.DropdownContentPadding),
                    ) {
                        filtered.forEach { (index, option) ->
                            val selected = index in selectedIndices
                            AqMenuItem(
                                title = option,
                                selected = selected,
                                enabled = true,
                                onClick = {
                                    onToggle(index)
                                    if (!multiple) onExpandedChange(false)
                                },
                                leading = if (multiple && selected) {
                                    {
                                        AqIcon(
                                            imageVector = AqIcons.Check,
                                            contentDescription = null,
                                            size = AqSize.Xs,
                                            tint = AqColor.EmphasisIcon,
                                        )
                                    }
                                } else {
                                    null
                                },
                            )
                        }
                        if (multiple && counterText != null) {
                            AqText(
                                text = counterText(selectedIndices.size),
                                size = AqType.BodyXs,
                                color = AqColor.TextTertiary,
                                modifier = Modifier.padding(
                                    horizontal = AqLayout.MenuItemGap,
                                    vertical = AqSize.X5s,
                                ),
                            )
                        }
                    }
                }
            }
        }
        if (helperMessage != null) {
            AqHelperMessage(
                message = helperMessage,
                modifier = Modifier.padding(top = AqSize.X4s).testTag("aq-field-support"),
            )
        }
    }
}
