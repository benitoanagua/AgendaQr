package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Roles `text-*` y escalas `font-size`/`font-weight`/`font-line-height`
 * de `@shopify/polaris-tokens@9.4.2`, con `1rem = 16dp` (D-01).
 * La familia Inter se inyecta en composición desde `AqTheme`; la tabla
 * estática de `AqMaterialTypography` conserva el valor por defecto y se
 * reescribe con `withAmFontFamily` antes de llegar a la pantalla.
 */
class AqTypographyTest {

    @Test
    fun canonical_sizes_and_line_heights_match_polaris() {
        assertEquals(36.sp, AqType.Display)
        assertEquals(40.sp, AqType.DisplayMedium)
        assertEquals(36.sp, AqType.HeadlineLarge)
        assertEquals(32.sp, AqType.Headline)
        assertEquals(24.sp, AqType.HeadlineSmall)
        assertEquals(20.sp, AqType.TitleLarge)
        assertEquals(18.sp, AqType.Title)
        assertEquals(16.sp, AqType.TitleMedium)
        assertEquals(14.sp, AqType.TitleSmall)
        assertEquals(16.sp, AqType.Label)
        assertEquals(14.sp, AqType.Body)
        assertEquals(12.sp, AqType.BodySmall)
        assertEquals(30.sp, AqType.Amount)
        assertEquals(14.sp, AqType.AmountSymbol)
        assertEquals(14.sp, AqType.SectionHeader)
        assertEquals(11.sp, AqType.CaptionSmall)
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
        val injected = AqMaterialTypography.withAmFontFamily(FontFamily.Serif)
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
    fun material_roles_have_explicit_polaris_sizes_and_line_heights() {
        assertEquals(36.sp, AqMaterialTypography.headlineLarge.fontSize)
        assertEquals(48.sp, AqMaterialTypography.headlineLarge.lineHeight)
        assertEquals(AqWeight.Bold, AqMaterialTypography.headlineLarge.fontWeight)

        assertEquals(32.sp, AqMaterialTypography.headlineMedium.fontSize)
        assertEquals(40.sp, AqMaterialTypography.headlineMedium.lineHeight)
        assertEquals(AqWeight.Bold, AqMaterialTypography.headlineMedium.fontWeight)

        assertEquals(24.sp, AqMaterialTypography.headlineSmall.fontSize)
        assertEquals(32.sp, AqMaterialTypography.headlineSmall.lineHeight)
        assertEquals(AqWeight.Bold, AqMaterialTypography.headlineSmall.fontWeight)

        assertEquals(20.sp, AqMaterialTypography.titleLarge.fontSize)
        assertEquals(24.sp, AqMaterialTypography.titleLarge.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleLarge.fontWeight)

        assertEquals(16.sp, AqMaterialTypography.titleMedium.fontSize)
        assertEquals(20.sp, AqMaterialTypography.titleMedium.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleMedium.fontWeight)

        assertEquals(14.sp, AqMaterialTypography.titleSmall.fontSize)
        assertEquals(20.sp, AqMaterialTypography.titleSmall.lineHeight)
        assertEquals(AqWeight.SemiBold, AqMaterialTypography.titleSmall.fontWeight)

        assertEquals(16.sp, AqMaterialTypography.bodyLarge.fontSize)
        assertEquals(24.sp, AqMaterialTypography.bodyLarge.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodyLarge.fontWeight)

        assertEquals(14.sp, AqMaterialTypography.bodyMedium.fontSize)
        assertEquals(20.sp, AqMaterialTypography.bodyMedium.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodyMedium.fontWeight)

        assertEquals(12.sp, AqMaterialTypography.bodySmall.fontSize)
        assertEquals(16.sp, AqMaterialTypography.bodySmall.lineHeight)
        assertEquals(AqWeight.Regular, AqMaterialTypography.bodySmall.fontWeight)
    }

    @Test
    fun primary_action_and_amount_styles_use_the_polaris_weights() {
        assertEquals(16.sp, AqTextStyles.ButtonLabel.fontSize)
        assertEquals(AqWeight.SemiBold, AqTextStyles.ButtonLabel.fontWeight)
        assertEquals(30.sp, AqTextStyles.Amount.fontSize)
        assertEquals(40.sp, AqTextStyles.Amount.lineHeight)
        assertEquals(AqWeight.SemiBold, AqTextStyles.Amount.fontWeight)
        assertEquals("tnum", AqTextStyles.Amount.fontFeatureSettings)
        assertEquals(14.sp, AqTextStyles.AmountSymbol.fontSize)
        assertEquals(20.sp, AqTextStyles.AmountSymbol.lineHeight)
        assertEquals(AqWeight.Bold, AqTextStyles.AmountSymbol.fontWeight)
        assertEquals(14.sp, AqTextStyles.SectionHeader.fontSize)
        assertEquals(20.sp, AqTextStyles.SectionHeader.lineHeight)
        assertEquals(AqWeight.Regular, AqTextStyles.SectionHeader.fontWeight)
        assertEquals(11.sp, AqTextStyles.Tag.fontSize)
        assertEquals(18.sp, AqTextStyles.Title.fontSize)
        assertEquals(28.sp, AqTextStyles.Title.lineHeight)
        assertEquals(AqWeight.SemiBold, AqTextStyles.Title.fontWeight)
    }

    @Test
    fun weights_come_only_from_the_published_weight_scale() {
        assertEquals(450, AqWeight.Regular.weight)
        assertEquals(550, AqWeight.Medium.weight)
        assertEquals(650, AqWeight.SemiBold.weight)
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
