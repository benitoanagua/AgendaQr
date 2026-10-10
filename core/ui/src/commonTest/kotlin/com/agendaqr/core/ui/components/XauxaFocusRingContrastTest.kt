package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.DarkXauxaColorScheme
import com.agendaqr.core.ui.theme.LightXauxaColorScheme
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaColorScheme
import com.agendaqr.core.ui.theme.contrastRatio
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrato §7 / spec §11: el anillo de foco es VISIBLE (>= 3:1) sobre
 * cada superficie real donde aparece — cada acento de tile, el bloque de
 * marca de la acción primaria de la app bar y las superficies del tema —
 * en claro y oscuro. El overlay de pulsación por defecto contrasta
 * fuertemente con el fondo del tema (>= 7:1): sin esto, sobre fondo negro
 * puro del tema oscuro un overlay negro no cambia ni un píxel.
 */
class XauxaFocusRingContrastTest {
    private val schemes: Map<String, XauxaColorScheme> =
        mapOf("claro" to LightXauxaColorScheme, "oscuro" to DarkXauxaColorScheme)

    @Test
    fun tile_focus_ring_meets_3_to_1_against_every_accent() {
        (XauxaAccents.Admitted + XauxaAccents.System).forEach { accent ->
            val ratio = contrastRatio(tileFocusRingColor(accent), accent.background)
            assertTrue(ratio >= 3.0, "anillo de foco sobre ${accent.id}: $ratio < 3:1")
        }
    }

    @Test
    fun primary_app_bar_focus_ring_meets_3_to_1_against_brand_in_both_themes() {
        schemes.forEach { (name, s) ->
            val ratio = contrastRatio(s.onBrand, s.brand)
            assertTrue(ratio >= 3.0, "acción primaria en tema $name: $ratio < 3:1")
        }
    }

    @Test
    fun default_focus_ring_meets_3_to_1_on_the_surfaces_it_is_used_on() {
        schemes.forEach { (name, s) ->
            listOf(s.background, s.surface2).forEach { surface ->
                val ratio = contrastRatio(s.focusRing, surface)
                assertTrue(ratio >= 3.0, "anillo por defecto en tema $name: $ratio < 3:1")
            }
        }
    }

    @Test
    fun press_overlay_default_color_differs_strongly_from_the_theme_background() {
        schemes.forEach { (name, s) ->
            val ratio = contrastRatio(s.textPrimary, s.background)
            assertTrue(ratio >= 7.0, "overlay de pulsación en tema $name: $ratio < 7:1")
        }
    }
}
