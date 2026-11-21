package com.agendaqr.destinations.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas de [balanceByCurrency] y [formatBalance] (D7, balance neto por moneda).
 * Usan UTC para ser deterministas sin depender de la zona del dispositivo.
 *
 * [groupOperationsByMonthAndDay] se valida desde la presentación (G4 UI).
 */
class OperationGroupingTest {

    private val utc = TimeZone.UTC

    private fun op(
        id: String,
        type: OperationType,
        occurredAtMillis: Long,
        amount: String?,
        currency: String? = "USD",
    ) = Operation(
        id = id,
        type = type,
        occurredAt = occurredAtMillis,
        createdAt = occurredAtMillis,
        amount = amount,
        currency = currency,
    )

    @Test
    fun balance_suma_cobros_y_resta_pagos_por_moneda() {
        val now = Instant.fromEpochMilliseconds(0).toEpochMilliseconds()
        val ops = listOf(
            op("c1", OperationType.COBRO, now, "100,00"),
            op("c2", OperationType.COBRO, now, "50,00"),
            op("p1", OperationType.PAGO, now, "30,00"),
            op("p2", OperationType.PAGO, now, "20,00"),
        )
        val balance = balanceByCurrency(ops)
        // 100 + 50 - 30 - 20 = 100
        assertEquals(mapOf("USD" to "100.00"), balance)
    }

    @Test
    fun balance_monedas_independientes() {
        val now = Instant.fromEpochMilliseconds(0).toEpochMilliseconds()
        val ops = listOf(
            op("c1", OperationType.COBRO, now, "100,00", "USD"),
            op("c2", OperationType.COBRO, now, "200,00", "BOB"),
            op("p1", OperationType.PAGO, now, "30,00", "USD"),
        )
        val balance = balanceByCurrency(ops)
        assertEquals(mapOf("USD" to "70.00", "BOB" to "200.00"), balance)
    }

    @Test
    fun balance_sin_importe_no_suma() {
        val now = Instant.fromEpochMilliseconds(0).toEpochMilliseconds()
        val ops = listOf(
            op("c1", OperationType.COBRO, now, "100,00"),
            op("sin", OperationType.COBRO, now, null),
            op("sin2", OperationType.PAGO, now, ""),
        )
        val balance = balanceByCurrency(ops)
        assertEquals(mapOf("USD" to "100.00"), balance)
    }

    @Test
    fun balance_sin_operaciones_esta_vacio() {
        assertTrue(balanceByCurrency(emptyList()).isEmpty())
    }

    @Test
    fun formato_balance_signo_direccional() {
        assertEquals("+100,00", formatBalance("100.00"))
        assertEquals("−30,00", formatBalance("-30.00"))
        assertEquals("+0,00", formatBalance("0.00"))
    }
}
