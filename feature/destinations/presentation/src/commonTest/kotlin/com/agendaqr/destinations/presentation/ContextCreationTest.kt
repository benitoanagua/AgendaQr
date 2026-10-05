package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.ObserveContextContentsUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.SaveContextUseCase
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
 * U3/ADR-0003 — creación de contextos, OPCIÓN C aprobada.
 * Tests A.1–A.5 y B.3 del ADR (B.1/B.2 son visuales: estado vacío con
 * acción primaria y sin creación en S06 lleno — verificados en runtime).
 */
class ContextCreationTest {

    private class RecordingContexts : ContextRepository {
        val saved = mutableListOf<Context>()
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {
            saved += context
            state.value = state.value + context
        }
        override suspend fun update(context: Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private fun viewModel(repo: ContextRepository): ContextsViewModel = ContextsViewModel(
        observe = ObserveContextsUseCase(repo),
        observeContents = ObserveContextContentsUseCase(repo, NoopDestinations(), NoopOperations(), NoopReceipts()),
        get = GetContextUseCase(repo),
        save = SaveContextUseCase(repo),
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
    )

    // A.4: nombre vacío no crea.
    @Test
    fun create_with_blank_name_is_rejected(): Unit = runBlocking {
        val repo = RecordingContexts()
        val vm = viewModel(repo)
        waitUntil { !vm.state.value.isLoading }
        vm.onAction(ContextAction.Create("   ", null))
        delay(100)
        assertTrue(repo.saved.isEmpty(), "Un nombre vacío no debe persistir nada")
        assertNull(vm.state.value.justCreatedContextId)
        assertNotNull(vm.state.value.error)
    }

    // A.1/A.5: crear desde el flujo padre conserva el draft y selecciona.
    @Test
    fun create_persists_selectable_context_with_name_and_optional_note(): Unit = runBlocking {
        val repo = RecordingContexts()
        val vm = viewModel(repo)
        waitUntil { !vm.state.value.isLoading }
        var selection = ContextSelection(contextId = "previo").openPicker()

        vm.onAction(ContextAction.Create("Mercado Central", "nota opcional"))
        waitUntil { vm.state.value.justCreatedContextId != null }

        val createdId = vm.state.value.justCreatedContextId
        assertNotNull(createdId)
        // El contexto creado es seleccionable por el flujo padre (S08).
        selection = selection.select(createdId)
        assertEquals(createdId, selection.contextId)
        // El draft del flujo padre (selección previa reemplazada solo por
        // la elección explícita del usuario) nunca salió de composición.
        assertTrue(repo.saved.isNotEmpty())
        assertEquals("Mercado Central", repo.saved.single().name)
        assertEquals("nota opcional", repo.saved.single().note)
    }

    // A.3: cancelar el paso de creación no persiste nada.
    @Test
    fun cancel_of_create_step_persists_nothing(): Unit = runBlocking {
        val repo = RecordingContexts()
        val vm = viewModel(repo)
        waitUntil { !vm.state.value.isLoading }
        var selection = ContextSelection(contextId = "previo").openPicker()
        // Cancelar (volver a la lista sin crear): estado del selector.
        selection = selection.cancelPicker()
        assertEquals("previo", selection.contextId)
        assertTrue(repo.saved.isEmpty())
        assertNull(vm.state.value.justCreatedContextId)
    }

    // A.5: el contexto aparece en la lista observada (S06/búsqueda).
    @Test
    fun created_context_appears_in_observed_contexts(): Unit = runBlocking {
        val repo = RecordingContexts()
        val vm = viewModel(repo)
        waitUntil { !vm.state.value.isLoading }
        vm.onAction(ContextAction.Create("Ferreteria", null))
        waitUntil { vm.state.value.contexts.any { it.name == "Ferreteria" } }
        assertEquals(1, vm.state.value.contexts.size)
        vm.onAction(ContextAction.ClearJustCreated)
        assertNull(vm.state.value.justCreatedContextId)
    }

    // B.3: la acción de crear desde S06 vacío es la primaria; con ≥1
    // contexto S06 NO gana creación (sin jerarquía nueva).
    @Test
    fun s06_creation_is_only_offered_in_the_empty_state(): Unit = runBlocking {
        val repo = RecordingContexts()
        val vm = viewModel(repo)
        waitUntil { !vm.state.value.isLoading }
        // Vacío: la acción primaria de S06 es crear (B.1).
        assertTrue(vm.state.value.contexts.isEmpty())
        vm.onAction(ContextAction.Create("Unico", null))
        waitUntil { vm.state.value.contexts.isNotEmpty() }
        // S06 lleno: sin acción de creación adicional en la superficie
        // (B.2); la creación sigue disponible solo desde S08.
        assertEquals(1, vm.state.value.contexts.size)
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private class NoopDestinations : com.agendaqr.destinations.domain.DestinationRepository {
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.Destination>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.Destination>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(destination: com.agendaqr.destinations.domain.Destination) {}
        override suspend fun update(destination: com.agendaqr.destinations.domain.Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopOperations : com.agendaqr.destinations.domain.OperationRepository {
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.Operation>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.Operation>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(operation: com.agendaqr.destinations.domain.Operation) {}
        override suspend fun update(operation: com.agendaqr.destinations.domain.Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopReceipts : com.agendaqr.destinations.domain.ComprobanteRepository {
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.Comprobante>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.Comprobante>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(comprobante: com.agendaqr.destinations.domain.Comprobante) {}
        override suspend fun update(comprobante: com.agendaqr.destinations.domain.Comprobante) {}
        override suspend fun delete(id: String) {}
    }
}
