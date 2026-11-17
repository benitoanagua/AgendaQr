package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Roles `text-*` y escalas `font-size`/`font-weight`/`font-line-height`
 * de `@shopify/polaris-tokens@9.4.2`, con `1rem = 16dp` (D-01).
 * La familia Inter se inyecta en composición desde `AmTheme`; la tabla
 * estática de `AmMaterialTypography` conserva el valor por defecto y se
 * reescribe con `withAmFontFamily` antes de llegar a la pantalla.
 */
class AmTypographyTest {

    @Test
    fun canonical_sizes_and_line_heights_match_polaris() {
        assertEquals(36.sp, AmType.Display)
        assertEquals(40.sp, AmType.DisplayMedium)
        assertEquals(36.sp, AmType.HeadlineLarge)
        assertEquals(32.sp, AmType.Headline)
        assertEquals(24.sp, AmType.HeadlineSmall)
        assertEquals(20.sp, AmType.TitleLarge)
        assertEquals(18.sp, AmType.Title)
        assertEquals(16.sp, AmType.TitleMedium)
        assertEquals(14.sp, AmType.TitleSmall)
        assertEquals(16.sp, AmType.Label)
        assertEquals(14.sp, AmType.Body)
        assertEquals(12.sp, AmType.BodySmall)
        assertEquals(30.sp, AmType.Amount)
        assertEquals(14.sp, AmType.AmountSymbol)
        assertEquals(14.sp, AmType.SectionHeader)
        assertEquals(11.sp, AmType.CaptionSmall)
        assertEquals(40.sp, AmType.LineAmount)
        assertEquals(20.sp, AmType.LineAmountSymbol)
    }

    @Test
    fun theme_injection_replaces_the_family_in_every_material_role() {
        val roles = listOf(
            AmMaterialTypography.headlineLarge,
            AmMaterialTypography.headlineMedium,
            AmMaterialTypography.headlineSmall,
            AmMaterialTypography.titleLarge,
            AmMaterialTypography.titleMedium,
            AmMaterialTypography.titleSmall,
            AmMaterialTypography.bodyLarge,
            AmMaterialTypography.bodyMedium,
            AmMaterialTypography.bodySmall,
            AmMaterialTypography.labelLarge,
            AmMaterialTypography.labelMedium,
            AmMaterialTypography.labelSmall,
        )
        assertTrue(roles.all { it.fontFamily == null })
        val injected = AmMaterialTypography.withAmFontFamily(FontFamily.Serif)
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
        assertEquals(36.sp, AmMaterialTypography.headlineLarge.fontSize)
        assertEquals(48.sp, AmMaterialTypography.headlineLarge.lineHeight)
        assertEquals(AmWeight.Bold, AmMaterialTypography.headlineLarge.fontWeight)

        assertEquals(32.sp, AmMaterialTypography.headlineMedium.fontSize)
        assertEquals(40.sp, AmMaterialTypography.headlineMedium.lineHeight)
        assertEquals(AmWeight.Bold, AmMaterialTypography.headlineMedium.fontWeight)

        assertEquals(24.sp, AmMaterialTypography.headlineSmall.fontSize)
        assertEquals(32.sp, AmMaterialTypography.headlineSmall.lineHeight)
        assertEquals(AmWeight.Bold, AmMaterialTypography.headlineSmall.fontWeight)

        assertEquals(20.sp, AmMaterialTypography.titleLarge.fontSize)
        assertEquals(24.sp, AmMaterialTypography.titleLarge.lineHeight)
        assertEquals(AmWeight.SemiBold, AmMaterialTypography.titleLarge.fontWeight)

        assertEquals(16.sp, AmMaterialTypography.titleMedium.fontSize)
        assertEquals(20.sp, AmMaterialTypography.titleMedium.lineHeight)
        assertEquals(AmWeight.SemiBold, AmMaterialTypography.titleMedium.fontWeight)

        assertEquals(14.sp, AmMaterialTypography.titleSmall.fontSize)
        assertEquals(20.sp, AmMaterialTypography.titleSmall.lineHeight)
        assertEquals(AmWeight.SemiBold, AmMaterialTypography.titleSmall.fontWeight)

        assertEquals(16.sp, AmMaterialTypography.bodyLarge.fontSize)
        assertEquals(24.sp, AmMaterialTypography.bodyLarge.lineHeight)
        assertEquals(AmWeight.Regular, AmMaterialTypography.bodyLarge.fontWeight)

        assertEquals(14.sp, AmMaterialTypography.bodyMedium.fontSize)
        assertEquals(20.sp, AmMaterialTypography.bodyMedium.lineHeight)
        assertEquals(AmWeight.Regular, AmMaterialTypography.bodyMedium.fontWeight)

        assertEquals(12.sp, AmMaterialTypography.bodySmall.fontSize)
        assertEquals(16.sp, AmMaterialTypography.bodySmall.lineHeight)
        assertEquals(AmWeight.Regular, AmMaterialTypography.bodySmall.fontWeight)
    }

    @Test
    fun primary_action_and_amount_styles_use_the_polaris_weights() {
        assertEquals(16.sp, AmTextStyles.ButtonLabel.fontSize)
        assertEquals(AmWeight.SemiBold, AmTextStyles.ButtonLabel.fontWeight)
        assertEquals(30.sp, AmTextStyles.Amount.fontSize)
        assertEquals(40.sp, AmTextStyles.Amount.lineHeight)
        assertEquals(AmWeight.SemiBold, AmTextStyles.Amount.fontWeight)
        assertEquals("tnum", AmTextStyles.Amount.fontFeatureSettings)
        assertEquals(14.sp, AmTextStyles.AmountSymbol.fontSize)
        assertEquals(20.sp, AmTextStyles.AmountSymbol.lineHeight)
        assertEquals(AmWeight.Bold, AmTextStyles.AmountSymbol.fontWeight)
        assertEquals(14.sp, AmTextStyles.SectionHeader.fontSize)
        assertEquals(20.sp, AmTextStyles.SectionHeader.lineHeight)
        assertEquals(AmWeight.Regular, AmTextStyles.SectionHeader.fontWeight)
        assertEquals(11.sp, AmTextStyles.Tag.fontSize)
        assertEquals(18.sp, AmTextStyles.Title.fontSize)
        assertEquals(28.sp, AmTextStyles.Title.lineHeight)
        assertEquals(AmWeight.SemiBold, AmTextStyles.Title.fontWeight)
    }

    @Test
    fun weights_come_only_from_the_published_weight_scale() {
        assertEquals(450, AmWeight.Regular.weight)
        assertEquals(550, AmWeight.Medium.weight)
        assertEquals(650, AmWeight.SemiBold.weight)
        assertEquals(700, AmWeight.Bold.weight)
    }

    @Test
    fun styles_respect_the_minimum_visible_size_and_system_font_scaling() {
        val styles = listOf(
            AmTextStyles.ButtonLabel,
            AmTextStyles.Body,
            AmTextStyles.Label,
            AmTextStyles.BodySmall,
            AmTextStyles.RowTitle,
            AmTextStyles.Amount,
            AmTextStyles.AmountSymbol,
            AmTextStyles.Caption,
            AmTextStyles.Tag,
            AmTextStyles.Support,
            AmTextStyles.SectionHeader,
            AmTextStyles.Title,
        )
        assertTrue(styles.all { it.fontSize.value >= 11f })
        assertTrue(styles.all { it.fontSize.isSp })
    }
}
