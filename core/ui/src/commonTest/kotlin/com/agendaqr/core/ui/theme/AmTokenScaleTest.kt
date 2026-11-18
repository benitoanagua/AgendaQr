package com.agendaqr.core.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Geometría transcrita de `@shopify/polaris-tokens@9.4.2`
 * (`docs/05-design-system/09-instantanea-tokens-polaris-9.4.2.md`).
 * `1rem = 16dp` (D-01) y la escala táctil de 48dp (D-02) son las únicas
 * adaptaciones móviles documentadas.
 */
class AmTokenScaleTest {

    @Test
    fun spacing_uses_the_published_space_scale() {
        assertEquals(0.dp, AmSpacing.None)
        assertEquals(2.dp, AmSpacing.Xs)
        assertEquals(4.dp, AmSpacing.Sm)
        assertEquals(8.dp, AmSpacing.Md)
        assertEquals(16.dp, AmSpacing.Lg)
        assertEquals(24.dp, AmSpacing.Xl)
        assertEquals(32.dp, AmSpacing.Xxl)
        assertEquals(48.dp, AmSpacing.Xxxl)
        assertEquals(96.dp, AmSpacing.Huge)
        assertEquals(16.dp, AmSpacing.ScreenMargin)
    }

    @Test
    fun radius_scale_matches_the_published_border_radius_scale() {
        assertEquals(2.dp, AmRadius.ExtraSmall)
        assertEquals(4.dp, AmRadius.Small)
        assertEquals(8.dp, AmRadius.Medium)
        assertEquals(12.dp, AmRadius.Large)
        assertEquals(16.dp, AmRadius.ExtraLarge)
        assertEquals(8.dp, AmRadius.GridIcon)
        assertEquals(9999.dp, AmRadius.Round)
    }

    @Test
    fun component_dimensions_pin_the_polaris_reference_sizes() {
        assertEquals(48.dp, AmMetrics.ControlMinSize)
        assertEquals(1.dp, AmMetrics.Border)
        assertEquals(1.dp, AmMetrics.DividerHeight)
        assertEquals(4.dp, AmMetrics.BorderStrong)
        assertEquals(48.dp, AmMetrics.TopBarHeight)
        assertEquals(64.dp, AmMetrics.NavBarHeight)
        assertEquals(80.dp, AmMetrics.NavRailWidth)
        assertEquals(48.dp, AmMetrics.ButtonLargeHeight)
        assertEquals(40.dp, AmMetrics.ButtonCompactHeight)
        assertEquals(32.dp, AmMetrics.ButtonSmallHeight)
        assertEquals(24.dp, AmMetrics.ButtonExtraSmallHeight)
        assertEquals(32.dp, AmMetrics.ChipCompactHeight)
        assertEquals(40.dp, AmMetrics.FieldCompactHeight)
        assertEquals(48.dp, AmMetrics.SectionHeaderHeight)
        assertEquals(16.dp, AmMetrics.ListDividerInset)
        assertEquals(16.dp, AmMetrics.TabIndicatorWidth)
        assertEquals(4.dp, AmMetrics.TabIndicatorHeight)
        assertEquals(311.dp, AmMetrics.DialogMinWidth)
        assertEquals(96.dp, AmMetrics.EmptyStateGlyphSize)
        assertEquals(64.dp, AmMetrics.ResultGlyphSize)
    }

    @Test
    fun elevation_keeps_the_published_shadow_blurs() {
        assertEquals(0.dp, AmElevation.None.elevation)
        assertEquals(0f, AmElevation.None.lightAlpha)
        assertEquals(1.dp, AmElevation.Shadow1.elevation)
        assertEquals(0.07f, AmElevation.Shadow1.lightAlpha)
        assertEquals(3.dp, AmElevation.Shadow2.elevation)
        assertEquals(0.07f, AmElevation.Shadow2.lightAlpha)
        assertEquals(6.dp, AmElevation.Shadow3.elevation)
        assertEquals(0.20f, AmElevation.Shadow3.lightAlpha)
        assertEquals(16.dp, AmElevation.Shadow4.elevation)
        assertEquals(0.22f, AmElevation.Shadow4.lightAlpha)
    }

    @Test
    fun composition_fractions_and_alphas_are_centralized() {
        assertEquals(0f, AmAlpha.Hidden)
        assertEquals(0.25f, AmAlpha.SelectedOnAccent)
        assertEquals(0.2f, AmAlpha.SelectedOnTone)
        assertEquals(0.6f, AmComposition.SkeletonTitleWidthFraction)
        assertEquals(0.55f, AmComposition.SuccessCheckIconFraction)
        assertEquals(1.25f, AmType.HeadingLineHeightRatio)
        assertEquals(1.5f, AmType.BodyLineHeightRatio)
    }

    @Test
    fun visible_type_tokens_stay_at_or_above_the_accessibility_floor() {
        val sizes = listOf(
            AmType.Display, AmType.DisplayMedium, AmType.HeadlineLarge, AmType.Headline,
            AmType.HeadlineSmall, AmType.TitleLarge, AmType.Title, AmType.TitleMedium,
            AmType.TitleSmall, AmType.Amount, AmType.AmountSymbol, AmType.RowTitle,
            AmType.SectionHeader, AmType.Body, AmType.Label, AmType.BodySmall,
            AmType.Caption, AmType.CaptionSmall,
        )
        assertTrue(sizes.all { it.value >= 11f }, "un tamaño visible cae por debajo de 11 sp")
    }
}
