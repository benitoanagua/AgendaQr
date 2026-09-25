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
 * - ts1: implementado acá — offset de contenido real, no solo fade.
 * - ts3 (regresión con predictive back, tarea bg1): esto NO la resuelve.
 *   Push/pop programático la usa; el gesto de predictive back en Android 14+
 *   pasa por su propio callback (`PredictiveBackHandler`) con una animación
 *   *interactiva* que no usa estas specs. Probar el swipe real antes de dar
 *   por resuelto ts3 — no asumir que "ya está" solo porque el botón atrás se
 *   ve bien.
 * - ts4: usa los mismos `XauxaMotion.DurationMediumMs` / `Easings.Standard`
 *   que [XauxaLiveTile], para que no se sienta como dos sistemas de
 *   animación distintos.
 * - ts2 (iOS) y ts5 (alcance: ¿toda navegación o solo lista↔detalle?) quedan
 *   fuera de este archivo — ts5 en particular es una decisión de producto.
 *   No fija duración 0 en `reducedMotion`: a diferencia de [XauxaLiveTile],
 *   quien lo use debe decidir el intercambio entre desactivar el slide y
 *   solo acortar duración — ver TODO de producto en `AgendaQrApp.kt` cuando
 *   se conecte.
 *
 * Uso previsto (pendiente de conectar en `AgendaQrApp.kt`, ver
 * docs/05-design-system/04-component-audit-register.md, Lote H):
 *
 *   AnimatedContent(
 *       targetState = currentScreen,
 *       transitionSpec = {
 *           xauxaTurnstileEnter(reverse = isBack) togetherWith xauxaTurnstileExit(reverse = isBack)
 *       },
 *   ) { screen -> /* when (screen) { ... } */ }
 */
private const val TURNSTILE_EXIT_OFFSET_FRACTION = 0.25f // grid de página: 1 de 4 columnas

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
        // ts1: la pantalla que sale se mueve una fracción del ancho, no el
        // 100% — es el "offset de contenido" que distingue turnstile de un
        // slide plano.
        targetOffsetX = { fullWidth ->
            (if (reverse) 1 else -1) * (fullWidth * TURNSTILE_EXIT_OFFSET_FRACTION).toInt()
        },
    ) + fadeOut(tween(XauxaMotion.DurationMediumMs, easing = XauxaMotion.Easings.Standard))
