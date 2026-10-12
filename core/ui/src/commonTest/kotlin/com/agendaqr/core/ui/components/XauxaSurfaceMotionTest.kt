package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.XauxaMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * La transición estándar entre superficies respeta el movimiento
 * reducido del sistema (spec §11: "reduced motion respetado").
 *
 * El contrato comprobable aquí: con reduced motion la transición no aplica
 * desplazamiento y su duración es 0 (cambio en el sitio); sin reduced
 * motion se aplica el fundido con desplazamiento pequeño con la duración
 * corta de XauxaMotion.
 */
class XauxaSurfaceMotionTest {

    @Test
    fun reduced_motion_disables_the_surface_displacement() {
        assertEquals(XauxaSurfaceMotion.Reduced, xauxaSurfaceMotion(reducedMotion = true))
        assertEquals(XauxaSurfaceMotion.Standard, xauxaSurfaceMotion(reducedMotion = false))
    }

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        // Sin movimiento: la pantalla cambia en el sitio, no "una animación
        // más corta". La comprensión no depende de la animación.
        assertEquals(0, XauxaMotion.DurationReducedMs)
    }

    @Test
    fun standard_motion_uses_the_short_xauxa_duration() {
        // El comportamiento normal no cambia por la accesibilidad.
        assertTrue(XauxaMotion.DurationShortMs > XauxaMotion.DurationReducedMs)
    }

    @Test
    fun the_slide_is_a_small_fraction_of_the_width() {
        // Desplazamiento horizontal PEQUEÑO (1/8 del ancho), no un slide
        // completo de la superficie (§12 V1.2).
        assertEquals(8, XauxaSurfaceSlideFraction)
    }
}
