package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Interruptor sobre [MoonSwitch] (`moon_design` 1.1.0, `widgets/switch`).
 * Control binario de efecto inmediato (ajustes). No usa `Switch` de Material
 * 3: se compone con piezas Moon y tokens.
 *
 * - Tamaños: x2s 16 / xs 24 / sm 32 dp de alto (`switch_sizes.dart`).
 * - Pista: 32 / 44 / 60 dp de ancho; relleno 2 / 4 / 4 dp.
 * - Pulgar: 12 / 16 / 24 dp de diámetro con sombra `shadows.sm`.
 * - Colores (`switch_theme.dart`): pista activa `piccolo`→[AqColor.BrandFill],
 *   inactiva `beerus`→[AqColor.Border], pulgar `goten`→[AqColor.OnBrandFill],
 *   icono del pulgar `popo`→[AqColor.Popo], texto activo `goten`, inactivo
 *   `bulma`→[AqColor.TextPrimary].
 * - Transición 200 ms `easeInOutCubic` (tokens.transitions por defecto de
 *   Moon); el pulgar se desliza y las etiquetas de pista hacen cross-fade.
 * - Slots de contenido: [activeTrackWidget]/[inactiveTrackWidget] (mitades de
 *   la pista, estilo `body.text6/8/10` según tamaño) y
 *   [activeThumbWidget]/[inactiveThumbWidget] (dentro del pulgar, color
 *   `popo`). Por defecto vacíos, como en Moon.
 * - Deshabilitado: opacidad 0.6 sobre todo el control (MoonOpacities.disabled).
 * - Área táctil ≥ 48 dp sin cambiar el aspecto ([AqTouchTarget],
 *   ADR-0039 §4.4). El nodo accesible expone `Role.Switch` y `selected`.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
    size: AqSwitchSize = AqSwitchSize.Sm,
    activeTrackWidget: (@Composable () -> Unit)? = null,
    inactiveTrackWidget: (@Composable () -> Unit)? = null,
    activeThumbWidget: (@Composable () -> Unit)? = null,
    inactiveThumbWidget: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val fraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqSwitch",
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) AqColor.BrandFill else AqColor.Border,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqSwitchTrack",
    )
    // Recorrido horizontal del pulgar: ancho de pista - pulgar - 2 × relleno.
    val thumbTravel = size.trackWidth - size.thumb - size.trackPadding * 2
    val thumbOffset by animateDpAsState(
        targetValue = thumbTravel * fraction,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqSwitchThumb",
    )

    // Sin etiqueta el control es la pista sola con la geometría exacta de
    // Moon (`switch_sizes.dart`: 32×16 / 44×24 / 60×32). Con etiqueta, el
    // objetivo táctil crece a 48 dp sin deformar la pista (ADR-0039 §4.4).
    val hasLabel = label.isNotBlank() || description != null
    Box(
        modifier = modifier
            .then(
                if (hasLabel) {
                    Modifier.defaultMinSize(
                        minWidth = AqMetrics.ControlMinSize,
                        minHeight = AqMetrics.ControlMinSize,
                    )
                } else {
                    Modifier.size(size.trackWidth, size.height)
                },
            )
            .clickable(
                enabled = state.enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics {
                contentDescription = description ?: label
                selected = checked
            }
            .alpha(if (state.enabled) 1f else AqStateAlpha.Disabled),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.size(size.trackWidth, size.height),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                if (hasLabel) AqSize.X4s else AqSize.X6s,
            ),
        ) {
            Box(
                modifier = Modifier
                    .size(size.trackWidth, size.height)
                    .background(trackColor, AqSquircles.Circle)
                    .padding(size.trackPadding),
            ) {
                // Mitades de pista con los slots de Moon: activa a la
                // izquierda (aparece al final de la transición), inactiva a
                // la derecha (desaparece al inicio).
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .alpha(fraction),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        // Moon envuelve el slot en DefaultTextStyle + IconTheme
                        // (`body.text6/8/10`, color goten/bulma); los widgets
                        // `Aq*` llevan estilo y tinte explícitos, así que el
                        // llamador fija el color del contenido del slot.
                        Box { activeTrackWidget?.invoke() }
                    }
                    Box(modifier = Modifier.size(size.trackPadding))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .alpha(1f - fraction),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Box { inactiveTrackWidget?.invoke() }
                    }
                }
                // Pulgar deslizante con sombra `shadows.sm` de Moon.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = thumbOffset)
                        .size(size.thumb)
                        .aqShadow(AqShadow.Sm, AqSquircles.Circle)
                        .background(AqColor.OnBrandFill, AqSquircles.Circle),
                    contentAlignment = Alignment.Center,
                ) {
                    Box {
                        (if (checked) activeThumbWidget else inactiveThumbWidget)
                            ?.invoke()
                    }
                }
            }
            if (hasLabel) {
                Column(modifier = Modifier) {
                    AqText(
                        text = label,
                        size = AqType.Body,
                        fontWeight = AqWeight.Medium,
                        color = AqColor.TextPrimary,
                    )
                    if (description != null) {
                        AqText(
                            text = description,
                            size = AqType.Caption,
                            color = AqColor.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

/** Tamaños de [AqSwitch] (Moon `switch_sizes.dart`). */
enum class AqSwitchSize(
    val height: Dp,
    val trackWidth: Dp,
    val thumb: Dp,
    val trackPadding: Dp,
) {
    X2s(
        AqLayout.SwitchHeightX2s,
        AqLayout.SwitchTrackWidthX2s,
        AqLayout.SwitchThumbX2s,
        AqLayout.SwitchTrackPaddingX2s,
    ),
    Xs(
        AqLayout.SwitchHeightXs,
        AqLayout.SwitchTrackWidthXs,
        AqLayout.SwitchThumbXs,
        AqLayout.SwitchTrackPaddingXs,
    ),
    Sm(
        AqLayout.SwitchHeightSm,
        AqLayout.SwitchTrackWidthSm,
        AqLayout.SwitchThumbSm,
        AqLayout.SwitchTrackPaddingSm,
    ),
}
