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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaSpacing
import kotlinx.coroutines.delay

/**
 * Xauxa checklist KMP §05 — Live Tile real: rota sola entre 2+ "caras" del
 * mismo tile, en vez del número estático que documentaba Foundations hasta
 * v10. Adaptado desde `xauxa-design-system/kotlin-compose/XauxaLiveTile.kt`
 * a los tokens propios de `core:ui` (`XauxaColor`/`XauxaMotion`/`XauxaSpacing`
 * en vez de `design.xauxa.tokens.*`) y a [LocalReducedMotion] multiplataforma.
 *
 * - lt1: timer propio (LaunchedEffect + delay), separado a propósito del
 *   refresh de datos del widget — este composable no sabe de dónde salen las
 *   `faces`, solo las rota. El caller decide cuándo cambia el *contenido*;
 *   esto solo decide cuándo cambia la *cara visible*.
 * - lt2: `isVisible` lo controla el caller (ej. el callback de visibilidad de
 *   una lista perezosa). En false, el timer se cancela sin perder `index`.
 * - lt3: usa `XauxaMotion.DurationMediumMs` / `Easings.Standard` — no un
 *   valor propio — así que si el token cambia, este archivo lo hereda sin
 *   tocarse.
 * - lt4: con reducedMotion=true (ver `core.ui.motion.ReducedMotion`) el
 *   cambio de cara es un corte directo (duración 0), no un crossfade
 *   acelerado a casi-cero.
 * - lt5 (qué tiles rotan) sigue siendo una decisión de producto: pasále una
 *   sola `face` a los tiles que no deben rotar y el timer nunca arranca.
 */
@Composable
fun XauxaLiveTile(
    faces: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    intervalMs: Long = 4_000L,
    reducedMotion: Boolean = LocalReducedMotion.current,
) {
    require(faces.isNotEmpty()) { "XauxaLiveTile necesita al menos una cara" }

    var index by remember { mutableIntStateOf(0) }
    val currentFaces = rememberUpdatedState(faces)

    LaunchedEffect(isVisible, currentFaces.value.size) {
        if (!isVisible || currentFaces.value.size < 2) return@LaunchedEffect
        while (true) {
            delay(intervalMs)
            index = (index + 1) % currentFaces.value.size
        }
    }

    // Si `faces` se achica desde afuera (ej. de 3 a 1 caras) el índice
    // guardado podría quedar fuera de rango un frame — se acota acá en vez
    // de confiar en que el caller siempre lo resetee.
    val safeIndex = index.coerceIn(0, faces.lastIndex)
    val transitionDurationMs = if (reducedMotion) 0 else XauxaMotion.DurationMediumMs
    val easing = XauxaMotion.Easings.Standard

    Box(
        modifier = modifier
            .background(XauxaColor.Surface)
            .padding(XauxaSpacing.Lg),
    ) {
        AnimatedContent(
            targetState = safeIndex,
            transitionSpec = {
                fadeIn(tween(transitionDurationMs, easing = easing)) togetherWith
                    fadeOut(tween(transitionDurationMs, easing = easing))
            },
            label = "xauxa_live_tile",
        ) { i ->
            faces[i]()
        }
    }
}
