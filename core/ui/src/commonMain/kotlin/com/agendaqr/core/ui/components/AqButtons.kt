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
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqColorScheme
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme

enum class AqButtonVariant { Primary, Default, Danger, Light, Outline, Text }

enum class AqButtonSize(
    val visualHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
) {
    Large(
        visualHeight = AqMetrics.ButtonLargeHeight,
        horizontalPadding = AqMetrics.ButtonLargeHorizontalPadding,
        iconSize = AqMetrics.IconSize,
    ),
    Medium(
        visualHeight = AqMetrics.ButtonCompactHeight,
        horizontalPadding = AqSpacing.Lg,
        iconSize = AqMetrics.ButtonMediumIconSize,
    ),
    Small(
        visualHeight = AqMetrics.ButtonSmallHeight,
        horizontalPadding = AqSpacing.Md,
        iconSize = AqMetrics.IconSizeCompact,
    ),
    ExtraSmall(
        visualHeight = AqMetrics.ButtonExtraSmallHeight,
        horizontalPadding = AqSpacing.Sm,
        iconSize = AqMetrics.IconSizeCompact,
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

/** Polaris button with a 48 dp interaction target and compact visual size. */
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
    val shape = AqShapes.Medium
    val haptics = LocalHapticFeedback.current
    val buttonModifier = modifier
        .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
        .clip(shape)
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
            onClickLabel = label,
            interactionSource = interactionSource,
            // Sin ripple: el estado presionado se resuelve en `resolved`
            // y se refleja como cambio de color del token.
            indication = null,
            onClick = {
                if (hapticFeedback) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
        )
    Layout(
        modifier = buttonModifier,
        content = {
            Row(
                modifier = Modifier
                    .heightIn(min = size.visualHeight)
                    .clip(shape)
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
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            ) {
                icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = size.iconSize,
                        tint = foreground,
                    )
                }
                Text(
                    text = label,
                    style = labelStyle,
                    fontWeight = fontWeight ?: AqWeight.SemiBold,
                    color = foreground,
                )
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
            val height = maxOf(row.height, AqMetrics.ControlMinSize.roundToPx())
                .coerceIn(constraints.minHeight, constraints.maxHeight)
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

/** Primary action with the Polaris brand fill and accessible foreground. */
@Composable
fun AqPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador). */
    busyDescription: String? = null,
    /** Glifo decorativo: la etiqueta visible sigue siendo el nombre
     * accesible (contrato de accesibilidad). */
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Large,
) {
    AqButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Primary,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
        contentColor = null,
        fontWeight = null,
        contentPadding = null,
        hapticFeedback = true,
    )
}

/**
 * Nivel 4 — Acción destructiva o excepcional.
 * Descartar, eliminar o realizar una operación con consecuencias especiales.
 * NO debe competir visualmente con la acción primaria: usa el tono de peligro
 * y el contenedor correspondientes,
 * con glifo (papelera/aviso por defecto) y la etiqueta específica de
 * consecuencia del llamador (el color nunca es la única señal).
 * Requiere confirmación cuando la acción pueda provocar una pérdida
 * irreversible de información (AqDialog destructive = true).
 */
@Composable
fun AqDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador). */
    busyDescription: String? = null,
    /** Glifo de la consecuencia (decorativo: la etiqueta comunica). */
    icon: ImageVector = AqIcons.Trash,
    size: AqButtonSize = AqButtonSize.Large,
) {
    AqButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Danger,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
        contentColor = null,
        fontWeight = null,
        contentPadding = null,
        hapticFeedback = true,
    )
}

/** Secondary action; [solid] preserves the legacy mapping to the primary fill. */
@Composable
fun AqSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    solid: Boolean = false,
    size: AqButtonSize = AqButtonSize.Medium,
) {
    AqButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = if (solid) AqButtonVariant.Primary else AqButtonVariant.Default,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = null,
        icon = icon,
        contentColor = null,
        fontWeight = null,
        contentPadding = null,
        hapticFeedback = false,
    )
}

@Composable
fun AqLightButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Medium,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Light,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        icon = icon,
    )
}

@Composable
fun AqOutlineButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    size: AqButtonSize = AqButtonSize.Medium,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Outline,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        icon = icon,
    )
}

/**
 * Acción compacta de fila/tarjeta (referencia historial: "Compartir",
 * "Ver QR"): píldora tonal de ~36 dp visuales con glifo y etiqueta 13,
 * sobre zona interactiva de 48 dp. [prominent] tiñe el texto de acento.
 */
@Composable
fun AqCompactAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    /** Texto de acento (acción destacada de la tarjeta). */
    prominent: Boolean = false,
    /** Color explícito del texto (p. ej. heredado de un banner de tono). */
    color: Color? = null,
    /** Peso del texto (p. ej. SemiBold para la acción principal del banner). */
    fontWeight: FontWeight? = null,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .heightIn(min = AqMetrics.ChipCompactHeight + AqSpacing.Xs)
                .background(AqColor.SurfaceContainerLow, AqShapes.Medium),
        )
        Row(
            modifier = Modifier.padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = color ?: if (prominent) AqColor.Link else AqColor.TextSecondary,
                )
            }
            Text(
                label,
                style = AqTextStyles.BodySmall,
                fontWeight = fontWeight ?: AqWeight.Medium,
                color = color ?: if (prominent) AqColor.Link else AqColor.TextSecondary,
            )
        }
    }
}

/**
 * Nivel 3 — Acción terciaria.
 * Alternativas contextuales, navegación auxiliar y acciones de menor prioridad.
 * Acción de texto con área táctil de 48 dp — la etiqueta
 * completa mantiene su target accesible sin peso visual de botón.
 * [color] lo anula el llamador cuando la acción vive sobre un fondo
 * donde el acento no cumple 4.5:1 (p. ej. dentro de un banner de tono,
 * que hereda el color de contenido del tono — par garantizado).
 */
/** Variante visual de [AqTextAction]: acento (por defecto), neutra o destructiva. */
enum class AqTextActionVariant { Default, Neutral, Danger }

@Composable
fun AqTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    /** Color explícito (p. ej. heredado de un banner de tono). Tiene
     *  prioridad sobre [variant]. */
    color: Color? = null,
    /** Variante: Default (acento) o Danger (destructiva). Prohíbe el uso de
     *  grises (TextSecondary/TextTertiary) como color de acción. */
    variant: AqTextActionVariant = AqTextActionVariant.Default,
    /** Padding interno. Por defecto el canónico (16 dp
     *  horizontal). Pásale `PaddingValues(AqSpacing.None)` para alinear el
     *  texto al borde de la superficie contenedora. */
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AqSpacing.Md,
        vertical = AqSpacing.None,
    ),
    size: AqButtonSize = AqButtonSize.Large,
) {
    val actionColor = color ?: when (variant) {
        AqTextActionVariant.Default -> AqColor.Link
        AqTextActionVariant.Neutral -> AqColor.TextPrimary
        AqTextActionVariant.Danger -> AqColor.Danger
    }
    val fontWeight = when (variant) {
        AqTextActionVariant.Neutral -> AqWeight.SemiBold
        else -> null
    }
    AqButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Text,
        size = size,
        enabled = enabled,
        isLoading = false,
        busyDescription = null,
        icon = icon,
        contentColor = actionColor,
        fontWeight = fontWeight,
        contentPadding = contentPadding,
        hapticFeedback = false,
    )
}

/**
 * Botón solo-icono: `IconButton` M3 con descripción accesible y área
 * táctil de 48 dp. El glifo lo aporta el llamador vía [content].
 */
@Composable
fun AqIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(AqMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
    ) {
        content()
    }
}

/**
 * Opción del control segmentado: etiqueta SIEMPRE (es la señal de texto),
 * glifo decorativo opcional, badge de conteo opcional y tono del estado
 * activo opcional (p. ej. cobro en success) — el tono activo comunica el
 * significado del segmento.
 */
data class AqSegmentedOption(
    val label: String,
    val icon: ImageVector? = null,
    /** Badge de conteo pequeño (p. ej. "5"). */
    val count: Int? = null,
    /** Color de TEXTO de la opción activa; null = acento del sistema. */
    val activeTone: AqTone? = null,
)

/**
 * Control segmentado (Segmented de la referencia): pista `SurfaceSecondary`
 * (fill-content) con la opción activa sobre píldora `Surface`, radios 2.
 * Mutuamente excluyente por semántica (`selectableGroup` + rol radio).
 * Cada opción conserva el objetivo táctil de 48 dp.
 * [enabled] controla si las opciones responden a la selección; cuando es
 * falso, el segmento se muestra deshabilitado y no emite onSelect.
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
    // -1 = ningún segmento activo (p. ej. atajos Hoy/Ayer con otra fecha).
    require(selectedIndex in -1 until options.size) { "AqSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary, AqShapes.Medium)
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
                    .clip(AqShapes.Medium)
                    .then(
                        if (selected && optionEnabled) {
                            Modifier.background(AqColor.SelectionSurface, AqShapes.Medium)
                                .border(AqMetrics.Border, AqColor.Border, AqShapes.Medium)
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
                                    if (selected && optionEnabled) activeColor.copy(alpha = AqAlpha.SelectedOnTone) else AqColor.SurfaceContainerHighest,
                                    AqShapes.Small,
                                )
                                .padding(horizontal = AqSpacing.Xs),
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
 * Conmutador de favorito: `IconToggleButton` con glifo de Polaris. El estado
 * seleccionado se expone en la semántica del control y
 * la descripción accesible la aporta el llamador (debe cambiar con el
 * estado). El color acompaña; nunca es la única señal.
 */
@Composable
fun AqFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    /** nombre accesible del control, copy del llamador. */
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
