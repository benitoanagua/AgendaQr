package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.agendaqr.core.ui.theme.XauxaMotion

/**
 * Xauxa checklist KMP §08 — transición turnstile entre pantallas completas.
 *
 * No es la pantalla completa deslizando sola: la que entra viene desde un
 * offset del ancho total, y la que sale se desplaza una fracción menor en
 * dirección contraria (parallax simple) — eso es lo que distingue "turnstile"
 * de un slide plano.
 *
 * Diferencia a propósito con `xauxa-design-system/kotlin-compose/XauxaTurnstileNav.kt`:
 * el ZIP tipa esto contra `AnimatedContentTransitionScope<NavBackStackEntry>`
 * (navigation-compose), pero Agenda QR no tiene esa dependencia — la
 * navegación actual es un `when` sobre estado de pantalla en `AgendaQrApp.kt`.
 * Se generaliza a `AnimatedContentTransitionScope<S>` para poder usarlo hoy
 * con `AnimatedContent(targetState = screen) { ... }` sin agregar
 * navigation-compose, y seguiría sirviendo si el proyecto la adopta más
 * adelante (el lambda de `enterTransition` de `NavHost` es exactamente esa
 * especialización con `S = NavBackStackEntry`).
 *
 * - Offset de contenido real, no solo fade: la pantalla entrante viene del
 *   borde y la saliente se desplaza una fracción en dirección contraria.
 * - Predictive back de Android 14+: el gesto pasa por su propio callback
 *   (`PredictiveBackHandler`) con una animación interactiva que NO usa
 *   estas specs; el turnstile cubre el push/pop programático y el de
 *   [BackHandler]. Probado el botón atrás no implica el gesto — se obvía
 *   en dispositivo físico.
 * - Usa los mismos `XauxaMotion.DurationMediumMs` / `Easings.Standard`
 *   que [XauxaLiveTile], para que no se sienta como dos sistemas de
 *   animación distintos.
 * - ReducedMotion está resuelto aquí: [xauxaReducedMotionEnter]/
 *   [xauxaReducedMotionExit] fijan duración 0 y cero desplazamiento; el
 *   call-site lee `LocalReducedMotion` y elige con [xauxaTurnstileMotion].
 *
 * Uso previsto (pendiente de conectar en `AgendaQrApp.kt`, ver
 * docs/05-design-system/04-component-audit-register.md):
 *
 *   AnimatedContent(
 *       targetState = currentScreen,
 *       transitionSpec = {
 *           xauxaTurnstileEnter(reverse = isBack) togetherWith xauxaTurnstileExit(reverse = isBack)
 *       },
 *   ) { screen -> /* when (screen) { ... } */ }
 */
private const val TURNSTILE_EXIT_OFFSET_FRACTION = 0.25f // grid de página: 1 de 4 columnas

/**
 * Movimiento aplicable en una transición turnstile según el ajuste de
 * movimiento reducido del sistema (§11: reduced motion respetado).
 *
 * - [Turnstile]: desplazamiento completo (comportamiento normal).
 * - [Reduced]: sin desplazamiento; la pantalla cambia en el sitio con un
 *   fade de duración 0 ([XauxaMotion.DurationReducedMs]). La comprensión no
 *   depende de la animación.
 */
enum class XauxaTurnstileMotion {
    Turnstile,
    Reduced,
}

/** Decisión pura y testeable del movimiento de la transición. */
fun xauxaTurnstileMotion(reducedMotion: Boolean): XauxaTurnstileMotion =
    if (reducedMotion) XauxaTurnstileMotion.Reduced else XauxaTurnstileMotion.Turnstile

fun <S> AnimatedContentTransitionScope<S>.xauxaTurnstileEnter(
    reverse: Boolean = false,
): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(XauxaMotion.DurationMediumMs, easing = XauxaMotion.Easings.Standard),
        initialOffsetX = { fullWidth -> (if (reverse) -1 else 1) * fullWidth },
    ) + fadeIn(tween(XauxaMotion.DurationMediumMs, easing = XauxaMotion.Easings.Standard))

fun <S> AnimatedContentTransitionScope<S>.xauxaTurnstileExit(
    reverse: Boolean = false,
): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(XauxaMotion.DurationMediumMs, easing = XauxaMotion.Easings.Standard),
        // La pantalla que sale se mueve una fracción del ancho, no el
        // 100% — es el "offset de contenido" que distingue turnstile de un
        // slide plano.
        targetOffsetX = { fullWidth ->
            (if (reverse) 1 else -1) * (fullWidth * TURNSTILE_EXIT_OFFSET_FRACTION).toInt()
        },
    ) + fadeOut(tween(XauxaMotion.DurationMediumMs, easing = XauxaMotion.Easings.Standard))

/**
 * Variante reduced-motion de la entrada: sin desplazamiento.
 * La pantalla entrante aparece en el sitio (fade de duración 0).
 */
fun <S> AnimatedContentTransitionScope<S>.xauxaReducedMotionEnter(): EnterTransition =
    fadeIn(tween(XauxaMotion.DurationReducedMs, easing = XauxaMotion.Easings.Standard))

/** Variante reduced-motion de la salida: sin desplazamiento. */
fun <S> AnimatedContentTransitionScope<S>.xauxaReducedMotionExit(): ExitTransition =
    fadeOut(tween(XauxaMotion.DurationReducedMs, easing = XauxaMotion.Easings.Standard))
