package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.DarkAmColorScheme
import com.agendaqr.core.ui.theme.LightAmColorScheme
import com.agendaqr.core.ui.theme.AmAccents
import com.agendaqr.core.ui.theme.AmColorScheme
import com.agendaqr.core.ui.theme.contrastRatio
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrato de foco visible: el anillo de foco es VISIBLE (>= 3:1) sobre
 * cada superficie real donde aparece — cada bloque de identidad de
 * contexto y las superficies del tema — en claro y oscuro. El overlay de
 * pulsación por defecto contrasta fuertemente con el fondo del tema
 * (>= 7:1): sin esto, sobre el fondo oscuro un overlay oscuro no cambia
 * ni un píxel.
 */
class AmFocusRingContrastTest {
    private val schemes: Map<String, AmColorScheme> =
        mapOf("claro" to LightAmColorScheme, "oscuro" to DarkAmColorScheme)

    @Test
    fun accent_content_meets_3_to_1_against_every_accent_block() {
        // El par onAccent/background de cada acento sigue siendo el color
        // de CONTENIDO legible sobre el bloque de identidad de contexto.
        (AmAccents.Admitted + AmAccents.System).forEach { accent ->
            val ratio = contrastRatio(accent.onAccent, accent.background)
            assertTrue(ratio >= 3.0, "contenido sobre ${accent.id}: $ratio < 3:1")
        }
    }

    @Test
    fun primary_action_focus_ring_meets_3_to_1_against_accent_in_both_themes() {
        schemes.forEach { (name, s) ->
            val ratio = contrastRatio(s.onAccent, s.accent)
            assertTrue(ratio >= 3.0, "acción primaria en tema $name: $ratio < 3:1")
        }
    }

    @Test
    fun default_focus_ring_meets_3_to_1_on_the_surfaces_it_is_used_on() {
        schemes.forEach { (name, s) ->
            listOf(s.background, s.surface, s.surfaceSecondary).forEach { surface ->
                val ratio = contrastRatio(s.focus, surface)
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
