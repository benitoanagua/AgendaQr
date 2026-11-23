package com.agendaqr.core.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals

class AqThumbnailSizeTest {
    @Test
    fun landscape_keeps_ratio_and_caps_longest_side() {
        assertEquals(96 to 72, thumbnailScaledSize(4000, 3000, 96))
    }

    @Test
    fun portrait_keeps_ratio_and_caps_longest_side() {
        assertEquals(54 to 96, thumbnailScaledSize(1080, 1920, 96))
    }

    @Test
    fun small_image_is_never_enlarged() {
        assertEquals(40 to 30, thumbnailScaledSize(40, 30, 96))
    }

    @Test
    fun extreme_aspect_ratio_keeps_at_least_one_pixel() {
        assertEquals(96 to 1, thumbnailScaledSize(10_000, 10, 96))
    }

    @Test
    fun invalid_dimensions_fall_back_to_one_pixel() {
        assertEquals(1 to 1, thumbnailScaledSize(0, 100, 96))
    }
}
