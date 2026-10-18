package com.agendaqr.core.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * TDesign Mobile 1.16.2 geometry tokens from `_radius.less` and
 * `_spacer.less`; Compose shadows approximate the source CSS blur radius.
 */
class AmTokenScaleTest {

    @Test
    fun spacing_uses_the_tdesign_scale_with_only_the_documented_micro_step() {
        assertEquals(4.dp, AmSpacing.Xs)
        assertEquals(8.dp, AmSpacing.Sm)
        assertEquals(12.dp, AmSpacing.Md)
        assertEquals(16.dp, AmSpacing.Lg)
        assertEquals(24.dp, AmSpacing.Xl)
        assertEquals(32.dp, AmSpacing.Xxl)
        assertEquals(48.dp, AmSpacing.Xxxl)
        assertEquals(80.dp, AmSpacing.Huge)
        assertEquals(16.dp, AmSpacing.ScreenMargin)
    }

    @Test
    fun radius_scale_matches_tdesign_mobile() {
        assertEquals(3.dp, AmRadius.ExtraSmall)
        assertEquals(4.dp, AmRadius.Band)
        assertEquals(6.dp, AmRadius.Small)
        assertEquals(9.dp, AmRadius.Medium)
        assertEquals(12.dp, AmRadius.Large)
        assertEquals(12.dp, AmRadius.ExtraLarge)
        assertEquals(999.dp, AmRadius.Round)
    }

    @Test
    fun component_dimensions_pin_the_reference_sizes() {
        assertEquals(48.dp, AmMetrics.ControlMinSize)
        assertEquals(48.dp, AmMetrics.TopBarHeight)
        assertEquals(48.dp, AmMetrics.NavBarHeight)
        assertEquals(80.dp, AmMetrics.NavRailWidth)
        assertEquals(40.dp, AmMetrics.ButtonCompactHeight)
        assertEquals(32.dp, AmMetrics.ChipCompactHeight)
        assertEquals(40.dp, AmMetrics.FieldCompactHeight)
        assertEquals(311.dp, AmMetrics.DialogMinWidth)
        assertEquals(96.dp, AmMetrics.EmptyStateGlyphSize)
    }

    @Test
    fun compose_shadow_approximations_derive_from_tdesign_css_blurs() {
        assertEquals(0.dp, AmElevation.None.elevation)
        assertEquals(5.dp, AmElevation.Shadow1.elevation)
        assertEquals(7.dp, AmElevation.Shadow2.elevation)
        assertEquals(15.dp, AmElevation.Shadow3.elevation)
        assertEquals(4.dp, AmElevation.Shadow4.elevation)
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
