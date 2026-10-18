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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Regresión del flujo de edición desde el detalle de operación (RF-15):
 * el botón "Editar" cablea `OperationAction.Edit`, que debe enrutar a
 * `OperationRoute.Edit` conservando la operación seleccionada, y Back debe
 * volver al detalle que la originó (contrato: "Back vuelve al estado
 * anterior").
 */
class OperationEditActionTest {
    @Test
    fun edit_action_routes_to_editor_and_back_returns_to_detail() {
        runBlocking {
            val operations = FakeOperations(
                listOf(
                    Operation("op-1", OperationType.PAGO, occurredAt = 100, createdAt = 100, amount = "50"),
                ),
            )
            val vm = viewModel(operations)
            waitUntil { vm.state.value.operations.any { it.id == "op-1" } }

            vm.onAction(OperationAction.Open("op-1"))
            waitUntil { vm.state.value.route is OperationRoute.Detail }
            assertEquals("op-1", (vm.state.value.route as OperationRoute.Detail).id)

            vm.onAction(OperationAction.Edit("op-1"))
            assertTrue(vm.state.value.route is OperationRoute.Edit)
            assertEquals("op-1", (vm.state.value.route as OperationRoute.Edit).id)
            assertEquals("op-1", vm.selectedOperation()?.id)

            vm.onAction(OperationAction.Back)
            waitUntil { vm.state.value.route is OperationRoute.Detail }
            assertEquals("op-1", (vm.state.value.route as OperationRoute.Detail).id)
            assertNotNull(vm.selectedOperation())
        }
    }

    private suspend fun waitUntil(condition: () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private fun viewModel(operations: FakeOperations): OperationsViewModel {
        val receipts = FakeReceipts()
        val files = MemoryFiles()
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
    ) : OperationRepository {
        private val state = MutableStateFlow(initial)
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

    private class FakeReceipts(initial: List<Comprobante> = emptyList()) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
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

    private class MemoryFiles : ComprobanteFileStore {
        private val files = mutableMapOf<String, ByteArray>()
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
