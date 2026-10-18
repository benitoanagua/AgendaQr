package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveContextContentsUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.SaveContextUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * T-e — REINTENTAR reintenta de verdad: los ViewModels guardan la última
 * operación fallida y RetryFailed la re-ejecuta; los errores "no
 * encontrado" ofrecen CERRAR en vez de un reintento sin sentido.
 */
class RetryFailedTest {

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private fun destinationsVm(repo: DestinationRepository): DestinationsViewModel =
        DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repo),
            get = GetDestinationUseCase(repo),
            save = SaveDestinationUseCase(repo),
            update = UpdateDestinationUseCase(repo),
            delete = DeleteDestinationUseCase(repo),
            toggleFavorite = ToggleFavoriteUseCase(repo),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )

    @Test
    fun destinations_retry_failed_re_runs_the_failed_save(): Unit = runBlocking {
        val repo = FlippableDestinations(fail = true)
        val vm = destinationsVm(repo)
        waitUntil { !vm.state.value.isLoading }

        vm.onAction(
            DestinationAction.Save(
                Destination("d-1", "Carniceria", QrAsset("eA=="), createdAt = 1, updatedAt = 1),
            ),
        )
        waitUntil { vm.state.value.error != null }
        assertEquals(ErrorAction.Retry, vm.state.value.error?.action)

        // El backend se recupera: el reintento guarda de verdad.
        repo.fail = false
        vm.onAction(DestinationAction.RetryFailed)
        waitUntil { vm.state.value.error == null && repo.saved.isNotEmpty() }
        assertEquals(1, repo.saved.size)

        // Tras el éxito no queda bloque pendiente: otro RetryFailed no
        // re-ejecuta el guardado viejo (sin duplicados por reintento).
        vm.onAction(DestinationAction.RetryFailed)
        delay(100)
        assertEquals(1, repo.saved.size)
    }

    @Test
    fun operations_retry_failed_re_runs_the_failed_save_new(): Unit = runBlocking {
        val operations = FlippableOperations(fail = true)
        val vm = OperationsViewModelFactory.create(operations)
        vm.onAction(
            OperationAction.SaveNew(
                type = OperationType.PAGO,
                occurredAt = 1_000,
                amount = "100",
                currency = null,
                personOrEntity = null,
                destinationId = null,
                concept = null,
                note = null,
                contextId = null,
            ),
        )
        waitUntil { vm.state.value.error != null }
        assertEquals(ErrorAction.Retry, vm.state.value.error?.action)

        operations.fail = false
        vm.onAction(OperationAction.RetryFailed)
        waitUntil { vm.state.value.error == null && vm.state.value.route is OperationRoute.Detail }
        assertTrue(operations.saved.isNotEmpty())
    }

    @Test
    fun contexts_retry_failed_re_runs_the_failed_create(): Unit = runBlocking {
        val repo = FlippableContexts(fail = true)
        val vm = ContextsViewModel(
            observe = ObserveContextsUseCase(repo),
            observeContents = ObserveContextContentsUseCase(repo, NoopDestinations2(), NoopOperations2(), NoopReceipts2()),
            get = GetContextUseCase(repo),
            save = SaveContextUseCase(repo),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        waitUntil { !vm.state.value.isLoading }

        vm.onAction(ContextAction.Create("Mercado", null))
        waitUntil { vm.state.value.error != null }
        assertEquals(ErrorAction.Retry, vm.state.value.error?.action)

        repo.fail = false
        vm.onAction(ContextAction.RetryFailed)
        waitUntil { vm.state.value.justCreatedContextId != null }
        assertNotNull(vm.state.value.justCreatedContextId)
    }

    @Test
    fun not_found_errors_offer_close_instead_of_retry() {
        assertEquals(ErrorAction.Close, userFacingError(IllegalStateException(), ErrorFlow.ContextOpen).action)
        assertEquals(ErrorAction.Close, comprobanteNotFound().action)
        assertEquals(ErrorAction.Close, operationNotFound().action)
        assertEquals(ErrorAction.Close, duplicateReceiptGuardError().action)
    }

    @Test
    fun dismissing_the_error_clears_a_stale_retry(): Unit = runBlocking {
        val repo = FlippableDestinations(fail = true)
        val vm = destinationsVm(repo)
        waitUntil { !vm.state.value.isLoading }
        vm.onAction(
            DestinationAction.Save(
                Destination("d-1", "Carniceria", QrAsset("eA=="), createdAt = 1, updatedAt = 1),
            ),
        )
        waitUntil { vm.state.value.error != null }
        // Descartar invalida el reintento: no hay guardado fantasma después.
        vm.onAction(DestinationAction.ClearError)
        assertNull(vm.state.value.error)
        vm.onAction(DestinationAction.RetryFailed)
        delay(100)
        assertTrue(repo.saved.isEmpty())
    }

    private class FlippableDestinations(var fail: Boolean) : DestinationRepository {
        val saved = mutableListOf<Destination>()
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            if (fail) throw IllegalStateException("storage unavailable")
            saved += destination
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) {
            if (fail) throw IllegalStateException("storage unavailable")
            state.value = state.value.map { if (it.id == destination.id) destination else it }
        }
        override suspend fun delete(id: String) {}
    }

    private class FlippableOperations(var fail: Boolean) : com.agendaqr.destinations.domain.OperationRepository {
        val saved = mutableListOf<com.agendaqr.destinations.domain.Operation>()
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.Operation>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: com.agendaqr.destinations.domain.Operation) {
            if (fail) throw IllegalStateException("storage unavailable")
            saved += operation
            state.value = state.value + operation
        }
        override suspend fun update(operation: com.agendaqr.destinations.domain.Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class FlippableContexts(var fail: Boolean) : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {
            if (fail) throw IllegalStateException("storage unavailable")
            state.value = state.value + context
        }
        override suspend fun update(context: Context) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopDestinations2 : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String): Destination? = null
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopOperations2 : com.agendaqr.destinations.domain.OperationRepository {
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.Operation>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.Operation>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(operation: com.agendaqr.destinations.domain.Operation) {}
        override suspend fun update(operation: com.agendaqr.destinations.domain.Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopReceipts2 : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(comprobante: Comprobante) {}
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }
}
