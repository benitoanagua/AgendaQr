package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqMotion.SurfaceSlideFraction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Duraciones y curvas de `@shopify/polaris-tokens@9.4.2`. Los roles de
 * producto son derivaciones documentadas (X-04).
 */
class AqSurfaceMotionTest {

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        assertEquals(0, AqMotion.DurationReducedMs)
    }

    @Test
    fun durations_match_the_published_motion_scale() {
        assertEquals(150, AqMotion.DurationShortMs)
        assertEquals(200, AqMotion.DurationMediumMs)
        assertEquals(250, AqMotion.DurationLongMs)
        assertTrue(AqMotion.DurationShortMs > AqMotion.DurationReducedMs)
        assertTrue(AqMotion.DurationShortMs < AqMotion.DurationMediumMs)
        assertTrue(AqMotion.DurationMediumMs < AqMotion.DurationLongMs)
    }

    @Test
    fun standard_easing_matches_the_published_cubic_bezier() {
        assertEquals(0.25f, AqMotion.EasingControlX1)
        assertEquals(0.1f, AqMotion.EasingControlY1)
        assertEquals(0.25f, AqMotion.EasingControlX2)
        assertEquals(1f, AqMotion.EasingControlY2)
    }

    @Test
    fun the_slide_remains_a_small_fraction_of_the_width() {
        assertEquals(8, SurfaceSlideFraction)
        assertEquals(SurfaceSlideFraction, AqMotion.SurfaceSlideFraction)
    }

    @Test
    fun predictive_back_geometry_is_centralized() {
        assertEquals(0.08f, AqMotion.PredictiveBackShrink)
        assertEquals(0.5f, AqMotion.PredictiveBackFade)
    }
}
