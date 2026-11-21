package com.agendaqr.core.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Transcripción de los tokens de la capa UX (ADR-0039) desde `moon_design`
 * 1.1.0 / `tokens/{sizes,borders,opacities,shadows,transitions}.dart`.
 */
class AqMoonTokensTest {

    @Test
    fun sizes_follow_the_published_scale() {
        assertEquals(listOf(2, 4, 8, 12, 16, 24, 32, 40, 48, 56, 64).map { it.dp }, listOf(
            AqSize.X6s, AqSize.X5s, AqSize.X4s, AqSize.X3s, AqSize.X2s, AqSize.Xs,
            AqSize.Sm, AqSize.Md, AqSize.Lg, AqSize.Xl, AqSize.X2l,
        ))
    }

    @Test
    fun corners_and_strokes_follow_the_published_tokens() {
        assertEquals(4.dp, AqCorner.InteractiveXs)
        assertEquals(8.dp, AqCorner.InteractiveSm)
        assertEquals(12.dp, AqCorner.InteractiveMd)
        assertEquals(16.dp, AqCorner.SurfaceLg)
        assertEquals(1.dp, AqStroke.Default)
        assertEquals(1.5.dp, AqStroke.Active)
        assertEquals(0.9f, AqCorner.Smoothing)
    }

    @Test
    fun disabled_alpha_is_sixty_percent() {
        assertEquals(0.6f, AqStateAlpha.Disabled)
    }

    @Test
    fun shadows_have_the_published_layer_counts() {
        assertEquals(2, AqShadow.Sm.light.size)
        assertEquals(2, AqShadow.Md.dark.size)
        assertEquals(3, AqShadow.Xl.light.size)
        assertEquals(2, AqShadow.Xl.dark.size)
        assertEquals(24.dp, AqShadow.Lg.light[1].blur)
        assertEquals(8.dp, AqShadow.Lg.light[1].offsetY)
        assertEquals(24.dp, AqShadow.Lg.dark[1].offsetY)
        assertEquals((-6).dp, AqShadow.Sm.light[1].spread)
    }

    @Test
    fun dark_shadows_are_never_lighter_than_light_ones() {
        AqShadow.entries.forEach { level ->
            val light = level.light.maxOf { it.alpha }
            val dark = level.dark.maxOf { it.alpha }
            assertTrue(dark >= light, "$level dark alpha must be >= light alpha")
        }
    }

    @Test
    fun transition_curve_is_ease_in_out_cubic() {
        assertEquals(0f, AqMotion.Easings.InOutCubic.transform(0f), 1e-4f)
        assertEquals(1f, AqMotion.Easings.InOutCubic.transform(1f), 1e-4f)
        assertEquals(AqMotion.DurationMediumMs, 200)
    }
}
