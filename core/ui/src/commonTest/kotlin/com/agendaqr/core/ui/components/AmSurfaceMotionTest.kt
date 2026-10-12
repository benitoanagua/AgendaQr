package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AmMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * La transición estándar entre superficies respeta el movimiento
 * reducido del sistema (contrato de accesibilidad: "reduced motion
 * respetado").
 *
 * El contrato comprobable aquí: con reduced motion la transición no
 * aplica desplazamiento y su duración es 0 (cambio en el sitio); sin
 * reduced motion se aplica el fundido con desplazamiento pequeño con la
 * duración corta de AmMotion.
 */
class AmSurfaceMotionTest {

    @Test
    fun reduced_motion_disables_the_surface_displacement() {
        assertEquals(AmSurfaceMotion.Reduced, amSurfaceMotion(reducedMotion = true))
        assertEquals(AmSurfaceMotion.Standard, amSurfaceMotion(reducedMotion = false))
    }

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
    fun the_slide_is_a_small_fraction_of_the_width() {
        // Desplazamiento horizontal PEQUEÑO (1/8 del ancho), no un slide
        // completo de la superficie.
        assertEquals(8, AmSurfaceSlideFraction)
    }
}
