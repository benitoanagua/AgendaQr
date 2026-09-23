package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContextTest {
    @Test
    fun context_allows_minimum_required_data() {
        val context = Context(
            id = "context-1",
            name = "Colegio de Mateo",
            createdAt = 1L,
            updatedAt = 1L,
        )

        assertEquals("context-1", context.id)
        assertEquals("Colegio de Mateo", context.name)
        assertNull(context.note)
    }

    @Test
    fun context_does_not_require_qr_activity_or_receipt() {
        val context = Context(
            id = "context-1",
            name = "Colegio de Mateo",
            createdAt = 1L,
            updatedAt = 1L,
        )

        // The absence of child collections is intentional:
        // relationships are owned by their respective repositories.
        assertEquals("context-1", context.id)
    }
}
