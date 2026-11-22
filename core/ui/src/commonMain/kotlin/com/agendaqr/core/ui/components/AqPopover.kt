package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Popover sobre [MoonPopover] (`moon_design` 1.1.0,
 * `widgets/popover`). Superficie flotante con contenido auxiliar,
 * anclada a un elemento disparador.
 *
 * - [open]: controla la visibilidad.
 * - [trigger]: contenido que activa el popover.
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
    if (!open) {
        trigger()
        return
    }
    val scheme = LocalAqColorScheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        trigger()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AqColor.Surface, AqSquircles.SurfaceMd)
                .padding(horizontal = AqSpacing.Sm, vertical = AqSize.Xs),
            contentAlignment = Alignment.TopStart,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            ) {
                content()
            }
        }
    }
}
