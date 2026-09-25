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
import kotlin.math.pow

/**
 * Token scheme tests: both schemes pin the current Xauxa values (no silent
 * visual change), both schemes are complete, and the pairs that must switch
 * with the theme are verified. Since the Metro-unified palette
 * (commit 86854d7) the interface is monochrome with a single brand accent:
 * background/surface are pure white/black, and brand/onBrand/brandContainer
 * are intentionally identical in light and dark. Shared identities between
 * light and dark (brand family, tertiaryContainer and their on-colors) are
 * pinned as the current code reality and flagged as pending official Xauxa
 * reference — they are NOT presented as validated design decisions.
 */
class XauxaSchemeTest {

    private fun XauxaColorScheme.all(): List<Color> = listOf(
        background, surface, surface2, surface3, surfaceVariant,
        border, borderVariant,
        textPrimary, textSecondary, textTertiary,
        brand, onBrand, brandContainer, onBrandContainer,
        secondary, onSecondary, secondaryContainer, onSecondaryContainer,
        tertiary, onTertiary, tertiaryContainer, onTertiaryContainer,
        danger, onDanger, dangerBg, onDangerBg,
        inverseSurface, inverseOnSurface, inverseBrand,
        focusRing,
    )

    private fun hex(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0').drop(2)

    private fun luminance(hex: String): Double {
        fun channel(v: Int): Double {
            val c = v / 255.0
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(hex.substring(0, 2).toInt(16))
        val g = channel(hex.substring(2, 4).toInt(16))
        val b = channel(hex.substring(4, 6).toInt(16))
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrast(a: String, b: String): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        val (hi, lo) = if (l1 >= l2) l1 to l2 else l2 to l1
        return (hi + 0.05) / (lo + 0.05)
    }

    @Test
    fun light_scheme_pins_the_current_xauxa_values() {
        val scheme = LightXauxaColorScheme
        assertEquals("FFFFFF", hex(scheme.background))
        assertEquals("FFFFFF", hex(scheme.surface))
        assertEquals("F2F2F2", hex(scheme.surface2))
        assertEquals("E0E0E0", hex(scheme.surface3))
        assertEquals("F2F2F2", hex(scheme.surfaceVariant))
        assertEquals("BDBDBD", hex(scheme.border))
        assertEquals("666666", hex(scheme.borderVariant))
        assertEquals("000000", hex(scheme.textPrimary))
        assertEquals("333333", hex(scheme.textSecondary))
        assertEquals("666666", hex(scheme.textTertiary))
        assertEquals("0067B8", hex(scheme.brand))
        assertEquals("FFFFFF", hex(scheme.onBrand))
        assertEquals("0067B8", hex(scheme.brandContainer))
        assertEquals("FFFFFF", hex(scheme.onBrandContainer))
        assertEquals("68577C", hex(scheme.secondary))
        assertEquals("FFFFFF", hex(scheme.onSecondary))
        assertEquals("E8D1FD", hex(scheme.secondaryContainer))
        assertEquals("69587D", hex(scheme.onSecondaryContainer))
        assertEquals("671448", hex(scheme.tertiary))
        assertEquals("FFFFFF", hex(scheme.onTertiary))
        assertEquals("842D60", hex(scheme.tertiaryContainer))
        assertEquals("FFA4D1", hex(scheme.onTertiaryContainer))
        assertEquals("BA1A1A", hex(scheme.danger))
        assertEquals("FFFFFF", hex(scheme.onDanger))
        assertEquals("FFDAD6", hex(scheme.dangerBg))
        assertEquals("93000A", hex(scheme.onDangerBg))
        assertEquals("332F35", hex(scheme.inverseSurface))
        assertEquals("F6EEF7", hex(scheme.inverseOnSurface))
        assertEquals("DAB9FF", hex(scheme.inverseBrand))
        assertEquals("0067B8", hex(scheme.focusRing))
    }

    @Test
    fun dark_scheme_pins_the_baseline_values() {
        val scheme = DarkXauxaColorScheme
        assertEquals("000000", hex(scheme.background))
        assertEquals("000000", hex(scheme.surface))
        assertEquals("1A1A1A", hex(scheme.surface2))
        assertEquals("2A2A2A", hex(scheme.surface3))
        assertEquals("1A1A1A", hex(scheme.surfaceVariant))
        assertEquals("555555", hex(scheme.border))
        assertEquals("999999", hex(scheme.borderVariant))
        assertEquals("FFFFFF", hex(scheme.textPrimary))
        assertEquals("CCCCCC", hex(scheme.textSecondary))
        assertEquals("999999", hex(scheme.textTertiary))
        assertEquals("0067B8", hex(scheme.brand))
        assertEquals("FFFFFF", hex(scheme.onBrand))
        assertEquals("0067B8", hex(scheme.brandContainer))
        assertEquals("FFFFFF", hex(scheme.onBrandContainer))
        assertEquals("D4BEE9", hex(scheme.secondary))
        assertEquals("39294B", hex(scheme.onSecondary))
        assertEquals("524266", hex(scheme.secondaryContainer))
        assertEquals("C5B0DA", hex(scheme.onSecondaryContainer))
        assertEquals("FFAFD5", hex(scheme.tertiary))
        assertEquals("5E0A41", hex(scheme.onTertiary))
        assertEquals("842D60", hex(scheme.tertiaryContainer))
        assertEquals("FFA4D1", hex(scheme.onTertiaryContainer))
        assertEquals("FFB4AB", hex(scheme.danger))
        assertEquals("690005", hex(scheme.onDanger))
        assertEquals("93000A", hex(scheme.dangerBg))
        assertEquals("FFDAD6", hex(scheme.onDangerBg))
        assertEquals("E8E0E9", hex(scheme.inverseSurface))
        assertEquals("332F35", hex(scheme.inverseOnSurface))
        assertEquals("734AA5", hex(scheme.inverseBrand))
        assertEquals("0067B8", hex(scheme.focusRing))
    }

    @Test
    fun both_schemes_are_complete_and_switchable() {
        listOf(LightXauxaColorScheme, DarkXauxaColorScheme).forEach { scheme ->
            assertEquals(30, scheme.all().size)
            assertTrue(scheme.all().none { it == Color.Unspecified }, "scheme has Unspecified colors")
        }
        assertNotEquals(LightXauxaColorScheme, DarkXauxaColorScheme)
        // Current code reality: the whole brand family and the tertiary
        // containers are shared between themes (identical hex) — a single
        // brand accent by design. Pinned here; requires official Xauxa
        // reference to confirm or correct — see report.
        assertEquals(LightXauxaColorScheme.brand, DarkXauxaColorScheme.brand)
        assertEquals(LightXauxaColorScheme.onBrand, DarkXauxaColorScheme.onBrand)
        assertEquals(LightXauxaColorScheme.brandContainer, DarkXauxaColorScheme.brandContainer)
        assertEquals(LightXauxaColorScheme.onBrandContainer, DarkXauxaColorScheme.onBrandContainer)
        assertEquals(LightXauxaColorScheme.tertiaryContainer, DarkXauxaColorScheme.tertiaryContainer)
        assertEquals(LightXauxaColorScheme.onTertiaryContainer, DarkXauxaColorScheme.onTertiaryContainer)
    }

    @Test
    fun light_foreground_background_pairs_meet_wcag_aa() {
        val s = LightXauxaColorScheme
        // Normal text and UI roles require >= 4.5:1.
        assertTrue(contrast(hex(s.brand), hex(s.onBrand)) >= 4.5, "light brand/onBrand")
        assertTrue(contrast(hex(s.brandContainer), hex(s.onBrandContainer)) >= 4.5, "light brandContainer/onBrandContainer")
        assertTrue(contrast(hex(s.secondary), hex(s.onSecondary)) >= 4.5, "light secondary/onSecondary")
        assertTrue(contrast(hex(s.secondaryContainer), hex(s.onSecondaryContainer)) >= 4.5, "light secondaryContainer/onSecondaryContainer")
        assertTrue(contrast(hex(s.tertiary), hex(s.onTertiary)) >= 4.5, "light tertiary/onTertiary")
        assertTrue(contrast(hex(s.tertiaryContainer), hex(s.onTertiaryContainer)) >= 4.5, "light tertiaryContainer/onTertiaryContainer")
        assertTrue(contrast(hex(s.danger), hex(s.onDanger)) >= 4.5, "light danger/onDanger")
        assertTrue(contrast(hex(s.dangerBg), hex(s.onDangerBg)) >= 4.5, "light dangerBg/onDangerBg")
        assertTrue(contrast(hex(s.surface), hex(s.textPrimary)) >= 4.5, "light surface/textPrimary")
        assertTrue(contrast(hex(s.surfaceVariant), hex(s.textSecondary)) >= 4.5, "light surfaceVariant/textSecondary")
    }

    @Test
    fun dark_foreground_background_pairs_meet_wcag_aa() {
        val s = DarkXauxaColorScheme
        assertTrue(contrast(hex(s.brand), hex(s.onBrand)) >= 4.5, "dark brand/onBrand")
        assertTrue(contrast(hex(s.brandContainer), hex(s.onBrandContainer)) >= 4.5, "dark brandContainer/onBrandContainer")
        assertTrue(contrast(hex(s.secondary), hex(s.onSecondary)) >= 4.5, "dark secondary/onSecondary")
        assertTrue(contrast(hex(s.secondaryContainer), hex(s.onSecondaryContainer)) >= 4.5, "dark secondaryContainer/onSecondaryContainer")
        assertTrue(contrast(hex(s.tertiary), hex(s.onTertiary)) >= 4.5, "dark tertiary/onTertiary")
        assertTrue(contrast(hex(s.tertiaryContainer), hex(s.onTertiaryContainer)) >= 4.5, "dark tertiaryContainer/onTertiaryContainer")
        assertTrue(contrast(hex(s.danger), hex(s.onDanger)) >= 4.5, "dark danger/onDanger")
        assertTrue(contrast(hex(s.dangerBg), hex(s.onDangerBg)) >= 4.5, "dark dangerBg/onDangerBg")
        assertTrue(contrast(hex(s.surface), hex(s.textPrimary)) >= 4.5, "dark surface/textPrimary")
    }

    /**
     * Required semantic fields per scheme. The data-class constructor makes
     * a missing field a compile error; this test pins the required set so a
     * silent rename/removal is caught in review, and proves no token is
     * dark-only.
     */
    @Test
    fun both_schemes_expose_the_same_required_semantic_fields() {
        fun XauxaColorScheme.fields(): Map<String, Color> = mapOf(
            "background" to background,
            "surface" to surface,
            "surface2" to surface2,
            "surface3" to surface3,
            "surfaceVariant" to surfaceVariant,
            "border" to border,
            "borderVariant" to borderVariant,
            "textPrimary" to textPrimary,
            "textSecondary" to textSecondary,
            "textTertiary" to textTertiary,
            "brand" to brand,
            "onBrand" to onBrand,
            "brandContainer" to brandContainer,
            "onBrandContainer" to onBrandContainer,
            "secondary" to secondary,
            "onSecondary" to onSecondary,
            "secondaryContainer" to secondaryContainer,
            "onSecondaryContainer" to onSecondaryContainer,
            "tertiary" to tertiary,
            "onTertiary" to onTertiary,
            "tertiaryContainer" to tertiaryContainer,
            "onTertiaryContainer" to onTertiaryContainer,
            "danger" to danger,
            "onDanger" to onDanger,
            "dangerBg" to dangerBg,
            "onDangerBg" to onDangerBg,
            "inverseSurface" to inverseSurface,
            "inverseOnSurface" to inverseOnSurface,
            "inverseBrand" to inverseBrand,
            "focusRing" to focusRing,
        )
        val light = LightXauxaColorScheme.fields()
        val dark = DarkXauxaColorScheme.fields()
        assertEquals(light.keys, dark.keys)
        assertEquals(30, light.size)
        // Every neutral role actually switches; the brand family and the
        // tertiary containers stay put by design (single accent, pending
        // reference) — see both_schemes_are_complete_and_switchable.
        listOf(
            "background", "surface", "surface2", "surface3", "surfaceVariant",
            "border", "borderVariant", "textPrimary", "textSecondary",
            "secondary", "danger", "dangerBg",
        ).forEach { key ->
            assertNotEquals(light.getValue(key), dark.getValue(key), "$key must switch with the theme")
        }
    }
}
