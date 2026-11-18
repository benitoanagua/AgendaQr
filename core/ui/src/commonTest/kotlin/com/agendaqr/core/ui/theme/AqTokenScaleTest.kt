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
class AqTokenScaleTest {

    @Test
    fun spacing_uses_the_published_space_scale() {
        assertEquals(0.dp, AqSpacing.None)
        assertEquals(2.dp, AqSpacing.Xs)
        assertEquals(4.dp, AqSpacing.Sm)
        assertEquals(8.dp, AqSpacing.Md)
        assertEquals(16.dp, AqSpacing.Lg)
        assertEquals(24.dp, AqSpacing.Xl)
        assertEquals(32.dp, AqSpacing.Xxl)
        assertEquals(48.dp, AqSpacing.Xxxl)
        assertEquals(96.dp, AqSpacing.Huge)
        assertEquals(16.dp, AqSpacing.ScreenMargin)
    }

    @Test
    fun radius_scale_matches_the_published_border_radius_scale() {
        assertEquals(2.dp, AqRadius.ExtraSmall)
        assertEquals(4.dp, AqRadius.Small)
        assertEquals(8.dp, AqRadius.Medium)
        assertEquals(12.dp, AqRadius.Large)
        assertEquals(16.dp, AqRadius.ExtraLarge)
        assertEquals(8.dp, AqRadius.GridIcon)
        assertEquals(9999.dp, AqRadius.Round)
    }

    @Test
    fun component_dimensions_pin_the_polaris_reference_sizes() {
        assertEquals(48.dp, AqMetrics.ControlMinSize)
        assertEquals(1.dp, AqMetrics.Border)
        assertEquals(1.dp, AqMetrics.DividerHeight)
        assertEquals(4.dp, AqMetrics.BorderStrong)
        assertEquals(48.dp, AqMetrics.TopBarHeight)
        assertEquals(64.dp, AqMetrics.NavBarHeight)
        assertEquals(80.dp, AqMetrics.NavRailWidth)
        assertEquals(48.dp, AqMetrics.ButtonLargeHeight)
        assertEquals(40.dp, AqMetrics.ButtonCompactHeight)
        assertEquals(32.dp, AqMetrics.ButtonSmallHeight)
        assertEquals(24.dp, AqMetrics.ButtonExtraSmallHeight)
        assertEquals(32.dp, AqMetrics.ChipCompactHeight)
        assertEquals(40.dp, AqMetrics.FieldCompactHeight)
        assertEquals(48.dp, AqMetrics.SectionHeaderHeight)
        assertEquals(16.dp, AqMetrics.ListDividerInset)
        assertEquals(16.dp, AqMetrics.TabIndicatorWidth)
        assertEquals(4.dp, AqMetrics.TabIndicatorHeight)
        assertEquals(311.dp, AqMetrics.DialogMinWidth)
        assertEquals(96.dp, AqMetrics.EmptyStateGlyphSize)
        assertEquals(64.dp, AqMetrics.ResultGlyphSize)
    }

    @Test
    fun elevation_keeps_the_published_shadow_blurs() {
        assertEquals(0.dp, AqElevation.None.elevation)
        assertEquals(0f, AqElevation.None.lightAlpha)
        assertEquals(1.dp, AqElevation.Shadow1.elevation)
        assertEquals(0.07f, AqElevation.Shadow1.lightAlpha)
        assertEquals(3.dp, AqElevation.Shadow2.elevation)
        assertEquals(0.07f, AqElevation.Shadow2.lightAlpha)
        assertEquals(6.dp, AqElevation.Shadow3.elevation)
        assertEquals(0.20f, AqElevation.Shadow3.lightAlpha)
        assertEquals(16.dp, AqElevation.Shadow4.elevation)
        assertEquals(0.22f, AqElevation.Shadow4.lightAlpha)
    }

    @Test
    fun composition_fractions_and_alphas_are_centralized() {
        assertEquals(0f, AqAlpha.Hidden)
        assertEquals(0.25f, AqAlpha.SelectedOnFill)
        assertEquals(0.2f, AqAlpha.SelectedOnTone)
        assertEquals(0.6f, AqComposition.SkeletonTitleWidthFraction)
        assertEquals(0.55f, AqComposition.SuccessCheckIconFraction)
        assertEquals(1.25f, AqType.HeadingLineHeightRatio)
        assertEquals(1.5f, AqType.BodyLineHeightRatio)
    }

    @Test
    fun visible_type_tokens_stay_at_or_above_the_accessibility_floor() {
        val sizes = listOf(
            AqType.Display, AqType.DisplayMedium, AqType.HeadlineLarge, AqType.Headline,
            AqType.HeadlineSmall, AqType.TitleLarge, AqType.Title, AqType.TitleMedium,
            AqType.TitleSmall, AqType.Amount, AqType.AmountSymbol, AqType.RowTitle,
            AqType.SectionHeader, AqType.Body, AqType.Label, AqType.BodySmall,
            AqType.Caption, AqType.CaptionSmall,
        )
        assertTrue(sizes.all { it.value >= 11f }, "un tamaño visible cae por debajo de 11 sp")
    }
}
