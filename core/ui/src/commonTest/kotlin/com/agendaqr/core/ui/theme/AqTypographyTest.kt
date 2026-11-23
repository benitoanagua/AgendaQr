package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Roles de texto sobre la escala de Moon (`MoonTextStyles`: cuerpo regular 400,
 * encabezado semibold 600; tamaños 12, 14, 16, 18, 20, 24, 32, 40).
 * La familia Inter se inyecta en composición desde `AqTheme`; la tabla
 * estática de `AqMaterialTypography` conserva el valor por defecto y se
 * reescribe con `withAqFontFamily` antes de llegar a la pantalla.
 */
class AqTypographyTest {

    @Test
    fun canonical_sizes_and_line_heights_follow_the_moon_text_scale() {
        assertEquals(40.sp, AqType.Heading3Xl)
        assertEquals(48.sp, AqType.LineHeightHeading3Xl)
        assertEquals(32.sp, AqType.Heading2Xl)
        assertEquals(40.sp, AqType.LineHeightHeading2Xl)
        assertEquals(24.sp, AqType.HeadingXl)
        assertEquals(32.sp, AqType.LineHeightHeadingXl)
        assertEquals(18.sp, AqType.HeadingLg)
        assertEquals(24.sp, AqType.LineHeightHeadingLg)
        assertEquals(16.sp, AqType.HeadingMd)
        assertEquals(20.sp, AqType.LineHeightHeadingMd)
        assertEquals(14.sp, AqType.HeadingSm)
        assertEquals(20.sp, AqType.LineHeightHeadingSm)
        assertEquals(12.sp, AqType.HeadingXs)
        assertEquals(16.sp, AqType.LineHeightHeadingXs)
        assertEquals(18.sp, AqType.BodyLg)
        assertEquals(28.sp, AqType.LineHeightBodyLg)
        assertEquals(16.sp, AqType.BodyMd)
        assertEquals(24.sp, AqType.LineHeightBodyMd)
        assertEquals(14.sp, AqType.BodySm)
        assertEquals(20.sp, AqType.LineHeightBodySm)
        assertEquals(12.sp, AqType.BodyXs)
        assertEquals(16.sp, AqType.LineHeightBodyXs)
        assertEquals(40.sp, AqType.Display)
        assertEquals(40.sp, AqType.DisplayMedium)
        assertEquals(40.sp, AqType.HeadlineLarge)
        assertEquals(32.sp, AqType.Headline)
        assertEquals(24.sp, AqType.HeadlineSmall)
        assertEquals(20.sp, AqType.TitleLarge)
        assertEquals(18.sp, AqType.Title)
        assertEquals(16.sp, AqType.TitleMedium)
        assertEquals(14.sp, AqType.TitleSmall)
        assertEquals(16.sp, AqType.Label)
        assertEquals(14.sp, AqType.Body)
        assertEquals(12.sp, AqType.BodySmall)
        assertEquals(32.sp, AqType.Amount)
        assertEquals(14.sp, AqType.AmountSymbol)
        assertEquals(14.sp, AqType.SectionHeader)
        assertEquals(12.sp, AqType.CaptionSmall)
        assertEquals(40.sp, AqType.LineAmount)
        assertEquals(20.sp, AqType.LineAmountSymbol)
    }

    @Test
    fun theme_injection_replaces_the_family_in_every_material_role() {
        val roles = listOf(
            AqMaterialTypography.headlineLarge,
            AqMaterialTypography.headlineMedium,
            AqMaterialTypography.headlineSmall,
            AqMaterialTypography.titleLarge,
            AqMaterialTypography.titleMedium,
            AqMaterialTypography.titleSmall,
            AqMaterialTypography.bodyLarge,
            AqMaterialTypography.bodyMedium,
            AqMaterialTypography.bodySmall,
            AqMaterialTypography.labelLarge,
            AqMaterialTypography.labelMedium,
            AqMaterialTypography.labelSmall,
        )
        assertTrue(roles.all { it.fontFamily == null })
        val injected = AqMaterialTypography.withAqFontFamily(FontFamily.Serif)
        assertTrue(
            listOf(
                injected.headlineLarge,
                injected.headlineMedium,
                injected.headlineSmall,
                injected.titleLarge,
                injected.titleMedium,
                injected.titleSmall,
                injected.bodyLarge,
                injected.bodyMedium,
                injected.bodySmall,
                injected.labelLarge,
                injected.labelMedium,
                injected.labelSmall,
            ).all { it.fontFamily == FontFamily.Serif },
        )
    }

    @Test
    fun material_roles_have_explicit_sizes_and_line_heights() {
        assertEquals(40.sp, AqMaterialTypography.headlineLarge.fontSize)
        assertEquals(48.sp, AqMaterialTypography.headlineLarge.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.headlineLarge.fontWeight)

        assertEquals(32.sp, AqMaterialTypography.headlineMedium.fontSize)
        assertEquals(40.sp, AqMaterialTypography.headlineMedium.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.headlineMedium.fontWeight)

        assertEquals(24.sp, AqMaterialTypography.headlineSmall.fontSize)
        assertEquals(32.sp, AqMaterialTypography.headlineSmall.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.headlineSmall.fontWeight)

        assertEquals(18.sp, AqMaterialTypography.titleLarge.fontSize)
        assertEquals(24.sp, AqMaterialTypography.titleLarge.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleLarge.fontWeight)

        assertEquals(16.sp, AqMaterialTypography.titleMedium.fontSize)
        assertEquals(20.sp, AqMaterialTypography.titleMedium.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleMedium.fontWeight)

        assertEquals(14.sp, AqMaterialTypography.titleSmall.fontSize)
        assertEquals(20.sp, AqMaterialTypography.titleSmall.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleSmall.fontWeight)

        assertEquals(18.sp, AqMaterialTypography.bodyLarge.fontSize)
        assertEquals(28.sp, AqMaterialTypography.bodyLarge.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodyLarge.fontWeight)

        assertEquals(16.sp, AqMaterialTypography.bodyMedium.fontSize)
        assertEquals(24.sp, AqMaterialTypography.bodyMedium.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodyMedium.fontWeight)

        assertEquals(14.sp, AqMaterialTypography.bodySmall.fontSize)
        assertEquals(20.sp, AqMaterialTypography.bodySmall.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodySmall.fontWeight)
    }

    @Test
    fun primary_action_and_amount_styles_use_the_moon_weights() {
        assertEquals(16.sp, AqTextStyles.ButtonLabel.fontSize)
        assertEquals(AqWeight.SemiBold, AqTextStyles.ButtonLabel.fontWeight)
        assertEquals(32.sp, AqTextStyles.Amount.fontSize)
        assertEquals(40.sp, AqTextStyles.Amount.lineHeight)
        assertEquals(AqWeight.SemiBold, AqTextStyles.Amount.fontWeight)
        assertEquals("tnum", AqTextStyles.Amount.fontFeatureSettings)
        assertEquals(14.sp, AqTextStyles.AmountSymbol.fontSize)
        assertEquals(20.sp, AqTextStyles.AmountSymbol.lineHeight)
        assertEquals(AqWeight.Bold, AqTextStyles.AmountSymbol.fontWeight)
        assertEquals(14.sp, AqTextStyles.SectionHeader.fontSize)
        assertEquals(20.sp, AqTextStyles.SectionHeader.lineHeight)
        assertEquals(AqWeight.Regular, AqTextStyles.SectionHeader.fontWeight)
        assertEquals(12.sp, AqTextStyles.Tag.fontSize)
        assertEquals(18.sp, AqTextStyles.Title.fontSize)
        assertEquals(24.sp, AqTextStyles.Title.lineHeight)
        assertEquals(AqWeight.SemiBold, AqTextStyles.Title.fontWeight)
    }

    @Test
    fun mobile_text_roles_carry_their_geometry() {
        val roles = listOf(
            AqTextStyles.Heading3Xl to Triple(40f, 48f, AqWeight.SemiBold),
            AqTextStyles.Heading2Xl to Triple(32f, 40f, AqWeight.SemiBold),
            AqTextStyles.HeadingXl to Triple(24f, 32f, AqWeight.SemiBold),
            AqTextStyles.HeadingLg to Triple(18f, 24f, AqWeight.SemiBold),
            AqTextStyles.HeadingMd to Triple(16f, 20f, AqWeight.SemiBold),
            AqTextStyles.HeadingSm to Triple(14f, 20f, AqWeight.SemiBold),
            AqTextStyles.HeadingXs to Triple(12f, 16f, AqWeight.SemiBold),
            AqTextStyles.BodyLg to Triple(18f, 28f, AqWeight.Regular),
            AqTextStyles.BodyMd to Triple(16f, 24f, AqWeight.Regular),
            AqTextStyles.BodySm to Triple(14f, 20f, AqWeight.Regular),
            AqTextStyles.BodyXs to Triple(12f, 16f, AqWeight.Regular),
        )
        roles.forEach { (style, expected) ->
            assertEquals(expected.first, style.fontSize.value)
            assertEquals(expected.second, style.lineHeight.value)
            assertEquals(expected.third, style.fontWeight)
        }
    }

    @Test
    fun weights_follow_moon_regular_body_and_semibold_heading() {
        assertEquals(400, AqWeight.Regular.weight)
        assertEquals(500, AqWeight.Medium.weight)
        assertEquals(600, AqWeight.SemiBold.weight)
        assertEquals(700, AqWeight.Bold.weight)
    }

    @Test
    fun styles_respect_the_minimum_visible_size_and_system_font_scaling() {
        val styles = listOf(
            AqTextStyles.ButtonLabel,
            AqTextStyles.Body,
            AqTextStyles.Label,
            AqTextStyles.BodySmall,
            AqTextStyles.RowTitle,
            AqTextStyles.Amount,
            AqTextStyles.AmountSymbol,
            AqTextStyles.Caption,
            AqTextStyles.Tag,
            AqTextStyles.Support,
            AqTextStyles.SectionHeader,
            AqTextStyles.Title,
        )
        assertTrue(styles.all { it.fontSize.value >= 11f })
        assertTrue(styles.all { it.fontSize.isSp })
    }
}
