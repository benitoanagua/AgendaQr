package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Contrato de la rejilla de tiles: la matemática es PURA y se fija aquí —
 * el retardo escalonado (30–50 ms por tile; retardo + fundido ≤ 300 ms
 * totales de la spec §12 Movimiento; 0 con reduced motion), el reparto de
 * filas/span y la GEOMETRÍA derivada de la unidad calculada (1×1
 * cuadrado por encima del ancho mínimo).
 */
class XauxaTileGridTest {

    @Test
    fun stagger_delay_is_40ms_per_tile_capped_so_total_fits_in_300ms() {
        assertEquals(0, tileStaggerDelayMs(0, reducedMotion = false))
        assertEquals(40, tileStaggerDelayMs(1, reducedMotion = false))
        assertEquals(80, tileStaggerDelayMs(2, reducedMotion = false))
        // Tope: retardo + fundido (DurationShortMs) = 300 ms totales (spec
        // §12 Movimiento) — antes el último tile terminaba a 450 ms.
        assertEquals(150, tileStaggerDelayMs(8, reducedMotion = false))
        assertEquals(150, tileStaggerDelayMs(50, reducedMotion = false))
    }

    @Test
    fun stagger_plus_fade_never_exceeds_the_300ms_budget() {
        // M11 "total ≤ 300 ms": el presupuesto cubre retardo Y fundido.
        (0..50).forEach { index ->
            val total = tileStaggerDelayMs(index, reducedMotion = false) + XauxaMotion.DurationShortMs
            assertTrue(
                total <= XauxaMotion.DurationMediumMs,
                "index=$index: retardo+fundido=$total > ${XauxaMotion.DurationMediumMs}",
            )
        }
    }

    @Test
    fun stagger_delay_is_zero_with_reduced_motion() {
        // §11: reduced motion = aparición inmediata, sin escalonado.
        (0..50).forEach { index ->
            assertEquals(0, tileStaggerDelayMs(index, reducedMotion = true), "index=$index")
        }
    }

    @Test
    fun small_tile_height_is_square_with_the_computed_unit() {
        // Geometría (defecto de la auditoría): a 411 dp de ancho la unidad
        // real mide ~88,75 dp; con el alto FIJAdo a 76 el 1×1 dejaba de
        // ser cuadrado. El alto se deriva de la unidad calculada.
        val unit = androidx.compose.ui.unit.Dp(88.75f)
        val height = tileHeight(XauxaTileSize.SMALL, unit)
        assertEquals(unit, height, "1×1 debe medir lo mismo de alto que de ancho")
    }

    @Test
    fun medium_and_wide_heights_are_two_units_plus_one_gap() {
        val unit = androidx.compose.ui.unit.Dp(88f)
        val gap = androidx.compose.ui.unit.Dp(12f)
        val expected = unit * 2 + gap
        assertEquals(expected, tileHeight(XauxaTileSize.MEDIUM, unit, gap))
        assertEquals(expected, tileHeight(XauxaTileSize.WIDE, unit, gap))
    }

    @Test
    fun minimum_unit_keeps_the_documented_compact_geometry() {
        // Con la unidad MÍNIMA (XauxaMetrics.TileUnit) y el gap del token,
        // el 2×2 mide el TileWideHeight documentado (2×76+8 = 160).
        assertEquals(
            XauxaMetrics.TileWideHeight,
            tileHeight(XauxaTileSize.WIDE, XauxaMetrics.TileUnit),
        )
        assertEquals(
            XauxaMetrics.TileUnit,
            tileHeight(XauxaTileSize.SMALL, XauxaMetrics.TileUnit),
        )
    }

    @Test
    fun rows_wrap_by_span_and_wide_fills_the_row() {
        val items = listOf(
            XauxaTileItem("a", accent = XauxaAccents.System),
            XauxaTileItem("b", accent = XauxaAccents.System),
            XauxaTileItem("c", accent = XauxaAccents.System),
            XauxaTileItem("w", accent = XauxaAccents.System, size = XauxaTileSize.WIDE),
            XauxaTileItem("d", accent = XauxaAccents.System),
        )
        val rows = tileRows(items, maxUnits = 4)
        // 3 tiles 1x1 en la primera fila; el ancho (4x2) rompe la fila y
        // ocupa la suya completa; el último 1x1 abre la tercera.
        assertEquals(3, rows.size)
        assertEquals(listOf("a", "b", "c"), rows[0].map { it.item.label })
        assertEquals(1, rows[1].size)
        assertEquals(4, rows[1].first().span)
        assertEquals(listOf("d"), rows[2].map { it.item.label })
        // Los índices son los del catálogo original (para el retardo).
        assertEquals(listOf(0, 1, 2), rows[0].map { it.index })
        assertEquals(3, rows[1].first().index)
        assertEquals(4, rows[2].first().index)
    }

    @Test
    fun medium_tile_takes_two_units() {
        val items = listOf(
            XauxaTileItem("m", accent = XauxaAccents.System, size = XauxaTileSize.MEDIUM),
            XauxaTileItem("a", accent = XauxaAccents.System),
            XauxaTileItem("b", accent = XauxaAccents.System),
        )
        val rows = tileRows(items, maxUnits = 4)
        // m(2) + a(1) caben; b no (2+1+1 = 4 cabe justo) — todos en una fila.
        assertEquals(1, rows.size)
        assertEquals(listOf(2, 1, 1), rows[0].map { it.span })
    }
}
