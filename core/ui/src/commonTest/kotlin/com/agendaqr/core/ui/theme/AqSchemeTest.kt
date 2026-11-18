package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.agendaqr.core.ui.components.AqTone
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
class AqSchemeTest {

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

    private fun AqColorScheme.fields(): Map<String, Color> = mapOf(
        "background" to background,
        "surface" to surface,
        "surfaceSecondary" to surfaceSecondary,
        "inputSurface" to inputSurface,
        "border" to border,
        "functionalBorder" to functionalBorder,
        "divider" to divider,
        "textPrimary" to textPrimary,
        "textSecondary" to textSecondary,
        "textPlaceholder" to textPlaceholder,
        "textTertiary" to textTertiary,
        "brandFill" to brandFill,
        "brandFillHover" to brandFillHover,
        "brandFillActive" to brandFillActive,
        "brandFillSelected" to brandFillSelected,
        "brandFillDisabled" to brandFillDisabled,
        "onBrandFill" to onBrandFill,
        "onBrandFillDisabled" to onBrandFillDisabled,
        "emphasisFill" to emphasisFill,
        "emphasisBorder" to emphasisBorder,
        "emphasisText" to emphasisText,
        "emphasisIcon" to emphasisIcon,
        "emphasisSurface" to emphasisSurface,
        "emphasisSurfaceHover" to emphasisSurfaceHover,
        "emphasisOnFill" to emphasisOnFill,
        "link" to link,
        "selectionFill" to selectionFill,
        "selectionSurface" to selectionSurface,
        "selectionBorder" to selectionBorder,
        "selectionIcon" to selectionIcon,
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
        "dangerActive" to dangerActive,
        "dangerDisabled" to dangerDisabled,
        "transparent" to transparent,
    )

    @Test
    fun official_palette_primitives_are_pinned() {
        fun assertColor(actual: Color, expected: String, name: String) =
            assertEquals(expected, argb(actual), name)

        assertColor(AqPalette.white, "FFFFFFFF", "white")
        assertColor(AqPalette.black, "FF000000", "black")
        assertColor(AqPalette.gray50, "FFF1F1F1", "gray50")
        assertColor(AqPalette.gray100, "FFEBEBEB", "gray100")
        assertColor(AqPalette.gray200, "FFE3E3E3", "gray200")
        assertColor(AqPalette.gray300, "FFD4D4D4", "gray300")
        assertColor(AqPalette.gray400, "FFB5B5B5", "gray400")
        assertColor(AqPalette.gray500, "FF8A8A8A", "gray500")
        assertColor(AqPalette.gray600, "FF616161", "gray600")
        assertColor(AqPalette.gray700, "FF4A4A4A", "gray700")
        assertColor(AqPalette.gray800, "FF303030", "gray800")
        assertColor(AqPalette.gray900, "FF1A1A1A", "gray900")
        assertColor(AqPalette.lightText, "FF303030", "lightText")
        assertColor(AqPalette.darkText, "FFE3E3E3", "darkText")

        assertColor(AqPalette.emphasis, "FF005BD3", "emphasis")
        assertColor(AqPalette.emphasisHover, "FF004299", "emphasisHover")
        assertColor(AqPalette.emphasisActive, "FF002E6A", "emphasisActive")
        assertColor(AqPalette.emphasisSurface, "FFF0F2FF", "emphasisSurface")
        assertColor(AqPalette.emphasisSurfaceHover, "FFEAEDFF", "emphasisSurfaceHover")
        assertColor(AqPalette.emphasisSurfaceActive, "FFE2E7FF", "emphasisSurfaceActive")
        assertColor(AqPalette.emphasisOnFill, "FFFCFDFF", "emphasisOnFill")
        assertColor(AqPalette.emphasisOnFillHover, "FFE2E7FF", "emphasisOnFillHover")
        assertColor(AqPalette.emphasisOnFillActive, "FFD5DCFF", "emphasisOnFillActive")
        assertColor(AqPalette.textMagic, "FF5700D1", "textMagic")
        assertColor(AqPalette.textMagicSecondary, "FF7126FF", "textMagicSecondary")
        assertColor(AqPalette.magicOnFill, "FFFDFDFF", "magicOnFill")
        assertColor(AqPalette.cautionFill, "FFFFE600", "cautionFill")
        assertColor(AqPalette.avatarOneFill, "FFC530C5", "avatarOneFill")
        assertColor(AqPalette.avatarOneOnFill, "FFFDEFFD", "avatarOneOnFill")
        assertColor(AqPalette.brandFill, "FF303030", "brandFill")
        assertColor(AqPalette.brandFillHover, "FF1A1A1A", "brandFillHover")
        assertColor(AqPalette.brandFillDisabled, "2B000000", "brandFillDisabled")
        assertColor(AqPalette.selectionFill, "FFCCCCCC", "selectionFill")
        assertColor(AqPalette.selectionSurface, "FFF1F1F1", "selectionSurface")
        assertColor(AqPalette.darkBrandFill, "FFFFFFFF", "darkBrandFill")
        assertColor(AqPalette.darkBrandFillHover, "FFF3F3F3", "darkBrandFillHover")
        assertColor(AqPalette.darkBrandFillActive, "FFF7F7F7", "darkBrandFillActive")
        assertColor(AqPalette.darkBrandFillSelected, "FFD4D4D4", "darkBrandFillSelected")
        assertColor(AqPalette.darkBrandFillDisabled, "38FFFFFF", "darkBrandFillDisabled")
        assertColor(AqPalette.info, "FF003A5A", "info")
        assertColor(AqPalette.infoFill, "FF91D0FF", "infoFill")
        assertColor(AqPalette.infoSurface, "FFEAF4FF", "infoSurface")
        assertColor(AqPalette.success, "FF014B40", "success")
        assertColor(AqPalette.successFill, "FF047B5D", "successFill")
        assertColor(AqPalette.successHover, "FF035E4C", "successHover")
        assertColor(AqPalette.successSurface, "FFCDFED4", "successSurface")
        assertColor(AqPalette.successOnFill, "FFFAFFFB", "successOnFill")
        assertColor(AqPalette.warning, "FF5E4200", "warning")
        assertColor(AqPalette.warningFill, "FFFFB800", "warningFill")
        assertColor(AqPalette.warningSurface, "FFFFF1E3", "warningSurface")
        assertColor(AqPalette.warningOnFill, "FF251A00", "warningOnFill")
        assertColor(AqPalette.critical, "FF8E0B21", "critical")
        assertColor(AqPalette.lightSurfaceSecondary, "FFF7F7F7", "lightSurfaceSecondary")
        assertColor(AqPalette.lightSurfaceTertiary, "FFF3F3F3", "lightSurfaceTertiary")
        assertColor(AqPalette.lightInputSurface, "FFFDFDFD", "lightInputSurface")
        assertColor(AqPalette.darkDangerContainer, "FF482A2E", "darkDangerContainer")
        assertColor(AqPalette.darkSuccessContainer, "FF293C37", "darkSuccessContainer")
        assertColor(AqPalette.darkWarningContainer, "FF514628", "darkWarningContainer")
        assertColor(AqPalette.darkInfoContainer, "FF404A51", "darkInfoContainer")
        assertColor(AqPalette.criticalFill, "FFC70A24", "criticalFill")
        assertColor(AqPalette.criticalHover, "FFA30A24", "criticalHover")
        assertColor(AqPalette.criticalSurface, "FFFEE8EB", "criticalSurface")
        assertColor(AqPalette.criticalBorder, "FFFEC1C7", "criticalBorder")
        assertColor(AqPalette.criticalOnFill, "FFFFFAFB", "criticalOnFill")
    }

    @Test
    fun semantic_tokens_use_the_polaris_light_and_dark_roles() {
        val light = LightAqColorScheme
        assertEquals("FFF1F1F1", argb(light.background))
        assertEquals("FFFFFFFF", argb(light.surface))
        assertEquals("FFF7F7F7", argb(light.surfaceSecondary))
        assertEquals("FFFDFDFD", argb(light.inputSurface))
        assertEquals("FFE3E3E3", argb(light.border))
        assertEquals("FF8A8A8A", argb(light.functionalBorder))
        assertEquals("FFE3E3E3", argb(light.divider))
        assertEquals("FF303030", argb(light.textPrimary))
        assertEquals("FF616161", argb(light.textSecondary))
        assertEquals("FF616161", argb(light.textPlaceholder))
        assertEquals("FF616161", argb(light.textTertiary))
        assertEquals("FF303030", argb(light.brandFill))
        assertEquals("FF1A1A1A", argb(light.brandFillHover))
        assertEquals("FF1A1A1A", argb(light.brandFillActive))
        assertEquals("FF303030", argb(light.brandFillSelected))
        assertEquals("2B000000", argb(light.brandFillDisabled))
        assertEquals("FFFFFFFF", argb(light.onBrandFill))
        assertEquals("FFFFFFFF", argb(light.onBrandFillDisabled))
        assertEquals("FF005BD3", argb(light.emphasisFill))
        assertEquals("FF005BD3", argb(light.emphasisBorder))
        assertEquals("FF005BD3", argb(light.emphasisText))
        assertEquals("FF005BD3", argb(light.emphasisIcon))
        assertEquals("FFF0F2FF", argb(light.emphasisSurface))
        assertEquals("FFEAEDFF", argb(light.emphasisSurfaceHover))
        assertEquals("FFFCFDFF", argb(light.emphasisOnFill))
        assertEquals("FF005BD3", argb(light.link))
        assertEquals("FFCCCCCC", argb(light.selectionFill))
        assertEquals("FFF1F1F1", argb(light.selectionSurface))
        assertEquals("FF005BD3", argb(light.selectionBorder))
        assertEquals("FF005BD3", argb(light.selectionIcon))
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
        assertEquals("FF005BD3", argb(light.focus))
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
        assertEquals("FFA30A24", argb(light.dangerActive))
        assertEquals("FFFEC1C7", argb(light.dangerDisabled))

        val dark = DarkAqColorScheme
        assertEquals("FF1A1A1A", argb(dark.background))
        assertEquals("FF303030", argb(dark.surface))
        assertEquals("FF303030", argb(dark.surfaceSecondary))
        assertEquals("FF303030", argb(dark.inputSurface))
        assertEquals("FF4A4A4A", argb(dark.border))
        assertEquals("FF8A8A8A", argb(dark.functionalBorder))
        assertEquals("FF4A4A4A", argb(dark.divider))
        assertEquals("FFE3E3E3", argb(dark.textPrimary))
        assertEquals("FFB5B5B5", argb(dark.textSecondary))
        assertEquals("FFB5B5B5", argb(dark.textPlaceholder))
        assertEquals("FFB5B5B5", argb(dark.textTertiary))
        assertEquals("FFFFFFFF", argb(dark.brandFill))
        assertEquals("FFF3F3F3", argb(dark.brandFillHover))
        assertEquals("FFF7F7F7", argb(dark.brandFillActive))
        assertEquals("FFD4D4D4", argb(dark.brandFillSelected))
        assertEquals("38FFFFFF", argb(dark.brandFillDisabled))
        assertEquals("FF303030", argb(dark.onBrandFill))
        assertEquals("FF8A8A8A", argb(dark.onBrandFillDisabled))
        assertEquals("FF91D0FF", argb(dark.emphasisFill))
        assertEquals("FF91D0FF", argb(dark.emphasisBorder))
        assertEquals("FF91D0FF", argb(dark.emphasisText))
        assertEquals("FF91D0FF", argb(dark.emphasisIcon))
        assertEquals("FF303030", argb(dark.emphasisSurface))
        assertEquals("FF4A4A4A", argb(dark.emphasisSurfaceHover))
        assertEquals("FF1A1A1A", argb(dark.emphasisOnFill))
        assertEquals("FF91D0FF", argb(dark.link))
        assertEquals("FF616161", argb(dark.selectionFill))
        assertEquals("FF616161", argb(dark.selectionSurface))
        assertEquals("FF91D0FF", argb(dark.selectionBorder))
        assertEquals("FF91D0FF", argb(dark.selectionIcon))
        assertEquals("FFFED1D7", argb(dark.danger))
        assertEquals("FFC70A24", argb(dark.dangerFill))
        assertEquals("FFFFFAFB", argb(dark.onDanger))
        assertEquals("FF482A2E", argb(dark.dangerContainer))
        assertEquals("FFCDFED4", argb(dark.success))
        assertEquals("FF047B5D", argb(dark.successFill))
        assertEquals("FFFAFFFB", argb(dark.onSuccessFill))
        assertEquals("FF293C37", argb(dark.successContainer))
        assertEquals("FFFFB800", argb(dark.warning))
        assertEquals("FFFFB800", argb(dark.warningFill))
        assertEquals("FF251A00", argb(dark.onWarningFill))
        assertEquals("FF514628", argb(dark.warningContainer))
        assertEquals("FF91D0FF", argb(dark.info))
        assertEquals("FF404A51", argb(dark.infoContainer))
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
        assertEquals("FFA30A24", argb(dark.dangerActive))
        assertEquals("FF8E0B21", argb(dark.dangerDisabled))
    }

    @Test
    fun both_schemes_expose_the_same_complete_semantic_roles() {
        val lightFields = LightAqColorScheme.fields()
        val darkFields = DarkAqColorScheme.fields()
        assertEquals(60, lightFields.size)
        assertEquals(lightFields.keys, darkFields.keys)
        assertTrue(lightFields.values.none { it == Color.Unspecified })
        assertTrue(darkFields.values.none { it == Color.Unspecified })
        assertNotEquals(LightAqColorScheme, DarkAqColorScheme)
        assertEquals("FFFFFFFF", argb(LightAqColorScheme.qrCanvas))
        assertEquals(LightAqColorScheme.qrCanvas, DarkAqColorScheme.qrCanvas)
    }

    @Test
    fun text_and_filled_state_pairs_meet_wcag_aa() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            assertTrue(contrast(scheme.onBrandFill, scheme.brandFill) >= 4.5, "onBrandFill/brandFill")
            assertTrue(
                contrast(scheme.onBrandFill, scheme.brandFillActive) >= 4.5,
                "onBrandFill/brandFillActive",
            )
            assertTrue(
                contrast(scheme.textPrimary, scheme.selectionFill) >= 4.5,
                "textPrimary/selectionFill",
            )
            assertTrue(
                contrast(scheme.emphasisText, scheme.emphasisSurface) >= 4.5,
                "emphasisText/emphasisSurface",
            )
            assertTrue(contrast(scheme.onDanger, scheme.dangerFill) >= 4.5, "onDanger/dangerFill")
            assertTrue(contrast(scheme.onDanger, scheme.dangerActive) >= 4.5, "onDanger/dangerActive")
            assertTrue(contrast(scheme.textPrimary, scheme.dangerDisabled) >= 4.5, "textPrimary/dangerDisabled")
            assertTrue(contrast(scheme.textPrimary, scheme.componentActive) >= 4.5, "textPrimary/componentActive")
            assertTrue(contrast(scheme.emphasisText, scheme.surfaceActive) >= 4.5, "emphasisText/surfaceActive")
            assertTrue(contrast(scheme.onSuccessFill, scheme.successFill) >= 4.5, "onSuccessFill/successFill")
            assertTrue(contrast(scheme.onWarningFill, scheme.warningFill) >= 4.5, "onWarningFill/warningFill")
            assertTrue(contrast(scheme.emphasisText, scheme.background) >= 4.5, "emphasisText/background")
            assertTrue(contrast(scheme.emphasisText, scheme.surface) >= 4.5, "emphasisText/surface")
            assertTrue(contrast(scheme.link, scheme.background) >= 4.5, "link/background")
            assertTrue(contrast(scheme.link, scheme.surface) >= 4.5, "link/surface")
            assertTrue(contrast(scheme.textPrimary, scheme.background) >= 4.5, "textPrimary/background")
            assertTrue(contrast(scheme.textPrimary, scheme.surface) >= 4.5, "textPrimary/surface")
            assertTrue(contrast(scheme.textSecondary, scheme.background) >= 4.5, "textSecondary/background")
            assertTrue(contrast(scheme.textTertiary, scheme.surface) >= 4.5, "textTertiary/surface")
            assertTrue(contrast(scheme.textPlaceholder, scheme.surfaceSecondary) >= 4.5, "textPlaceholder/surfaceSecondary")
            assertTrue(contrast(scheme.textPrimary, scheme.inputSurface) >= 4.5, "textPrimary/inputSurface")
            assertTrue(contrast(scheme.textPlaceholder, scheme.inputSurface) >= 4.5, "textPlaceholder/inputSurface")
            assertTrue(contrast(scheme.focus, scheme.background) >= 3.0, "focus/background")
            assertTrue(contrast(scheme.functionalBorder, scheme.surface) >= 3.0, "functionalBorder/surface")
            assertTrue(
                contrast(scheme.functionalBorder, scheme.inputSurface) >= 3.0,
                "functionalBorder/inputSurface",
            )
        }
    }

    @Test
    fun state_tone_text_meets_wcag_aa_on_its_container() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            AqTone.entries.forEach { tone ->
                val colors = scheme.toneColors(tone)
                assertTrue(
                    contrast(colors.content, colors.container) >= 4.5,
                    "$tone content/container",
                )
            }
            assertEquals(scheme.success, scheme.toneColors(AqTone.Success).content)
            assertEquals(scheme.warning, scheme.toneColors(AqTone.Warning).content)
            assertEquals(scheme.danger, scheme.toneColors(AqTone.Danger).content)
            assertEquals(scheme.info, scheme.toneColors(AqTone.Info).content)
        }
    }
}
