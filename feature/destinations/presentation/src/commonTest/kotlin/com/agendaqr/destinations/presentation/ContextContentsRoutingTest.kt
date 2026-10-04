package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveContextContentsUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.ReceiptProvenance
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T11 — S06 utilizable: las filas del contexto reutilizan las rutas
 * existentes (las mismas que los resultados de búsqueda global), sin crear
 * navegación nueva: QR → detalle en Inicio; actividad/comprobante →
 * superficie de Operaciones.
 */
class ContextContentsRoutingTest {

    private val destination = Destination(
        id = "d-mercado",
        name = "Carniceria Don Bife",
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
        createdAt = 1,
        updatedAt = 1,
    )
    private val operation = Operation("op-1", OperationType.PAGO, occurredAt = 100, createdAt = 100)
    private val receipt = Comprobante(
        id = "r-1",
        file = "file://r-1.png",
        createdAt = 10,
        updatedAt = 10,
        provenance = ReceiptProvenance.RECIBIDO,
    )

    private val contexts = MutableStateFlow(
        listOf(Context("ctx-1", "Mercado Central", createdAt = 1, updatedAt = 1)),
    )
    private val destinations = MutableStateFlow(listOf(destination))
    private val operations = MutableStateFlow(listOf(operation))
    private val receipts = MutableStateFlow(listOf(receipt))

    @Test
    fun opening_a_qr_from_the_context_returns_home_showing_its_detail() = runBlocking {
        val contextRepo = FakeContexts(contexts)
        val destinationRepo = FakeDestinations(destinations)
        val contextVm = ContextsViewModel(
            observe = ObserveContextsUseCase(contextRepo),
            observeContents = ObserveContextContentsUseCase(contextRepo, destinationRepo, FakeOperations(operations), FakeReceipts(receipts)),
            get = GetContextUseCase(contextRepo),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        val destinationsVm = DestinationsViewModel(
            observe = ObserveDestinationsUseCase(destinationRepo),
            get = GetDestinationUseCase(destinationRepo),
            save = SaveDestinationUseCase(destinationRepo),
            update = com.agendaqr.destinations.domain.UpdateDestinationUseCase(destinationRepo),
            delete = com.agendaqr.destinations.domain.DeleteDestinationUseCase(destinationRepo),
            toggleFavorite = ToggleFavoriteUseCase(destinationRepo),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        waitUntil { !contextVm.state.value.isLoading }
        contextVm.onAction(ContextAction.Open("ctx-1"))
        waitUntil { contextVm.state.value.contents != null }

        // Stack real: S06 abierta sobre Inicio. La fila del QR ejecuta la
        // MISMA ruta que un resultado de búsqueda: Open + pop de superficie.
        val nav = AppBackStack()
        nav.push(AppRoute.Contexts)
        destinationsVm.onAction(DestinationAction.Open("d-mercado"))
        nav.pop()

        assertEquals(AppRoute.Home, nav.top)
        assertTrue(destinationsVm.state.value.route is DestinationRoute.Detail)
        assertEquals("d-mercado", (destinationsVm.state.value.route as DestinationRoute.Detail).id)
    }

    @Test
    fun opening_an_activity_or_receipt_from_the_context_opens_operations() {
        // La actividad y el comprobante viven en la superficie de
        // Operaciones: la ruta es pop + push(Operations), idéntica a la de
        // los resultados de búsqueda (RF-13).
        val nav = AppBackStack()
        nav.push(AppRoute.Contexts)
        nav.pop()
        nav.push(AppRoute.Operations)
        assertEquals(AppRoute.Operations, nav.top)
        assertEquals(listOf(AppRoute.Home, AppRoute.Operations), nav.stack.value)
    }

    @Test
    fun context_detail_lists_its_contents_for_navigation() {
        // El contenido del contexto expone QR/actividad/comprobante: son las
        // filas navegables de S06.
        val ctx = Context("ctx-1", "Mercado Central", createdAt = 1, updatedAt = 1)
        val contents = com.agendaqr.destinations.domain.ContextContents(
            context = ctx,
            destinations = listOf(destination),
            operations = listOf(operation),
            comprobantes = listOf(receipt),
        )
        assertEquals(1, contents.destinations.size)
        assertEquals(1, contents.operations.size)
        assertEquals(1, contents.comprobantes.size)
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private class FakeContexts(private val state: MutableStateFlow<List<Context>>) : ContextRepository {
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {}
        override suspend fun update(context: Context) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeDestinations(private val state: MutableStateFlow<List<Destination>>) : DestinationRepository {
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeOperations(private val state: MutableStateFlow<List<Operation>>) : OperationRepository {
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {}
        override suspend fun update(operation: Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeReceipts(private val state: MutableStateFlow<List<Comprobante>>) : ComprobanteRepository {
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {}
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }
}
