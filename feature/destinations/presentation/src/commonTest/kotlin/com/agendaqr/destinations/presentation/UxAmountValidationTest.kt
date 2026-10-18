package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * U-07/D6: validación real del importe (dígitos y un separador decimal,
 * máx. 2 decimales, sin espacios) y canonización con punto al guardar.
 * El importe sigue siendo opcional (vacío = válido).
 */
class UxAmountValidationTest {

    @Test
    fun valid_amounts_pass() {
        listOf("", "12", "12,5", "12.50", "0,99", ".5", "5.", "007").forEach { text ->
            assertTrue(isValidAmount(text), "'$text' debe ser válido")
        }
    }

    @Test
    fun invalid_amounts_fail() {
        listOf("12.505", "abc", "12 3", "1.2.3", "1,2,3", ".", ",", "-", "$12", "12a", " 12").forEach { text ->
            assertFalse(isValidAmount(text), "'$text' debe ser inválido")
        }
    }

    @Test
    fun canonize_uses_dot() {
        assertEquals("12.5", canonizeAmount("12,5"))
        assertEquals("12.50", canonizeAmount("12.50"))
        assertEquals("12", canonizeAmount("12"))
    }

    @Test
    fun most_recent_currency_comes_from_the_latest_operation() {
        val operations = listOf(
            operation(id = "new", occurredAt = 3_000L, currency = "USD"),
            operation(id = "old", occurredAt = 1_000L, currency = "BOB"),
        )
        assertEquals("USD", mostRecentCurrency(operations))
        assertEquals("", mostRecentCurrency(emptyList()))
        assertEquals("", mostRecentCurrency(listOf(operation(id = "x", occurredAt = 1L, currency = null))))
    }

    @Test
    fun recent_counterparts_are_distinct_and_capped() {
        val operations = listOf(
            operation(id = "a", occurredAt = 5_000L, person = "Mercado"),
            operation(id = "b", occurredAt = 4_000L, person = "Mercado"),
            operation(id = "c", occurredAt = 3_000L, person = "Ferretería"),
            operation(id = "d", occurredAt = 2_000L, person = null),
            operation(id = "e", occurredAt = 1_000L, person = "  "),
        )
        assertEquals(listOf("Mercado", "Ferretería"), recentCounterparts(operations))
        assertEquals(listOf("Mercado"), recentCounterparts(operations, max = 1))
    }

    private fun operation(id: String, occurredAt: Long, currency: String? = null, person: String? = null) =
        Operation(
            id = id,
            type = OperationType.PAGO,
            occurredAt = occurredAt,
            createdAt = occurredAt,
            updatedAt = occurredAt,
            currency = currency,
            personOrEntity = person,
        )
}
