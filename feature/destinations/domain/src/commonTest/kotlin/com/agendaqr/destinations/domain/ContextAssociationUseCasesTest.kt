package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

class ContextAssociationUseCasesTest {
    @Test
    fun destination_can_be_assigned_and_unassigned_from_context() = runTest {
        val contexts = FakeContextRepository(listOf(context()))
        val destinations = FakeDestinationRepository(listOf(destination()))

        AssignDestinationToContextUseCase(contexts, destinations)("qr-1", "ctx-1")
        assertEquals("ctx-1", destinations.get("qr-1")!!.contextId)

        AssignDestinationToContextUseCase(contexts, destinations)("qr-1", null)
        assertEquals(null, destinations.get("qr-1")!!.contextId)
    }

    @Test
    fun operation_can_be_assigned_to_context() = runTest {
        val contexts = FakeContextRepository(listOf(context()))
        val operations = FakeOperationRepository(listOf(operation()))

        AssignOperationToContextUseCase(contexts, operations)("op-1", "ctx-1")

        assertEquals("ctx-1", operations.get("op-1")!!.contextId)
    }

    @Test
    fun receipt_can_be_assigned_to_context_without_operation() = runTest {
        val contexts = FakeContextRepository(listOf(context()))
        val receipts = FakeComprobanteRepository(listOf(receipt()))

        AssignComprobanteToContextUseCase(contexts, receipts)("r-1", "ctx-1")

        assertEquals("ctx-1", receipts.get("r-1")!!.contextId)
        assertEquals(null, receipts.get("r-1")!!.operationId)
    }

    @Test
    fun association_requires_existing_context() = runTest {
        val contexts = FakeContextRepository(emptyList())
        val destinations = FakeDestinationRepository(listOf(destination()))

        kotlin.test.assertFailsWith<IllegalArgumentException> {
            AssignDestinationToContextUseCase(contexts, destinations)("qr-1", "missing")
        }
    }

    private fun context() = Context("ctx-1", "Colegio", createdAt = 1, updatedAt = 1)
    private fun destination() = Destination("qr-1", "QR", QrAsset("encoded"), createdAt = 1, updatedAt = 1)
    private fun operation() = Operation("op-1", OperationType.PAGO, occurredAt = 1, createdAt = 1)
    private fun receipt() = Comprobante("r-1", "local://r.png", createdAt = 1, updatedAt = 1)

    private class FakeContextRepository(initial: List<Context>) : ContextRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) { state.value = state.value + context }
        override suspend fun update(context: Context) { state.value = state.value.map { if (it.id == context.id) context else it } }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }

    private class FakeDestinationRepository(initial: List<Destination>) : DestinationRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) { state.value = state.value + destination }
        override suspend fun update(destination: Destination) { state.value = state.value.map { if (it.id == destination.id) destination else it } }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }

    private class FakeOperationRepository(initial: List<Operation>) : OperationRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) { state.value = state.value + operation }
        override suspend fun update(operation: Operation) { state.value = state.value.map { if (it.id == operation.id) operation else it } }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }

    private class FakeComprobanteRepository(initial: List<Comprobante>) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) { state.value = state.value + comprobante }
        override suspend fun update(comprobante: Comprobante) { state.value = state.value.map { if (it.id == comprobante.id) comprobante else it } }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }
}
