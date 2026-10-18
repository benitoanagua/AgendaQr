package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.toneColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.math.pow

/**
 * TDesign Mobile 1.16.2 color-token contract. Palette values come from
 * `package/esm/_common/style/mobile/theme/_light.less` and `_dark.less`.
 */
class AmSchemeTest {

    private fun argb(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0')

    private fun luminance(color: Color): Double {
        fun channel(value: Float): Double {
            val c = value.toDouble()
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) +
            0.0722 * channel(color.blue)
    }

    private fun opaqueOver(foreground: Color, background: Color): Color {
        val alpha = foreground.alpha
        return Color(
            red = foreground.red * alpha + background.red * (1f - alpha),
            green = foreground.green * alpha + background.green * (1f - alpha),
            blue = foreground.blue * alpha + background.blue * (1f - alpha),
        )
    }

    private fun contrast(foreground: Color, background: Color): Double {
        val rendered = opaqueOver(foreground, background)
        val first = luminance(rendered)
        val second = luminance(background)
        val (lighter, darker) = if (first >= second) first to second else second to first
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun AmColorScheme.fields(): Map<String, Color> = mapOf(
        "background" to background,
        "surface" to surface,
        "surfaceSecondary" to surfaceSecondary,
        "border" to border,
        "functionalBorder" to functionalBorder,
        "divider" to divider,
        "textPrimary" to textPrimary,
        "textSecondary" to textSecondary,
        "textTertiary" to textTertiary,
        "accent" to accent,
        "accentActive" to accentActive,
        "accentDisabled" to accentDisabled,
        "accentFocus" to accentFocus,
        "accentLightActive" to accentLightActive,
        "onAccent" to onAccent,
        "accentContainer" to accentContainer,
        "onAccentContainer" to onAccentContainer,
        "accentText" to accentText,
        "danger" to danger,
        "dangerFill" to dangerFill,
        "onDanger" to onDanger,
        "dangerContainer" to dangerContainer,
        "success" to success,
        "successFill" to successFill,
        "onSuccessFill" to onSuccessFill,
        "successContainer" to successContainer,
        "warning" to warning,
        "warningFill" to warningFill,
        "onWarningFill" to onWarningFill,
        "warningContainer" to warningContainer,
        "info" to info,
        "infoContainer" to infoContainer,
        "focus" to focus,
        "disabled" to disabled,
        "onDisabled" to onDisabled,
        "inverseSurface" to inverseSurface,
        "inverseOnSurface" to inverseOnSurface,
        "scrim" to scrim,
        "qrCanvas" to qrCanvas,
        "surfaceContainerLow" to surfaceContainerLow,
        "surfaceContainer" to surfaceContainer,
        "surfaceContainerHigh" to surfaceContainerHigh,
        "surfaceContainerHighest" to surfaceContainerHighest,
    )

    @Test
    fun official_palette_scales_are_pinned_for_both_themes() {
        fun assertScale(actual: List<Color>, expected: List<String>, name: String) {
            assertEquals(expected, actual.map(::argb), name)
        }

        assertScale(
            AmPalette.lightBrand,
            listOf("FFF2F3FF", "FFD9E1FF", "FFB5C7FF", "FF8EABFF", "FF618DFF", "FF366EF4", "FF0052D9", "FF003CAB", "FF002A7C", "FF001A57"),
            "light brand",
        )
        assertScale(
            AmPalette.darkBrand,
            listOf("FF1B2F51", "FF173463", "FF143975", "FF103D88", "FF0D429A", "FF054BBE", "FF2667D4", "FF4582E6", "FF699EF5", "FF96BBF8"),
            "dark brand",
        )
        assertScale(
            AmPalette.lightSuccessScale,
            listOf("FFE3F9E9", "FFC6F3D7", "FF92DAB2", "FF56C08D", "FF2BA471", "FF008858", "FF006C45", "FF005334", "FF003B23", "FF002515"),
            "light success",
        )
        assertScale(
            AmPalette.darkSuccessScale,
            listOf("FF193A2A", "FF1A4230", "FF17533D", "FF0D7A55", "FF059465", "FF43AF8A", "FF46BF96", "FF80D2B6", "FFB4E1D3", "FFDEEDE8"),
            "dark success",
        )
        assertScale(
            AmPalette.lightWarningScale,
            listOf("FFFFF1E9", "FFFFD9C2", "FFFFB98C", "FFFA9550", "FFE37318", "FFBE5A00", "FF954500", "FF713300", "FF532300", "FF3B1700"),
            "light warning",
        )
        assertScale(
            AmPalette.darkWarningScale,
            listOf("FF4F2A1D", "FF582F21", "FF733C23", "FFA75D2B", "FFCF6E2D", "FFDC7633", "FFE8935C", "FFECBF91", "FFEED7BF", "FFF3E9DC"),
            "dark warning",
        )
        assertScale(
            AmPalette.lightErrorScale,
            listOf("FFFFF0ED", "FFFFD8D2", "FFFFB9B0", "FFFF9285", "FFF6685D", "FFD54941", "FFAD352F", "FF881F1C", "FF68070A", "FF490002"),
            "light error",
        )
        assertScale(
            AmPalette.darkErrorScale,
            listOf("FF472324", "FF5E2A2D", "FF703439", "FF83383E", "FFA03F46", "FFC64751", "FFDE6670", "FFEC888E", "FFEDB1B6", "FFEECED0"),
            "dark error",
        )
        assertScale(
            AmPalette.lightGray,
            listOf("FFF3F3F3", "FFEEEEEE", "FFE7E7E7", "FFDCDCDC", "FFC5C5C5", "FFA6A6A6", "FF8B8B8B", "FF777777", "FF5E5E5E", "FF4B4B4B", "FF383838", "FF2C2C2C", "FF242424", "FF181818"),
            "light gray",
        )
        assertScale(
            AmPalette.darkGray,
            listOf("FFF3F3F3", "FFEEEEEE", "FFE8E8E8", "FFDDDDDD", "FFC6C6C6", "FFA6A6A6", "FF8B8B8B", "FF777777", "FF5E5E5E", "FF4B4B4B", "FF383838", "FF2C2C2C", "FF242424", "FF181818"),
            "dark gray",
        )
    }

    @Test
    fun semantic_tokens_use_the_tdesign_light_and_dark_roles() {
        val light = LightAmColorScheme
        assertEquals("FFF3F3F3", argb(light.background))
        assertEquals("FFFFFFFF", argb(light.surface))
        assertEquals("FFF3F3F3", argb(light.surfaceSecondary))
        assertEquals("FFDCDCDC", argb(light.border))
        assertEquals("FF8B8B8B", argb(light.functionalBorder))
        assertEquals("FFE7E7E7", argb(light.divider))
        assertEquals("E6000000", argb(light.textPrimary))
        assertEquals("99000000", argb(light.textSecondary))
        assertEquals("FF5E5E5E", argb(light.textTertiary))
        assertEquals("FF0052D9", argb(light.accent))
        assertEquals("FF003CAB", argb(light.accentActive))
        assertEquals("FFB5C7FF", argb(light.accentDisabled))
        assertEquals("FFF2F3FF", argb(light.accentFocus))
        assertEquals("FFD9E1FF", argb(light.accentLightActive))
        assertEquals("FFFFFFFF", argb(light.onAccent))
        assertEquals("FFF2F3FF", argb(light.accentContainer))
        assertEquals("FF0052D9", argb(light.onAccentContainer))
        assertEquals("FFAD352F", argb(light.danger))
        assertEquals("FFAD352F", argb(light.dangerFill))
        assertEquals("FFFFF0ED", argb(light.dangerContainer))
        assertEquals("FF006C45", argb(light.success))
        assertEquals("FF008858", argb(light.successFill))
        assertEquals("FFE3F9E9", argb(light.successContainer))
        assertEquals("FF954500", argb(light.warning))
        assertEquals("FFBE5A00", argb(light.warningFill))
        assertEquals("FFFFF1E9", argb(light.warningContainer))
        assertEquals("FF0052D9", argb(light.info))
        assertEquals("FFF2F3FF", argb(light.infoContainer))
        assertEquals("FFF3F3F3", argb(light.surfaceContainer))
        assertEquals("FFE7E7E7", argb(light.surfaceContainerHigh))
        assertEquals("FFDCDCDC", argb(light.surfaceContainerHighest))

        val dark = DarkAmColorScheme
        assertEquals("FF181818", argb(dark.background))
        assertEquals("FF242424", argb(dark.surface))
        assertEquals("FF2C2C2C", argb(dark.surfaceSecondary))
        assertEquals("FF5E5E5E", argb(dark.border))
        assertEquals("FF777777", argb(dark.functionalBorder))
        assertEquals("FF383838", argb(dark.divider))
        assertEquals("FFFFFFFF", argb(dark.textPrimary))
        assertEquals("8CFFFFFF", argb(dark.textSecondary))
        assertEquals("FFA6A6A6", argb(dark.textTertiary))
        assertEquals("FF2667D4", argb(dark.accent))
        assertEquals("FF699EF5", argb(dark.accentActive))
        assertEquals("FF143975", argb(dark.accentDisabled))
        assertEquals("FF1B2F51", argb(dark.accentFocus))
        assertEquals("FF173463", argb(dark.accentLightActive))
        assertEquals("FFFFFFFF", argb(dark.onAccent))
        assertEquals("FF1B2F51", argb(dark.accentContainer))
        assertEquals("FF699EF5", argb(dark.onAccentContainer))
        assertEquals("FFEC888E", argb(dark.danger))
        assertEquals("FFC64751", argb(dark.dangerFill))
        assertEquals("FF472324", argb(dark.dangerContainer))
        assertEquals("FF43AF8A", argb(dark.success))
        assertEquals("FF059465", argb(dark.successFill))
        assertEquals("FF181818", argb(dark.onSuccessFill))
        assertEquals("FF193A2A", argb(dark.successContainer))
        assertEquals("FFE8935C", argb(dark.warning))
        assertEquals("FFCF6E2D", argb(dark.warningFill))
        assertEquals("FF181818", argb(dark.onWarningFill))
        assertEquals("FF4F2A1D", argb(dark.warningContainer))
        assertEquals("FF699EF5", argb(dark.info))
        assertEquals("FF1B2F51", argb(dark.infoContainer))
        assertEquals("FF181818", argb(dark.surfaceContainer))
        assertEquals("FF383838", argb(dark.surfaceContainerHigh))
        assertEquals("FF4B4B4B", argb(dark.surfaceContainerHighest))
    }

    @Test
    fun both_schemes_expose_the_same_complete_semantic_roles() {
        val lightFields = LightAmColorScheme.fields()
        val darkFields = DarkAmColorScheme.fields()
        assertEquals(43, lightFields.size)
        assertEquals(lightFields.keys, darkFields.keys)
        assertTrue(lightFields.values.none { it == Color.Unspecified })
        assertTrue(darkFields.values.none { it == Color.Unspecified })
        assertNotEquals(LightAmColorScheme, DarkAmColorScheme)
        assertEquals("FFFFFFFF", argb(LightAmColorScheme.qrCanvas))
        assertEquals(LightAmColorScheme.qrCanvas, DarkAmColorScheme.qrCanvas)
    }

    @Test
    fun text_and_filled_state_pairs_meet_wcag_aa() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(contrast(scheme.onAccent, scheme.accent) >= 4.5, "onAccent/accent")
            assertTrue(contrast(scheme.onDanger, scheme.dangerFill) >= 4.5, "onDanger/dangerFill")
            assertTrue(contrast(scheme.onSuccessFill, scheme.successFill) >= 4.5, "onSuccessFill/successFill")
            assertTrue(contrast(scheme.onWarningFill, scheme.warningFill) >= 4.5, "onWarningFill/warningFill")
            assertTrue(contrast(scheme.accentText, scheme.background) >= 4.5, "accentText/background")
            assertTrue(contrast(scheme.accentText, scheme.surface) >= 4.5, "accentText/surface")
            assertTrue(contrast(scheme.textPrimary, scheme.background) >= 4.5, "textPrimary/background")
            assertTrue(contrast(scheme.textPrimary, scheme.surface) >= 4.5, "textPrimary/surface")
            assertTrue(contrast(scheme.textSecondary, scheme.background) >= 4.5, "textSecondary/background")
            assertTrue(contrast(scheme.textTertiary, scheme.surface) >= 4.5, "textTertiary/surface")
            assertTrue(contrast(scheme.focus, scheme.background) >= 3.0, "focus/background")
            assertTrue(contrast(scheme.functionalBorder, scheme.surface) >= 3.0, "functionalBorder/surface")
        }
    }

    @Test
    fun state_tone_text_meets_wcag_aa_on_its_container() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            AmTone.entries.forEach { tone ->
                val colors = scheme.toneColors(tone)
                assertTrue(
                    contrast(colors.content, colors.container) >= 4.5,
                    "$tone content/container",
                )
            }
            assertEquals(scheme.success, scheme.toneColors(AmTone.Success).content)
            assertEquals(scheme.warning, scheme.toneColors(AmTone.Warning).content)
            assertEquals(scheme.danger, scheme.toneColors(AmTone.Danger).content)
            assertEquals(scheme.info, scheme.toneColors(AmTone.Info).content)
        }
    }

}
