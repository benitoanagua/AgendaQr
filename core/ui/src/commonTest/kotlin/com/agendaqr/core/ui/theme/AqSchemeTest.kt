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
 * Contrato de color de Moon (`moon_tokens` 0.0.6, `MoonColors.light/dark`;
 * `docs/05-design-system/13-catalogo-moon.md`). Los roles semánticos se
 * derivan de la capa primitiva Moon; los valores derivados (D-02) se
 * documentan en el contrato normativo.
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
    fun moon_palette_primitives_are_pinned() {
        fun assertColor(actual: Color, expected: String, name: String) =
            assertEquals(expected, argb(actual), name)

        assertColor(AqPalette.piccolo, "FF5C33CF", "piccolo")
        assertColor(AqPalette.hit, "FF1BD29A", "hit")
        assertColor(AqPalette.goten, "FFFFFFFF", "goten")
        assertColor(AqPalette.popo, "FF000000", "popo")
        assertColor(AqPalette.krillin, "FFFFB319", "krillin")
        assertColor(AqPalette.chichi, "FFFF4E64", "chichi")
        assertColor(AqPalette.roshi, "FF49B356", "roshi")
        assertColor(AqPalette.dodoria, "FFD33030", "dodoria")
        assertColor(AqPalette.cell, "FF95F1D5", "cell")
        assertColor(AqPalette.raditz, "FFB3804A", "raditz")
        assertColor(AqPalette.whis, "FF3448F0", "whis")
        assertColor(AqPalette.nappa, "FF725550", "nappa")
        assertColor(AqPalette.accentGreen, "FF2C6B34", "accentGreen")
        assertColor(AqPalette.accentBronze, "FF745330", "accentBronze")
        assertColor(AqPalette.zeno, "8F000000", "zeno")
        assertColor(AqPalette.lightBeerus, "FFDCDEE3", "lightBeerus")
        assertColor(AqPalette.lightGoku, "FFFFFFFF", "lightGoku")
        assertColor(AqPalette.lightGohan, "FFF6F7F9", "lightGohan")
        assertColor(AqPalette.lightBulma, "FF000000", "lightBulma")
        assertColor(AqPalette.lightTrunks, "FF687083", "lightTrunks")
        assertColor(AqPalette.lightHeles, "0A000000", "lightHeles")
        assertColor(AqPalette.darkBeerus, "FF292929", "darkBeerus")
        assertColor(AqPalette.darkGoku, "FF000000", "darkGoku")
        assertColor(AqPalette.darkGohan, "FF1F1F1F", "darkGohan")
        assertColor(AqPalette.darkBulma, "FFFFFFFF", "darkBulma")
        assertColor(AqPalette.darkTrunks, "FF94989E", "darkTrunks")
        assertColor(AqPalette.darkHeles, "14FFFFFF", "darkHeles")
        assertColor(AqPalette.piccoloDisabled, "995C33CF", "piccoloDisabled")
        assertColor(AqPalette.lightTrunksDisabled, "99687083", "lightTrunksDisabled")
        assertColor(AqPalette.darkTrunksDisabled, "9994989E", "darkTrunksDisabled")
        assertColor(AqPalette.lightBrandHover, "FF512DB6", "lightBrandHover")
        assertColor(AqPalette.lightBrandActive, "FF46279D", "lightBrandActive")
        assertColor(AqPalette.lightEmphasisSurface, "FFEBE7F9", "lightEmphasisSurface")
        assertColor(AqPalette.lightEmphasisSurfaceHover, "FFDED6F5", "lightEmphasisSurfaceHover")
        assertColor(AqPalette.lightSelectionFill, "FFDED6F5", "lightSelectionFill")
        assertColor(AqPalette.lightSurfaceHigh, "FFECEDEF", "lightSurfaceHigh")
        assertColor(AqPalette.lightDanger, "FFBA2A2A", "lightDanger")
        assertColor(AqPalette.lightDangerActive, "FFA02424", "lightDangerActive")
        assertColor(AqPalette.lightDangerContainer, "FFFBEEEE", "lightDangerContainer")
        assertColor(AqPalette.lightDangerDisabled, "FFEDACAC", "lightDangerDisabled")
        assertColor(AqPalette.lightSuccess, "FF2C6B34", "lightSuccess")
        assertColor(AqPalette.lightSuccessFill, "FF337D3C", "lightSuccessFill")
        assertColor(AqPalette.lightSuccessContainer, "FFEDF7EE", "lightSuccessContainer")
        assertColor(AqPalette.lightWarning, "FF805A0C", "lightWarning")
        assertColor(AqPalette.lightWarningContainer, "FFFFF6E3", "lightWarningContainer")
        assertColor(AqPalette.lightInfoContainer, "FFEFF0FE", "lightInfoContainer")
        assertColor(AqPalette.darkBrandHover, "FF6943D3", "darkBrandHover")
        assertColor(AqPalette.darkBrandActive, "FF7654D7", "darkBrandActive")
        assertColor(AqPalette.darkPiccoloText, "FFA58FE5", "darkPiccoloText")
        assertColor(AqPalette.darkEmphasisSurface, "FF2B2342", "darkEmphasisSurface")
        assertColor(AqPalette.darkEmphasisSurfaceHover, "FF332557", "darkEmphasisSurfaceHover")
        assertColor(AqPalette.darkSelectionFill, "FF372765", "darkSelectionFill")
        assertColor(AqPalette.darkSelectionSurface, "FF302550", "darkSelectionSurface")
        assertColor(AqPalette.darkSurfaceHighest, "FF3A3A3A", "darkSurfaceHighest")
        assertColor(AqPalette.darkDanger, "FFFF7A8B", "darkDanger")
        assertColor(AqPalette.darkDangerActive, "FFBA2A2A", "darkDangerActive")
        assertColor(AqPalette.darkDangerContainer, "FF43272A", "darkDangerContainer")
        assertColor(AqPalette.darkDangerDisabled, "FF6A1818", "darkDangerDisabled")
        assertColor(AqPalette.darkSuccessContainer, "FF263728", "darkSuccessContainer")
        assertColor(AqPalette.darkWarningContainer, "FF43371E", "darkWarningContainer")
        assertColor(AqPalette.darkInfo, "FF8F9AF7", "darkInfo")
        assertColor(AqPalette.darkInfoContainer, "FF232749", "darkInfoContainer")
        assertColor(AqPalette.qrCanvas, "FFFFFFFF", "qrCanvas")
    }

    @Test
    fun semantic_tokens_use_the_moon_light_and_dark_roles() {
        val light = LightAqColorScheme
        assertEquals("FFFFFFFF", argb(light.background))
        assertEquals("FFF6F7F9", argb(light.surface))
        assertEquals("FFF6F7F9", argb(light.surfaceSecondary))
        assertEquals("FFF6F7F9", argb(light.inputSurface))
        assertEquals("FFDCDEE3", argb(light.border))
        assertEquals("FF687083", argb(light.functionalBorder))
        assertEquals("FFDCDEE3", argb(light.divider))
        assertEquals("FF000000", argb(light.textPrimary))
        assertEquals("FF687083", argb(light.textSecondary))
        assertEquals("FF687083", argb(light.textPlaceholder))
        assertEquals("FF687083", argb(light.textTertiary))
        assertEquals("FF5C33CF", argb(light.brandFill))
        assertEquals("FF512DB6", argb(light.brandFillHover))
        assertEquals("FF46279D", argb(light.brandFillActive))
        assertEquals("FF5C33CF", argb(light.brandFillSelected))
        assertEquals("995C33CF", argb(light.brandFillDisabled))
        assertEquals("FFFFFFFF", argb(light.onBrandFill))
        assertEquals("FFFFFFFF", argb(light.onBrandFillDisabled))
        assertEquals("FF5C33CF", argb(light.emphasisFill))
        assertEquals("FF5C33CF", argb(light.emphasisBorder))
        assertEquals("FF5C33CF", argb(light.emphasisText))
        assertEquals("FF5C33CF", argb(light.emphasisIcon))
        assertEquals("FFEBE7F9", argb(light.emphasisSurface))
        assertEquals("FFDED6F5", argb(light.emphasisSurfaceHover))
        assertEquals("FFFFFFFF", argb(light.emphasisOnFill))
        assertEquals("FF5C33CF", argb(light.link))
        assertEquals("FFDED6F5", argb(light.selectionFill))
        assertEquals("FFEBE7F9", argb(light.selectionSurface))
        assertEquals("FF5C33CF", argb(light.selectionBorder))
        assertEquals("FF5C33CF", argb(light.selectionIcon))
        assertEquals("FFBA2A2A", argb(light.danger))
        assertEquals("FFD33030", argb(light.dangerFill))
        assertEquals("FFFFFFFF", argb(light.onDanger))
        assertEquals("FFFBEEEE", argb(light.dangerContainer))
        assertEquals("FF2C6B34", argb(light.success))
        assertEquals("FF337D3C", argb(light.successFill))
        assertEquals("FFFFFFFF", argb(light.onSuccessFill))
        assertEquals("FFEDF7EE", argb(light.successContainer))
        assertEquals("FF805A0C", argb(light.warning))
        assertEquals("FFFFB319", argb(light.warningFill))
        assertEquals("FF000000", argb(light.onWarningFill))
        assertEquals("FFFFF6E3", argb(light.warningContainer))
        assertEquals("FF3448F0", argb(light.info))
        assertEquals("FFEFF0FE", argb(light.infoContainer))
        assertEquals("FF5C33CF", argb(light.focus))
        assertEquals("0A000000", argb(light.disabled))
        assertEquals("99687083", argb(light.onDisabled))
        assertEquals("FF000000", argb(light.inverseSurface))
        assertEquals("FFFFFFFF", argb(light.inverseOnSurface))
        assertEquals("8F000000", argb(light.scrim))
        assertEquals("FFFFFFFF", argb(light.qrCanvas))
        assertEquals("FFFFFFFF", argb(light.surfaceContainerLow))
        assertEquals("FFF6F7F9", argb(light.surfaceContainer))
        assertEquals("FFECEDEF", argb(light.surfaceContainerHigh))
        assertEquals("FFDCDEE3", argb(light.surfaceContainerHighest))
        assertEquals("FFECEDEF", argb(light.surfaceActive))
        assertEquals("FFF6F7F9", argb(light.componentActive))
        assertEquals("FFA02424", argb(light.dangerActive))
        assertEquals("FFEDACAC", argb(light.dangerDisabled))

        val dark = DarkAqColorScheme
        assertEquals("FF000000", argb(dark.background))
        assertEquals("FF1F1F1F", argb(dark.surface))
        assertEquals("FF1F1F1F", argb(dark.surfaceSecondary))
        assertEquals("FF1F1F1F", argb(dark.inputSurface))
        assertEquals("FF292929", argb(dark.border))
        assertEquals("FF94989E", argb(dark.functionalBorder))
        assertEquals("FF292929", argb(dark.divider))
        assertEquals("FFFFFFFF", argb(dark.textPrimary))
        assertEquals("FF94989E", argb(dark.textSecondary))
        assertEquals("FF94989E", argb(dark.textPlaceholder))
        assertEquals("FF94989E", argb(dark.textTertiary))
        assertEquals("FF5C33CF", argb(dark.brandFill))
        assertEquals("FF6943D3", argb(dark.brandFillHover))
        assertEquals("FF7654D7", argb(dark.brandFillActive))
        assertEquals("FF5C33CF", argb(dark.brandFillSelected))
        assertEquals("995C33CF", argb(dark.brandFillDisabled))
        assertEquals("FFFFFFFF", argb(dark.onBrandFill))
        assertEquals("FFFFFFFF", argb(dark.onBrandFillDisabled))
        assertEquals("FF5C33CF", argb(dark.emphasisFill))
        assertEquals("FFA58FE5", argb(dark.emphasisBorder))
        assertEquals("FFA58FE5", argb(dark.emphasisText))
        assertEquals("FFA58FE5", argb(dark.emphasisIcon))
        assertEquals("FF2B2342", argb(dark.emphasisSurface))
        assertEquals("FF332557", argb(dark.emphasisSurfaceHover))
        assertEquals("FFFFFFFF", argb(dark.emphasisOnFill))
        assertEquals("FFA58FE5", argb(dark.link))
        assertEquals("FF372765", argb(dark.selectionFill))
        assertEquals("FF302550", argb(dark.selectionSurface))
        assertEquals("FFA58FE5", argb(dark.selectionBorder))
        assertEquals("FFA58FE5", argb(dark.selectionIcon))
        assertEquals("FFFF7A8B", argb(dark.danger))
        assertEquals("FFD33030", argb(dark.dangerFill))
        assertEquals("FFFFFFFF", argb(dark.onDanger))
        assertEquals("FF43272A", argb(dark.dangerContainer))
        assertEquals("FF49B356", argb(dark.success))
        assertEquals("FF337D3C", argb(dark.successFill))
        assertEquals("FFFFFFFF", argb(dark.onSuccessFill))
        assertEquals("FF263728", argb(dark.successContainer))
        assertEquals("FFFFB319", argb(dark.warning))
        assertEquals("FFFFB319", argb(dark.warningFill))
        assertEquals("FF000000", argb(dark.onWarningFill))
        assertEquals("FF43371E", argb(dark.warningContainer))
        assertEquals("FF8F9AF7", argb(dark.info))
        assertEquals("FF232749", argb(dark.infoContainer))
        assertEquals("FFA58FE5", argb(dark.focus))
        assertEquals("14FFFFFF", argb(dark.disabled))
        assertEquals("9994989E", argb(dark.onDisabled))
        assertEquals("FFFFFFFF", argb(dark.inverseSurface))
        assertEquals("FF000000", argb(dark.inverseOnSurface))
        assertEquals("8F000000", argb(dark.scrim))
        assertEquals("FFFFFFFF", argb(dark.qrCanvas))
        assertEquals("FF000000", argb(dark.surfaceContainerLow))
        assertEquals("FF1F1F1F", argb(dark.surfaceContainer))
        assertEquals("FF292929", argb(dark.surfaceContainerHigh))
        assertEquals("FF3A3A3A", argb(dark.surfaceContainerHighest))
        assertEquals("FF292929", argb(dark.surfaceActive))
        assertEquals("FF1F1F1F", argb(dark.componentActive))
        assertEquals("FFBA2A2A", argb(dark.dangerActive))
        assertEquals("FF6A1818", argb(dark.dangerDisabled))
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
