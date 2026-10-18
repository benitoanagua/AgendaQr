package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.DeleteComprobanteUseCase
import com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase
import com.agendaqr.destinations.domain.DeletedOperationHistory
import com.agendaqr.destinations.domain.DeletedOperationHistoryRepository
import com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase
import com.agendaqr.destinations.domain.GetOperationUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase
import com.agendaqr.destinations.domain.ObserveOperationsUseCase
import com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OperationsComprobanteActionsTest {
    @Test
    fun openComprobante_loads_bytes_and_shows_viewer() {
        runBlocking {
            val files = MemoryFiles()
            files.put("file://r-1.png", byteArrayOf(1, 2, 3))
            val receipts = FakeReceipts(
                listOf(Comprobante("r-1", "file://r-1.png", createdAt = 10, updatedAt = 10)),
            )
            val vm = viewModel(FakeOperations(), receipts, files)
            waitUntil { vm.state.value.unassociated.any { it.id == "r-1" } }

            vm.onAction(OperationAction.OpenComprobante("r-1"))
            waitUntil { !vm.state.value.isLoadingComprobante }

            assertEquals("r-1", vm.state.value.openedComprobante?.id)
            assertContentEquals(byteArrayOf(1, 2, 3), vm.state.value.openedComprobanteBytes)

            vm.onAction(OperationAction.CloseComprobante)
            assertNull(vm.state.value.openedComprobante)
            assertNull(vm.state.value.openedComprobanteBytes)
        }
    }

    @Test
    fun deleteComprobante_removes_receipt_and_keeps_operation() {
        runBlocking {
            val files = MemoryFiles()
            files.put("file://r-1.png", byteArrayOf(9))
            val operations = FakeOperations(
                listOf(
                    Operation("op-1", OperationType.PAGO, occurredAt = 100, createdAt = 100),
                ),
            )
            val receipts = FakeReceipts(
                listOf(Comprobante("r-1", "file://r-1.png", createdAt = 10, updatedAt = 10, operationId = "op-1")),
            )
            val vm = viewModel(operations, receipts, files)
            waitUntil { vm.state.value.operations.any { it.id == "op-1" } }

            // El borrado se alcanza desde el visor: abrir operación y comprobante, como en producción.
            vm.onAction(OperationAction.Open("op-1"))
            waitUntil { vm.state.value.operationComprobantes.any { it.id == "r-1" } }
            vm.onAction(OperationAction.OpenComprobante("r-1"))
            waitUntil { !vm.state.value.isLoadingComprobante }

            vm.onAction(OperationAction.Disassociate("r-1"))
            waitUntil { receipts.get("r-1")?.operationId == null }
            assertNull(receipts.get("r-1")?.operationId)

            vm.onAction(OperationAction.Associate("r-1", "op-1"))
            waitUntil { receipts.get("r-1")?.operationId == "op-1" }

            vm.onAction(OperationAction.DeleteComprobante("r-1"))
            waitUntil { receipts.deleted.contains("r-1") }

            assertTrue(receipts.deleted.contains("r-1"))
            assertNull(files.read("file://r-1.png"))
            // La operación permanece: el borrado de comprobante no la toca.
            assertNotNull(operations.get("op-1"))
            assertNull(vm.state.value.openedComprobante)
        }
    }

    @Test
    fun saveNew_resets_saving_flag_on_success_and_failure() {
        runBlocking {
            val operations = FakeOperations()
            val vm = viewModel(operations, FakeReceipts(), MemoryFiles())
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
            waitUntil { !vm.state.value.isSavingOperation }
            assertFalse(vm.state.value.isSavingOperation)
            assertTrue(vm.state.value.route is OperationRoute.Detail)

            val failing = FakeOperations(failOnSave = true)
            val failingVm = viewModel(failing, FakeReceipts(), MemoryFiles())
            failingVm.onAction(
                OperationAction.SaveNew(
                    type = OperationType.COBRO,
                    occurredAt = 2_000,
                    amount = null,
                    currency = null,
                    personOrEntity = null,
                    destinationId = null,
                    concept = null,
                    note = null,
                    contextId = null,
                ),
            )
            waitUntil { !failingVm.state.value.isSavingOperation }
            assertFalse(failingVm.state.value.isSavingOperation)
            assertNotNull(failingVm.state.value.error)
        }
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private fun viewModel(
        operations: FakeOperations,
        receipts: FakeReceipts,
        files: MemoryFiles,
    ): OperationsViewModel {
        val contexts = FakeContexts()
        val history = FakeHistory()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        return OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operations),
            observeContexts = ObserveContextsUseCase(contexts),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receipts),
            getOperation = GetOperationUseCase(operations),
            saveOperation = SaveOperationUseCase(operations),
            updateOperation = UpdateOperationUseCase(operations),
            deleteOperation = DeleteOperationWithHistoryUseCase(operations, receipts, files, history),
            associate = AssociateComprobanteToOperationUseCase(operations, receipts),
            unassociate = UnassociateComprobanteUseCase(receipts),
            saveComprobante = SaveComprobanteUseCase(receipts, files),
            findDuplicates = FindDuplicateComprobantesUseCase(receipts, files),
            getComprobante = com.agendaqr.destinations.domain.GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receipts, operations),
            deleteComprobante = DeleteComprobanteUseCase(receipts, files),
            comprobanteFiles = files,
            scope = scope,
        )
    }

    private class FakeOperations(
        initial: List<Operation> = emptyList(),
        val failOnSave: Boolean = false,
    ) : OperationRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            if (failOnSave) throw IllegalStateException("storage unavailable")
            state.value = state.value.filterNot { it.id == operation.id } + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeReceipts(initial: List<Comprobante> = emptyList()) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
        val deleted = mutableListOf<String>()
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
            deleted += id
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeContexts : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {
            state.value = state.value + context
        }
        override suspend fun update(context: Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class MemoryFiles : ComprobanteFileStore {
        private val files = mutableMapOf<String, ByteArray>()
        fun put(path: String, bytes: ByteArray) {
            files[path] = bytes.copyOf()
        }
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
            val path = "file://$id.$extension"
            files[path] = bytes.copyOf()
            return path
        }
        override suspend fun read(file: String): ByteArray? = files[file]?.copyOf()
        override suspend fun delete(file: String) {
            files.remove(file)
        }
    }

    private class FakeHistory : DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<DeletedOperationHistory>> = state
        override suspend fun save(history: DeletedOperationHistory) {
            state.value = state.value + history
        }
    }
}
