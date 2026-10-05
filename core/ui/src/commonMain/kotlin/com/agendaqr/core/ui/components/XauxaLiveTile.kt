package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * V1.1 (ADR-0005, spec §12 Movimiento / §11): tile vivo (S01) — muestra el
 * último dato (último QR o actividad reciente) como texto sobre un bloque
 * de acento.
 *
 * - El contenido cambia SOLO cuando cambia [data]; cada cambio realiza UNA
 *   transición. No hay rotación en bucle ni timer: los loops decorativos no
 *   forman parte del lenguaje (invariante Xauxa 6).
 * - Con reduced motion el cambio es un corte directo (duración 0), no un
 *   crossfade acelerado (§11); la comprensión nunca depende de la
 *   animación (el texto del dato siempre está presente).
 *
 * Historia: la versión previa (checklist lt1–lt5) rotaba varias caras con
 * timer propio; esa rotación se retira en V1.1 (M11) y con ella su API de
 * `faces`/`intervalMs`.
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
        modifier = modifier
            .background(XauxaColor.Surface)
            .padding(XauxaSpacing.Lg),
    ) {
        AnimatedContent(
            targetState = data,
            transitionSpec = {
                fadeIn(tween(transitionDurationMs, easing = easing)) togetherWith
                    fadeOut(tween(transitionDurationMs, easing = easing))
            },
            label = "xauxa_live_tile",
        ) { current ->
            content(current)
        }
    }
}
