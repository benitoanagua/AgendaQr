package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
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
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmColorScheme
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.LocalAmColorScheme

enum class AmButtonVariant { Primary, Default, Danger, Light, Outline, Text }

enum class AmButtonSize(
    val visualHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
) {
    Large(
        visualHeight = AmMetrics.ButtonLargeHeight,
        horizontalPadding = AmMetrics.ButtonLargeHorizontalPadding,
        iconSize = AmMetrics.IconSize,
    ),
    Medium(
        visualHeight = AmMetrics.ButtonCompactHeight,
        horizontalPadding = AmSpacing.Lg,
        iconSize = AmMetrics.ButtonMediumIconSize,
    ),
    Small(
        visualHeight = AmMetrics.ButtonSmallHeight,
        horizontalPadding = AmSpacing.Md,
        iconSize = AmMetrics.IconSizeCompact,
    ),
    ExtraSmall(
        visualHeight = AmMetrics.ButtonExtraSmallHeight,
        horizontalPadding = AmSpacing.Sm,
        iconSize = AmMetrics.IconSizeCompact,
    ),
}

internal data class AmButtonColors(
    val container: Color,
    val content: Color,
    val border: Color? = null,
)

internal fun resolveAmButtonColors(
    variant: AmButtonVariant,
    scheme: AmColorScheme,
    enabled: Boolean,
    pressed: Boolean,
): AmButtonColors = when (variant) {
    AmButtonVariant.Primary -> when {
        !enabled -> AmButtonColors(scheme.accentDisabled, scheme.textPrimary)
        pressed -> AmButtonColors(scheme.accentActive, scheme.accentActiveContent)
        else -> AmButtonColors(scheme.accent, scheme.onAccent)
    }
    AmButtonVariant.Default -> when {
        !enabled -> AmButtonColors(scheme.disabled, scheme.textTertiary)
        pressed -> AmButtonColors(scheme.componentActive, scheme.textPrimary)
        else -> AmButtonColors(scheme.surfaceContainerHigh, scheme.textPrimary)
    }
    AmButtonVariant.Danger -> when {
        !enabled -> AmButtonColors(scheme.dangerDisabled, scheme.textPrimary)
        pressed -> AmButtonColors(scheme.dangerActive, scheme.onDanger)
        else -> AmButtonColors(scheme.dangerFill, scheme.onDanger)
    }
    AmButtonVariant.Light -> when {
        !enabled -> AmButtonColors(scheme.disabled, scheme.textTertiary)
        pressed -> AmButtonColors(scheme.accentLightActive, scheme.accentText)
        else -> AmButtonColors(scheme.accentContainer, scheme.accentText)
    }
    AmButtonVariant.Outline -> AmButtonColors(
        container = if (pressed && enabled) scheme.surfaceActive else scheme.transparent,
        content = if (enabled) scheme.textPrimary else scheme.textTertiary,
        border = scheme.functionalBorder,
    )
    AmButtonVariant.Text -> AmButtonColors(
        container = if (pressed && enabled) scheme.surfaceActive else scheme.transparent,
        content = if (enabled) scheme.accentText else scheme.textTertiary,
    )
}

/** TDesign Mobile button with a 48 dp interaction target and compact visual size. */
@Composable
fun AmButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AmButtonVariant = AmButtonVariant.Primary,
    size: AmButtonSize = AmButtonSize.Large,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
    icon: ImageVector? = null,
    contentColor: Color? = null,
    fontWeight: FontWeight? = null,
    contentPadding: PaddingValues? = null,
) {
    AmButtonImpl(
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
private fun AmButtonImpl(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    variant: AmButtonVariant,
    size: AmButtonSize,
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
    val resolved = resolveAmButtonColors(
        variant = variant,
        scheme = LocalAmColorScheme.current,
        enabled = isEnabled,
        pressed = pressed,
    )
    val foreground = contentColor?.takeIf { isEnabled } ?: resolved.content
    val labelStyle = if (size == AmButtonSize.Small || size == AmButtonSize.ExtraSmall) {
        AmTextStyles.ButtonLabel.copy(
            fontSize = AmType.Body,
            lineHeight = AmType.LineHeightBody,
        )
    } else {
        AmTextStyles.ButtonLabel
    }
    val shape = AmShapes.Small
    val haptics = LocalHapticFeedback.current
    val buttonModifier = modifier
        .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
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
            indication = LocalIndication.current,
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
                        resolved.border?.let { Modifier.border(AmMetrics.Border, it, shape) } ?: Modifier,
                    )
                    .padding(
                        contentPadding
                            ?: PaddingValues(
                                horizontal = size.horizontalPadding,
                                vertical = AmSpacing.None,
                            ),
                    )
                    .then(if (isLoading) Modifier.alpha(0f) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            ) {
                icon?.let {
                    AmIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = size.iconSize,
                        tint = foreground,
                    )
                }
                Text(
                    text = label,
                    style = labelStyle,
                    fontWeight = fontWeight ?: FontWeight.SemiBold,
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
            val height = maxOf(row.height, AmMetrics.ControlMinSize.roundToPx())
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

/** Primary action with the TDesign brand fill and accessible foreground. */
@Composable
fun AmPrimaryButton(
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
    size: AmButtonSize = AmButtonSize.Large,
) {
    AmButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Primary,
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
 * irreversible de información (AmDialog destructive = true).
 */
@Composable
fun AmDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador). */
    busyDescription: String? = null,
    /** Glifo de la consecuencia (decorativo: la etiqueta comunica). */
    icon: ImageVector = AmIcons.Trash,
    size: AmButtonSize = AmButtonSize.Large,
) {
    AmButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Danger,
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
fun AmSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    solid: Boolean = false,
    size: AmButtonSize = AmButtonSize.Medium,
) {
    AmButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = if (solid) AmButtonVariant.Primary else AmButtonVariant.Default,
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
fun AmLightButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    size: AmButtonSize = AmButtonSize.Medium,
) {
    AmButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Light,
        size = size,
        enabled = enabled,
        isLoading = isLoading,
        icon = icon,
    )
}

@Composable
fun AmOutlineButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    size: AmButtonSize = AmButtonSize.Medium,
) {
    AmButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Outline,
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
fun AmCompactAction(
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
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .heightIn(min = AmMetrics.ChipCompactHeight + AmSpacing.Xs)
                .background(AmColor.SurfaceContainerLow, AmShapes.Small),
        )
        Row(
            modifier = Modifier.padding(horizontal = AmSpacing.Sm, vertical = AmSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            icon?.let {
                AmIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = color ?: if (prominent) AmColor.AccentText else AmColor.TextSecondary,
                )
            }
            Text(
                label,
                style = AmTextStyles.BodySmall,
                fontWeight = fontWeight ?: FontWeight.Medium,
                color = color ?: if (prominent) AmColor.AccentText else AmColor.TextSecondary,
            )
        }
    }
}

/**
 * Nivel 3 — Acción terciaria.
 * Alternativas contextuales, navegación auxiliar y acciones de menor prioridad.
 * Acción de texto TDesign con área táctil de 48 dp — la etiqueta
 * completa mantiene su target accesible sin peso visual de botón.
 * [color] lo anula el llamador cuando la acción vive sobre un fondo
 * donde el acento no cumple 4.5:1 (p. ej. dentro de un banner de tono,
 * que hereda el color de contenido del tono — par garantizado).
 */
/** Variante visual de [AmTextAction]: acento (por defecto), neutra o destructiva. */
enum class AmTextActionVariant { Default, Neutral, Danger }

@Composable
fun AmTextAction(
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
    variant: AmTextActionVariant = AmTextActionVariant.Default,
    /** Padding interno. Por defecto el canónico (16 dp
     *  horizontal). Pásale `PaddingValues(AmSpacing.None)` para alinear el
     *  texto al borde de la superficie contenedora. */
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AmSpacing.Md,
        vertical = AmSpacing.None,
    ),
    size: AmButtonSize = AmButtonSize.Large,
) {
    val actionColor = color ?: when (variant) {
        AmTextActionVariant.Default -> AmColor.AccentText
        AmTextActionVariant.Neutral -> AmColor.TextPrimary
        AmTextActionVariant.Danger -> AmColor.Danger
    }
    val fontWeight = when (variant) {
        AmTextActionVariant.Neutral -> FontWeight.SemiBold
        else -> null
    }
    AmButtonImpl(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Text,
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
fun AmIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(AmMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
    ) {
        content()
    }
}

/**
 * Píldora de filtro (cápsula tipo CapsuleTabs de la referencia):
 * inactiva con relleno box (`SurfaceSecondary`) SIN borde; activa sólida
 * de acento (par garantizado). Etiqueta 15; conteo opcional en badge de
 * Tag. Zona interactiva de 48 dp — la superficie visible es menor que el
 * target (mandato de compactación). La selección también vive en la
 * semántica.
 */
@Composable
fun AmFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** Conteo real junto a la etiqueta (p. ej. "Todas · 18"). */
    count: Int? = null,
    /** Glifo decorativo de la píldora (p. ej. favorito). */
    icon: ImageVector? = null,
    /** Descripción de estado accesible (p. ej. "Seleccionado"); copy del
     * llamador. La selección también vive en la semántica `selected`. */
    stateDescription: String? = null,
) {
    // Zona interactiva: 48 dp aunque la píldora visible sea compacta.
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = label,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = AmMetrics.ChipCompactHeight)
                .semantics {
                    this.selected = selected
                    if (selected && stateDescription != null) {
                        this.stateDescription = stateDescription
                    }
                }
                .then(
                    if (selected) {
                        Modifier.background(AmColor.Accent, AmShapes.Pill)
                    } else {
                        Modifier.background(AmColor.SurfaceSecondary, AmShapes.Pill)
                    },
                )
                .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            icon?.let {
                AmIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = if (selected) AmColor.OnAccent else AmColor.TextSecondary,
                )
            }
            Text(
                label,
                style = AmTextStyles.Label,
                color = if (selected) AmColor.OnAccent else AmColor.TextPrimary,
            )
            count?.takeIf { it != 0 }?.let {
                Box(
                    modifier = Modifier
                        .background(
                            if (selected) AmColor.OnAccent.copy(alpha = 0.25f) else AmColor.SurfaceContainerHighest,
                            AmShapes.ExtraSmall,
                        )
                        .padding(horizontal = AmSpacing.Xs),
                ) {
                    Text(
                        it.toString(),
                        style = AmTextStyles.Tag,
                        color = if (selected) AmColor.OnAccent else AmColor.TextSecondary,
                    )
                }
            }
        }
    }
}

/**
 * Opción del control segmentado: etiqueta SIEMPRE (es la señal de texto),
 * glifo decorativo opcional, badge de conteo opcional y tono del estado
 * activo opcional (p. ej. cobro en success) — el tono activo comunica el
 * significado del segmento.
 */
data class AmSegmentedOption(
    val label: String,
    val icon: ImageVector? = null,
    /** Badge de conteo pequeño (p. ej. "5"). */
    val count: Int? = null,
    /** Color de TEXTO de la opción activa; null = acento del sistema. */
    val activeTone: AmTone? = null,
)

/**
 * Control segmentado (Segmented de la referencia): pista `SurfaceSecondary`
 * (fill-content) con la opción activa sobre píldora `Surface`, radios 2.
 * Mutuamente excluyente por semántica (`selectableGroup` + rol radio).
 * Cada opción conserva el objetivo táctil de 48 dp.
 */
@Composable
fun AmSegmentedControl(
    options: List<AmSegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(options.isNotEmpty()) { "AmSegmentedControl necesita al menos una opción" }
    require(selectedIndex in options.indices) { "AmSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AmColor.SurfaceSecondary, AmShapes.ExtraSmall)
            .selectableGroup()
            .padding(AmSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val activeColor = option.activeTone?.content() ?: AmColor.AccentText
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(AmShapes.ExtraSmall)
                    .then(
                        if (selected) {
                            Modifier.background(AmColor.Surface, AmShapes.ExtraSmall)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = { onSelect(index) },
                    )
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                ) {
                    option.icon?.let {
                        AmIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AmMetrics.IconSizeCompact,
                            tint = if (selected) activeColor else AmColor.TextSecondary,
                        )
                    }
                    Text(
                        option.label,
                        style = AmTextStyles.Label,
                        color = if (selected) activeColor else AmColor.TextSecondary,
                        maxLines = 1,
                    )
                    option.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected) activeColor.copy(alpha = 0.2f) else AmColor.SurfaceContainerHighest,
                                    AmShapes.ExtraSmall,
                                )
                                .padding(horizontal = AmSpacing.Xs),
                        ) {
                            Text(
                                it.toString(),
                                style = AmTextStyles.Tag,
                                color = if (selected) activeColor else AmColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Conmutador de favorito: `IconToggleButton` M3 con la estrella de
 * Lucide. El estado seleccionado se expone en la semántica del control y
 * la descripción accesible la aporta el llamador (debe cambiar con el
 * estado). El color acompaña; nunca es la única señal.
 */
@Composable
fun AmFavoriteToggle(
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
            .size(AmMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
    ) {
        AmIcon(
            imageVector = AmIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) AmColor.Accent else AmColor.TextTertiary,
        )
    }
}
