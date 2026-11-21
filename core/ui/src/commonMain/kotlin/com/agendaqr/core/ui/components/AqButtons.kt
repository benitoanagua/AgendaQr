package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqColorScheme
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqEffects
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqMotion

/**
 * Botón unificado sobre [MoonButton] (`moon_design` 1.1.0,
 * `lib/src/widgets/buttons/button.dart`). Una sola API con variante, tamaño,
 * estados y efectos de foco / escala de Moon. El color y la tipografía vienen
 * de Polaris; la forma, el espacio, el borde y el movimiento, de Moon.
 *
 * Reglas de Moon respetadas:
 * - Cinco tamaños visuales (xs, sm, md, lg, xl) con su altura, relleno
 *   horizontal, hueco ícono–etiqueta, radio y tamaño de ícono propios
 *   (`MoonButtonSizes`).
 * - Radio squircle del borde interactivo de cada tamaño (`MoonBorders`).
 * - Efecto de foco (borde de énfasis), escala al pulsar (0,95) y opacidad de
 *   deshabilitado (0,6) de `MoonBaseControl` / `MoonEffectsTheme`.
 * - Área táctil mínima de 48 dp (D-02): cuando el tamaño visual es menor
 *   (xs, sm), se conserva el aspecto y se amplía el área táctil.
 * - Sin ripple: los estados se resuelven en cambio de color de token.
 *
 * La API pública histórica (`AqPrimaryButton`, `AqSecondaryButton`,
 * `AqOutlineButton`, `AqDangerButton`, `AqTextAction`, `AqCompactAction`,
 * `AqIconButton`) se conserva como envolturas delegadoras durante la
 * migración de pantallas; se retiran en el cierre del viraje.
 */
enum class AqButtonVariant { Filled, Outlined, Text, Primary, Default, Danger, Light, Outline }

/** Variantes históricas (Primary/Default/Danger/Light/Outline) mapeadas a las de Moon. */
internal fun AqButtonVariant.asMoonVariant(): AqButtonVariant = when (this) {
    AqButtonVariant.Primary, AqButtonVariant.Danger -> AqButtonVariant.Filled
    AqButtonVariant.Default, AqButtonVariant.Outline -> AqButtonVariant.Outlined
    AqButtonVariant.Light -> AqButtonVariant.Text
    else -> this
}

enum class AqButtonSize(
    val visualHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val gap: Dp,
    val corner: Shape,
) {
    Xs(
        visualHeight = AqSize.Xs,
        horizontalPadding = AqSize.X4s,
        iconSize = AqSize.X2s,
        gap = AqSize.X5s,
        corner = AqSquircles.InteractiveXs,
    ),
    Sm(
        visualHeight = AqSize.Sm,
        horizontalPadding = AqSize.X3s,
        iconSize = AqSize.Xs,
        gap = AqSize.X5s,
        corner = AqSquircles.InteractiveSm,
    ),
    Md(
        visualHeight = AqSize.Md,
        horizontalPadding = AqSize.X2s,
        iconSize = AqSize.Xs,
        gap = AqSize.X4s,
        corner = AqSquircles.InteractiveSm,
    ),
    Lg(
        visualHeight = AqSize.Lg,
        horizontalPadding = AqSize.X2s,
        iconSize = AqSize.Xs,
        gap = AqSize.X3s,
        corner = AqSquircles.InteractiveSm,
    ),
    Xl(
        visualHeight = AqSize.Xl,
        horizontalPadding = AqSize.Xs,
        iconSize = AqSize.Xs,
        gap = AqSize.X2s,
        corner = AqSquircles.InteractiveMd,
    );
}

/** Colores resueltos por variante, estado y tema (Polaris). */
internal data class AqButtonColors(
    val container: Color,
    val content: Color,
    val border: Color? = null,
)

/** Resuelve los colores del botón para el estado actual. */
internal fun resolveAqButtonColors(
    scheme: AqColorScheme,
    variant: AqButtonVariant,
    enabled: Boolean,
    pressed: Boolean,
    focused: Boolean,
): AqButtonColors {
    val moonVariant = variant.asMoonVariant()
    return when (moonVariant) {
        AqButtonVariant.Filled -> when {
            !enabled -> AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled)
            pressed -> AqButtonColors(scheme.brandFillActive, scheme.onBrandFill)
            focused -> AqButtonColors(scheme.brandFillHover, scheme.onBrandFill)
            else -> AqButtonColors(scheme.brandFill, scheme.onBrandFill)
        }
        AqButtonVariant.Outlined -> when {
            !enabled -> AqButtonColors(scheme.transparent, scheme.onDisabled, scheme.border)
            pressed -> AqButtonColors(scheme.surfaceActive, scheme.textPrimary, scheme.border)
            focused -> AqButtonColors(scheme.transparent, scheme.textPrimary, scheme.focus)
            else -> AqButtonColors(scheme.transparent, scheme.textPrimary, scheme.border)
        }
        AqButtonVariant.Text -> when {
            !enabled -> AqButtonColors(scheme.transparent, scheme.onDisabled)
            pressed -> AqButtonColors(scheme.surfaceActive, scheme.emphasisFill)
            focused -> AqButtonColors(scheme.transparent, scheme.emphasisFill, scheme.focus)
            else -> AqButtonColors(scheme.transparent, scheme.link)
        }
        else -> AqButtonColors(scheme.transparent, scheme.textPrimary)
    }
}

/**
 * Botón Moon. [onClick] nulo deshabilita el control (semántica
 * `enabled = false`), como `MoonButton` (habilitado solo si hay `onTap`).
 *
 * - [variant]: `Filled` (relleno de marca), `Outlined` (contorneado), `Text`
 *   (solo texto).
 * - [size]: `Xs` … `Xl`; el tamaño visual es el de Moon y el área táctil se
 *   eleva a 48 dp cuando el visual es menor.
 * - [isLoading]: oculta la etiqueta y muestra el indicador de carga de Moon.
 * - [busyDescription]: `stateDescription` anunciado mientras carga.
 */
@Composable
fun AqButton(
    label: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    variant: AqButtonVariant = AqButtonVariant.Filled,
    size: AqButtonSize = AqButtonSize.Lg,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** `stateDescription` anunciado mientras el botón carga. */
    busyDescription: String? = null,
    icon: ImageVector? = null,
    /** Contenido adicional tras la etiqueta (p. ej. chevron). */
    trailing: (@Composable () -> Unit)? = null,
    /** Color de contenido explícito; anula el del tema para la variante. */
    contentColor: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val isEnabled = enabled && onClick != null && !isLoading
    val colors = resolveAqButtonColors(
        LocalAqColorScheme.current, variant, isEnabled, pressed, focused,
    )
    val foreground = contentColor?.takeIf { isEnabled } ?: colors.content

    val containerColor by animateColorAsState(
        targetValue = if (isLoading) Color.Transparent else colors.container,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isEnabled) 1f else AqStateAlpha.Disabled,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
    )

    val clickModifier = Modifier.clickable(
        enabled = isEnabled,
        role = Role.Button,
        onClickLabel = label,
        interactionSource = interactionSource,
        indication = null,
        onClick = { onClick?.invoke() },
    )
    val buttonModifier = modifier
        .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = AqMetrics.ControlMinSize)
        .then(clickModifier)

    Box(
        modifier = buttonModifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size.visualHeight)
                .clip(size.corner)
                .background(containerColor, size.corner)
                .then(if (variant == AqButtonVariant.Outlined && isEnabled) {
                    Modifier.border(
                        AqStroke.Default,
                        colors.border ?: AqColor.Border,
                        size.corner,
                    )
                } else {
                    Modifier
                })
                .then(if (focused && isEnabled) {
                    Modifier.border(
                        AqStroke.Active,
                        AqColor.Focus,
                        size.corner,
                    )
                } else {
                    Modifier
                })
                .then(if (isLoading) Modifier.alpha(0f) else Modifier)
                .padding(horizontal = size.horizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(size.gap),
            ) {
                icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = size.iconSize,
                        tint = foreground.copy(alpha = contentAlpha),
                    )
                }
                if (!isLoading) {
                    Text(
                        text = label,
                        style = AqTextStyles.ButtonLabel,
                        color = foreground.copy(alpha = contentAlpha),
                    )
                }
                trailing?.invoke()
            }
            if (isLoading) {
                AqCircularLoader(
                    loaderSize = size.iconSize,
                    contentDescription = busyDescription,
                    color = foreground.copy(alpha = contentAlpha),
                )
            }
        }
    }
}

/** @deprecated Usa [AqButton] con [AqButtonVariant.Filled]. */
@Composable
fun AqPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Lg,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Filled,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
    )
}

/** @deprecated Usa [AqButton] con [AqButtonVariant.Outlined] o [AqButtonVariant.Text]. */
@Composable
fun AqSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    solid: Boolean = false,
    size: AqButtonSize = AqButtonSize.Md,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = if (solid) AqButtonVariant.Filled else AqButtonVariant.Text,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        icon = icon,
    )
}

/** @deprecated Usa [AqButton] con [AqButtonVariant.Outlined]. */
@Composable
fun AqOutlineButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Md,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Outlined,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        icon = icon,
    )
}

/** @deprecated Usa [AqButton] con [AqButtonVariant.Filled] (peligro por el llamador). */
@Composable
fun AqDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Lg,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Filled,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
    )
}

/** Variante visual de [AqTextAction]: acento (por defecto), neutra o destructiva. */
enum class AqTextActionVariant { Default, Neutral, Danger }

/** @deprecated Usa [AqButton] con [AqButtonVariant.Text]. */
@Composable
fun AqTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    color: Color? = null,
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(
        horizontal = com.agendaqr.core.ui.theme.AqSpacing.Md,
        vertical = com.agendaqr.core.ui.theme.AqSpacing.None,
    ),
    variant: AqTextActionVariant = AqTextActionVariant.Default,
    size: AqButtonSize = AqButtonSize.Lg,
) {
    val actionColor = color ?: when (variant) {
        AqTextActionVariant.Default -> AqColor.Link
        AqTextActionVariant.Neutral -> AqColor.TextPrimary
        AqTextActionVariant.Danger -> AqColor.Danger
    }
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Text,
        size = size,
        enabled = enabled,
        icon = icon,
        contentColor = actionColor,
    )
}

/** @deprecated Usa [AqButton] (tamaño visual Moon, área táctil 48 dp). */
@Composable
fun AqCompactAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    prominent: Boolean = false,
    color: Color? = null,
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Text,
        size = AqButtonSize.Sm,
        enabled = true,
        icon = icon,
        contentColor = color,
    )
}

/** @deprecated Usa [AqIconButton]. */
@Composable
fun AqIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val description = contentDescription
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = description
            },
        enabled = enabled,
    ) {
        content()
    }
}

/**
 * Opción del control segmentado: etiqueta SIEMPRE (señal de texto), glifo
 * decorativo opcional y badge de conteo opcional.
 */
data class AqSegmentedOption(
    val label: String,
    val icon: ImageVector? = null,
    /** Badge de conteo pequeño (p. ej. "5"). */
    val count: Int? = null,
    /** Color de TEXTO de la opción activa; null = acento del sistema. */
    val activeTone: com.agendaqr.core.ui.components.AqTone? = null,
)

/**
 * Control segmentado sobre [MoonSegmentedControl]
 * (`moon_design` 1.1.0, `lib/src/widgets/segmented_control`). Pista
 * `SurfaceSecondary` con la opción activa sobre píldora, radios interactivos
 * y mutua exclusividad por semántica (`selectableGroup` + rol radio). Cada
 * opción conserva el objetivo táctil de 48 dp. `[selectedIndex] = -1` indica
 * ninguna opción activa (p. ej. atajos Hoy/Ayer con otra fecha).
 */
@Composable
fun AqSegmentedControl(
    options: List<AqSegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    /** Cuando falso, las opciones no son seleccionables y se muestran
     *  con el estilo deshabilitado. */
    enabled: Boolean = true,
) {
    require(options.isNotEmpty()) { "AqSegmentedControl necesita al menos una opción" }
    require(selectedIndex in -1 until options.size) { "AqSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary, AqSquircles.InteractiveSm)
            .selectableGroup()
            .padding(AqSize.X5s),
        horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val optionEnabled = enabled
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(AqSquircles.InteractiveSm)
                    .then(
                        if (selected && optionEnabled) {
                            Modifier.background(AqColor.SelectionSurface, AqSquircles.InteractiveSm)
                                .border(AqStroke.Default, AqColor.Border, AqSquircles.InteractiveSm)
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
                val activeColor = option.activeTone?.content() ?: AqColor.EmphasisText
                val textColor = when {
                    !optionEnabled -> AqColor.OnDisabled
                    selected -> activeColor
                    else -> AqColor.TextSecondary
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
                ) {
                    option.icon?.let {
                        AqIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AqMetrics.IconSizeCompact,
                            tint = if (selected && optionEnabled) activeColor else AqColor.OnDisabled,
                        )
                    }
                    Text(
                        option.label,
                        style = AqTextStyles.Label,
                        color = textColor,
                        maxLines = 1,
                    )
                    option.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected && optionEnabled) activeColor.copy(alpha = AqAlpha.SelectedOnTone)
                                    else AqColor.SurfaceContainerHighest,
                                    AqSquircles.SurfaceSm,
                                )
                                .padding(horizontal = AqSize.Xs),
                        ) {
                            Text(
                                it.toString(),
                                style = AqTextStyles.Tag,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Conmutador de favorito: `IconButton` con glifo de Polaris. El estado
 * seleccionado se expone en la semántica y la descripción accesible la
 * aporta el llamador (debe cambiar con el estado).
 */
@Composable
fun AqFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    /** nombre accesible del control, copy del llamador. */
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val description = contentDescription
    IconButton(
        onClick = { onCheckedChange(!favorite) },
        modifier = modifier
            .size(AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = description
                stateDescription = if (favorite) "Quitar favorito" else "Marcar favorito"
            },
        enabled = true,
    ) {
        AqIcon(
            imageVector = com.agendaqr.core.ui.components.AqIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) AqColor.SelectionIcon else AqColor.TextTertiary,
        )
    }
}
