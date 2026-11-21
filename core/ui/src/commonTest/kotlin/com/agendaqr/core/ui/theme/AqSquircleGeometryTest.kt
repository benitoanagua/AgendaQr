package com.agendaqr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AqSquircleGeometryTest {

    @Test
    fun control_points_satisfy_the_construction_invariants() {
        listOf(4f, 8f, 12f, 16f, 30f).forEach { radius ->
            val g = aqSquircleGeometry(radius, 0.9f, 200f, 120f)
            assertEquals(2f * g.b, g.a, 1e-3f, "a = 2b (r=$radius)")
            assertEquals(g.arcLength + g.c + g.d + 3f * g.b, g.p, 1e-3f, "p closure (r=$radius)")
        }
    }

    @Test
    fun radius_is_clamped_to_half_the_shorter_side() {
        val g = aqSquircleGeometry(1000f, 0.9f, 100f, 40f)
        assertEquals(20f, g.cornerRadius, 1e-4f)
        assertTrue(g.p <= 20f + 1e-4f)
    }

    @Test
    fun zero_radius_degenerates_to_a_plain_corner() {
        val g = aqSquircleGeometry(0f, 0.9f, 100f, 40f)
        assertEquals(0f, g.cornerRadius)
        assertEquals(0f, g.p)
    }
}
