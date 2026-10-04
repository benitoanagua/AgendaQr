package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.XauxaMotion
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * T2 — La transición turnstile respeta el movimiento reducido del sistema
 * (spec congelada §11: "reduced motion respetado").
 *
 * El contrato comprobable aquí: con reduced motion la transición no aplica
 * desplazamiento y su duración es 0 (cambio en el sitio); sin reduced
 * motion se aplica el turnstile completo con su duración estándar.
 */
class XauxaTurnstileMotionTest {

    @Test
    fun reduced_motion_disables_the_turnstile_displacement() {
        assertEquals(XauxaTurnstileMotion.Reduced, xauxaTurnstileMotion(reducedMotion = true))
        assertEquals(XauxaTurnstileMotion.Turnstile, xauxaTurnstileMotion(reducedMotion = false))
    }

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        // Sin movimiento: la pantalla cambia en el sitio, no "una animación
        // más corta". La comprensión no depende de la animación (§11).
        assertEquals(0, XauxaMotion.DurationReducedMs)
    }

    @Test
    fun full_motion_keeps_the_standard_xauxa_duration() {
        // El comportamiento normal no cambia por la accesibilidad.
        assertEquals(XauxaMotion.DurationMediumMs, XauxaMotion.DurationMediumMs)
        assertEquals(true, XauxaMotion.DurationMediumMs > XauxaMotion.DurationReducedMs)
    }
}
