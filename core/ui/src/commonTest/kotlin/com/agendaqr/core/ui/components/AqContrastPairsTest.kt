package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AqAccents
import com.agendaqr.core.ui.theme.DarkAmColorScheme
import com.agendaqr.core.ui.theme.LightAmColorScheme
import com.agendaqr.core.ui.theme.contrastRatio
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pares de estado y acento de `@shopify/polaris-tokens@9.4.2`, más la
 * paleta de identidad de contextos validada en X-06. Violet e Indigo usan
 * `magicOnFill` (`:204`); Magenta aplica PV-07 sobre `avatarOneFill` (`:255`).
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
            "light" to LightAmColorScheme,
            "dark" to DarkAmColorScheme,
        ).forEach { (name, scheme) ->
            assertTrue(contrastRatio(scheme.onBrandFill, scheme.brandFill) >= 4.5, "$name primary")
            assertTrue(contrastRatio(scheme.onDanger, scheme.dangerFill) >= 4.5, "$name danger")
            assertTrue(contrastRatio(scheme.onSuccessFill, scheme.successFill) >= 4.5, "$name success")
            assertTrue(contrastRatio(scheme.onWarningFill, scheme.warningFill) >= 4.5, "$name warning")
        }
    }

    @Test
    fun focus_and_functional_field_border_meet_non_text_contrast() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            listOf(scheme.background, scheme.surface, scheme.surfaceSecondary).forEach { surface ->
                assertTrue(contrastRatio(scheme.focus, surface) >= 3.0, "focus/surface")
            }
            assertTrue(contrastRatio(scheme.functionalBorder, scheme.surface) >= 3.0)
        }
    }
}
