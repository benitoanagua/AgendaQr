package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.QrAsset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * S01 (ADR-0005, punto abierto 8 resuelto): el contenido del tile vivo se
 * DERIVA de los datos sin filtrar — el más reciente entre el último QR y
 * la actividad más reciente — e ignora la búsqueda y el filtro de
 * favoritos (antes usaba `visibleDestinations.firstOrNull()` y una
 * actividad nunca podía aparecer).
 */
class LatestTileDatumTest {

    private fun qr(id: String, updatedAt: Long) = Destination(
        id = id,
        name = "QR $id",
        qr = QrAsset(encoded = "eA=="),
        createdAt = updatedAt,
        updatedAt = updatedAt,
    )

    private fun activity(id: String, occurredAt: Long) = Operation(
        id = id,
        type = OperationType.PAGO,
        occurredAt = occurredAt,
        createdAt = occurredAt,
        updatedAt = occurredAt,
    )

    @Test
    fun empty_data_yields_no_datum() {
        assertNull(latestTileDatum(destinations = emptyList(), operations = emptyList()))
    }

    @Test
    fun only_destinations_shows_the_most_recent_qr() {
        val datum = latestTileDatum(
            destinations = listOf(qr("d-old", updatedAt = 10), qr("d-new", updatedAt = 90)),
            operations = emptyList(),
        )
        assertEquals("d-new", assertIs<LatestTileDatum.Qr>(datum).destination.id)
    }

    @Test
    fun only_operations_shows_the_most_recent_activity() {
        val datum = latestTileDatum(
            destinations = emptyList(),
            operations = listOf(activity("o-old", occurredAt = 5), activity("o-new", occurredAt = 70)),
        )
        assertEquals("o-new", assertIs<LatestTileDatum.Activity>(datum).operation.id)
    }

    @Test
    fun the_most_recent_of_both_wins_by_date() {
        val qrNewest = latestTileDatum(
            destinations = listOf(qr("d-1", updatedAt = 200)),
            operations = listOf(activity("o-1", occurredAt = 100)),
        )
        assertEquals("d-1", assertIs<LatestTileDatum.Qr>(qrNewest).destination.id)

        val activityNewest = latestTileDatum(
            destinations = listOf(qr("d-1", updatedAt = 100)),
            operations = listOf(activity("o-1", occurredAt = 200)),
        )
        assertEquals("o-1", assertIs<LatestTileDatum.Activity>(activityNewest).operation.id)
    }

    @Test
    fun ignores_the_favorites_filter_and_the_search_query() {
        // El datum se calcula sobre TODOS los datos: un QR NO favorito con
        // nombre que no matchea la búsqueda sigue siendo el más reciente.
        val datum = latestTileDatum(
            destinations = listOf(
                qr("d-not-favorite", updatedAt = 300).copy(favorite = false),
                qr("d-favorite", updatedAt = 50).copy(favorite = true),
            ),
            operations = emptyList(),
        )
        assertEquals("d-not-favorite", assertIs<LatestTileDatum.Qr>(datum).destination.id)
    }
}
