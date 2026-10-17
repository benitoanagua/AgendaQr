package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AmMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Contrato de movimiento de las transiciones de superficie (contrato
 * contrato §8): la transición estándar respeta el movimiento
 * reducido del sistema.
 *
 * El contrato comprobable aquí: con reduced motion la transición no
 * aplica desplazamiento y su duración es 0 (cambio en el sitio); sin
 * reduced motion se aplica el fundido con desplazamiento pequeño con la
 * duración corta de AmMotion. Las pantallas leen LocalReducedMotion y
 * eligen la variante reducida o estándar.
 */
class AmSurfaceMotionTest {

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        // Sin movimiento: la pantalla cambia en el sitio, no "una
        // animación más corta". La comprensión no depende de la animación.
        assertEquals(0, AmMotion.DurationReducedMs)
    }

    @Test
    fun standard_motion_uses_the_short_system_duration() {
        // El comportamiento normal no cambia por la accesibilidad.
        assertTrue(AmMotion.DurationShortMs > AmMotion.DurationReducedMs)
    }

    @Test
    fun durations_follow_the_annex_range() {
        // Anexo A: 200–300 ms, con una duración reducida de 0 (cambio en
        // el sitio). La escala es creciente corto → medio → largo.
        assertEquals(200, AmMotion.DurationShortMs)
        assertEquals(260, AmMotion.DurationMediumMs)
        assertEquals(300, AmMotion.DurationLongMs)
        assertTrue(AmMotion.DurationShortMs < AmMotion.DurationMediumMs)
        assertTrue(AmMotion.DurationMediumMs < AmMotion.DurationLongMs)
    }

    @Test
    fun press_scale_is_the_annex_value_and_shimmer_is_bounded() {
        // Escala de pulsación 0.95 del Anexo A; el brillo de entrada dura
        // ≤ 5 s (luego estático): el contenido en movimiento es detenible.
        assertEquals(AmMotion.PressScale, 0.95f)
        assertEquals(5000, AmMotion.ShimmerDurationMs)
    }

    @Test
    fun the_slide_is_a_small_fraction_of_the_width() {
        // Desplazamiento horizontal PEQUEÑO (1/8 del ancho), no un slide
        // completo de la superficie.
        assertEquals(8, AmSurfaceSlideFraction)
    }
}
