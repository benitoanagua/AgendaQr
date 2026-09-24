package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class AgendaSearchTest {
    @Test
    fun global_search_returns_context_qr_activity_and_receipt_matches() = runTest {
        val contexts = FakeContextRepository(
            listOf(Context("ctx-1", "Colegio de Mateo", note = "Mensualidad", createdAt = 1, updatedAt = 1))
        )
        val destinations = FakeDestinationRepository(
            listOf(
                Destination(
                    id = "qr-1",
                    name = "QR mensualidad",
                    qr = QrAsset("encoded"),
                    contextId = "ctx-1",
                    createdAt = 1,
                    updatedAt = 1,
                )
            )
        )
        val operations = FakeOperationRepository(
            listOf(
                Operation(
                    id = "op-1",
                    type = OperationType.PAGO,
                    occurredAt = 2,
                    createdAt = 2,
                    amount = "450",
                    currency = "BOB",
                    personOrEntity = "Colegio de Mateo",
                    contextId = "ctx-1",
                )
            )
        )
        val receipts = FakeComprobanteRepository(
            listOf(
                Comprobante(
                    id = "receipt-1",
                    file = "local://colegio-450.png",
                    createdAt = 3,
                    updatedAt = 3,
                    contextId = "ctx-1",
                )
            )
        )

        val results = SearchAgendaQrUseCase(contexts, destinations, operations, receipts)(
            AgendaSearchQuery("colegio")
        ).first()

        assertEquals(
            setOf(
                AgendaSearchResultType.CONTEXT,
                AgendaSearchResultType.QR,
                AgendaSearchResultType.ACTIVITY,
                AgendaSearchResultType.COMPROBANTE,
            ),
            results.map { it.type }.toSet(),
        )
        assertEquals("ctx-1", results.first { it.type == AgendaSearchResultType.CONTEXT }.contextId)
        assertEquals("ctx-1", results.first { it.type == AgendaSearchResultType.QR }.contextId)
        assertEquals("ctx-1", results.first { it.type == AgendaSearchResultType.ACTIVITY }.contextId)
        assertEquals("ctx-1", results.first { it.type == AgendaSearchResultType.COMPROBANTE }.contextId)
    }

    @Test
    fun empty_query_does_not_return_everything() = runTest {
        val results = SearchAgendaQrUseCase(
            FakeContextRepository(listOf(Context("ctx-1", "Colegio", createdAt = 1, updatedAt = 1))),
            FakeDestinationRepository(emptyList()),
            FakeOperationRepository(emptyList()),
            FakeComprobanteRepository(emptyList()),
        )(AgendaSearchQuery("   ")).first()

        assertEquals(emptyList(), results)
    }

    @Test
    fun qr_is_not_returned_when_neither_it_nor_its_context_match() = runTest {
        val results = SearchAgendaQrUseCase(
            FakeContextRepository(listOf(Context("ctx-1", "Colegio de Mateo", createdAt = 1, updatedAt = 1))),
            FakeDestinationRepository(
                listOf(
                    Destination(
                        id = "qr-1",
                        name = "QR mensualidad",
                        qr = QrAsset("encoded"),
                        contextId = "ctx-1",
                        createdAt = 1,
                        updatedAt = 1,
                    )
                )
            ),
            FakeOperationRepository(emptyList()),
            FakeComprobanteRepository(emptyList()),
        )(AgendaSearchQuery("farmacia")).first()

        assertEquals(emptyList(), results)
    }

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
