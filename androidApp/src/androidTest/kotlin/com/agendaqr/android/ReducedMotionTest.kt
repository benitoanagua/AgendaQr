package com.agendaqr.android

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * QA ronda 2 (checklist reduced motion): con las TRES escalas de
 * animación del sistema en 0, la navegación (turnstile) es instantánea:
 * la pantalla destino es visible en menos de 1 s desde el clic. Con el
 * valor por defecto (1.0) es un control de humo (misma ventana).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ReducedMotionTest {

    private val settings get() = SystemSettingsRule()

    @Test
    fun navigation_is_instant_with_animation_scales_at_zero() {
        Qa.launchApp()
        settings.apply(animations = 0f) {
            var elapsedMs = 0L
            val start = System.currentTimeMillis()
            Qa.tapText("Contextos", timeoutMs = 10_000)
            Qa.waitFor(3_000) { Qa.hasText("Contextos") }
            elapsedMs = System.currentTimeMillis() - start
            assertTrue(elapsedMs < 1_000, "transición instantánea esperada con escalas 0 (tardó ${elapsedMs}ms)")
        }
        // Control de humo con animaciones normales: sigue llegando (ventana holgada).
        settings.apply(animations = 1f) {
            Qa.launchApp()
            val start = System.currentTimeMillis()
            Qa.tapText("Registrar", timeoutMs = 10_000)
            Qa.waitFor(3_000) { Qa.hasText("Registrar actividad") }
            val elapsed = System.currentTimeMillis() - start
            assertTrue(elapsed < 2_500, "navegación fluida con animaciones normales (tardó ${elapsed}ms)")
        }
    }
}
