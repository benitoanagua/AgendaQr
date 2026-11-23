package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AqAccents
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LightAqColorScheme
import com.agendaqr.core.ui.theme.contrastRatio
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pares de estado y acento de Moon (`MoonColors`), más la paleta de
 * identidad de contextos validada en X-06.
 */
class AqContrastPairsTest {

    @Test
    fun context_accent_content_meets_aa_against_every_identity_block() {
        (AqAccents.Admitted + AqAccents.System).forEach { accent ->
            val ratio = contrastRatio(accent.onAccent, accent.background)
            assertTrue(ratio >= 4.5, "${accent.id}: onAccent/background = $ratio < 4.5:1")
        }
    }

    @Test
    fun primary_and_state_fills_have_accessible_content_in_both_themes() {
        listOf(
            "light" to LightAqColorScheme,
            "dark" to DarkAqColorScheme,
        ).forEach { (name, scheme) ->
            assertTrue(contrastRatio(scheme.onBrandFill, scheme.brandFill) >= 4.5, "$name primary")
            assertTrue(contrastRatio(scheme.onDanger, scheme.dangerFill) >= 4.5, "$name danger")
            assertTrue(contrastRatio(scheme.onSuccessFill, scheme.successFill) >= 4.5, "$name success")
            assertTrue(contrastRatio(scheme.onWarningFill, scheme.warningFill) >= 4.5, "$name warning")
        }
    }

    @Test
    fun focus_and_functional_field_border_meet_non_text_contrast() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            listOf(scheme.background, scheme.surface, scheme.surfaceSecondary).forEach { surface ->
                assertTrue(contrastRatio(scheme.focus, surface) >= 3.0, "focus/surface")
            }
            assertTrue(contrastRatio(scheme.functionalBorder, scheme.surface) >= 3.0)
            assertTrue(
                contrastRatio(scheme.functionalBorder, scheme.inputSurface) >= 3.0,
                "functionalBorder/inputSurface",
            )
        }
    }

    @Test
    fun field_text_meets_aa_on_the_input_surface() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            assertTrue(
                contrastRatio(scheme.textPrimary, scheme.inputSurface) >= 4.5,
                "textPrimary/inputSurface",
            )
            assertTrue(
                contrastRatio(scheme.textPlaceholder, scheme.inputSurface) >= 4.5,
                "textPlaceholder/inputSurface",
            )
        }
    }

    /**
     * Etiqueta, ayuda y error de un campo: el texto debe conservar AA sobre
     * la superficie de la página y sobre la superficie del propio campo en
     * ambos temas (la ayuda y la etiqueta usan `textSecondary`; el error,
     * `danger`).
     */
    @Test
    fun form_label_help_and_error_meet_aa_on_surface_and_input_surface() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            listOf(scheme.surface, scheme.inputSurface).forEach { surface ->
                assertTrue(
                    contrastRatio(scheme.textSecondary, surface) >= 4.5,
                    "textSecondary/surface",
                )
                assertTrue(
                    contrastRatio(scheme.danger, surface) >= 4.5,
                    "danger/surface",
                )
            }
        }
    }

    /** D-02: los contenedores tonales derivados conservan AA. */
    @Test
    fun tone_content_meets_aa_on_derived_containers_in_both_themes() {
        listOf(
            "light" to LightAqColorScheme,
            "dark" to DarkAqColorScheme,
        ).forEach { (name, scheme) ->
            val pairs = listOf(
                "danger" to (scheme.danger to scheme.dangerContainer),
                "success" to (scheme.success to scheme.successContainer),
                "warning" to (scheme.warning to scheme.warningContainer),
                "info" to (scheme.info to scheme.infoContainer),
            )
            pairs.forEach { (tone, colors) ->
                val ratio = contrastRatio(colors.first, colors.second)
                assertTrue(ratio >= 4.5, "$name $tone content/container = $ratio < 4.5:1")
            }
        }
    }
}
