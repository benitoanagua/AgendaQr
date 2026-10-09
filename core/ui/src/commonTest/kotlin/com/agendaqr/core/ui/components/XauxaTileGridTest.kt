package com.agendaqr.core.ui.components

import com.agendaqr.core.ui.theme.XauxaAccents
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Fase 3 (auditoría): la matemática de la rejilla de tiles es PURA y se
 * fija aquí — el retardo escalonado (30–50 ms por tile, total <= 300 ms,
 * 0 con reduced motion) y el reparto de filas/span.
 */
class XauxaTileGridTest {

    @Test
    fun stagger_delay_is_40ms_per_tile_capped_at_300ms() {
        assertEquals(0, tileStaggerDelayMs(0, reducedMotion = false))
        assertEquals(40, tileStaggerDelayMs(1, reducedMotion = false))
        assertEquals(80, tileStaggerDelayMs(2, reducedMotion = false))
        // Tope total de la spec §12 Movimiento: 300 ms.
        assertEquals(300, tileStaggerDelayMs(8, reducedMotion = false))
        assertEquals(300, tileStaggerDelayMs(50, reducedMotion = false))
    }

    @Test
    fun stagger_delay_is_zero_with_reduced_motion() {
        // §11: reduced motion = aparición inmediata, sin escalonado.
        (0..50).forEach { index ->
            assertEquals(0, tileStaggerDelayMs(index, reducedMotion = true), "index=$index")
        }
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
