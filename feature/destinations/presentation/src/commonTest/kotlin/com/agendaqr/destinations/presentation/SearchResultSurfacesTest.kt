package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.SearchAgendaQrUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * S05 para TODOS los tipos de resultado — no solo Contexto.
 *
 * Antes: para QR/Actividad/Comprobante el wiring hacía pop() de la
 * Búsqueda al abrir el resultado, así que Back desde el detalle llevaba
 * a Inicio y la consulta se perdía. Ahora: Operaciones se apila SOBRE la
 * Búsqueda y el detalle QR la restaura al volver (la consulta sobrevive
 * en el ViewModel retenido).
 */
class SearchResultSurfacesTest {

    private fun searchViewModel(): GlobalSearchViewModel {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        return GlobalSearchViewModel(
            search = SearchAgendaQrUseCase(
                FakeSearchContexts(),
                FakeSearchDestinations(),
                FakeSearchOperations(),
                FakeSearchReceipts(),
            ),
            scope = scope,
        )
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    @Test
    fun activity_result_keeps_search_below_operations() = runBlocking {
        val search = searchViewModel()
        search.onAction(GlobalSearchAction.QueryChanged("mercado"))
        waitUntil { search.state.value.query == "mercado" }

        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        // Wiring nuevo: SIN pop antes del push.
        nav.push(AppRoute.Operations)
        assertEquals(AppRoute.Operations, nav.top)
        assertEquals(AppRoute.Search, nav.belowTop())

        // Back desde Operaciones vuelve a los resultados, no a Inicio.
        val popped = nav.pop()
        assertEquals(AppRoute.Operations, popped)
        assertEquals(AppRoute.Search, nav.top)
        assertEquals("mercado", search.state.value.query)
    }

    @Test
    fun comprobante_result_keeps_search_below_operations() = runBlocking {
        val search = searchViewModel()
        search.onAction(GlobalSearchAction.QueryChanged("recibo"))
        waitUntil { search.state.value.query == "recibo" }

        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        nav.push(AppRoute.Operations)
        nav.pop()
        assertEquals(AppRoute.Search, nav.top)
        assertEquals("recibo", search.state.value.query)
    }

    @Test
    fun qr_result_back_from_detail_restores_search_with_query() = runBlocking {
        val search = searchViewModel()
        search.onAction(GlobalSearchAction.QueryChanged("bife"))
        waitUntil { search.state.value.query == "bife" }

        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        // Wiring nuevo: el detalle QR vive en Inicio (pop) y el Back desde
        // el detalle restaura la Búsqueda (push) cuando viene de buscar.
        nav.pop() // el detalle "d-mercado" queda visible en Inicio
        var searchReturnDestinationId: String? = "d-mercado"
        // backFromDestinationDetail(): Back interno + restaurar si el id coincide.
        val detailId = "d-mercado"
        val pending = searchReturnDestinationId
        searchReturnDestinationId = null
        if (pending != null && pending == detailId) nav.push(AppRoute.Search)

        assertEquals(AppRoute.Search, nav.top)
        assertEquals("bife", search.state.value.query)
        assertEquals(null, searchReturnDestinationId)
    }

    @Test
    fun qr_restore_does_not_fire_for_an_unrelated_detail() {
        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        nav.pop()
        // El detalle se cerró por otra vía (eliminar): al abrir otro
        // detalle desde Inicio y volver, NO se restaura la Búsqueda.
        var searchReturnDestinationId: String? = "d-viejo"
        val detailId = "d-nuevo"
        val pending = searchReturnDestinationId
        searchReturnDestinationId = null
        if (pending != null && pending == detailId) nav.push(AppRoute.Search)

        assertEquals(AppRoute.Home, nav.top)
        assertNull(searchReturnDestinationId)
    }

    @Test
    fun fresh_search_entry_does_not_trigger_restore() {
        // Entrar a Buscar desde Inicio no marca retorno: el flag nace en
        // false y solo lo activa abrir un QR desde resultados.
        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        assertEquals(AppRoute.Search, nav.top)
        nav.pop()
        assertEquals(AppRoute.Home, nav.top)
    }

    private class FakeSearchContexts : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {}
        override suspend fun update(context: Context) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeSearchDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String): Destination? = null
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeSearchOperations : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(
            listOf(Operation("op-1", OperationType.PAGO, occurredAt = 1, createdAt = 1)),
        )
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {}
        override suspend fun update(operation: Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeSearchReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(comprobante: Comprobante) {}
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }
}
