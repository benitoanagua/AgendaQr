package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.ObserveContextContentsUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * T1 — Contextos sin salida: Back desde el Detalle de un contexto vuelve a
 * la Lista (S06); el cierre de la superficie completa (Lista) es manejado
 * por el back stack de la app y está cubierto en [AppBackStackTest].
 */
class ContextsBackStateTest {

    @Test
    fun back_from_detail_returns_to_list() = runBlocking {
        val contexts = FakeContexts(listOf(context("ctx-1")))
        val vm = ContextsViewModel(
            observe = ObserveContextsUseCase(contexts),
            observeContents = ObserveContextContentsUseCase(
                contexts,
                FakeDestinations(),
                FakeOperations(),
                FakeReceipts(),
            ),
            get = GetContextUseCase(contexts),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        waitUntil { !vm.state.value.isLoading }

        vm.onAction(ContextAction.Open("ctx-1"))
        waitUntil { vm.state.value.route is ContextRoute.Detail }
        assertTrue(vm.state.value.route is ContextRoute.Detail)

        vm.onAction(ContextAction.Back)
        waitUntil { vm.state.value.route is ContextRoute.List }
        assertTrue(vm.state.value.route is ContextRoute.List)
        // El contenido del detalle no sobrevive al retorno a la Lista.
        assertNull(vm.state.value.contents)
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private fun context(id: String) = Context(
        id = id,
        name = "Mercado Central",
        createdAt = 1,
        updatedAt = 1,
    )

    private class FakeContexts(initial: List<Context> = emptyList()) : ContextRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) { state.value = state.value + context }
        override suspend fun update(context: Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }

    private class FakeDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String): Destination? = null
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class FakeOperations : OperationRepository {
        private val state = MutableStateFlow(
            listOf(
                Operation("op-1", OperationType.PAGO, occurredAt = 100, createdAt = 100),
            ),
        )
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) { state.value = state.value + operation }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }

    private class FakeReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) { state.value = state.value + comprobante }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
    }
}
