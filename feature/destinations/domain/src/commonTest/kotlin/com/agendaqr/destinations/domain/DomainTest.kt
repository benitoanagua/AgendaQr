package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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


class OperationUseCaseTest {
    @Test
    fun search_matches_entity_or_amount_and_orders_newest_first() = runTest {
        val repository = FakeOperationRepository(
            listOf(
                Operation("old", OperationType.PAGO, 100, 101, "120", "BOB", "Mercado"),
                Operation("new", OperationType.PAGO, 200, 201, "850", "BOB", "Colegio San José"),
                Operation("other", OperationType.COBRO, 300, 301, "50", "USD", "Juan"),
            )
        )

        val results = SearchOperationsUseCase(repository)(
            OperationSearchQuery(text = "850")
        ).first()

        assertEquals(listOf("new"), results.map { it.id })
    }

    @Test
    fun search_can_filter_by_type_and_date_range() = runTest {
        val repository = FakeOperationRepository(
            listOf(
                Operation("p1", OperationType.PAGO, 100, 101),
                Operation("p2", OperationType.PAGO, 200, 201),
                Operation("c1", OperationType.COBRO, 250, 251),
            )
        )

        val results = SearchOperationsUseCase(repository)(
            OperationSearchQuery(
                type = OperationType.PAGO,
                occurredFrom = 150,
                occurredTo = 220,
            )
        ).first()

        assertEquals(listOf("p2"), results.map { it.id })
    }

    @Test
    fun association_is_reversible_and_does_not_change_file() = runTest {
        val operations = FakeOperationRepository(
            listOf(Operation("op-1", OperationType.PAGO, 100, 101))
        )
        val receipts = FakeComprobanteRepository(
            listOf(Comprobante("r-1", "local://receipt.png", 102))
        )

        AssociateComprobanteToOperationUseCase(operations, receipts)("r-1", "op-1")
        assertEquals("op-1", receipts.get("r-1")?.operationId)

        UnassociateComprobanteUseCase(receipts)("r-1")
        val result = receipts.get("r-1")!!
        assertNull(result.operationId)
        assertEquals("local://receipt.png", result.file)
    }

    private class FakeOperationRepository(
        initial: List<Operation>,
    ) : OperationRepository {
        private val state = MutableStateFlow(initial)

        override fun observe() = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            state.value = state.value + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeComprobanteRepository(
        initial: List<Comprobante>,
    ) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)

        override fun observe() = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }
}


class OperationIntegrityTest {
    @Test
    fun sensitive_changes_are_detected_without_treating_notes_as_sensitive() {
        val original = Operation(
            id = "op-1",
            type = OperationType.PAGO,
            occurredAt = 100,
            createdAt = 101,
            amount = "850",
            currency = "BOB",
            personOrEntity = "Colegio",
            destinationId = "dest-1",
            note = "nota original",
        )

        assertTrue(original.copy(note = "nota nueva").hasSensitiveChangesComparedTo(original))
            .not()
        assertTrue(
            original.copy(amount = "900").hasSensitiveChangesComparedTo(original)
        )
        assertTrue(
            original.copy(type = OperationType.COBRO).hasSensitiveChangesComparedTo(original)
        )
    }
}
