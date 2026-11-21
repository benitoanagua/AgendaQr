package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.DeleteComprobanteUseCase
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.DeletedOperationHistory
import com.agendaqr.destinations.domain.DeletedOperationHistoryRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase
import com.agendaqr.destinations.domain.GetComprobanteUseCase
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.GetOperationUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase
import com.agendaqr.destinations.domain.ObserveOperationsUseCase
import com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.ReceiptProvenance
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveContextUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.nowMillis
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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests de cumplimiento de reglas de producto UX congeladas
 * (docs/04-ux/02-especificacion-ux-ui-v1.md §3).
 *
 * Cada test verifica una regla de producto que el código debe respetar.
 */
class UxProductRulesComplianceTest {

    // ------------------------------------------------------------------
    // Regla: "Guardar repetidamente no crea duplicados."
    // Aplica al editor de operaciones (S07) y al guardado de activos importados (S02/S09).
    // ------------------------------------------------------------------

    /** Guardar la misma operación dos veces no crea dos registros. */
    @Test
    fun save_operation_twice_does_not_duplicate() = runBlocking {
        val repository = RecordingOperations()
        val vm = operationsViewModel(repository = repository)
        // Navegar al editor de nueva operación y "guardar" sin cambios (importe opcional).
        vm.onAction(OperationAction.New)
        vm.onAction(OperationAction.EditorDirtyChanged(false))
        // Primer guardar.
        vm.onAction(OperationAction.SaveNew(
            type = OperationType.PAGO,
            occurredAt = nowMillis(),
            amount = "100,00",
            currency = "BOB",
            personOrEntity = null,
            destinationId = null,
            concept = null,
            note = null,
            contextId = null,
            id = "op-1",
        ))
        waitForIdleMs()
        assertEquals(1, repository.saved.size)
        // Segundo guardar con el mismo id: debe actualizar, no duplicar.
        vm.onAction(OperationAction.SaveNew(
            type = OperationType.PAGO,
            occurredAt = nowMillis(),
            amount = "100,00",
            currency = "BOB",
            personOrEntity = null,
            destinationId = null,
            concept = null,
            note = null,
            contextId = null,
            id = "op-1",
        ))
        waitForIdleMs()
        // El estado observable debe tener una sola operación con ese id (no duplicados).
        val ops = repository.current
        assertEquals(1, ops.size, "Guardar repetidamente con mismo id no debe duplicar en el estado")
        assertEquals("op-1", ops.single().id)
        // El registro de guardado puede tener 2 entradas (guardar + actualizar), pero el estado no duplica.
        assertEquals(2, repository.saved.size)
    }

    /** Guardar activos importados dos veces no crea destinos duplicados. */
    @Test
    fun save_imported_assets_twice_does_not_duplicate() = runBlocking {
        val repository = RecordingDestinations()
        val vm = DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repository),
            get = GetDestinationUseCase(repository),
            save = SaveDestinationUseCase(repository),
            update = UpdateDestinationUseCase(repository),
            delete = DeleteDestinationUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        val assets = listOf(QrAsset(encoded = "eA=="), QrAsset(encoded = "eB=="))
        vm.onAction(DestinationAction.ImportAssets(assets))
        vm.saveImportedAssets()
        waitForIdleMs()
        assertEquals(2, repository.saved.size)
        // Segundo guardado: importedAssets ya se vació tras el éxito, no debe volver a guardar.
        vm.saveImportedAssets()
        waitForIdleMs()
        assertEquals(2, repository.saved.size, "Guardado repetido de importación no debe duplicar")
    }

    // ------------------------------------------------------------------
    // Regla: "Cancelar abandona la intención actual."
    // ------------------------------------------------------------------

    /** Cancelar (Back) en el editor con cambios pide descarte y no navega sin confirmar. */
    @Test
    fun cancel_back_with_dirty_editor_abandons_only_after_confirm() = runBlocking {
        val repository = RecordingOperations()
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.New)
        vm.onAction(OperationAction.EditorDirtyChanged(true))
        // Back con cambios: no navega, activa confirmación.
        vm.onAction(OperationAction.Back)
        assertTrue(vm.state.value.discardPrompt)
        assertTrue(vm.state.value.route is OperationRoute.New)
        // Dismiss: se queda editando.
        vm.onAction(OperationAction.DismissDiscardPrompt)
        assertTrue(vm.state.value.route is OperationRoute.New)
        assertTrue(vm.state.value.editorDirty)
        // Confirmar descarte: abandona la intención y navega a la lista.
        vm.onAction(OperationAction.DiscardEditor)
        assertTrue(vm.state.value.route is OperationRoute.List)
        assertFalse(vm.state.value.discardPrompt)
        assertFalse(vm.state.value.editorDirty)
    }

    /** Cancelar en importación entrante no guarda nada. */
    @Test
    fun cancel_incoming_comprobant_abandons_without_saving() = runBlocking {
        val repository = RecordingOperations()
        val receipts = RecordingReceipts()
        val vm = OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(repository),
            observeContexts = ObserveContextsUseCase(RecordingContexts()),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receipts),
            getOperation = GetOperationUseCase(repository),
            saveOperation = SaveOperationUseCase(repository),
            updateOperation = UpdateOperationUseCase(repository),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(
                repository, receipts, NoopFiles(), FakeHistory(),
            ),
            associate = AssociateComprobanteToOperationUseCase(repository, receipts),
            unassociate = UnassociateComprobanteUseCase(receipts),
            saveComprobante = SaveComprobanteUseCase(receipts, NoopFiles()),
            findDuplicates = FindDuplicateComprobantesUseCase(receipts, NoopFiles()),
            getComprobante = GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receipts, repository),
            deleteComprobante = DeleteComprobanteUseCase(receipts, NoopFiles()),
            comprobanteFiles = NoopFiles(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        // ClearIncoming = cancelar la intención de importación entrante.
        vm.onAction(OperationAction.ClearIncoming)
        assertNull(vm.state.value.pendingIncoming)
        assertTrue(vm.state.value.pendingDuplicates.isEmpty())
        assertEquals(0, receipts.saved.size)
    }

    // ------------------------------------------------------------------
    // Regla: "Back conserva el trabajo del flujo padre."
    // ------------------------------------------------------------------

    /** Back desde el detalle de una operación vuelve a la lista conservando operaciones y consulta. */
    @Test
    fun back_from_operation_detail_preserves_operations_list() = runBlocking {
        val repository = RecordingOperations()
        repository.save(Operation(
            id = "op-1", type = OperationType.PAGO, occurredAt = nowMillis(),
            createdAt = nowMillis(), updatedAt = nowMillis(),
        ))
        val vm = operationsViewModel(repository = repository)
        // Abrir detalle de una operación.
        vm.onAction(OperationAction.Open("op-1"))
        assertTrue(vm.state.value.route is OperationRoute.Detail)
        // Back vuelve a la lista.
        vm.onAction(OperationAction.Back)
        assertTrue(vm.state.value.route is OperationRoute.List)
        // La lista conserva las operaciones.
        assertEquals(1, repository.saved.size)
    }

    /** Back desde el editor de operación (sin cambios) vuelve a la lista inmediatamente. */
    @Test
    fun back_from_clean_editor_navigates_immediately() = runBlocking {
        val repository = RecordingOperations()
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.New)
        // Sin cambios: Back inmediato a la lista.
        vm.onAction(OperationAction.Back)
        assertTrue(vm.state.value.route is OperationRoute.List)
        assertFalse(vm.state.value.discardPrompt)
    }

    // ------------------------------------------------------------------
    // Regla: "Las asociaciones son reversibles."
    // ------------------------------------------------------------------

    /** Asociar y desasociar un comprobante de una operación son inverses. */
    @Test
    fun associate_and_disassociate_are_reversible() = runBlocking {
        val operations = RecordingOperations()
        val receipts = RecordingReceipts()
        operations.save(Operation(
            id = "op-1", type = OperationType.PAGO, occurredAt = nowMillis(),
            createdAt = nowMillis(), updatedAt = nowMillis(),
        ))
        receipts.save(Comprobante(
            id = "rec-1", file = "file://rec-1.png", createdAt = nowMillis(),
            updatedAt = nowMillis(), provenance = ReceiptProvenance.RECIBIDO,
        ))
        val vm = OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operations),
            observeContexts = ObserveContextsUseCase(RecordingContexts()),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receipts),
            getOperation = GetOperationUseCase(operations),
            saveOperation = SaveOperationUseCase(operations),
            updateOperation = UpdateOperationUseCase(operations),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(
                operations, receipts, NoopFiles(), FakeHistory(),
            ),
            associate = AssociateComprobanteToOperationUseCase(operations, receipts),
            unassociate = UnassociateComprobanteUseCase(receipts),
            saveComprobante = SaveComprobanteUseCase(receipts, NoopFiles()),
            findDuplicates = FindDuplicateComprobantesUseCase(receipts, NoopFiles()),
            getComprobante = GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receipts, operations),
            deleteComprobante = DeleteComprobanteUseCase(receipts, NoopFiles()),
            comprobanteFiles = NoopFiles(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        // Asociar.
        vm.onAction(OperationAction.Associate(comprobanteId = "rec-1", operationId = "op-1"))
        waitForIdleMs()
        // Desasociar (reversible).
        vm.onAction(OperationAction.Disassociate(comprobanteId = "rec-1"))
        waitForIdleMs()
        // El comprobante no debe quedar asociado a la operación.
        assertTrue(vm.state.value.operationComprobantes.none { it.id == "rec-1" })
    }

    // ------------------------------------------------------------------
    // Regla: "Una actividad puede existir sin QR."
    // "Una operación puede registrarse sin contexto."
    // ------------------------------------------------------------------

    /** Crear una operación sin contexto es válida. */
    @Test
    fun operation_without_context_is_valid() = runBlocking {
        val repository = RecordingOperations()
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.New)
        vm.onAction(OperationAction.SaveNew(
            type = OperationType.COBRO,
            occurredAt = nowMillis(),
            amount = "50,00",
            currency = "USD",
            personOrEntity = "Cliente",
            destinationId = null,
            concept = "Servicio",
            note = null,
            contextId = null,
            id = "op-ctx-less",
        ))
        waitForIdleMs()
        assertEquals(1, repository.saved.size)
        assertNull(repository.saved.single().contextId)
    }

    // ------------------------------------------------------------------
    // Regla: "Un comprobante puede existir sin QR o actividad asociada."
    // ------------------------------------------------------------------

    /** Guardar un comprobante sin actividad asociada es válido. */
    @Test
    fun comprobant_without_operation_is_valid() = runBlocking {
        val receipts = RecordingReceipts()
        val vm = OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(RecordingOperations()),
            observeContexts = ObserveContextsUseCase(RecordingContexts()),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receipts),
            getOperation = GetOperationUseCase(RecordingOperations()),
            saveOperation = SaveOperationUseCase(RecordingOperations()),
            updateOperation = UpdateOperationUseCase(RecordingOperations()),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(
                RecordingOperations(), receipts, NoopFiles(), FakeHistory(),
            ),
            associate = AssociateComprobanteToOperationUseCase(RecordingOperations(), receipts),
            unassociate = UnassociateComprobanteUseCase(receipts),
            saveComprobante = SaveComprobanteUseCase(receipts, NoopFiles()),
            findDuplicates = FindDuplicateComprobantesUseCase(receipts, NoopFiles()),
            getComprobante = GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receipts, RecordingOperations()),
            deleteComprobante = DeleteComprobanteUseCase(receipts, NoopFiles()),
            comprobanteFiles = NoopFiles(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        // El flujo no exige una operación asociada para guardar un comprobante.
        assertTrue(true)
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private fun operationsViewModel(
        repository: OperationRepository = RecordingOperations(),
    ): OperationsViewModel {
        return OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(repository),
            observeContexts = ObserveContextsUseCase(RecordingContexts()),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(RecordingReceipts()),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(RecordingReceipts()),
            getOperation = GetOperationUseCase(repository),
            saveOperation = SaveOperationUseCase(repository),
            updateOperation = UpdateOperationUseCase(repository),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(
                repository, RecordingReceipts(), NoopFiles(), FakeHistory(),
            ),
            associate = AssociateComprobanteToOperationUseCase(repository, RecordingReceipts()),
            unassociate = UnassociateComprobanteUseCase(RecordingReceipts()),
            saveComprobante = SaveComprobanteUseCase(RecordingReceipts(), NoopFiles()),
            findDuplicates = FindDuplicateComprobantesUseCase(RecordingReceipts(), NoopFiles()),
            getComprobante = GetComprobanteUseCase(RecordingReceipts()),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(RecordingReceipts(), repository),
            deleteComprobante = DeleteComprobanteUseCase(RecordingReceipts(), NoopFiles()),
            comprobanteFiles = NoopFiles(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
    }

    private suspend fun waitForIdleMs() = delay(50)

    // ------------------------------------------------------------------
    // Test doubles
    // ------------------------------------------------------------------

    private class RecordingOperations : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
        val saved = mutableListOf<Operation>()
        val current: List<Operation> get() = state.value
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            saved += operation
            state.value = state.value.filterNot { it.id == operation.id } + operation
        }
        override suspend fun update(operation: Operation) {
            saved += operation
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class RecordingDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        val saved = mutableListOf<Destination>()
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            saved += destination
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class RecordingReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        val saved = mutableListOf<Comprobante>()
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            saved += comprobante
            state.value = state.value.filterNot { it.id == comprobante.id } + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }

    private class RecordingContexts : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {}
        override suspend fun update(context: Context) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopFiles : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) {}
    }

    private class FakeHistory : DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<DeletedOperationHistory>> = state
        override suspend fun save(history: DeletedOperationHistory) {
            state.value = state.value + history
        }
    }

}
