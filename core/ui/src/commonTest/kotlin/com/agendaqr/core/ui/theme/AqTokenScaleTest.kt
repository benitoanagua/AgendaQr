package com.agendaqr.core.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Geometría de producto sobre la escala de Moon (`moon_design` 1.1.0,
 * `docs/05-design-system/13-catalogo-moon.md`). La escala táctil de 48dp
 * (D-02) es la única adaptación móvil documentada.
 */
class AqTokenScaleTest {

    @Test
    fun spacing_uses_the_product_space_scale() {
        assertEquals(0.dp, AqSpacing.None)
        assertEquals(2.dp, AqSpacing.Xs)
        assertEquals(4.dp, AqSpacing.Sm)
        assertEquals(8.dp, AqSpacing.Md)
        assertEquals(12.dp, AqSpacing.MdPlus)
        assertEquals(16.dp, AqSpacing.Lg)
        assertEquals(20.dp, AqSpacing.LgPlus)
        assertEquals(24.dp, AqSpacing.Xl)
        assertEquals(32.dp, AqSpacing.Xxl)
        assertEquals(48.dp, AqSpacing.Xxxl)
        assertEquals(96.dp, AqSpacing.Huge)
        assertEquals(16.dp, AqSpacing.ScreenMargin)
    }

    @Test
    fun product_layout_tokens_are_pinned() {
        assertEquals(16.dp, AqLayout.ScreenMargin)
        assertEquals(16.dp, AqLayout.BetweenOrganisms)
        assertEquals(8.dp, AqLayout.InGroup)
        assertEquals(12.dp, AqLayout.CellPadding)
        assertEquals(16.dp, AqLayout.CardPadding)
        assertEquals(8.dp, AqLayout.LabelToField)
        assertEquals(12.dp, AqLayout.DockVertical)
    }

    @Test
    fun radius_scale_is_pinned() {
        assertEquals(2.dp, AqRadius.ExtraSmall)
        assertEquals(4.dp, AqRadius.Small)
        assertEquals(8.dp, AqRadius.Medium)
        assertEquals(12.dp, AqRadius.Large)
        assertEquals(16.dp, AqRadius.ExtraLarge)
        assertEquals(8.dp, AqRadius.GridIcon)
        assertEquals(9999.dp, AqRadius.Round)
    }

    @Test
    fun component_dimensions_are_pinned() {
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
    fun shadows_keep_the_moon_layer_geometry() {
        assertEquals(listOf(AqShadow.Sm, AqShadow.Md, AqShadow.Lg, AqShadow.Xl), AqShadow.entries)
        assertEquals(listOf(1.dp, 6.dp), AqShadow.Sm.light.map { it.blur })
        assertEquals((-6).dp, AqShadow.Sm.light[1].spread)
        assertEquals(0.4f, AqShadow.Sm.light[0].alpha)
        assertEquals(12.dp, AqShadow.Md.light[1].offsetY)
        assertEquals(24.dp, AqShadow.Lg.light[1].blur)
        assertEquals(3, AqShadow.Xl.light.size)
        assertEquals(2, AqShadow.Xl.dark.size)
        assertEquals(0.639f, AqShadow.Md.dark[1].alpha)
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
