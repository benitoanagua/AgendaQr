package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ImportBatchTest {
    @Test
    fun keeps_valid_items_when_one_item_is_unknown() {
        val batch = ImportBatch(
            listOf(
                candidate("qr-1", ImportKind.QR, "a"),
                candidate("unknown", ImportKind.DESCONOCIDO, "b"),
                candidate("receipt-1", ImportKind.COMPROBANTE, "c"),
            )
        )

        assertEquals(2, batch.recognized.size)
        assertEquals(1, batch.unknown.size)
        assertEquals(2, batch.uniqueRecognized.size)
        assertEquals(1, batch.pendingReview.size)
    }

    @Test
    fun duplicate_content_is_not_saved_twice() {
        val batch = ImportBatch(
            listOf(
                candidate("qr-1", ImportKind.QR, "same"),
                candidate("qr-2", ImportKind.QR, "same"),
                candidate("receipt-1", ImportKind.COMPROBANTE, "other"),
            )
        )

        assertEquals(setOf("same"), batch.duplicateFingerprints)
        assertEquals(listOf("qr-2"), batch.duplicates.map { it.id })
        assertEquals(listOf("qr-1", "receipt-1"), batch.uniqueRecognized.map { it.id })
        assertEquals(listOf("qr-2"), batch.pendingReview.map { it.id })
    }

    @Test
    fun unknown_duplicate_does_not_block_first_valid_occurrence() {
        val batch = ImportBatch(
            listOf(
                candidate("qr", ImportKind.QR, "same"),
                candidate("unknown", ImportKind.DESCONOCIDO, "same"),
            )
        )

        assertEquals(listOf("qr"), batch.uniqueRecognized.map { it.id })
        assertEquals(listOf("unknown"), batch.pendingReview.map { it.id })
    }

    @Test
    fun unknown_between_duplicate_occurrences_keeps_first_valid_as_original() {
        val batch = ImportBatch(
            listOf(
                candidate("qr-1", ImportKind.QR, "same"),
                candidate("unknown", ImportKind.DESCONOCIDO, "same"),
                candidate("qr-2", ImportKind.QR, "same"),
            )
        )

        assertEquals(listOf("qr-1"), batch.uniqueRecognized.map { it.id })
        assertEquals(listOf("qr-2"), batch.duplicates.map { it.id })
        assertEquals(listOf("unknown", "qr-2"), batch.pendingReview.map { it.id })
    }

    private fun candidate(id: String, kind: ImportKind, fingerprint: String) =
        ImportCandidate(id = id, kind = kind, fingerprint = fingerprint)
}
