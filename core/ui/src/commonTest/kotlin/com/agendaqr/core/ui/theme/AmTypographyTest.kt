package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * TDesign Mobile 1.16.2 type scale from `theme/_font.less`, with the
 * approved system-font and 11 sp minimum deviations.
 */
class AmTypographyTest {

    @Test
    fun canonical_sizes_and_line_heights_match_tdesign() {
        assertEquals(64.sp, AmType.Display)
        assertEquals(48.sp, AmType.DisplayMedium)
        assertEquals(36.sp, AmType.HeadlineLarge)
        assertEquals(28.sp, AmType.Headline)
        assertEquals(24.sp, AmType.HeadlineSmall)
        assertEquals(20.sp, AmType.TitleLarge)
        assertEquals(18.sp, AmType.Title)
        assertEquals(16.sp, AmType.TitleMedium)
        assertEquals(14.sp, AmType.TitleSmall)
        assertEquals(16.sp, AmType.Label)
        assertEquals(14.sp, AmType.Body)
        assertEquals(12.sp, AmType.BodySmall)
        assertEquals(28.sp, AmType.Amount)
        assertEquals(36.sp, AmType.LineAmount)
    }

    @Test
    fun typography_uses_the_platform_default_sans_family() {
        assertEquals(FontFamily.Default, AmType.FamilyUi)
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
        assertTrue(roles.all { it.fontFamily == FontFamily.Default })
    }

    @Test
    fun material_roles_have_explicit_tdesign_sizes_and_line_heights() {
        assertEquals(36.sp, AmMaterialTypography.headlineLarge.fontSize)
        assertEquals(44.sp, AmMaterialTypography.headlineLarge.lineHeight)
        assertEquals(28.sp, AmMaterialTypography.headlineMedium.fontSize)
        assertEquals(36.sp, AmMaterialTypography.headlineMedium.lineHeight)
        assertEquals(24.sp, AmMaterialTypography.headlineSmall.fontSize)
        assertEquals(32.sp, AmMaterialTypography.headlineSmall.lineHeight)
        assertEquals(20.sp, AmMaterialTypography.titleLarge.fontSize)
        assertEquals(28.sp, AmMaterialTypography.titleLarge.lineHeight)
        assertEquals(16.sp, AmMaterialTypography.titleMedium.fontSize)
        assertEquals(24.sp, AmMaterialTypography.titleMedium.lineHeight)
        assertEquals(14.sp, AmMaterialTypography.titleSmall.fontSize)
        assertEquals(22.sp, AmMaterialTypography.titleSmall.lineHeight)
        assertEquals(16.sp, AmMaterialTypography.bodyLarge.fontSize)
        assertEquals(24.sp, AmMaterialTypography.bodyLarge.lineHeight)
        assertEquals(14.sp, AmMaterialTypography.bodyMedium.fontSize)
        assertEquals(22.sp, AmMaterialTypography.bodyMedium.lineHeight)
        assertEquals(12.sp, AmMaterialTypography.bodySmall.fontSize)
        assertEquals(20.sp, AmMaterialTypography.bodySmall.lineHeight)
    }

    @Test
    fun primary_action_and_amount_styles_use_the_reference_weights() {
        assertEquals(16.sp, AmTextStyles.ButtonLabel.fontSize)
        assertEquals(FontWeight.SemiBold, AmTextStyles.ButtonLabel.fontWeight)
        assertEquals(28.sp, AmTextStyles.Amount.fontSize)
        assertEquals(36.sp, AmTextStyles.Amount.lineHeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.Amount.fontWeight)
        assertEquals("tnum", AmTextStyles.Amount.fontFeatureSettings)
        assertEquals(12.sp, AmTextStyles.Tag.fontSize)
        assertEquals(18.sp, AmTextStyles.Title.fontSize)
        assertEquals(26.sp, AmTextStyles.Title.lineHeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.Title.fontWeight)
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
