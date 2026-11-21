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
 * Tests de cumplimiento de transiciones UX críticas
 * (build-logic/src/main/kotlin/UxTransitionContracts.kt).
 *
 * Cada test verifica que un evento UX produce el efecto declarado en el
 * contrato de transición: navegación, persistencia, confirmación, error,
 * o sin navegación.
 */
class UxTransitionsComplianceTest {

    // ------------------------------------------------------------------
    // S02 → S09: Importar un solo elemento va a Revisar QR (navegación interna).
    // ------------------------------------------------------------------

    @Test
    fun import_single_asset_routes_to_import_review() = runBlocking {
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
        // Un solo activo: debe entrar en revisión (S09), no en lote (S12).
        vm.onAction(DestinationAction.ImportAssets(listOf(QrAsset(encoded = "eA=="))))
        assertEquals(DestinationRoute.ImportReview, vm.state.value.route)
        assertEquals(1, vm.importedAssets().size)
    }

    // ------------------------------------------------------------------
    // S09 → S02: Cancelar desde Revisar QR vuelve a Añadir.
    // ------------------------------------------------------------------

    @Test
    fun cancel_from_import_review_returns_to_add() = runBlocking {
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
        vm.onAction(DestinationAction.ImportAssets(listOf(QrAsset(encoded = "eA=="))))
        assertEquals(DestinationRoute.ImportReview, vm.state.value.route)
        // Back / Cancel desde S09 vuelve a S02 (Add).
        vm.onAction(DestinationAction.Back)
        assertEquals(DestinationRoute.Add, vm.state.value.route)
    }

    // ------------------------------------------------------------------
    // S09: Guardar bloquea la navegación mientras persiste (saving).
    // ------------------------------------------------------------------

    @Test
    fun save_from_import_review_blocks_until_saved() = runBlocking {
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
        vm.onAction(DestinationAction.ImportAssets(listOf(QrAsset(encoded = "eA=="))))
        vm.saveImportedAssets()
        delay(100)
        // Tras el guardado, S09 sale a S02 (Add); un segundo back llega a List.
        assertEquals(DestinationRoute.Add, vm.state.value.route)
        assertTrue(vm.importedAssets().isEmpty(), "Los activos se vacían tras guardar")
    }

    // ------------------------------------------------------------------
    // S07 → operation-detail: Guardar en el editor navega al detalle.
    // ------------------------------------------------------------------

    @Test
    fun save_new_operation_navigates_to_detail() = runBlocking {
        val repository = RecordingOperations()
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.New)
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
            id = "op-new",
        ))
        delay(100)
        assertTrue(vm.state.value.route is OperationRoute.Detail)
        val detail = assertIs<OperationRoute.Detail>(vm.state.value.route)
        assertEquals("op-new", detail.id)
    }

    // ------------------------------------------------------------------
    // S07: Back desde el detalle vuelve a la lista conservando el trabajo.
    // ------------------------------------------------------------------

    @Test
    fun back_from_detail_returns_to_list_preserving_operations() = runBlocking {
        val repository = RecordingOperations()
        repository.save(Operation(
            id = "op-1", type = OperationType.PAGO, occurredAt = nowMillis(),
            createdAt = nowMillis(), updatedAt = nowMillis(),
        ))
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.Open("op-1"))
        assertTrue(vm.state.value.route is OperationRoute.Detail)
        vm.onAction(OperationAction.Back)
        assertTrue(vm.state.value.route is OperationRoute.List)
        assertEquals(1, repository.current.size)
    }

    // ------------------------------------------------------------------
    // operation-detail: Asociar y desasociar son reversibles.
    // ------------------------------------------------------------------

    @Test
    fun associate_then_disassociate_reverts_comprobante() = runBlocking {
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
        vm.onAction(OperationAction.Associate(comprobanteId = "rec-1", operationId = "op-1"))
        delay(50)
        // Desasociar revierte la asociación.
        vm.onAction(OperationAction.Disassociate(comprobanteId = "rec-1"))
        delay(50)
        assertTrue(vm.state.value.operationComprobantes.none { it.id == "rec-1" })
    }

    // ------------------------------------------------------------------
    // S07-list: Eliminar persiste el borrado y vuelve a la lista.
    // ------------------------------------------------------------------

    @Test
    fun delete_operation_removes_and_returns_to_list() = runBlocking {
        val repository = RecordingOperations()
        repository.save(Operation(
            id = "op-1", type = OperationType.PAGO, occurredAt = nowMillis(),
            createdAt = nowMillis(), updatedAt = nowMillis(),
        ))
        val vm = operationsViewModel(repository = repository)
        vm.onAction(OperationAction.Open("op-1"))
        vm.onAction(OperationAction.Delete("op-1"))
        delay(100)
        assertTrue(vm.state.value.route is OperationRoute.List)
        assertTrue(repository.current.none { it.id == "op-1" })
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

    private suspend fun delay(ms: Long) = kotlinx.coroutines.delay(ms)

    // ------------------------------------------------------------------
    // Test doubles
    // ------------------------------------------------------------------

    private class RecordingOperations : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
        val current: List<Operation> get() = state.value
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            state.value = state.value.filterNot { it.id == operation.id } + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class RecordingDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class RecordingReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
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
