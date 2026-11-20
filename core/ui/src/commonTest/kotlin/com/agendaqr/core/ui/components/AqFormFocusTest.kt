package com.agendaqr.core.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AqFormFocusTest {
    private val order = listOf("email", "password", "note")

    @Test
    fun sinErroresNoHayFoco() {
        assertNull(firstErrorKey(order, emptySet()))
    }

    @Test
    fun enfocaElPrimerErrorEnOrdenVisualNoEnOrdenDelConjunto() {
        assertEquals("email", firstErrorKey(order, setOf("note", "password", "email")))
        assertEquals("password", firstErrorKey(order, setOf("note", "password")))
    }

    @Test
    fun ignoraClavesDesconocidas() {
        assertNull(firstErrorKey(order, setOf("otro")))
        assertEquals("note", firstErrorKey(order, setOf("otro", "note")))
    }
}
