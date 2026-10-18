package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncDestinationRepositoryTest {

    @Test
    fun pullsRemoteRecordsIntoLocalState() = runTest {
        val remote = FakeRemoteDestinationRepository()
        remote.items += destination("remote", updatedAt = 20)
        val repository = SyncDestinationRepository(
            local = FakeDestinationRepository(),
            remote = remote,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemorySyncQueueStore())),
        )

        repository.syncFromRemote()

        assertEquals("remote", repository.observe().first().single().id)
    }

    @Test
    fun localMutationIsKeptWhenRemoteFails() = runTest {
        val remote = FakeRemoteDestinationRepository(failWrites = true)
        val repository = SyncDestinationRepository(
            local = FakeDestinationRepository(),
            remote = remote,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemorySyncQueueStore())),
        )

        repository.save(destination("local", updatedAt = 10))

        assertTrue(repository.observe().first().any { it.id == "local" })
    }

    @Test
    fun newerRemoteRecordWinsDuringPull() = runTest {
        val local = FakeDestinationRepository()
        local.save(destination("same", updatedAt = 10))
        val remote = FakeRemoteDestinationRepository()
        remote.items += destination("same", updatedAt = 20)

        val repository = SyncDestinationRepository(
            local,
            remote,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemorySyncQueueStore())),
        )
        repository.syncFromRemote()

        assertEquals(20, repository.get("same")?.updatedAt)
    }

    private fun destination(id: String, updatedAt: Long) =
        Destination(id, id, QrAsset("qr"), createdAt = 1, updatedAt = updatedAt)
}

private class FakeRemoteDestinationRepository(
    private val failWrites: Boolean = false,
) : RemoteDestinationRepository {
    val items = mutableListOf<Destination>()

    override suspend fun observe(): List<Destination> = items.toList()
    override suspend fun save(destination: Destination) {
        if (failWrites) error("remote unavailable")
        items.removeAll { it.id == destination.id }
        items += destination
    }
    override suspend fun update(destination: Destination) = save(destination)
    override suspend fun delete(id: String) {
        if (failWrites) error("remote unavailable")
        items.removeAll { it.id == id }
    }
}

private class FakeDestinationRepository : DestinationRepository {
    private val state = MutableStateFlow<List<Destination>>(emptyList())

    override fun observe(): Flow<List<Destination>> = state
    override suspend fun get(id: String): Destination? = state.value.firstOrNull { it.id == id }
    override suspend fun save(destination: Destination) {
        state.value = state.value + destination
    }
    override suspend fun update(destination: Destination) {
        state.value = state.value.map { if (it.id == destination.id) destination else it }
    }
    override suspend fun delete(id: String) {
        state.value = state.value.filterNot { it.id == id }
    }
}
