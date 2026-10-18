package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.AgendaSearchResultType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regresión semántica: los resultados de búsqueda nunca muestran el nombre
 * técnico del enum (PAGO/COBRO/RECIBIDO...), siempre la palabra visible.
 */
class GlobalSearchResultSemanticsTest {
    @Test
    fun activity_title_falls_back_to_semantic_type_label() {
        val result = AgendaSearchResult(
            type = AgendaSearchResultType.ACTIVITY,
            id = "op-1",
            title = "PAGO",
        )
        assertEquals("Pago", searchResultTitle(result))
        assertEquals(
            "Cobro",
            searchResultTitle(result.copy(id = "op-2", title = "COBRO")),
        )
    }

    @Test
    fun normal_titles_pass_through_unchanged() {
        val result = AgendaSearchResult(
            type = AgendaSearchResultType.ACTIVITY,
            id = "op-1",
            title = "Mercado Central",
            subtitle = "150.50",
        )
        assertEquals("Mercado Central", searchResultTitle(result))
        assertEquals("150.50", searchResultSubtitle(result))
    }

    @Test
    fun comprobante_subtitle_maps_provenance_semantically() {
        val result = AgendaSearchResult(
            type = AgendaSearchResultType.COMPROBANTE,
            id = "rec-1",
            title = "rec-1.png",
            subtitle = "RECIBIDO",
        )
        assertEquals("Recibido", searchResultSubtitle(result))
        assertEquals("Enviado", searchResultSubtitle(result.copy(subtitle = "ENVIADO")))
        assertEquals("Origen desconocido", searchResultSubtitle(result.copy(subtitle = "DESCONOCIDO")))
        // Los MIME types no son enums: pasan tal cual.
        assertEquals("image/png", searchResultSubtitle(result.copy(subtitle = "image/png")))
        assertEquals(null, searchResultSubtitle(result.copy(subtitle = null)))
    }
}
