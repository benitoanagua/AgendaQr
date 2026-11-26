package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.aqShadow

/**
 * Popover sobre [MoonPopover] (`moon_design` 1.1.0,
 * `widgets/popover` + `popover_theme.dart`).
 *
 * Superficie flotante anclada al [trigger]: fondo `goku`→
 * [AqColor.Background], radio `interactiveMd`, padding `x3s` (12 dp) en
 * los cuatro lados, sombra `shadows.sm`, separación al ancla
 * `distanceToTarget` = `x4s` (8 dp). Se muestra en un [Popup] sobre el
 * resto de la UI; el cierre lo gestiona el llamador vía [open].
 *
 * Posición y ancho replican Moon (`MoonPopoverPosition.top` por defecto):
 * centrado sobre el ancla con separación `x4s` (8 dp) y
 * `maxWidth = overlayWidth - |overlayWidth/2 - anchorCenterX| * 2 -
 * popoverMargin * 2`; el contenido se ajusta a su ancho intrínseco hasta
 * ese tope y envuelve el texto al alcanzarlo.
 *
 * - [open]: controla la visibilidad.
 * - [trigger]: contenido que ancla el popover.
 * - [content]: contenido del popover.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqPopover(
    open: Boolean,
    trigger: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (anchorPosition, anchorModifier) = rememberAqAnchor()
    val maxWidth = aqAnchorMaxWidthDp(anchorPosition.centerXPx)
    val density = LocalDensity.current
    val gapPx = with(density) { AqAnchorGap.toPx().toInt() }
    Box(modifier = modifier) {
        Box(modifier = anchorModifier) { trigger() }
        if (open) {
            Popup(
                popupPositionProvider = AqCenteredAnchorPositionProvider(
                    gapPx = gapPx,
                    below = false,
                ),
            ) {
                AqPopoverSurface(
                    maxWidth = maxWidth,
                    content = content,
                )
            }
        }
    }
}

/**
 * Tarjeta del popover (fondo, radio, sombra y padding). Separada del
 * [Popup] para poder renderizarla inline en capturas de prueba: las ventanas
 * `Popup` no rasterizan bajo Robolectric.
 */
@Composable
internal fun AqPopoverSurface(
    maxWidth: Dp,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .aqWrapIntrinsicWidth()
            // `testTag` antes de `padding`: las bounds semánticas
            // del tag deben abarcar la tarjeta completa (como el
            // `Container` de Moon), no solo el contenido.
            .testTag("aq-popover-popup")
            .aqShadow(AqShadow.Sm, AqSquircles.InteractiveMd)
            .background(AqColor.Background, AqSquircles.InteractiveMd)
            // Moon v1 «Popover»: padding de contenido 4 dp.
            .padding(AqLayout.PopoverContentPadding),
    ) {
        content()
    }
}
