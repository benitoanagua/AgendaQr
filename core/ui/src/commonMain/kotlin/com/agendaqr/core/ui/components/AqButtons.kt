package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqColorScheme
import com.agendaqr.core.ui.theme.AqControlParams
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.motion.LocalReducedMotion

enum class AqButtonVariant { Primary, Default, Danger, Light, Outline, Text }

enum class AqButtonSize(
    val visualHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    /** Hueco entre ícono y etiqueta (Moon § Botón: xs/sm 4, md 8, lg 12, xl 16). */
    val iconTextGap: Dp,
) {
    ExtraSmall(
        visualHeight = AqLayout.ButtonHeightXs,
        horizontalPadding = AqLayout.ButtonPaddingHorizontalXs,
        iconSize = AqMetrics.IconSizeCompact,
        iconTextGap = AqLayout.ButtonGapXs,
    ),
    Small(
        visualHeight = AqLayout.ButtonHeightSm,
        horizontalPadding = AqLayout.ButtonPaddingHorizontalSm,
        iconSize = AqMetrics.ButtonMediumIconSize,
        iconTextGap = AqLayout.ButtonGapSm,
    ),
    Medium(
        visualHeight = AqLayout.ButtonHeightMd,
        horizontalPadding = AqLayout.ButtonPaddingHorizontalMd,
        iconSize = AqMetrics.ButtonMediumIconSize,
        iconTextGap = AqLayout.ButtonGapMd,
    ),
    Large(
        visualHeight = AqLayout.ButtonHeightLg,
        horizontalPadding = AqLayout.ButtonPaddingHorizontalLg,
        iconSize = AqMetrics.IconSize,
        iconTextGap = AqLayout.ButtonGapLg,
    ),
    ExtraLarge(
        visualHeight = AqLayout.ButtonHeightXl,
        horizontalPadding = AqLayout.ButtonPaddingHorizontalXl,
        iconSize = AqMetrics.IconSize,
        iconTextGap = AqLayout.ButtonGapXl,
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
): AqButtonColors {
    // MoonBaseControl define el estado pressed mediante su efecto de escala,
    // no mediante un cambio de relleno. PV-01 prohíbe inventar una paleta
    // pressed/active para AqButton.
    if (!enabled) {
        return when (variant) {
            AqButtonVariant.Primary -> AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled)
            AqButtonVariant.Default -> AqButtonColors(scheme.disabled, scheme.textTertiary)
            AqButtonVariant.Danger -> AqButtonColors(scheme.dangerDisabled, scheme.textPrimary)
            AqButtonVariant.Light -> AqButtonColors(scheme.disabled, scheme.textTertiary)
            AqButtonVariant.Outline -> AqButtonColors(
                container = scheme.transparent,
                content = scheme.textTertiary,
                border = scheme.functionalBorder,
            )
            AqButtonVariant.Text -> AqButtonColors(
                container = scheme.transparent,
                content = scheme.textTertiary,
            )
        }
    }
    return when (variant) {
        AqButtonVariant.Primary -> AqButtonColors(scheme.brandFill, scheme.onBrandFill)
        AqButtonVariant.Default -> AqButtonColors(scheme.surface, scheme.textPrimary, scheme.border)
        AqButtonVariant.Danger -> AqButtonColors(scheme.dangerFill, scheme.onDanger)
        AqButtonVariant.Light -> AqButtonColors(scheme.emphasisSurface, scheme.emphasisText)
        AqButtonVariant.Outline -> AqButtonColors(
            container = scheme.transparent,
            content = scheme.textPrimary,
            border = scheme.functionalBorder,
        )
        AqButtonVariant.Text -> AqButtonColors(
            container = scheme.transparent,
            content = scheme.link,
        )
    }
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
    /** Ocupa el ancho disponible del contenedor padre, sin fijar ancho de pantalla. */
    fullWidth: Boolean = false,
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
        fullWidth = fullWidth,
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
    fullWidth: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val pressScale = if (pressed && enabled && !isLoading && !reducedMotion) AqControlParams().pressEffect else 1f
    val isEnabled = enabled && !isLoading
    val resolved = resolveAqButtonColors(
        variant = variant,
        scheme = LocalAqColorScheme.current,
        enabled = isEnabled,
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
        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
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
        .testTag("aq-button-touch-target")
    Layout(
        modifier = buttonModifier,
        content = {
            Row(
                modifier = Modifier
                    // Capa solo durante la presión: una graphicsLayer
                    // permanente crea un RenderNode que las capturas
                    // Robolectric no invalidan entre escenarios.
                    .then(
                        if (pressScale != 1f) {
                            Modifier.graphicsLayer { scaleX = pressScale; scaleY = pressScale }
                        } else {
                            Modifier
                        },
                    )
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
                    .then(if (isLoading) Modifier.alpha(AqAlpha.Hidden) else Modifier)
                    .testTag("aq-button-visual"),
                verticalAlignment = Alignment.CenterVertically,
                // Contenido centrado: con fullWidth/ancho fijo la fila se
                // estira y sin esto la etiqueta quedaba pegada al inicio.
                horizontalArrangement = Arrangement.spacedBy(
                    size.iconTextGap,
                    Alignment.CenterHorizontally,
                ),
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
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
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
                AqCircularLoader(
                    loaderSize = size.iconSize,
                    color = foreground,
                    backgroundColor = AqColor.Transparent,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { scaleX = pressScale; scaleY = pressScale },
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
            // Moon fija la altura visual del botón (24/32/40/48/56 dp); el
            // botón entero es el objetivo táctil, sin expansión extra.
            val height = row.height.coerceIn(0, constraints.maxHeight)
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

/** Tamaños de [AqSegmentedControl] (Moon `segmented_control_sizes.dart`:
 *  sm altura `sizes.md` / md `sizes.lg`; padding horizontal `x3s`/`x2s`;
 *  hueco interno `x5s`/`x4s`). */
enum class AqSegmentedControlSize(
    val height: Dp,
    val paddingHorizontal: Dp,
    val segmentGap: Dp,
) {
    Sm(AqLayout.SegmentedControlHeightSm, AqLayout.SegmentedControlPaddingHorizontalSm, AqLayout.SegmentedControlGapSm),
    Md(AqLayout.SegmentedControlHeightMd, AqLayout.SegmentedControlPaddingHorizontalMd, AqLayout.SegmentedControlGapMd),
}

/** Control segmentado sobre [MoonSegmentedControl] (`moon_design` 1.1.0,
 *  `widgets/segmented_control`). Pista `gohan`→[AqColor.Surface] con radio
 *  `interactiveMd`, relleno `x5s` y hueco `x5s` entre segmentos; el segmento
 *  seleccionado se eleva a `goku`→[AqColor.Background] con squircle
 *  `interactiveSm`, transición de 200 ms `easeInOutCubic`. Texto
 *  `heading.textDefault` (14 sp semibold) en `textPrimary` en ambos
 *  estados. Mutuamente excluyente. */
@Composable
fun AqSegmentedControl(
    options: List<AqSegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AqSegmentedControlSize = AqSegmentedControlSize.Sm,
) {
    require(options.isNotEmpty()) { "AqSegmentedControl necesita al menos una opción" }
    require(selectedIndex in -1 until options.size) { "AqSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Moon `segmented_control.dart`: el contenedor fija
            // `height = size.height` y el padding `x5s` va dentro; los
            // segmentos ocupan `height - 2×padding` y son el objetivo táctil.
            .height(size.height)
            .background(AqColor.Surface, AqSquircles.InteractiveMd)
            .selectableGroup()
            .padding(AqLayout.SegmentedControlPadding)
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled),
        horizontalArrangement = Arrangement.spacedBy(AqLayout.SegmentedControlOuterGap),
    ) {
        val segmentHeight = size.height - AqLayout.SegmentedControlPadding * 2
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val optionEnabled = enabled
            val segmentColor by animateColorAsState(
                targetValue = when {
                    selected && optionEnabled -> AqColor.Background
                    else -> AqColor.Transparent
                },
                animationSpec = tween(
                    durationMillis = AqMotion.DurationMediumMs,
                    easing = AqMotion.Easings.InOutCubic,
                ),
                label = "aqSegmented$index",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(segmentHeight)
                    .semantics(mergeDescendants = true) {
                        this.selected = selected
                    }
                    .clickable(
                        enabled = optionEnabled,
                        role = Role.RadioButton,
                        onClick = { if (optionEnabled) onSelect(index) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Alto visual Moon (`sizes.md`/`sizes.lg` menos el padding del
                // contenedor); el segmento es el objetivo táctil.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(segmentHeight)
                        .clip(AqSquircles.InteractiveSm)
                        .background(segmentColor, AqSquircles.InteractiveSm),
                    contentAlignment = Alignment.Center,
                ) {
                Row(
                    modifier = Modifier.padding(horizontal = size.paddingHorizontal),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(size.segmentGap),
                ) {
                    option.icon?.let {
                        AqIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AqLayout.SegmentedControlIconSize,
                            tint = option.activeTone?.content() ?: AqColor.TextPrimary,
                        )
                    }
                    BasicText(
                        text = option.label,
                        style = AqTextStyles.HeadingSm.copy(
                            color = option.activeTone?.content() ?: AqColor.TextPrimary,
                        ),
                        maxLines = 1,
                    )
                    option.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    AqColor.SurfaceContainerHigh,
                                    AqSquircles.InteractiveXs,
                                )
                                .defaultMinSize(minWidth = AqSize.X2s)
                                .padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                text = it.toString(),
                                style = AqTextStyles.Tag.copy(color = AqColor.TextPrimary),
                                maxLines = 1,
                            )
                        }
                    }
                }
                }
            }
        }
    }
}

/** Conmutador de favorito: táctil de 48 dp con glifo de Moon y rol
 *  [Role.Checkbox] (sin Material3). */
@Composable
fun AqFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(AqMetrics.ControlMinSize)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!favorite) },
            )
            .semantics {
                this.contentDescription = contentDescription
                toggleableState = if (favorite) ToggleableState.On else ToggleableState.Off
            },
        contentAlignment = Alignment.Center,
    ) {
        AqIcon(
            imageVector = AqIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) AqColor.SelectionIcon else AqColor.TextTertiary,
        )
    }
}
