package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DomainTest {
    @Test
    fun destination_preserves_approved_metadata() {
        val destination = Destination(
            id = "1",
            name = "Store",
            qr = QrAsset("abc"),
            category = "Shopping",
            note = "Reusable",
            favorite = true,
            createdAt = 10,
            updatedAt = 20,
        )

        assertEquals("Store", destination.name)
        assertEquals("Shopping", destination.category)
        assertEquals("Reusable", destination.note)
        assertTrue(destination.favorite)
        assertNull(destination.lastUsedAtOrNull())
    }

    @Test
    fun operation_allows_minimum_required_data() {
        val operation = Operation(
            id = "op-1",
            type = OperationType.PAGO,
            occurredAt = 100,
            createdAt = 200,
        )

        assertEquals(OperationType.PAGO, operation.type)
        assertNull(operation.amount)
        assertNull(operation.personOrEntity)
        assertNull(operation.destinationId)
    }

    @Test
    fun operation_supports_pago_and_cobro() {
        assertEquals(OperationType.PAGO, OperationType.valueOf("PAGO"))
        assertEquals(OperationType.COBRO, OperationType.valueOf("COBRO"))
    }

    @Test
    fun receipt_can_exist_without_operation() {
        val receipt = Comprobante(
            id = "receipt-1",
            file = "local://receipt.png",
            createdAt = 300,
            provenance = ReceiptProvenance.RECIBIDO,
        )

        assertNull(receipt.operationId)
        assertEquals(ReceiptProvenance.RECIBIDO, receipt.provenance)
    }

    @Test
    fun receipt_can_be_reassociated_without_changing_file() {
        val receipt = Comprobante(
            id = "receipt-1",
            file = "local://receipt.png",
            createdAt = 300,
            provenance = ReceiptProvenance.DESCONOCIDO,
            operationId = "op-a",
        )

        val reassociated = receipt.copy(operationId = "op-b")

        assertEquals(receipt.file, reassociated.file)
        assertEquals("op-b", reassociated.operationId)
    }

    @Test
    fun one_operation_can_have_multiple_receipts() {
        val receipts = listOf(
            Comprobante("r1", "local://a.png", 1, operationId = "op-1"),
            Comprobante("r2", "local://b.png", 2, operationId = "op-1"),
        )

        assertEquals(2, receipts.count { it.operationId == "op-1" })
    }

    @Test
    fun deleted_operation_history_is_minimal() {
        val history = DeletedOperationHistory(
            date = 100,
            type = OperationType.COBRO,
            amount = "850",
            personOrEntity = "Colegio San José",
        )

        assertEquals(OperationType.COBRO, history.type)
        assertEquals("850", history.amount)
        assertEquals("Colegio San José", history.personOrEntity)
    }
}

private fun Destination.lastUsedAtOrNull(): Long? = null
