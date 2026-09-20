package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DomainTest {
    @Test
    fun destination_preserves_all_specified_metadata() {
        val destination = Destination(
            id = "1",
            name = "Store",
            qr = QrAsset("abc"),
            category = "Shopping",
            note = "Reusable",
            favorite = true,
            createdAt = 10,
            updatedAt = 20,
            lastUsedAt = 30,
        )

        assertEquals("Store", destination.name)
        assertEquals("Shopping", destination.category)
        assertEquals("Reusable", destination.note)
        assertTrue(destination.favorite)
        assertEquals(30, destination.lastUsedAt)
        assertNotNull(destination.qr)
    }
}
