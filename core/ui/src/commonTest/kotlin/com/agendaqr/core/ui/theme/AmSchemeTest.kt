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
 * Contrato de color de `@shopify/polaris-tokens@9.4.2`
 * (`docs/05-design-system/09-instantanea-tokens-polaris-9.4.2.md`).
 * Los roles semánticos se derivan de la capa primitiva publicada; las
 * adaptaciones D-02/D-07 y X-05 se documentan en el contrato normativo.
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
        "textPlaceholder" to textPlaceholder,
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
        "surfaceActive" to surfaceActive,
        "componentActive" to componentActive,
        "accentActiveContent" to accentActiveContent,
        "dangerActive" to dangerActive,
        "dangerDisabled" to dangerDisabled,
        "transparent" to transparent,
    )

    @Test
    fun official_palette_primitives_are_pinned() {
        fun assertColor(actual: Color, expected: String, name: String) =
            assertEquals(expected, argb(actual), name)

        assertColor(AmPalette.white, "FFFFFFFF", "white")
        assertColor(AmPalette.black, "FF000000", "black")
        assertColor(AmPalette.gray50, "FFF1F1F1", "gray50")
        assertColor(AmPalette.gray100, "FFEBEBEB", "gray100")
        assertColor(AmPalette.gray200, "FFE3E3E3", "gray200")
        assertColor(AmPalette.gray300, "FFD4D4D4", "gray300")
        assertColor(AmPalette.gray400, "FFB5B5B5", "gray400")
        assertColor(AmPalette.gray500, "FF8A8A8A", "gray500")
        assertColor(AmPalette.gray600, "FF616161", "gray600")
        assertColor(AmPalette.gray700, "FF4A4A4A", "gray700")
        assertColor(AmPalette.gray800, "FF303030", "gray800")
        assertColor(AmPalette.gray900, "FF1A1A1A", "gray900")
        assertColor(AmPalette.lightText, "FF303030", "lightText")
        assertColor(AmPalette.darkText, "FFE3E3E3", "darkText")

        assertColor(AmPalette.emphasis, "FF005BDB", "emphasis")
        assertColor(AmPalette.emphasisHover, "FF004299", "emphasisHover")
        assertColor(AmPalette.emphasisActive, "FF002E6A", "emphasisActive")
        assertColor(AmPalette.emphasisSurface, "FFF0F2FF", "emphasisSurface")
        assertColor(AmPalette.emphasisSurfaceHover, "FFEAEDFF", "emphasisSurfaceHover")
        assertColor(AmPalette.emphasisOnFill, "FFFCFDFF", "emphasisOnFill")
        assertColor(AmPalette.info, "FF003A5A", "info")
        assertColor(AmPalette.infoFill, "FF91D0FF", "infoFill")
        assertColor(AmPalette.infoSurface, "FFEAF4FF", "infoSurface")
        assertColor(AmPalette.success, "FF014B40", "success")
        assertColor(AmPalette.successFill, "FF047B5D", "successFill")
        assertColor(AmPalette.successHover, "FF035E4C", "successHover")
        assertColor(AmPalette.successSurface, "FFCDFED4", "successSurface")
        assertColor(AmPalette.successOnFill, "FFFAFFFB", "successOnFill")
        assertColor(AmPalette.warning, "FF5E4200", "warning")
        assertColor(AmPalette.warningFill, "FFFFB800", "warningFill")
        assertColor(AmPalette.warningSurface, "FFFFF1E3", "warningSurface")
        assertColor(AmPalette.warningOnFill, "FF251A00", "warningOnFill")
        assertColor(AmPalette.critical, "FF8E0B21", "critical")
        assertColor(AmPalette.criticalFill, "FFC70A24", "criticalFill")
        assertColor(AmPalette.criticalHover, "FFA30A24", "criticalHover")
        assertColor(AmPalette.criticalSurface, "FFFEE8EB", "criticalSurface")
        assertColor(AmPalette.criticalBorder, "FFFEC1C7", "criticalBorder")
        assertColor(AmPalette.criticalOnFill, "FFFFFAFB", "criticalOnFill")
    }

    @Test
    fun semantic_tokens_use_the_polaris_light_and_dark_roles() {
        val light = LightAmColorScheme
        assertEquals("FFF1F1F1", argb(light.background))
        assertEquals("FFFFFFFF", argb(light.surface))
        assertEquals("FFF7F7F7", argb(light.surfaceSecondary))
        assertEquals("FFE3E3E3", argb(light.border))
        assertEquals("FF8A8A8A", argb(light.functionalBorder))
        assertEquals("FFE3E3E3", argb(light.divider))
        assertEquals("FF303030", argb(light.textPrimary))
        assertEquals("FF616161", argb(light.textSecondary))
        assertEquals("FF616161", argb(light.textPlaceholder))
        assertEquals("FF616161", argb(light.textTertiary))
        assertEquals("FF005BDB", argb(light.accent))
        assertEquals("FF004299", argb(light.accentActive))
        assertEquals("FFB5B5B5", argb(light.accentDisabled))
        assertEquals("FF005BDB", argb(light.accentFocus))
        assertEquals("FFEAEDFF", argb(light.accentLightActive))
        assertEquals("FFFCFDFF", argb(light.onAccent))
        assertEquals("FFF0F2FF", argb(light.accentContainer))
        assertEquals("FF005BDB", argb(light.onAccentContainer))
        assertEquals("FF8E0B21", argb(light.danger))
        assertEquals("FFC70A24", argb(light.dangerFill))
        assertEquals("FFFFFAFB", argb(light.onDanger))
        assertEquals("FFFEE8EB", argb(light.dangerContainer))
        assertEquals("FF014B40", argb(light.success))
        assertEquals("FF047B5D", argb(light.successFill))
        assertEquals("FFFAFFFB", argb(light.onSuccessFill))
        assertEquals("FFCDFED4", argb(light.successContainer))
        assertEquals("FF5E4200", argb(light.warning))
        assertEquals("FFFFB800", argb(light.warningFill))
        assertEquals("FF251A00", argb(light.onWarningFill))
        assertEquals("FFFFF1E3", argb(light.warningContainer))
        assertEquals("FF003A5A", argb(light.info))
        assertEquals("FFEAF4FF", argb(light.infoContainer))
        assertEquals("FF005BDB", argb(light.focus))
        assertEquals("0D000000", argb(light.disabled))
        assertEquals("FFB5B5B5", argb(light.onDisabled))
        assertEquals("FF303030", argb(light.inverseSurface))
        assertEquals("FFE3E3E3", argb(light.inverseOnSurface))
        assertEquals("B5000000", argb(light.scrim))
        assertEquals("FFFFFFFF", argb(light.qrCanvas))
        assertEquals("FFFFFFFF", argb(light.surfaceContainerLow))
        assertEquals("FFF7F7F7", argb(light.surfaceContainer))
        assertEquals("FFF1F1F1", argb(light.surfaceContainerHigh))
        assertEquals("FFEBEBEB", argb(light.surfaceContainerHighest))
        assertEquals("FFF3F3F3", argb(light.surfaceActive))
        assertEquals("FFF7F7F7", argb(light.componentActive))
        assertEquals("FFFCFDFF", argb(light.accentActiveContent))
        assertEquals("FFA30A24", argb(light.dangerActive))
        assertEquals("FFFEC1C7", argb(light.dangerDisabled))

        val dark = DarkAmColorScheme
        assertEquals("FF1A1A1A", argb(dark.background))
        assertEquals("FF303030", argb(dark.surface))
        assertEquals("FF303030", argb(dark.surfaceSecondary))
        assertEquals("FF4A4A4A", argb(dark.border))
        assertEquals("FF8A8A8A", argb(dark.functionalBorder))
        assertEquals("FF4A4A4A", argb(dark.divider))
        assertEquals("FFE3E3E3", argb(dark.textPrimary))
        assertEquals("FFB5B5B5", argb(dark.textSecondary))
        assertEquals("FFB5B5B5", argb(dark.textPlaceholder))
        assertEquals("FFB5B5B5", argb(dark.textTertiary))
        assertEquals("FFFFFFFF", argb(dark.accent))
        assertEquals("FFEBEBEB", argb(dark.accentActive))
        assertEquals("FF616161", argb(dark.accentDisabled))
        assertEquals("FF91D0FF", argb(dark.accentFocus))
        assertEquals("FF4A4A4A", argb(dark.accentLightActive))
        assertEquals("FF303030", argb(dark.onAccent))
        assertEquals("FF303030", argb(dark.accentContainer))
        assertEquals("FF91D0FF", argb(dark.onAccentContainer))
        assertEquals("FFFED1D7", argb(dark.danger))
        assertEquals("FFC70A24", argb(dark.dangerFill))
        assertEquals("FFFFFAFB", argb(dark.onDanger))
        assertEquals("FF303030", argb(dark.dangerContainer))
        assertEquals("FFCDFED4", argb(dark.success))
        assertEquals("FF047B5D", argb(dark.successFill))
        assertEquals("FFFAFFFB", argb(dark.onSuccessFill))
        assertEquals("FF303030", argb(dark.successContainer))
        assertEquals("FFFFB800", argb(dark.warning))
        assertEquals("FFFFB800", argb(dark.warningFill))
        assertEquals("FF251A00", argb(dark.onWarningFill))
        assertEquals("FF303030", argb(dark.warningContainer))
        assertEquals("FF91D0FF", argb(dark.info))
        assertEquals("FF303030", argb(dark.infoContainer))
        assertEquals("FF91D0FF", argb(dark.focus))
        assertEquals("1CFFFFFF", argb(dark.disabled))
        assertEquals("FF8A8A8A", argb(dark.onDisabled))
        assertEquals("FFF1F1F1", argb(dark.inverseSurface))
        assertEquals("FF1A1A1A", argb(dark.inverseOnSurface))
        assertEquals("B5000000", argb(dark.scrim))
        assertEquals("FFFFFFFF", argb(dark.qrCanvas))
        assertEquals("FF1A1A1A", argb(dark.surfaceContainerLow))
        assertEquals("FF303030", argb(dark.surfaceContainer))
        assertEquals("FF4A4A4A", argb(dark.surfaceContainerHigh))
        assertEquals("FF616161", argb(dark.surfaceContainerHighest))
        assertEquals("FF4A4A4A", argb(dark.surfaceActive))
        assertEquals("FF303030", argb(dark.componentActive))
        assertEquals("FF303030", argb(dark.accentActiveContent))
        assertEquals("FFA30A24", argb(dark.dangerActive))
        assertEquals("FF8E0B21", argb(dark.dangerDisabled))
    }

    @Test
    fun both_schemes_expose_the_same_complete_semantic_roles() {
        val lightFields = LightAmColorScheme.fields()
        val darkFields = DarkAmColorScheme.fields()
        assertEquals(50, lightFields.size)
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
            assertTrue(
                contrast(scheme.accentActiveContent, scheme.accentActive) >= 4.5,
                "accentActiveContent/accentActive",
            )
            assertTrue(contrast(scheme.onDanger, scheme.dangerFill) >= 4.5, "onDanger/dangerFill")
            assertTrue(contrast(scheme.onDanger, scheme.dangerActive) >= 4.5, "onDanger/dangerActive")
            assertTrue(contrast(scheme.textPrimary, scheme.dangerDisabled) >= 4.5, "textPrimary/dangerDisabled")
            assertTrue(contrast(scheme.textPrimary, scheme.componentActive) >= 4.5, "textPrimary/componentActive")
            assertTrue(contrast(scheme.accentText, scheme.surfaceActive) >= 4.5, "accentText/surfaceActive")
            assertTrue(contrast(scheme.onSuccessFill, scheme.successFill) >= 4.5, "onSuccessFill/successFill")
            assertTrue(contrast(scheme.onWarningFill, scheme.warningFill) >= 4.5, "onWarningFill/warningFill")
            assertTrue(contrast(scheme.accentText, scheme.background) >= 4.5, "accentText/background")
            assertTrue(contrast(scheme.accentText, scheme.surface) >= 4.5, "accentText/surface")
            assertTrue(contrast(scheme.textPrimary, scheme.background) >= 4.5, "textPrimary/background")
            assertTrue(contrast(scheme.textPrimary, scheme.surface) >= 4.5, "textPrimary/surface")
            assertTrue(contrast(scheme.textSecondary, scheme.background) >= 4.5, "textSecondary/background")
            assertTrue(contrast(scheme.textTertiary, scheme.surface) >= 4.5, "textTertiary/surface")
            assertTrue(
                contrast(scheme.textPlaceholder, scheme.surfaceSecondary) >= 4.5,
                "textPlaceholder/surfaceSecondary",
            )
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
