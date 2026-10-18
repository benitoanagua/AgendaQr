package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Subtítulo distintivo de la fila de destino (test PURO): nombres
 * genéricos ("Sin nombre"/"QR importado") llevan origen + fecha;
 * los nombrados conservan nota/categoría.
 */
class DestinationSubtitleTest {

    private fun destination(name: String, createdAt: Long = 1_700_000_000_000) = Destination(
        id = "d-1",
        name = name,
        qr = QrAsset(encoded = "eA=="),
        createdAt = createdAt,
        updatedAt = createdAt,
    )

    @Test
    fun blank_name_gets_added_origin_and_date() {
        assertEquals(
            "Añadido el 14/11/2023",
            destinationSubtitle(destination("")),
        )
    }

    @Test
    fun auto_import_name_gets_imported_origin_and_date() {
        assertEquals(
            "Importado el 14/11/2023",
            destinationSubtitle(destination("QR importado")),
        )
        assertEquals(
            "Importado el 14/11/2023",
            destinationSubtitle(destination("QR importado 2")),
        )
    }

    @Test
    fun named_destination_keeps_note_or_category() {
        assertEquals(
            "Detalle comercial",
            destinationSubtitle(destination("Carniceria Don Bife").copy(note = "Detalle comercial")),
        )
        assertEquals(
            "Comida",
            destinationSubtitle(destination("Carniceria Don Bife").copy(category = "Comida")),
        )
    }
}
