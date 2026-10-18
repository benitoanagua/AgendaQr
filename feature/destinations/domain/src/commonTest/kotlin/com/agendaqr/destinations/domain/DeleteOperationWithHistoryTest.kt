package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Adversarial coverage for the documented deletion rule:
 * deleting an operation deletes its associated receipts and files while the
 * minimal history survives; receipts of other operations and unassociated
 * receipts must remain untouched.
 */
class DeleteOperationWithHistoryTest {

    @Test
    fun deleting_operation_removes_its_receipts_and_files_and_keeps_minimal_history() = runTest {
        val operations = FakeOperationRepository(
            listOf(
                operation("op-1", OperationType.PAGO, occurredAt = 100, amount = "850", personOrEntity = "Colegio"),
                operation("op-2", OperationType.COBRO, occurredAt = 200, amount = "50", personOrEntity = "Juan"),
            )
        )
        val receipts = FakeComprobanteRepository(
            listOf(
                Comprobante("r-1", "file://r-1.png", createdAt = 1, updatedAt = 1, operationId = "op-1"),
                Comprobante("r-2", "file://r-2.png", createdAt = 2, updatedAt = 2, operationId = "op-1"),
                Comprobante("r-other", "file://r-other.png", createdAt = 3, updatedAt = 3, operationId = "op-2"),
                Comprobante("r-loose", "file://r-loose.png", createdAt = 4, updatedAt = 4),
            )
        )
        val files = MemoryFileStore()
        listOf("file://r-1.png", "file://r-2.png", "file://r-other.png", "file://r-loose.png").forEach { path ->
            files.put(path, byteArrayOf(1))
        }
        val history = MemoryHistoryRepository()
        val historyObserver = history.observe()

        val deleted = DeleteOperationWithHistoryUseCase(operations, receipts, files, history)("op-1")

        assertEquals(DeletedOperationHistory(date = 100, type = OperationType.PAGO, amount = "850", personOrEntity = "Colegio"), deleted)
        assertEquals(listOf(deleted), historyObserver.first())

        assertNull(operations.get("op-1"))
        assertNull(receipts.get("r-1"))
        assertNull(receipts.get("r-2"))
        assertNull(files.read("file://r-1.png"))
        assertNull(files.read("file://r-2.png"))

        // Only this operation's receipts are removed; everything else survives.
        assertNotNull(operations.get("op-2"))
        assertNotNull(receipts.get("r-other"))
        assertNotNull(files.read("file://r-other.png"))
        assertNotNull(receipts.get("r-loose"))
        assertNotNull(files.read("file://r-loose.png"))
    }

    @Test
    fun deleting_missing_operation_returns_null_without_side_effects() = runTest {
        val operations = FakeOperationRepository(emptyList())
        val receipts = FakeComprobanteRepository(
            listOf(Comprobante("r-1", "file://r-1.png", createdAt = 1, updatedAt = 1))
        )
        val files = MemoryFileStore()
        files.put("file://r-1.png", byteArrayOf(1))
        val history = MemoryHistoryRepository()

        val deleted = DeleteOperationWithHistoryUseCase(operations, receipts, files, history)("missing")

        assertNull(deleted)
        assertEquals(0, history.observe().first().size)
        assertNotNull(receipts.get("r-1"))
        assertNotNull(files.read("file://r-1.png"))
    }

    private fun operation(
        id: String,
        type: OperationType,
        occurredAt: Long,
        amount: String?,
        personOrEntity: String?,
    ) = Operation(
        id = id,
        type = type,
        occurredAt = occurredAt,
        createdAt = occurredAt,
        updatedAt = occurredAt,
        amount = amount,
        personOrEntity = personOrEntity,
    )

    private class FakeOperationRepository(initial: List<Operation>) : OperationRepository {
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

    private class FakeComprobanteRepository(initial: List<Comprobante>) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value.filterNot { it.id == comprobante.id } + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class MemoryFileStore : ComprobanteFileStore {
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

    private class MemoryHistoryRepository : DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<DeletedOperationHistory>> = state
        override suspend fun save(history: DeletedOperationHistory) {
            state.value = state.value + history
        }
    }
}
