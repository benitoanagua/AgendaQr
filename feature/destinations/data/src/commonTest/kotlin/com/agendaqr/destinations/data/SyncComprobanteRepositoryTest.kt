package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncComprobanteRepositoryTest {

    @Test
    fun pullsRemoteReceiptAndMaterializesLocalFile() = runTest {
        val remote = FakeRemoteComprobanteRepository()
        val local = FakeComprobanteRepository()
        val files = FakeComprobanteFileStore()
        remote.items += RemoteComprobanteRecord(
            Comprobante("receipt", "remote.png", createdAt = 1, updatedAt = 1),
            "user/receipt.png",
        )
        val downloads = mutableListOf<String>()
        val repository = SyncComprobanteRepository(
            local = local,
            remote = remote,
            fileStore = files,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemoryQueueStore())),
            scope = backgroundScope,
            downloadRemoteBytes = { path ->
                downloads += path
                byteArrayOf(1, 2, 3)
            },
        )

        repository.syncFromRemote()

        val saved = repository.observe().first().single()
        assertEquals("receipt", saved.id)
        assertEquals("receipt.png", saved.file.substringAfterLast('/'))
        assertEquals(listOf("user/receipt.png"), downloads)
    }

    @Test
    fun does_not_download_remote_receipt_when_local_version_is_newer() = runTest {
        val remote = FakeRemoteComprobanteRepository()
        val local = FakeComprobanteRepository()
        val files = FakeComprobanteFileStore()
        local.save(Comprobante("receipt", "local/receipt.png", createdAt = 1, updatedAt = 5))
        remote.items += RemoteComprobanteRecord(
            Comprobante("receipt", "remote.png", createdAt = 1, updatedAt = 4),
            "user/receipt.png",
        )
        val downloads = mutableListOf<String>()
        val repository = SyncComprobanteRepository(
            local = local,
            remote = remote,
            fileStore = files,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemoryQueueStore())),
            scope = backgroundScope,
            downloadRemoteBytes = { path ->
                downloads += path
                byteArrayOf(9)
            },
        )

        repository.syncFromRemote()

        assertEquals(emptyList(), downloads)
        assertEquals("local/receipt.png", local.get("receipt")?.file)
        assertEquals(5, local.get("receipt")?.updatedAt)
    }

    @Test
    fun downloads_equal_timestamp_when_local_file_is_missing() = runTest {
        val remote = FakeRemoteComprobanteRepository()
        val local = FakeComprobanteRepository()
        val files = FakeComprobanteFileStore()
        remote.items += RemoteComprobanteRecord(
            Comprobante("receipt", "remote.png", createdAt = 1, updatedAt = 5),
            "user/receipt.png",
        )
        val downloads = mutableListOf<String>()
        val repository = SyncComprobanteRepository(
            local = local,
            remote = remote,
            fileStore = files,
            enqueuer = SyncMutationEnqueuer(LocalSyncQueue(MemoryQueueStore())),
            scope = backgroundScope,
            downloadRemoteBytes = { path ->
                downloads += path
                byteArrayOf(4, 5)
            },
        )
        local.save(Comprobante("receipt", "", createdAt = 1, updatedAt = 5))

        repository.syncFromRemote()

        assertEquals(listOf("user/receipt.png"), downloads)
        assertTrue(local.get("receipt")?.file?.isNotBlank() == true)
    }
}

private class MemoryQueueStore : SyncQueueStore {
    private var items = emptyList<PendingSyncMutation>()
    override fun read(): List<PendingSyncMutation> = items
    override fun write(items: List<PendingSyncMutation>) { this.items = items }
}

private class FakeRemoteComprobanteRepository : RemoteComprobanteRepository {
    val items = mutableListOf<RemoteComprobanteRecord>()
    override suspend fun observe() = items.toList()
    override suspend fun save(comprobante: Comprobante, bytes: ByteArray) {}
    override suspend fun update(comprobante: Comprobante) {}
    override suspend fun delete(record: RemoteComprobanteRecord) {}
}

private class FakeComprobanteRepository : ComprobanteRepository {
    private val state = MutableStateFlow<List<Comprobante>>(emptyList())
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

private class FakeComprobanteFileStore : ComprobanteFileStore {
    private val files = mutableMapOf<String, ByteArray>()
    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        val path = "local/" + id + "." + extension
        files[path] = bytes.copyOf()
        return path
    }
    override suspend fun read(file: String): ByteArray? = files[file]
    override suspend fun delete(file: String) { files.remove(file) }
}
