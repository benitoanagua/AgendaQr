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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
    /** `true` pone la etiqueta a la izquierda de la pista (fila de ajuste
     *  convencional: texto + control al final). `false` (defecto) mantiene
     *  la anatomía pista + etiqueta a la derecha. */
    labelFirst: Boolean = false,
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

    // Sin etiqueta la pista conserva la geometría exacta de Moon
    // (`switch_sizes.dart`: 32×16 / 44×24 / 60×32) centrada en el área
    // táctil; con o sin etiqueta el objetivo crece a 48 dp sin deformar
    // la pista (ADR-0039 §4.4).
    val hasLabel = label.isNotBlank() || description != null
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AqMetrics.ControlMinSize,
                minHeight = AqMetrics.ControlMinSize,
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
            // Sin etiqueta la fila es exactamente la pista de Moon; con
            // etiqueta la fila abraza pista + texto (si no, la etiqueta se
            // medía con el ancho restante de la pista y quedaba invisible).
            // Con [labelFirst] la fila llena el ancho: texto al inicio y
            // control al final (fila de ajuste convencional).
            modifier = when {
                hasLabel && labelFirst -> Modifier.fillMaxWidth()
                hasLabel -> Modifier
                else -> Modifier.size(size.trackWidth, size.height)
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                if (hasLabel) AqSize.X4s else AqSize.X6s,
            ),
        ) {
            val labelContent: @Composable () -> Unit = {
                Column(
                    modifier = if (labelFirst) Modifier.weight(1f) else Modifier,
                ) {
                    // Moon v1: etiqueta 14/24 Regular (gap 8 con la pista).
                    androidx.compose.foundation.text.BasicText(
                        text = label,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = AqType.BodySm,
                            lineHeight = AqType.LineHeightBodyMd,
                            fontFamily = AqType.FamilyUi,
                            fontWeight = AqWeight.Regular,
                            color = AqColor.TextPrimary,
                        ),
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
            if (labelFirst && hasLabel) {
                labelContent()
            }
            Box(
                modifier = Modifier
                    .size(size.trackWidth, size.height)
                    // El tag va antes del padding interno para que sus
                    // bounds midan la pista completa (Moon `switch_sizes`).
                    .testTag("aq-switch-track")
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
            if (hasLabel && !labelFirst) {
                labelContent()
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
