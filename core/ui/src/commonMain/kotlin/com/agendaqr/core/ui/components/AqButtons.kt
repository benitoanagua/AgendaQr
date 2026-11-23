package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconToggleButton
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqColorScheme
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqCorner

enum class AqButtonVariant { Primary, Default, Danger, Light, Outline, Text }

enum class AqButtonSize(
    val visualHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    /** Hueco entre ícono y etiqueta (Moon § Botón: xs/sm 4, md 8, lg 12, xl 16). */
    val iconTextGap: Dp,
) {
    Large(
        visualHeight = AqMetrics.ButtonLargeHeight,
        horizontalPadding = AqSize.X2s,
        iconSize = AqMetrics.IconSize,
        iconTextGap = AqSize.X3s,
    ),
    Medium(
        visualHeight = AqMetrics.ButtonCompactHeight,
        horizontalPadding = AqSize.X3s,
        iconSize = AqMetrics.ButtonMediumIconSize,
        iconTextGap = AqSize.X4s,
    ),
    Small(
        visualHeight = AqMetrics.ButtonSmallHeight,
        horizontalPadding = AqSize.X4s,
        iconSize = AqMetrics.IconSizeCompact,
        iconTextGap = AqSize.X5s,
    ),
    ExtraSmall(
        visualHeight = AqMetrics.ButtonExtraSmallHeight,
        horizontalPadding = AqSize.X4s,
        iconSize = AqMetrics.IconSizeCompact,
        iconTextGap = AqSize.X5s,
    ),
}

internal data class AqButtonColors(
    val container: Color,
    val content: Color,
    val border: Color? = null,
)

internal fun resolveAqButtonColors(
    variant: AqButtonVariant,
    scheme: AqColorScheme,
    enabled: Boolean,
    pressed: Boolean,
): AqButtonColors = when (variant) {
    AqButtonVariant.Primary -> when {
        !enabled -> AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled)
        pressed -> AqButtonColors(scheme.brandFillActive, scheme.onBrandFill)
        else -> AqButtonColors(scheme.brandFill, scheme.onBrandFill)
    }
    AqButtonVariant.Default -> when {
        !enabled -> AqButtonColors(scheme.disabled, scheme.textTertiary)
        pressed -> AqButtonColors(scheme.surfaceActive, scheme.textPrimary, scheme.border)
        else -> AqButtonColors(scheme.surface, scheme.textPrimary, scheme.border)
    }
    AqButtonVariant.Danger -> when {
        !enabled -> AqButtonColors(scheme.dangerDisabled, scheme.textPrimary)
        pressed -> AqButtonColors(scheme.dangerActive, scheme.onDanger)
        else -> AqButtonColors(scheme.dangerFill, scheme.onDanger)
    }
    AqButtonVariant.Light -> when {
        !enabled -> AqButtonColors(scheme.disabled, scheme.textTertiary)
        pressed -> AqButtonColors(scheme.emphasisSurfaceHover, scheme.emphasisText)
        else -> AqButtonColors(scheme.emphasisSurface, scheme.emphasisText)
    }
    AqButtonVariant.Outline -> AqButtonColors(
        container = if (pressed && enabled) scheme.surfaceActive else scheme.transparent,
        content = if (enabled) scheme.textPrimary else scheme.textTertiary,
        border = scheme.functionalBorder,
    )
    AqButtonVariant.Text -> AqButtonColors(
        container = if (pressed && enabled) scheme.surfaceActive else scheme.transparent,
        content = if (enabled) scheme.link else scheme.textTertiary,
    )
}

/** Botón con variante/tamaño Moon. Sustituto único de AqPrimaryButton,
 *  AqSecondaryButton, AqDangerButton, AqOutlineButton, AqCompactAction. */
@Composable
fun AqButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AqButtonVariant = AqButtonVariant.Primary,
    size: AqButtonSize = AqButtonSize.Large,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
    icon: ImageVector? = null,
    contentColor: Color? = null,
    fontWeight: FontWeight? = null,
    contentPadding: PaddingValues? = null,
    /** Cuando es verdadero y hay [icon], solo se pinta el glifo (centrado)
     *  y se oculta el texto. */
    iconOnly: Boolean = false,
) {
    AqButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
        contentColor = contentColor,
        fontWeight = fontWeight,
        contentPadding = contentPadding,
        hapticFeedback = false,
        iconOnly = iconOnly,
    )
}

@Composable
private fun AqButtonImpl(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    variant: AqButtonVariant,
    size: AqButtonSize,
    enabled: Boolean,
    isLoading: Boolean,
    busyDescription: String?,
    icon: ImageVector?,
    contentColor: Color?,
    fontWeight: FontWeight?,
    contentPadding: PaddingValues?,
    hapticFeedback: Boolean,
    iconOnly: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isEnabled = enabled && !isLoading
    val resolved = resolveAqButtonColors(
        variant = variant,
        scheme = LocalAqColorScheme.current,
        enabled = isEnabled,
        pressed = pressed,
    )
    val foreground = contentColor?.takeIf { isEnabled } ?: resolved.content
    val labelStyle = if (size == AqButtonSize.Small || size == AqButtonSize.ExtraSmall) {
        AqTextStyles.ButtonLabel.copy(
            fontSize = AqType.Body,
            lineHeight = AqType.LineHeightBody,
        )
    } else {
        AqTextStyles.ButtonLabel
    }
    val shape = AqSquircles.InteractiveSm
    val haptics = LocalHapticFeedback.current
    val buttonModifier = modifier
        .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
        .then(
            if (isLoading && busyDescription != null) {
                Modifier.semantics { stateDescription = busyDescription }
            } else {
                Modifier
            },
        )
        .clickable(
            enabled = isEnabled,
            role = Role.Button,
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (hapticFeedback) {
                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                }
                onClick()
            },
        )
        .semantics {
            contentDescription = label
            text = AnnotatedString(label)
        }
    Layout(
        modifier = buttonModifier,
        content = {
            Row(
                modifier = Modifier
                    .heightIn(min = size.visualHeight)
                    .background(resolved.container, shape)
                    .then(
                        resolved.border?.let { Modifier.border(AqMetrics.Border, it, shape) } ?: Modifier,
                    )
                    .padding(
                        contentPadding
                            ?: PaddingValues(
                                horizontal = size.horizontalPadding,
                                vertical = AqSpacing.None,
                            ),
                    )
                    .then(if (isLoading) Modifier.alpha(AqAlpha.Hidden) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(size.iconTextGap),
            ) {
                icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = size.iconSize,
                        tint = foreground,
                    )
                }
                if (!iconOnly) {
                    BasicText(
                        text = label,
                        style = labelStyle.copy(
                            fontWeight = fontWeight ?: AqWeight.SemiBold,
                            color = foreground,
                        ),
                        modifier = Modifier.clearAndSetSemantics {
                            // El texto visible no expone texto accesible propio:
                            // el nodo accesible del botón (target de 48 dp)
                            // declara [text] y [role]. (T-02)
                            text = AnnotatedString("")
                        },
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size.iconSize),
                    color = foreground,
                )
            }
        },
        measurePolicy = { measurables, constraints ->
            val rowConstraints = constraints.copy(
                minWidth = if (constraints.hasFixedWidth) constraints.maxWidth else constraints.minWidth,
                minHeight = minOf(size.visualHeight.roundToPx(), constraints.maxHeight),
            )
            val row = measurables.first().measure(rowConstraints)
            val width = row.width.coerceIn(constraints.minWidth, constraints.maxWidth)
            // D-02: el objetivo táctil nunca baja de ControlMinSize (48 dp) aunque
            // el Row padre apriete las restricciones verticales (coerceIn al
            // maxHeight podría comprimir el botón por debajo del umbral).
            val height = maxOf(row.height, AqMetrics.ControlMinSize.roundToPx())
                .coerceIn(0, constraints.maxHeight)
            val spinner = if (isLoading) {
                val spinnerSize = size.iconSize.roundToPx()
                measurables[1].measure(Constraints.fixed(spinnerSize, spinnerSize))
            } else {
                null
            }
            layout(width, height) {
                row.placeRelative((width - row.width) / 2, (height - row.height) / 2)
                spinner?.placeRelative((width - spinner.width) / 2, (height - spinner.height) / 2)
            }
        },
    )
}

/** Opción del control segmentado: etiqueta SIEMPRE, glifo decorativo opcional,
 *  badge de conteo opcional y tono del estado activo opcional. */
data class AqSegmentedOption(
    val label: String,
    val icon: ImageVector? = null,
    val count: Int? = null,
    val activeTone: AqTone? = null,
)

/** Control segmentado (Segmented de la referencia): pista SurfaceSecondary con
 *  la opción activa sobre píldora Surface. Mutuamente excluyente. */
@Composable
fun AqSegmentedControl(
    options: List<AqSegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(options.isNotEmpty()) { "AqSegmentedControl necesita al menos una opción" }
    require(selectedIndex in -1 until options.size) { "AqSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary, AqSquircles.InteractiveSm)
            .selectableGroup()
            .padding(AqSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val activeColor = option.activeTone?.content() ?: AqColor.EmphasisText
            val optionEnabled = enabled
            val textColor = when {
                !optionEnabled -> AqColor.OnDisabled
                selected -> activeColor
                else -> AqColor.TextSecondary
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected && optionEnabled) AqColor.SelectionSurface else AqColor.SurfaceContainerHigh,
                        AqSquircles.InteractiveSm,
                    )
                    .then(
                        if (selected && optionEnabled) {
                            Modifier.border(AqMetrics.Border, AqColor.Border, AqSquircles.InteractiveSm)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        enabled = optionEnabled,
                        onClick = { if (optionEnabled) onSelect(index) },
                    )
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    option.icon?.let {
                        AqIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AqMetrics.IconSizeCompact,
                            tint = if (selected && optionEnabled) activeColor else AqColor.OnDisabled,
                        )
                    }
                    BasicText(
                        text = option.label,
                        style = AqTextStyles.Label.copy(color = textColor),
                    )
                    option.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected && optionEnabled) activeColor.copy(alpha = AqAlpha.SelectedOnTone) else AqColor.SurfaceContainerHighest,
                                    AqSquircles.InteractiveXs,
                                )
                                .padding(horizontal = AqSpacing.Xs),
                        ) {
                            BasicText(
                                text = it.toString(),
                                style = AqTextStyles.Tag.copy(color = textColor),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Conmutador de favorito: IconToggleButton con glifo de Polaris. */
@Composable
fun AqFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = favorite,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .size(AqMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
    ) {
        AqIcon(
            imageVector = AqIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) AqColor.SelectionIcon else AqColor.TextTertiary,
        )
    }
}
