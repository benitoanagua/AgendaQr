package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AmAccents
import com.agendaqr.core.ui.theme.contrastRatio
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pares de contraste del sistema que no viven en el esquema (AmSchemeTest)
 * pero que llegan a pantalla: el contenido legible sobre cada bloque de
 * identidad de contexto y el rol `focus` sobre las superficies del tema,
 * en claro y oscuro (contrato §4.5).
 */
class AmContrastPairsTest {

    @Test
    fun accent_content_meets_3_to_1_against_every_accent_block() {
        // El par onAccent/background de cada acento de identidad es el
        // color de CONTENIDO legible sobre el bloque (punto de fila de
        // contexto, tinte de pestañas de S06).
        (AmAccents.Admitted + AmAccents.System).forEach { accent ->
            val ratio = contrastRatio(accent.onAccent, accent.background)
            assertTrue(ratio >= 3.0, "contenido sobre ${accent.id}: $ratio < 3:1")
        }
    }

    @Test
    fun primary_action_content_meets_3_to_1_against_accent_in_both_themes() {
        // El contenido del botón primario (onAccent) sobre su relleno
        // (accent) es visible en ambos temas.
        schemes.forEach { (name, s) ->
            val ratio = contrastRatio(s.onAccent, s.accent)
            assertTrue(ratio >= 3.0, "acción primaria en tema $name: $ratio < 3:1")
        }
    }

    @Test
    fun focus_role_meets_3_to_1_on_the_surfaces_it_is_used_on() {
        // El rol `focus` (guía/indicador de foco de los campos, token
        // focus) es visible sobre las superficies del tema.
        schemes.forEach { (name, s) ->
            listOf(s.background, s.surface, s.surfaceSecondary).forEach { surface ->
                val ratio = contrastRatio(s.focus, surface)
                assertTrue(ratio >= 3.0, "foco en tema $name: $ratio < 3:1")
            }
        }
    }

    @Test
    fun action_gradient_content_meets_4_5_to_1_over_every_stop() {
        // El texto de la acción primaria (onActionGradient) es legible
        // sobre los tres extremos del gradiente de marca en ambos temas
        // (el gradiente es idéntico: ADR-0022, AmSchemeTest).
        schemes.forEach { (name, s) ->
            listOf(s.actionGradientStart, s.actionGradientMid, s.actionGradientEnd).forEach { stop ->
                val ratio = contrastRatio(s.onActionGradient, stop)
                assertTrue(ratio >= 4.5, "contenido del gradiente en tema $name: $ratio < 4.5:1")
            }
        }
    }

    private val schemes: Map<String, com.agendaqr.core.ui.theme.AmColorScheme> =
        mapOf(
            "claro" to com.agendaqr.core.ui.theme.LightAmColorScheme,
            "oscuro" to com.agendaqr.core.ui.theme.DarkAmColorScheme,
        )
}
