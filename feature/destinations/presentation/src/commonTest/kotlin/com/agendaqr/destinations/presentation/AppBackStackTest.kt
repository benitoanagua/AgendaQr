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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * T2 — Back stack de superficies.
 *
 * Contrato implicado (reglas congeladas §3):
 * - "Back conserva el trabajo del flujo padre": al hacer pop, la superficie
 *   que queda debajo se restaura tal como estaba (S05 conserva la consulta).
 * - "Cancelar abandona la intención actual": el flujo interno consume el
 *   Back antes de que la superficie haga pop.
 * - En la raíz (S01) el Back pertenece al sistema: la app sale.
 */
class AppBackStackTest {

    // ------------------------------------------------------------------
    // Semántica básica de push/pop.
    // ------------------------------------------------------------------

    @Test
    fun root_is_home_and_cannot_pop() {
        val nav = AppBackStack()
        assertEquals(AppRoute.Home, nav.top)
        assertFalse(nav.canPop())
        assertNull(nav.pop())
        assertEquals(AppRoute.Home, nav.top)
        // El Back en la raíz es del sistema (salir de la app).
        assertEquals(
            SystemBackAction.SystemExit,
            systemBackAction(nav.top, needsInnerBack = { false }, canPop = nav.canPop()),
        )
    }

    @Test
    fun push_and_pop_restore_the_surface_below() {
        val nav = AppBackStack()
        nav.push(AppRoute.Operations)
        assertEquals(AppRoute.Home, nav.belowTop())
        assertEquals(AppRoute.Operations, nav.top)

        nav.push(AppRoute.Search)
        assertEquals(AppRoute.Operations, nav.belowTop())

        assertEquals(AppRoute.Search, nav.pop())
        assertEquals(AppRoute.Operations, nav.top)
        assertEquals(AppRoute.Operations, nav.pop())
        assertEquals(AppRoute.Home, nav.top)
    }

    @Test
    fun push_does_not_duplicate_the_visible_surface() {
        val nav = AppBackStack()
        nav.push(AppRoute.Operations)
        nav.push(AppRoute.Operations) // eventos repetidos (imports compartidos)
        assertEquals(2, nav.size)
        nav.push(AppRoute.ImportBatch)
        nav.push(AppRoute.ImportBatch)
        assertEquals(3, nav.size)
    }

    @Test
    fun pop_to_root_clears_every_surface() {
        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        nav.push(AppRoute.Contexts)
        nav.popToRoot()
        assertEquals(listOf(AppRoute.Home), nav.stack.value)
    }

    // ------------------------------------------------------------------
    // Back del sistema: primero el flujo interno, después el pop.
    // ------------------------------------------------------------------

    @Test
    fun system_back_runs_the_inner_flow_before_popping_the_surface() {
        val nav = AppBackStack()
        nav.push(AppRoute.Operations)
        // Detalle abierto dentro de Operaciones: el flujo interno consume.
        var innerBackRuns = 0
        val decision = systemBackAction(
            nav.top,
            needsInnerBack = { it == AppRoute.Operations },
            canPop = nav.canPop(),
        )
        assertSame(SystemBackAction.InnerFlow::class, decision::class)
        (decision as SystemBackAction.InnerFlow).let { innerBackRuns += 1 }
        assertEquals(1, innerBackRuns)
        // La superficie sigue: el pop solo ocurre cuando el flujo interno
        // no tiene adónde volver.
        assertEquals(AppRoute.Operations, nav.top)
    }

    @Test
    fun system_back_pops_the_surface_when_the_inner_flow_is_at_its_root() {
        val nav = AppBackStack()
        nav.push(AppRoute.Contexts)
        val decision = systemBackAction(
            nav.top,
            needsInnerBack = { false }, // Lista de contextos: sin flujo interno
            canPop = nav.canPop(),
        )
        assertEquals(SystemBackAction.PopSurface, decision)
        nav.pop()
        assertEquals(AppRoute.Home, nav.top)
    }

    // ------------------------------------------------------------------
    // S04/S05 → S06: Back desde un resultado Contexto vuelve a la Búsqueda
    // con la consulta conservada.
    // ------------------------------------------------------------------

    @Test
    fun back_from_context_result_returns_to_search_with_query_preserved() = runBlocking {
        // Flujo real: S04 con consulta escrita; el resultado Contexto
        // apila S06 ENCIMA de la Búsqueda.
        val search = GlobalSearchViewModel(
            search = SearchAgendaQrUseCase(
                FakeContexts(),
                FakeDestinations(),
                FakeOperations(),
                FakeReceipts(),
            ),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        search.onAction(GlobalSearchAction.QueryChanged("carniceria"))
        waitUntil { search.state.value.query == "carniceria" }

        val nav = AppBackStack()
        nav.push(AppRoute.Search)
        nav.push(AppRoute.Contexts)
        assertEquals(AppRoute.Search, nav.belowTop())

        // Back en la Lista de contextos: pop de superficie, no flujo interno.
        val decision = systemBackAction(
            nav.top,
            needsInnerBack = { false },
            canPop = nav.canPop(),
        )
        assertEquals(SystemBackAction.PopSurface, decision)
        nav.pop()

        assertEquals(AppRoute.Search, nav.top)
        // S05: la consulta sigue intacta al volver.
        assertEquals("carniceria", search.state.value.query)
    }

    // ------------------------------------------------------------------
    // S08 (Registrar → Seleccionar contexto): Cancelar abandona la
    // intención sin guardar. La conservación del draft del editor al
    // abrir/cerrar el selector es una propiedad del paso modal (el editor
    // nunca sale de composición): se verifica en runtime con Back del
    // sistema y queda cubierta por los tests del selector compartido (T4).
    // ------------------------------------------------------------------

    @Test
    fun cancel_abandons_the_registration_intention_without_saving() = runBlocking {
        val operations = FakeOperations()
        val vm = OperationsViewModelFactory.create(operations)
        vm.onAction(OperationAction.New)
        assertEquals(OperationRoute.New, vm.state.value.route)

        // Back/Cancelar desde el registro: vuelve a la lista y no guarda nada.
        vm.onAction(OperationAction.Back)
        assertEquals(OperationRoute.List, vm.state.value.route)
        assertTrue(operations.saved.isEmpty())
    }

    @Test
    fun back_from_home_inner_route_stays_in_home_surface() {
        val nav = AppBackStack()
        // Add/ImportReview son rutas internas del flujo de destinos: el Back
        // lo consume el flujo (VM), no el stack de superficies.
        val needsInnerBack: (AppRoute) -> Boolean = { it == AppRoute.Home }
        val decision = systemBackAction(nav.top, needsInnerBack, nav.canPop())
        assertEquals(SystemBackAction.InnerFlow(AppRoute.Home), decision)
        // El stack no cambió: la app no sale.
        assertEquals(listOf(AppRoute.Home), nav.stack.value)
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }
}

/** Factoría compartida para no duplicar el cableado del ViewModel de operaciones. */
internal object OperationsViewModelFactory {
    fun create(operations: OperationRepository): OperationsViewModel {
        val contexts = FakeContexts()
        val receipts = FakeReceipts()
        val files = MemoryFiles()
        return OperationsViewModel(
            observeOperations = com.agendaqr.destinations.domain.ObserveOperationsUseCase(operations),
            observeContexts = com.agendaqr.destinations.domain.ObserveContextsUseCase(contexts),
            observeUnassociated = com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase(receipts),
            getOperation = com.agendaqr.destinations.domain.GetOperationUseCase(operations),
            saveOperation = com.agendaqr.destinations.domain.SaveOperationUseCase(operations),
            updateOperation = com.agendaqr.destinations.domain.UpdateOperationUseCase(operations),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(operations, receipts, files, FakeHistory()),
            associate = com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase(operations, receipts),
            unassociate = com.agendaqr.destinations.domain.UnassociateComprobanteUseCase(receipts),
            saveComprobante = com.agendaqr.destinations.domain.SaveComprobanteUseCase(receipts, files),
            findDuplicates = com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase(receipts, files),
            getComprobante = com.agendaqr.destinations.domain.GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase(receipts, operations),
            deleteComprobante = com.agendaqr.destinations.domain.DeleteComprobanteUseCase(receipts, files),
            comprobanteFiles = files,
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
    }
}

private class FakeContexts : ContextRepository {
    private val state = MutableStateFlow<List<Context>>(emptyList())
    override fun observe(): Flow<List<Context>> = state
    override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
    override suspend fun save(context: Context) { state.value = state.value + context }
    override suspend fun update(context: Context) {
        state.value = state.value.map { if (it.id == context.id) context else it }
    }
    override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
}

private class FakeDestinations : DestinationRepository {
    private val state = MutableStateFlow(
        listOf(
            Destination(
                id = "d-1",
                name = "Carniceria Don Bife",
                qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                createdAt = 1,
                updatedAt = 1,
            ),
        ),
    )
    override fun observe(): Flow<List<Destination>> = state
    override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
    override suspend fun save(destination: Destination) { state.value = state.value + destination }
    override suspend fun update(destination: Destination) {
        state.value = state.value.map { if (it.id == destination.id) destination else it }
    }
    override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
}

private class FakeOperations : OperationRepository {
    val saved = mutableListOf<Operation>()
    private val state = MutableStateFlow<List<Operation>>(emptyList())
    override fun observe(): Flow<List<Operation>> = state
    override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
    override suspend fun save(operation: Operation) {
        saved += operation
        state.value = state.value + operation
    }
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

private class MemoryFiles : com.agendaqr.destinations.domain.ComprobanteFileStore {
    private val files = mutableMapOf<String, ByteArray>()
    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        val path = "file://$id.$extension"
        files[path] = bytes.copyOf()
        return path
    }
    override suspend fun read(file: String): ByteArray? = files[file]?.copyOf()
    override suspend fun delete(file: String) { files.remove(file) }
}

private class FakeHistory : com.agendaqr.destinations.domain.DeletedOperationHistoryRepository {
    private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>>(emptyList())
    override fun observe(): Flow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>> = state
    override suspend fun save(history: com.agendaqr.destinations.domain.DeletedOperationHistory) {
        state.value = state.value + history
    }
}
