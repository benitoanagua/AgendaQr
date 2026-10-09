package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaShape
import com.agendaqr.core.ui.theme.XauxaRadius
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * P2 — tests PUROS de las funciones del contrato de tiles (todos los
 * targets: Android, iOS, wasm/JVM). Sin Compose: solo matemática.
 */
class XauxaTileContractTest {

    // --- tileLabelMaxLines ---

    @Test
    fun label_max_lines_grows_with_font_scale() {
        assertEquals(2, tileLabelMaxLines(1.0f))
        assertEquals(2, tileLabelMaxLines(1.3f)) // límite exacto: aún 2
        assertEquals(4, tileLabelMaxLines(1.31f)) // justo pasado: 4
        assertEquals(4, tileLabelMaxLines(2.0f))
    }

    // --- tileColumnsFor ---

    @Test
    fun columns_shrink_with_large_font() {
        // Escala normal: tantas columnas como quepan.
        assertEquals(4, tileColumnsFor(4, 1.0f))
        assertEquals(3, tileColumnsFor(3, 1.0f))
        // >1.15: máximo 3 columnas.
        assertEquals(3, tileColumnsFor(4, 1.16f))
        assertEquals(2, tileColumnsFor(2, 1.16f)) // nunca más de las que caben
        // ≥1.5: máximo 2 columnas.
        assertEquals(2, tileColumnsFor(4, 1.5f))
        assertEquals(2, tileColumnsFor(3, 1.5f))
        assertEquals(1, tileColumnsFor(1, 1.5f))
    }

    // --- pageTitleScaleFactor ---

    @Test
    fun page_title_scale_capped_at_1_3x() {
        // Escala normal: factor 1 (sin ajuste).
        assertEquals(1.0f, pageTitleScaleFactor(1.0f))
        assertEquals(1.0f, pageTitleScaleFactor(1.3f))
        // Fuente grande: el factor reduce la escala efectiva del DISPLAY
        // a 1.3x; el cuerpo sigue escalando completo (§11).
        // fontScale=2 → factor = 1.3/2 = 0.65 → display efectivo = 2*0.65 = 1.3x
        val f = pageTitleScaleFactor(2.0f)
        assertTrue(f < 1.0f, "factor < 1 con fuente grande: $f")
        assertEquals(1.3f, 2.0f * f, 0.01f, "escala efectiva del display = 1.3x")
    }

    // --- tileRows con 2 y 3 columnas ---

    @Test
    fun tile_rows_valid_with_two_columns() {
        val items = listOf(
            XauxaTileItem("a", accent = XauxaAccents.System),
            XauxaTileItem("b", accent = XauxaAccents.System),
            XauxaTileItem("w", accent = XauxaAccents.System, size = XauxaTileSize.WIDE),
            XauxaTileItem("c", accent = XauxaAccents.System),
        )
        val rows = tileRows(items, maxUnits = 2)
        // a(1) + b(1) caben; w(2) ocupa fila completa; c(1) abre la cuarta.
        assertEquals(3, rows.size)
        assertEquals(listOf("a", "b"), rows[0].map { it.item.label })
        assertEquals(2, rows[1].first().span) // WIDE llena la fila de 2
        assertEquals(listOf("c"), rows[2].map { it.item.label })
    }

    @Test
    fun tile_rows_valid_with_three_columns() {
        val items = listOf(
            XauxaTileItem("a", accent = XauxaAccents.System),
            XauxaTileItem("b", accent = XauxaAccents.System),
            XauxaTileItem("c", accent = XauxaAccents.System),
            XauxaTileItem("m", accent = XauxaAccents.System, size = XauxaTileSize.MEDIUM),
            XauxaTileItem("d", accent = XauxaAccents.System),
        )
        val rows = tileRows(items, maxUnits = 3)
        // a+b+c en fila 1; m(2)+d(1) en fila 2.
        assertEquals(2, rows.size)
        assertEquals(3, rows[0].size)
        assertEquals(2, rows[1].size)
        assertEquals(listOf(2, 1), rows[1].map { it.span })
    }

    // --- Contraste de TODOS los acentos en reposo y presionado ---

    private fun linearChannel(c: Float): Double {
        val d = c.toDouble()
        return if (d <= 0.04045) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
    }

    private fun luminance(r: Float, g: Float, b: Float): Double =
        0.2126 * linearChannel(r) + 0.7152 * linearChannel(g) + 0.0722 * linearChannel(b)

    private fun contrast(on: Triple<Float, Float, Float>, bg: Triple<Float, Float, Float>): Double {
        val l1 = luminance(on.first, on.second, on.third)
        val l2 = luminance(bg.first, bg.second, bg.third)
        val hi = maxOf(l1, l2)
        val lo = minOf(l1, l2)
        return (hi + 0.05) / (lo + 0.05)
    }

    private fun blend(fg: Triple<Float, Float, Float>, overlay: Triple<Float, Float, Float>, alpha: Float): Triple<Float, Float, Float> =
        Triple(
            fg.first * (1 - alpha) + overlay.first * alpha,
            fg.second * (1 - alpha) + overlay.second * alpha,
            fg.third * (1 - alpha) + overlay.third * alpha,
        )

    @Test
    fun all_accents_meet_contrast_in_rest_and_pressed() {
        // P2: TODOS los acentos admitidos, en reposo y CON el overlay de
        // pulsación (opuesto al texto, alpha 0.08) — nunca bajo 4.5:1.
        val pressedAlpha = 0.08f
        (XauxaAccents.Admitted + XauxaAccents.System).forEach { accent ->
            val bg = Triple(accent.background.red, accent.background.green, accent.background.blue)
            val on = Triple(accent.onAccent.red, accent.onAccent.green, accent.onAccent.blue)
            // Reposo.
            val restRatio = contrast(on, bg)
            assertTrue(
                restRatio >= 4.5,
                "${accent.id} reposo: $restRatio < 4.5",
            )
            // Presionado: overlay OPUESTO al texto (negro si texto blanco).
            val overlayIsBlack = accent.onAccent == androidx.compose.ui.graphics.Color.White
            val overlay = if (overlayIsBlack) Triple(0f, 0f, 0f) else Triple(1f, 1f, 1f)
            val pressedBg = blend(bg, overlay, pressedAlpha)
            val pressedRatio = contrast(on, pressedBg)
            assertTrue(
                pressedRatio >= 4.5,
                "${accent.id} presionado: $pressedRatio < 4.5 (bg=$bg, overlay=$overlay)",
            )
        }
    }

    @Test
    fun tile_stagger_delay_capped_and_zero_with_reduced_motion() {
        assertEquals(0, tileStaggerDelayMs(0, reducedMotion = false))
        assertEquals(40, tileStaggerDelayMs(1, reducedMotion = false))
        assertEquals(300, tileStaggerDelayMs(8, reducedMotion = false)) // tope
        assertEquals(0, tileStaggerDelayMs(5, reducedMotion = true)) // siempre 0
    }
}
