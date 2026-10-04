package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
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
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OperationsSavingStateTest {
    @Test
    fun saveNew_success_resets_saving_flag_and_opens_detail() {
        runBlocking {
        val operations = FakeOperations()
        val vm = viewModel(operations)
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
        repeat(50) {
            if (!vm.state.value.isSavingOperation) return@repeat
            delay(20)
        }
        assertFalse(vm.state.value.isSavingOperation)
        assertTrue(vm.state.value.route is OperationRoute.Detail)
        assertNotNull(operations.saved)
        assertEquals(OperationType.PAGO, operations.saved!!.type)
        }
    }

    @Test
    fun saveNew_failure_resets_saving_flag_and_reports_error() {
        runBlocking {
        val operations = FakeOperations(failOnSave = true)
        val vm = viewModel(operations)
        vm.onAction(
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
        repeat(50) {
            if (!vm.state.value.isSavingOperation) return@repeat
            delay(20)
        }
        assertFalse(vm.state.value.isSavingOperation)
        assertNotNull(vm.state.value.error)
        }
    }

    private fun viewModel(operations: FakeOperations): OperationsViewModel {
        val receipts = FakeReceipts()
        val contexts = FakeContexts()
        val files = NoopFiles()
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

    private class FakeOperations(val failOnSave: Boolean = false) : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
        var saved: Operation? = null
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            if (failOnSave) throw IllegalStateException("storage unavailable")
            saved = operation
            state.value = state.value + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
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

    private class NoopFiles : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String) = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) = Unit
    }

    private class FakeHistory : DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<DeletedOperationHistory>> = state
        override suspend fun save(history: DeletedOperationHistory) {
            state.value = state.value + history
        }
    }
}
