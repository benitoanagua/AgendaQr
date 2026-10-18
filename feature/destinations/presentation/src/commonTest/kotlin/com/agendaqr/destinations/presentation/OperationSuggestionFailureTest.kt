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
import com.agendaqr.destinations.domain.ReceiptAssociationSuggestion
import com.agendaqr.destinations.domain.ReceiptMatchKind
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regresión: la sugerencia de asociación no puede dejar el diálogo en
 * "Estamos analizando el comprobante." para siempre. En runtime (emulador) el
 * use case falló con NoClassDefFoundError por un desaline de
 * kotlinx-datetime y el usuario jamás pudo asociar manualmente; ahora un
 * fallo degrada a un NONE explícito y la bandeja sigue usable.
 */
class OperationSuggestionFailureTest {
    @Test
    fun suggestion_failure_degrades_to_no_match_instead_of_eternal_loading() {
        runBlocking {
            val receipts = FakeReceipts(
                listOf(Comprobante("r-1", "local/r-1.png", createdAt = 10, updatedAt = 10)),
                failGetFor = setOf("r-1"),
            )
            val vm = viewModel(FakeOperations(), receipts)
            waitUntil { vm.receiptSuggestion("r-1") != null }

            val suggestion = vm.receiptSuggestion("r-1")
            assertEquals(ReceiptMatchKind.NONE, suggestion?.kind)
            assertEquals(emptyList(), suggestion?.operationIds)
        }
    }

    private suspend fun waitUntil(condition: () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private fun viewModel(operations: FakeOperations, receipts: FakeReceipts): OperationsViewModel {
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

    private class FakeOperations : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
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

    private class FakeReceipts(
        initial: List<Comprobante>,
        private val failGetFor: Set<String> = emptySet(),
    ) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String): Comprobante? {
            if (id in failGetFor) throw IllegalStateException("simulated suggestion failure")
            return state.value.firstOrNull { it.id == id }
        }
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
