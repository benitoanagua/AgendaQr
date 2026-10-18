package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.AmMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** TDesign Mobile 1.16.2 motion values from the mobile theme components. */
class AmSurfaceMotionTest {

    @Test
    fun reduced_motion_transitions_have_zero_duration() {
        assertEquals(0, AmMotion.DurationReducedMs)
    }

    @Test
    fun durations_match_tdesign_mobile() {
        assertEquals(200, AmMotion.DurationShortMs)
        assertEquals(240, AmMotion.DurationMediumMs)
        assertEquals(280, AmMotion.DurationLongMs)
        assertTrue(AmMotion.DurationShortMs > AmMotion.DurationReducedMs)
        assertTrue(AmMotion.DurationShortMs < AmMotion.DurationMediumMs)
        assertTrue(AmMotion.DurationMediumMs < AmMotion.DurationLongMs)
    }

    @Test
    fun standard_easing_matches_tdesign_cubic_bezier() {
        assertEquals(0.38f, AmMotion.EasingControlX1)
        assertEquals(0f, AmMotion.EasingControlY1)
        assertEquals(0.24f, AmMotion.EasingControlX2)
        assertEquals(1f, AmMotion.EasingControlY2)
    }

    @Test
    fun the_slide_remains_a_small_fraction_of_the_width() {
        assertEquals(8, AmSurfaceSlideFraction)
    }
}
