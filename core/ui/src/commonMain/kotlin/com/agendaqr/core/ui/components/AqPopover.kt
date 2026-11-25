package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqShadow
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
    Box(modifier = modifier) {
        trigger()
        if (open) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, with(androidx.compose.ui.platform.LocalDensity.current) {
                    AqSize.X4s.roundToPx()
                }),
            ) {
                Column(
                    modifier = Modifier
                        .aqShadow(AqShadow.Sm, AqSquircles.InteractiveMd)
                        .background(AqColor.Background, AqSquircles.InteractiveMd)
                        .padding(AqSize.X3s),
                ) {
                    Column(
                        modifier = Modifier
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
