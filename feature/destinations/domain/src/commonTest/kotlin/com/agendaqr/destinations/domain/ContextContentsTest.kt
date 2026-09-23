package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ContextContentsTest {
    @Test
    fun context_contents_aggregate_only_related_items() = runTest {
        val result = ObserveContextContentsUseCase(
            FakeContextRepository(
                listOf(
                    Context("ctx-1", "Colegio", createdAt = 1, updatedAt = 1),
                    Context("ctx-2", "Mercado", createdAt = 1, updatedAt = 1),
                )
            ),
            FakeDestinationRepository(
                listOf(
                    destination("qr-1", "ctx-1"),
                    destination("qr-2", "ctx-2"),
                )
            ),
            FakeOperationRepository(
                listOf(
                    operation("op-1", "ctx-1", 20),
                    operation("op-2", "ctx-2", 30),
                )
            ),
            FakeComprobanteRepository(
                listOf(
                    receipt("r-1", "ctx-1", 40),
                    receipt("r-2", "ctx-2", 50),
                )
            ),
        )("ctx-1").first()!!

        assertEquals("Colegio", result.context.name)
        assertEquals(listOf("qr-1"), result.destinations.map { it.id })
        assertEquals(listOf("op-1"), result.operations.map { it.id })
        assertEquals(listOf("r-1"), result.comprobantes.map { it.id })
    }

    private fun destination(id: String, contextId: String) = Destination(
        id = id,
        name = id,
        qr = QrAsset("encoded"),
        contextId = contextId,
        createdAt = 1,
        updatedAt = 1,
    )

    private fun operation(id: String, contextId: String, occurredAt: Long) = Operation(
        id = id,
        type = OperationType.PAGO,
        occurredAt = occurredAt,
        createdAt = occurredAt,
        contextId = contextId,
    )

    private fun receipt(id: String, contextId: String, createdAt: Long) = Comprobante(
        id = id,
        file = "local://$id.png",
        createdAt = createdAt,
        updatedAt = createdAt,
        contextId = contextId,
    )

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
