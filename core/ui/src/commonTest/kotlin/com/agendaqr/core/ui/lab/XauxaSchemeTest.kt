package com.agendaqr.core.ui.lab

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.agendaqr.core.ui.theme.DarkXauxaColorScheme
import com.agendaqr.core.ui.theme.LightXauxaColorScheme
import com.agendaqr.core.ui.theme.XauxaColorScheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Token scheme tests: the light scheme pins the production appearance (no
 * silent visual change), both schemes are complete, and the dark scheme
 * carries the reference values pending product validation.
 */
class XauxaSchemeTest {

    private fun XauxaColorScheme.all(): List<Color> = listOf(
        background, surface, surface2, surface3, border,
        textPrimary, textSecondary, textTertiary,
        brand, brandAccent, onBrand, white,
        success, successBg, danger, dangerBg,
        warning, warningBg, info, infoBg, focusRing,
    )

    private fun hex(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0').drop(2)

    @Test
    fun light_scheme_preserves_the_production_appearance() {
        val scheme = LightXauxaColorScheme
        assertEquals("F8FAFA", hex(scheme.background))
        assertEquals("FFFFFF", hex(scheme.surface))
        assertEquals("F0F4F3", hex(scheme.surface2))
        assertEquals("D7DEDC", hex(scheme.border))
        assertEquals("17201E", hex(scheme.textPrimary))
        assertEquals("4E5B57", hex(scheme.textSecondary))
        assertEquals("6C7773", hex(scheme.textTertiary))
        assertEquals("0E7D6E", hex(scheme.brand))
        assertEquals("7FD9C9", hex(scheme.brandAccent))
        assertEquals("2E7D5B", hex(scheme.success))
        assertEquals("EAF2EF", hex(scheme.successBg))
        assertEquals("B3261E", hex(scheme.danger))
        assertEquals("F7E9E8", hex(scheme.dangerBg))
        assertEquals("8A5A00", hex(scheme.warning))
        assertEquals("F3EEE6", hex(scheme.warningBg))
        assertEquals("245E9B", hex(scheme.info))
        assertEquals("E9EFF5", hex(scheme.infoBg))
        assertEquals("7FD9C9", hex(scheme.focusRing))
    }

    @Test
    fun light_surface3_is_the_documented_new_proposal() {
        assertEquals("E6EBEA", hex(LightXauxaColorScheme.surface3))
    }

    @Test
    fun dark_scheme_carries_the_reference_values() {
        val scheme = DarkXauxaColorScheme
        assertEquals("151218", hex(scheme.background))
        assertEquals("17171B", hex(scheme.surface))
        assertEquals("1F1F24", hex(scheme.surface2))
        assertEquals("26262C", hex(scheme.surface3))
        assertEquals("2A2A30", hex(scheme.border))
        assertEquals("F4F4F6", hex(scheme.textPrimary))
        assertEquals("9C9CA6", hex(scheme.textSecondary))
        assertEquals("90909A", hex(scheme.textTertiary))
        assertEquals("3DA35D", hex(scheme.success))
        assertEquals("163521", hex(scheme.successBg))
        assertEquals("D9534F", hex(scheme.danger))
        assertEquals("3A1E1D", hex(scheme.dangerBg))
        assertEquals("B5790A", hex(scheme.warning))
        assertEquals("3A2B0B", hex(scheme.warningBg))
        assertEquals("3A7BD5", hex(scheme.info))
        assertEquals("17263D", hex(scheme.infoBg))
    }

    @Test
    fun both_schemes_are_complete_and_switchable() {
        listOf(LightXauxaColorScheme, DarkXauxaColorScheme).forEach { scheme ->
            assertEquals(21, scheme.all().size)
            assertTrue(scheme.all().none { it == Color.Unspecified }, "scheme has Unspecified colors")
        }
        assertNotEquals(LightXauxaColorScheme, DarkXauxaColorScheme)
        // Brand accent identity is shared by design (single brand accent).
        assertEquals(LightXauxaColorScheme.brand, DarkXauxaColorScheme.brand)
        assertEquals(LightXauxaColorScheme.focusRing, DarkXauxaColorScheme.focusRing)
    }
}
