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

class SyncComprobanteRepositoryTest {

    @Test
    fun pullsRemoteReceiptAndMaterializesLocalFile() = runTest {
        val remote = FakeRemoteComprobanteRepository()
        val local = FakeComprobanteRepository()
        val files = FakeComprobanteFileStore()
        remote.items += RemoteComprobanteRecord(
            Comprobante("receipt", "remote.png", 1),
            "user/receipt.png",
        )
        files.remoteBytes["user/receipt.png"] = byteArrayOf(1, 2, 3)

        val repository = TestSyncComprobanteRepository(local, remote, files)
        repository.syncFromRemote()

        val saved = repository.observe().first().single()
        assertEquals("receipt", saved.id)
        assertEquals("receipt.png", saved.file.substringAfterLast('/'))
    }
}

private class TestSyncComprobanteRepository(
    private val local: ComprobanteRepository,
    private val remote: FakeRemoteComprobanteRepository,
    private val files: FakeComprobanteFileStore,
) {
    fun observe() = local.observe()
    suspend fun syncFromRemote() {
        remote.items.forEach { record ->
            val bytes = files.remoteBytes.getValue(record.remoteFilePath)
            val localFile = files.save(record.comprobante.id, bytes, "png")
            local.save(record.comprobante.copy(file = localFile))
        }
    }
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
    override suspend fun save(comprobante: Comprobante) { state.value = state.value + comprobante }
    override suspend fun update(comprobante: Comprobante) { state.value = state.value.map { if (it.id == comprobante.id) comprobante else it } }
    override suspend fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
}

private class FakeComprobanteFileStore : ComprobanteFileStore {
    val remoteBytes = mutableMapOf<String, ByteArray>()
    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        val path = "local/" + id + "." + extension
        remoteBytes[path] = bytes
        return path
    }
    override suspend fun read(file: String) = remoteBytes[file]
    override suspend fun delete(file: String) { remoteBytes.remove(file) }
}
