package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Chip seleccionable sobre [MoonChip] (`moon_design` 1.1.0,
 * `widgets/chip` + `chip_theme.dart`/`chip_sizes.dart`).
 *
 * - Tamaños: sm 32 / md 40 dp de alto (`sizes.sm`/`sizes.md`), ancho mínimo
 *   igual al alto (`BoxConstraints(minWidth: height)`).
 * - Radio: interactiveXs (sm) / interactiveSm (md) — esquinas suavizadas,
 *   NO píldora completa.
 * - Colores: reposo fondo `goku`→[AqColor.Background], texto `textPrimary`;
 *   activo fondo `jiren`→[AqColor.Jiren], borde y texto `piccolo`→
 *   [AqColor.BrandFill]. Transición de 200 ms `easeInOutCubic` (hover effect
 *   de Moon usa la transición por defecto).
 * - Geometría: padding horizontal x3s (sm) / x2s (md) solo en los bordes
 *   externos (Moon `correctedPadding`: con leading/trailing el padding
 *   lateral es 0 y separa el hueco `x4s`); hueco interno `x4s`; icono
 *   `sizes.xs`; texto `body.textDefault` (14 sp regular).
 * - [selected] activa el estado; [enabled] controla la interacción.
 *   Área táctil ≥ 48 dp sin cambiar el aspecto (D-02, ADR-0039 §4.4);
 *   deshabilitado = opacidad 0.6 (MoonOpacities.disabled).
 * - [icon] glifo decorativo (slot `leading` de Moon); [count] conteo
 *   opcional anunciado como texto semántico separado del label.
 * - [showBorder]: borde de 1 dp (`defaultBorderWidth`) que Moon solo dibuja
 *   cuando se pide; el activo lo tiñe `piccolo`.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqChip(
    text: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    count: Int? = null,
    modifier: Modifier = Modifier,
    size: AqChipSize = AqChipSize.Md,
    showBorder: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isSelected = selected
    val container by animateColorAsState(
        targetValue = if (isSelected && enabled) AqColor.Jiren else AqColor.Background,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqChipBg",
    )
    val accent by animateColorAsState(
        targetValue = if (isSelected && enabled) AqColor.BrandFill else AqColor.Transparent,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqChipAccent",
    )
    val content by animateColorAsState(
        targetValue = if (isSelected && enabled) AqColor.BrandFill else AqColor.TextPrimary,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqChipContent",
    )
    // PV-01: el estado pressed no cambia el relleno del chip; el efecto es
    // el scale de Moon aplicado por el contenedor.
    //
    // El Row es el único nodo semántico del chip: mergeDescendants = true
    // fusiona el texto del label y del count como elementos de una misma
    // lista, de modo que "Todos" y "12" se leen por separado (nunca
    // "Todos12") y onNodeWithText resuelve cada uno al nodo táctil.
    // Moon fija el alto del chip (32/40 dp) y ese contenedor es el objetivo
    // táctil; no hay expansión extra (Moon `chip.dart` usa el Container como
    // nodo interactivo).
    Row(
        modifier = modifier
            .height(size.height)
            .defaultMinSize(minWidth = size.height)
            .semantics(mergeDescendants = true) {
                this.selected = isSelected
                this[SemanticsProperties.Role] = Role.Button
            }
            .clickable(
                role = Role.Button,
                onClickLabel = text,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onClick != null,
                onClick = { onClick?.invoke() },
            )
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled)
            .background(container, size.shape)
            .then(
                if (showBorder) {
                    Modifier.border(AqStroke.Default, accent, size.shape)
                } else {
                    Modifier
                },
            )
            // Moon `correctedPadding`: el padding horizontal solo aplica
            // en el borde externo cuando no hay slot leading/trailing.
            .padding(
                start = if (icon == null) size.horizontalPadding else AqSpacing.None,
                end = if (count == null) size.horizontalPadding else AqSpacing.None,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqLayout.ChipGap),
    ) {
        icon?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqLayout.ChipIconSize,
                tint = content,
            )
        }
        AqText(
            text = text,
            size = AqType.BodySm,
            color = content,
            modifier = Modifier.weight(1f, fill = false),
        )
        count?.let { count ->
            AqText(
                text = count.toString(),
                size = AqType.BodySm,
                color = content,
            )
        }
    }
}

/** Tamaños de [AqChip] (Moon `chip_sizes.dart`: sm altura `sizes.sm` / md
 *  `sizes.md`; padding horizontal `x3s`/`x2s`; radio interactiveXs/Sm). */
enum class AqChipSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val shape: Shape,
) {
    Sm(AqSize.Sm, AqSize.X3s, AqSquircles.InteractiveXs),
    Md(AqSize.Md, AqSize.X2s, AqSquircles.InteractiveSm),
}
