package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaMotion

/**
 * V1.1 (ADR-0005, spec §12 Movimiento / §11): tile vivo (S01) — muestra el
 * último dato (último QR o actividad reciente) como texto sobre un bloque
 * de acento.
 *
 * - El contenido cambia SOLO cuando cambia [data]; cada cambio realiza UNA
 *   transición. No hay rotación en bucle ni timer (invariante Xauxa 6).
 * - Con reduced motion: corte directo (duración 0).
 * - Contrato §11: el Box es una REGIÓN VIVA cortés — el lector anuncia
 *   el cambio del dato. El contenido SALIENTE del crossfade queda OCULTO
 *   a los lectores (clearAndSetSemantics en el slot de salida) para no
 *   leer el valor viejo y el nuevo a la vez. El nombre accesible es el
 *   texto visible fusionado (NO se pisa con contentDescription).
 * - No pinta fondo propio: vive sobre el bloque de acento del tile ancho.
 */
@Composable
fun XauxaLiveTile(
    data: Any?,
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = LocalReducedMotion.current,
    content: @Composable (Any?) -> Unit,
) {
    val transitionDurationMs = if (reducedMotion) {
        XauxaMotion.DurationReducedMs
    } else {
        XauxaMotion.DurationMediumMs
    }
    val easing = XauxaMotion.Easings.Standard

    Box(
        modifier = modifier.semantics {
            liveRegion = LiveRegionMode.Polite
        },
    ) {
        AnimatedContent(
            targetState = data,
            transitionSpec = {
                fadeIn(tween(transitionDurationMs, easing = easing)) togetherWith
                    fadeOut(tween(transitionDurationMs, easing = easing))
            },
            label = "xauxa_live_tile",
        ) { current ->
            // El slot de ENTRADA conserva su semántica normal (el texto
            // visible ES el nombre accesible — no lo pisamos con un
            // contentDescription adicional).
            content(current)
        }
    }
}
