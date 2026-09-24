package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.ReceiptProvenance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Adversarial coverage for the offline -> online recovery flow: mutations
 * enqueued while the remote was unavailable are drained once connectivity
 * returns, per resource, and failures stay queued with backoff.
 */
class SyncMutationProcessorTest {

    @Test
    fun offline_mutations_are_processed_when_back_online() = runTest {
        val fixture = Fixture()
        val operation = Operation("op-1", OperationType.PAGO, occurredAt = 100, createdAt = 100)
        fixture.operations.save(operation)
        // Same enqueue the queued repository performs when the remote write fails.
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.OPERATION,
                mutation = SyncMutationType.UPSERT,
                entityId = "op-1",
                enqueuedAt = 100,
                nextAttemptAt = 100,
            )
        )

        val processed = fixture.processor.drain(now = 200)

        assertEquals(1, processed)
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("op-1"), fixture.remoteOperations.saved.map { it.id })
    }

    @Test
    fun failed_remote_write_remains_queued_and_is_retried_after_backoff() = runTest {
        val fixture = Fixture()
        fixture.remoteOperations.failWrites = true
        fixture.operations.save(Operation("op-1", OperationType.PAGO, 100, 100))
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.OPERATION,
                mutation = SyncMutationType.UPSERT,
                entityId = "op-1",
                enqueuedAt = 100,
                nextAttemptAt = 100,
            )
        )

        // Still offline: the drain fails and the mutation survives with backoff.
        assertEquals(0, fixture.processor.drain(now = 100))
        val failed = fixture.queue.all().single()
        assertEquals(SyncMutationState.FAILED, failed.state)
        assertEquals(1, failed.attempts)
        assertEquals(2100, failed.nextAttemptAt)

        // Back online: the retry after nextAttemptAt completes the mutation.
        fixture.remoteOperations.failWrites = false
        assertEquals(0, fixture.processor.drain(now = 2099))
        assertEquals(1, fixture.processor.drain(now = 2100))
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("op-1"), fixture.remoteOperations.saved.map { it.id })
    }

    @Test
    fun offline_context_upsert_is_forwarded_to_the_remote() = runTest {
        val fixture = Fixture()
        val context = Context("ctx-1", "Colegio", createdAt = 50, updatedAt = 50)
        fixture.contexts.save(context)
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.CONTEXT,
                mutation = SyncMutationType.UPSERT,
                entityId = "ctx-1",
                enqueuedAt = 100,
                nextAttemptAt = 100,
            )
        )

        val processed = fixture.processor.drain(now = 200)

        assertEquals(1, processed)
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("ctx-1"), fixture.remoteContexts.saved.map { it.id })
    }

    @Test
    fun offline_delete_removes_the_remote_record() = runTest {
        val fixture = Fixture()
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.DESTINATION,
                mutation = SyncMutationType.DELETE,
                entityId = "d-1",
                enqueuedAt = 100,
                nextAttemptAt = 100,
            )
        )

        val processed = fixture.processor.drain(now = 200)

        assertEquals(1, processed)
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("d-1"), fixture.remoteDestinations.deleted)
    }

    @Test
    fun offline_comprobante_upsert_forwards_the_local_file_bytes() = runTest {
        val fixture = Fixture()
        val path = fixture.fileStore.save("r-1", byteArrayOf(7, 8, 9), "png")
        fixture.comprobantes.save(
            Comprobante(
                id = "r-1",
                file = path,
                createdAt = 100,
                updatedAt = 100,
                provenance = ReceiptProvenance.RECIBIDO,
            )
        )
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.COMPROBANTE,
                mutation = SyncMutationType.UPSERT,
                entityId = "r-1",
                enqueuedAt = 100,
                nextAttemptAt = 100,
            )
        )

        val processed = fixture.processor.drain(now = 200)

        assertEquals(1, processed)
        assertTrue(fixture.queue.all().isEmpty())
        val saved = fixture.remoteComprobantes.saved.single()
        assertEquals("r-1", saved.first.id)
        assertTrue(saved.second.contentEquals(byteArrayOf(7, 8, 9)))
    }

    private class Fixture {
        val queue = LocalSyncQueue(MemorySyncQueueStore())
        val operations = FakeOperationRepository()
        val destinations = FakeDestinationRepository()
        val contexts = FakeContextRepository()
        val comprobantes = FakeComprobanteRepository()
        val fileStore = MemoryFileStore()
        val remoteOperations = FakeRemoteOperations()
        val remoteDestinations = FakeRemoteDestinations()
        val remoteComprobantes = FakeRemoteComprobantes()
        val remoteContexts = FakeRemoteContexts()
        val processor = SyncMutationProcessor(
            queue = queue,
            contexts = contexts,
            destinations = destinations,
            operations = operations,
            comprobantes = comprobantes,
            remoteContexts = remoteContexts,
            remoteDestinations = remoteDestinations,
            remoteOperations = remoteOperations,
            remoteComprobantes = remoteComprobantes,
            fileStore = fileStore,
        )
    }

    private class MemorySyncQueueStore : SyncQueueStore {
        private var items = emptyList<PendingSyncMutation>()
        override fun read(): List<PendingSyncMutation> = items
        override fun write(items: List<PendingSyncMutation>) {
            this.items = items
        }
    }

    private class FakeOperationRepository : OperationRepository {
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

    private class FakeDestinationRepository : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) {
            state.value = state.value.map { if (it.id == destination.id) destination else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeContextRepository : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {
            state.value = state.value.filterNot { it.id == context.id } + context
        }
        override suspend fun update(context: Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
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

    private class MemoryFileStore : ComprobanteFileStore {
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

    private class FakeRemoteContexts : RemoteContextRepository {
        val saved = mutableListOf<Context>()
        val deleted = mutableListOf<String>()
        override suspend fun observe(): List<Context> = emptyList()
        override suspend fun save(context: Context) { saved += context }
        override suspend fun update(context: Context) { saved.removeAll { it.id == context.id }; saved += context }
        override suspend fun delete(id: String) { deleted += id }
    }

    private class FakeRemoteDestinations : RemoteDestinationRepository {
        val saved = mutableListOf<Destination>()
        val deleted = mutableListOf<String>()
        override suspend fun observe(): List<Destination> = emptyList()
        override suspend fun save(destination: Destination) { saved += destination }
        override suspend fun update(destination: Destination) {
            saved.removeAll { it.id == destination.id }
            saved += destination
        }
        override suspend fun delete(id: String) { deleted += id }
    }

    private class FakeRemoteOperations : RemoteOperationRepository {
        var failWrites = false
        val saved = mutableListOf<Operation>()
        val deleted = mutableListOf<String>()
        override suspend fun observe(): List<Operation> = emptyList()
        override suspend fun save(operation: Operation) {
            if (failWrites) error("remote unavailable")
            saved.removeAll { it.id == operation.id }
            saved += operation
        }
        override suspend fun update(operation: Operation) {
            if (failWrites) error("remote unavailable")
            saved.removeAll { it.id == operation.id }
            saved += operation
        }
        override suspend fun delete(id: String) {
            if (failWrites) error("remote unavailable")
            deleted += id
        }
    }

    private class FakeRemoteComprobantes : RemoteComprobanteRepository {
        val saved = mutableListOf<Pair<Comprobante, ByteArray>>()
        override suspend fun observe(): List<RemoteComprobanteRecord> = emptyList()
        override suspend fun save(comprobante: Comprobante, bytes: ByteArray) {
            saved.removeAll { it.first.id == comprobante.id }
            saved += comprobante to bytes.copyOf()
        }
        override suspend fun update(comprobante: Comprobante) = Unit
        override suspend fun delete(record: RemoteComprobanteRecord) = Unit
    }
}
