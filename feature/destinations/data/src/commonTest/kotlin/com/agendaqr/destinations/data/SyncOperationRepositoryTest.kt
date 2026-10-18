package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncOperationRepositoryTest {

    @Test
    fun pullsRemoteRecordsIntoLocalState() = runTest {
        val remote = FakeRemoteOperationRepository()
        remote.items += operation("remote", 20)
        val repository = SyncOperationRepository(
            FakeOperationRepository(),
            remote,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemorySyncQueueStore())),
        )

        repository.syncFromRemote()

        assertEquals("remote", repository.observe().first().single().id)
    }

    @Test
    fun localMutationSurvivesRemoteFailure() = runTest {
        val repository = SyncOperationRepository(
            FakeOperationRepository(),
            FakeRemoteOperationRepository(failWrites = true),
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemorySyncQueueStore())),
        )

        repository.save(operation("local", 10))

        assertTrue(repository.observe().first().any { it.id == "local" })
    }

    private fun operation(id: String, timestamp: Long) =
        Operation(
            id = id,
            type = OperationType.PAGO,
            occurredAt = timestamp,
            createdAt = timestamp,
        )
}

private class FakeRemoteOperationRepository(
    private val failWrites: Boolean = false,
) : RemoteOperationRepository {
    val items = mutableListOf<Operation>()

    override suspend fun observe(): List<Operation> = items.toList()
    override suspend fun save(operation: Operation) {
        if (failWrites) error("remote unavailable")
        items.removeAll { it.id == operation.id }
        items += operation
    }
    override suspend fun update(operation: Operation) = save(operation)
    override suspend fun delete(id: String) {
        if (failWrites) error("remote unavailable")
        items.removeAll { it.id == id }
    }
}

private class FakeOperationRepository : OperationRepository {
    private val state = MutableStateFlow<List<Operation>>(emptyList())

    override fun observe(): Flow<List<Operation>> = state
    override suspend fun get(id: String): Operation? = state.value.firstOrNull { it.id == id }
    override suspend fun save(operation: Operation) { state.value = state.value + operation }
    override suspend fun update(operation: Operation) { state.value = state.value.map { if (it.id == operation.id) operation else it } }
    override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
}
