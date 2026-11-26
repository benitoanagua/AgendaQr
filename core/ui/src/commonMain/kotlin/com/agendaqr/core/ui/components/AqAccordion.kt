package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqTextRoles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.aqShadow

/**
 * Acordeón sobre [MoonAccordion] (`moon_design` 1.1.0,
 * `widgets/accordion` + `accordion_sizes.dart`/`accordion_theme.dart`).
 * Panel expandible/colapsable con cabecera y contenido animado. El estado
 * de expansión lo gestiona el llamador (mutuamente excluyente en grupos).
 *
 * - Contenedor: squircle `interactiveSm`, fondo `goku`→[AqColor.Background]
 *   en todos los estados, sombra `shadows.sm`, borde solo con
 *   [showBorder] (`beerus`→[AqColor.Border]); hover funde `heles` sobre el
 *   fondo. Deshabilitado = opacidad 0.6.
 * - Cabecera: altura fija por tamaño (32/40/48/56), padding horizontal
 *   x4s/x3s/x3s/x2s, texto `heading.text12/default/default/16` semibold en
 *   `textPrimary`, icono chevron `controls_chevron_down_small` que gira
 *   180° al expandir (200 ms `easeInOutCubic`), tamaño x2s/xs/xs/xs.
 * - Contenido: `body.text12/default/default/default`, divisor de 1 dp
 *   `beerus` sobre el contenido cuando [showDivider] (defecto Moon: true),
 *   animación de altura con recorte ([AnimatedVisibility]).
 * - Slots [leading]/[trailing] de Moon; por defecto el trailing es el
 *   chevron. Hueco interno = padding horizontal de la cabecera.
 * - La cabecera mide la altura fija del tamaño y es el objetivo táctil
 *   (Moon `accordion.dart`: `SizedBox(height: headerHeight)`).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAccordion(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    size: AqAccordionSize = AqAccordionSize.Md,
    enabled: Boolean = true,
    showDivider: Boolean = true,
    showBorder: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val hoverAlpha by animateFloatAsState(
        targetValue = if (hovered && enabled) 1f else 0f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqAccordionHover",
    )
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqAccordionChevron",
    )
    val shape = size.shape
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aqShadow(AqShadow.Sm, shape)
            .clip(shape)
            .background(AqColor.Background, shape)
            .then(
                if (showBorder) Modifier.border(
                    AqMetrics.Border,
                    AqColor.Border,
                    shape,
                ) else Modifier,
            )
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled),
    ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera: altura fija del tamaño (Moon `SizedBox(height:
        // headerHeight)`), tappable en toda su área.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(size.headerHeight)
                .clickable(
                    role = Role.Button,
                    onClickLabel = title,
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onToggle,
                )
                .semantics { this.contentDescription = title }
                .padding(horizontal = size.headerPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.let {
                Box(modifier = Modifier.padding(end = size.headerPadding)) { it() }
            }
            AqText(
                text = title,
                size = size.headerTextSize,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
                // `weight(fill = true)`: la etiqueta ocupa el espacio
                // restante y el trailing queda anclado al borde final
                // (Moon `Expanded(label)`); con `fill = false` el icono
                // queda pegado al texto.
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.padding(start = size.headerPadding)) {
                if (trailing != null) {
                    trailing()
                } else {
                    // `controls_chevron_down_small` de Moon: chevron derecho
                    // rotado 90° (v) + giro de expansión de 180°.
                    AqIcon(
                        imageVector = AqIcons.ChevronRight,
                        contentDescription = null,
                        size = size.iconSize,
                        tint = AqColor.TextPrimary,
                        modifier = Modifier.rotate(90f + rotation),
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = AqMotion.DurationMediumMs,
                    easing = AqMotion.Easings.InOutCubic,
                ),
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = AqMotion.DurationMediumMs,
                    easing = AqMotion.Easings.InOutCubic,
                ),
            ) + fadeOut(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showDivider) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AqMetrics.DividerHeight)
                            .background(AqColor.Border),
                    )
                }
                // `childrenPadding` de Moon por defecto es cero: el contenido
                // llega pegado al divisor y el llamador aporta su relleno.
                // Moon envuelve los hijos en `DefaultTextStyle(
                // contentTextStyle)` y `CrossAxisAlignment.center`.
                val contentRole = if (size == AqAccordionSize.Sm) {
                    AqTextRoles.BodyXs
                } else {
                    AqTextRoles.BodySm
                }
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalTextStyle provides
                        androidx.compose.ui.text.TextStyle(
                            fontSize = contentRole.size,
                            lineHeight = contentRole.lineHeight,
                            fontWeight = contentRole.weight,
                            letterSpacing = contentRole.tracking,
                            fontFamily = AqType.FamilyUi,
                            color = AqColor.TextPrimary,
                        ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        content()
                    }
                }
            }
        }
    }
    // Velo de hover de Moon (`primaryHoverColor` = heles translúcido) sobre
    // toda la tarjeta, recortado por el squircle del contenedor.
    Box(
        modifier = Modifier
            .matchParentSize()
            .alpha(hoverAlpha)
            .background(AqColor.Disabled, shape),
    )
    }
}

/** Tamaños de [AqAccordion] (Moon `accordion_sizes.dart`). */
enum class AqAccordionSize(
    val headerHeight: Dp,
    val headerPadding: Dp,
    val iconSize: Dp,
    val headerTextSize: TextUnit,
    val shape: Shape,
) {
    Sm(
        AqSize.Sm,
        AqSize.X4s,
        AqSize.X2s,
        AqType.HeadingXs,
        AqSquircles.InteractiveSm,
    ),
    Md(
        AqSize.Md,
        AqSize.X3s,
        AqSize.Xs,
        AqType.HeadingSm,
        AqSquircles.InteractiveSm,
    ),
    Lg(
        AqSize.Lg,
        AqSize.X3s,
        AqSize.Xs,
        AqType.HeadingSm,
        AqSquircles.InteractiveSm,
    ),
    Xl(
        AqSize.Xl,
        AqSize.X2s,
        AqSize.Xs,
        AqType.HeadingMd,
        AqSquircles.InteractiveSm,
    ),
}
