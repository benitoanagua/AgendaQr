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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
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
import com.agendaqr.core.ui.theme.AqSquircleShape
import com.agendaqr.core.ui.icons.AqPhosphorIcon
import com.agendaqr.core.ui.icons.vectorForSize

/**
 * Interruptor sobre [MoonSwitch] (`moon_design` 1.1.0, `widgets/switch`).
 * Control binario de efecto inmediato (ajustes). No usa `Switch` de Material
 * 3: se compone con piezas Moon y tokens.
 *
 * - Tamaños: x2s 16 / xs 24 / sm 32 dp de alto (`switch_sizes.dart`).
 * - Pista: 32 / 44 / 60 dp de ancho; relleno 2 / 4 / 4 dp.
 * - Geometría (ADR-0054): pista y pulgar son rectángulos redondeados con
 *   radio `AqLayout.SwitchCornerRadius*` (un cuarto del alto: 4 / 6 / 8 dp),
 *   no la cápsula `radius=999` del spec Moon. El pulgar es un cuadrado de
 *   12 / 16 / 24 dp con el mismo radio, así la esquina del pulgar casa con
 *   la del contorno de la pista.
 * - Colores (`switch_theme.dart`): pista activa `piccolo`→[AqColor.BrandFill],
 *   inactiva `beerus`→[AqColor.Border], pulgar `goten`→[AqColor.OnBrandFill],
 *   icono del pulgar `popo`→[AqColor.Popo], texto activo `goten`, inactivo
 *   `bulma`→[AqColor.TextPrimary]. Excepción documentada (ADR-0051): la pista
 *   inactiva usa [AqColor.FunctionalBorder] porque `beerus` queda a ~1,3:1 de
 *   la superficie (bajo el mínimo de 3:1 para componentes, D-02); la sombra
 *   del pulgar se recorta al contorno interior de la pista para que no
 *   desborde como halo (ADR-0051).
 * - Transición 200 ms `easeInOutCubic` (tokens.transitions por defecto de
 *   Moon); el pulgar se desliza y las etiquetas de pista hacen cross-fade.
 * - Slots de contenido: [activeTrackWidget]/[inactiveTrackWidget] (mitades de
 *   la pista, estilo `body.text6/8/10` según tamaño) y
 *   [activeThumbWidget]/[inactiveThumbWidget] (dentro del pulgar, color
 *   `popo`). Los valores por defecto de `activeThumbWidget` y
 *   `inactiveThumbWidget` son Check/X Phosphor Bold (ADR-0051/0052); no se
 *   configuran por pantalla ni por Lab. Solo se sustituyen si una variante
 *   global del componente lo documenta y se comparte con producción.
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
    activeThumbWidget: (@Composable () -> Unit)? = {
        AqIcon(
            imageVector = AqPhosphorIcon.Check.vectorForSize(AqSize.X3s),
            contentDescription = null,
            size = AqSize.X3s,
            tint = AqColor.Popo,
        )
    },
    inactiveThumbWidget: (@Composable () -> Unit)? = {
        AqIcon(
            imageVector = AqPhosphorIcon.X.vectorForSize(AqSize.X3s),
            contentDescription = null,
            size = AqSize.X3s,
            tint = AqColor.Popo,
        )
    },
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
        // ADR-0051: la pista inactiva usa el límite funcional (trunks,
        // ≥ 3:1 sobre superficie, D-02) en lugar del `beerus` decorativo
        // del spec (~1,3:1). Moon resuelve el apagado con una pista casi
        // invisible; aquí el estado debe identificarse sin ambigüedad.
        targetValue = if (checked) AqColor.BrandFill else AqColor.FunctionalBorder,
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

    // Geometría rectangular redondeada (ADR-0054): squircle con el radio
    // del tamaño, compartida por pista, recorte de sombra y pulgar para
    // que todas las esquinas coincidan.
    val shape = AqSquircleShape(size.cornerRadius)
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
                val switchName = description ?: label
                text = AnnotatedString(switchName)
                contentDescription = switchName
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
                    .background(trackColor, shape)
                    .padding(size.trackPadding)
                    // La sombra del pulgar (`shadows.sm`) se dibuja con blur
                    // que desbordaría la pista: se recorta al contorno
                    // interior (mismo radio de esquina) en las tres
                    // plataformas. El pulgar nunca excede esa zona.
                    .clip(shape),
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
                // Pulgar deslizante con sombra `shadows.sm` de Moon:
                // cuadrado redondeado con el mismo radio que la pista
                // (ADR-0054), no un disco.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = thumbOffset)
                        .size(size.thumb)
                        .testTag("aq-switch-thumb")
                        .aqShadow(AqShadow.Sm, shape)
                        .background(AqColor.OnBrandFill, shape),
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

/** Tamaños de [AqSwitch] (Moon `switch_sizes.dart` + radio ADR-0054). */
enum class AqSwitchSize(
    val height: Dp,
    val trackWidth: Dp,
    val thumb: Dp,
    val trackPadding: Dp,
    val cornerRadius: Dp,
) {
    X2s(
        AqLayout.SwitchHeightX2s,
        AqLayout.SwitchTrackWidthX2s,
        AqLayout.SwitchThumbX2s,
        AqLayout.SwitchTrackPaddingX2s,
        AqLayout.SwitchCornerRadiusX2s,
    ),
    Xs(
        AqLayout.SwitchHeightXs,
        AqLayout.SwitchTrackWidthXs,
        AqLayout.SwitchThumbXs,
        AqLayout.SwitchTrackPaddingXs,
        AqLayout.SwitchCornerRadiusXs,
    ),
    Sm(
        AqLayout.SwitchHeightSm,
        AqLayout.SwitchTrackWidthSm,
        AqLayout.SwitchThumbSm,
        AqLayout.SwitchTrackPaddingSm,
        AqLayout.SwitchCornerRadiusSm,
    ),
}
