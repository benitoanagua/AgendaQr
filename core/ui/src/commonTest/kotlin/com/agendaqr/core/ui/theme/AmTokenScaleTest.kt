package com.agendaqr.core.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Escala de densidad, radios, elevación y tipografía mínima del Anexo A
 * (ADR-0022). Fija los valores que la dirección Super-App introdujo para que
 * ningún cambio silencioso los desvíe: margen 12, radios 4/6/8/12/16/20 con
 * rejilla 14, elevación E-0..E-3 creciente y el mínimo absoluto de 11 sp.
 */
class AmTokenScaleTest {

    @Test
    fun screen_margin_is_twelve() {
        assertEquals(12.dp, AmSpacing.ScreenMargin)
    }

    @Test
    fun radius_scale_matches_the_annex() {
        assertEquals(4.dp, AmRadius.ExtraSmall)
        assertEquals(6.dp, AmRadius.Band)
        assertEquals(8.dp, AmRadius.Small)
        assertEquals(12.dp, AmRadius.Medium)
        assertEquals(16.dp, AmRadius.Large)
        assertEquals(20.dp, AmRadius.ExtraLarge)
        assertEquals(14.dp, AmRadius.GridIcon)
    }

    @Test
    fun elevation_levels_follow_the_annex() {
        assertEquals(0.dp, AmElevation.None.elevation)
        assertTrue(AmElevation.None.elevation < AmElevation.E1.elevation, "E-0 < E-1")
        assertTrue(AmElevation.E1.elevation < AmElevation.E2.elevation, "E-1 < E-2")
        assertTrue(AmElevation.E2.elevation < AmElevation.E3.elevation, "E-2 < E-3")
        assertEquals(8.dp, AmElevation.E3.elevation)
    }

    @Test
    fun typography_never_goes_below_eleven_sp() {
        val sizes = listOf(
            AmType.Display, AmType.Headline, AmType.TitleLarge, AmType.Title,
            AmType.Amount, AmType.AmountSymbol, AmType.RowTitle, AmType.SectionHeader,
            AmType.Body, AmType.Label, AmType.BodySmall, AmType.Caption, AmType.CaptionSmall,
        )
        assertTrue(sizes.all { it.value >= 11f }, "un tamaño cae por debajo de 11 sp")
    }
}
