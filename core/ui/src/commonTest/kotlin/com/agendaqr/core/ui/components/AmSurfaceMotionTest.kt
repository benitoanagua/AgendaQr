package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AmMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Duraciones y curvas de `@shopify/polaris-tokens@9.4.2`. Los roles de
 * producto son derivaciones documentadas (X-04).
 */
class AmSurfaceMotionTest {

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        assertEquals(0, AmMotion.DurationReducedMs)
    }

    @Test
    fun durations_match_the_published_motion_scale() {
        assertEquals(150, AmMotion.DurationShortMs)
        assertEquals(200, AmMotion.DurationMediumMs)
        assertEquals(250, AmMotion.DurationLongMs)
        assertTrue(AmMotion.DurationShortMs > AmMotion.DurationReducedMs)
        assertTrue(AmMotion.DurationShortMs < AmMotion.DurationMediumMs)
        assertTrue(AmMotion.DurationMediumMs < AmMotion.DurationLongMs)
    }

    @Test
    fun standard_easing_matches_the_published_cubic_bezier() {
        assertEquals(0.25f, AmMotion.EasingControlX1)
        assertEquals(0.1f, AmMotion.EasingControlY1)
        assertEquals(0.25f, AmMotion.EasingControlX2)
        assertEquals(1f, AmMotion.EasingControlY2)
    }

    @Test
    fun the_slide_remains_a_small_fraction_of_the_width() {
        assertEquals(8, AmSurfaceSlideFraction)
    }
}
