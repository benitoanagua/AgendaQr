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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Valida el flujo completo offline → reconexión:
 * desconexión prolongada, resolución LWW en ambas direcciones,
 * duelo borrado-vs-update, backoff y cuarentena de errores permanentes.
 */
class SyncResilienceTest {

    @Test
    fun prolonged_offline_then_reconnect_drains_everything() = runTest {
        val fixture = Fixture()
        fixture.remoteOperations.failWrites = true

        // Tres escrituras offline mientras no hay red.
        repeat(3) { i ->
            val op = Operation("op-$i", OperationType.PAGO, occurredAt = 100L + i, createdAt = 100L + i)
            fixture.operations.save(op)
            fixture.queue.enqueue(
                PendingSyncMutation(
                    id = "m-$i",
                    resource = SyncResource.OPERATION,
                    mutation = SyncMutationType.UPSERT,
                    entityId = "op-$i",
                    enqueuedAt = 100,
                    nextAttemptAt = 100,
                    localUpdatedAt = 100L + i,
                )
            )
        }

        // Desconexión prolongada: varios drains fallan sin perder nada.
        assertEquals(0, fixture.processor.drain(now = 100))
        assertEquals(0, fixture.processor.drain(now = 150))
        assertEquals(3, fixture.queue.all().size)
        assertTrue(fixture.queue.all().all { it.state == SyncMutationState.FAILED })

        // Aún sin esperar el backoff, nada se reclama.
        assertEquals(0, fixture.processor.drain(now = 101))

        // Vuelve la red: tras el backoff todo converge en orden.
        fixture.remoteOperations.failWrites = false
        val result = fixture.processor.drainWithReport(now = 100_000)
        assertEquals(3, result.processed)
        assertEquals(3, result.pushed)
        assertEquals(0, result.failed)
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("op-0", "op-1", "op-2"), fixture.remoteOperations.saved.map { it.id })
    }

    @Test
    fun concurrent_edit_remote_newer_pulls_and_keeps_server_value() = runTest {
        val fixture = Fixture()
        val conflicts = mutableListOf<SyncConflict>()
        val processor = fixture.processorWithConflicts(conflicts)

        // Base conocida v1; el cliente edita offline (v2) y el servidor avanza a v3.
        fixture.operations.save(Operation("op-1", OperationType.PAGO, 100, 100, updatedAt = 200))
        fixture.remoteOperations.store("op-1", Operation("op-1", OperationType.COBRO, 100, 100, updatedAt = 300))
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.OPERATION,
                mutation = SyncMutationType.UPSERT,
                entityId = "op-1",
                enqueuedAt = 150,
                nextAttemptAt = 150,
                baseUpdatedAt = 100,
                localUpdatedAt = 200,
            )
        )

        val result = processor.drainWithReport(now = 400)

        assertEquals(1, result.processed)
        assertEquals(1, result.pulled)
        assertEquals(0, result.pushed)
        // Gana el servidor: el local se sobrescribe y NO se sube nada.
        assertEquals(OperationType.COBRO, fixture.operations.get("op-1")?.type)
        assertTrue(fixture.remoteOperations.saved.isEmpty())
        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(1, conflicts.size)
        assertTrue(conflicts.single().diverged)
    }

    @Test
    fun concurrent_edit_local_newer_pushes() = runTest {
        val fixture = Fixture()
        val processor = fixture.processorWithConflicts(mutableListOf())

        fixture.operations.save(Operation("op-1", OperationType.COBRO, 100, 100, updatedAt = 5_000))
        fixture.remoteOperations.store("op-1", Operation("op-1", OperationType.PAGO, 100, 100, updatedAt = 3_000))
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.OPERATION,
                mutation = SyncMutationType.UPSERT,
                entityId = "op-1",
                enqueuedAt = 4_000,
                nextAttemptAt = 4_000,
                baseUpdatedAt = 100,
                localUpdatedAt = 5_000,
            )
        )

        val result = processor.drainWithReport(now = 6_000)

        assertEquals(1, result.pushed)
        assertEquals(OperationType.COBRO, fixture.remoteOperations.saved.single().type)
        assertTrue(fixture.queue.all().isEmpty())
    }

    @Test
    fun delete_loses_against_newer_remote_and_resurrects_locally() = runTest {
        val fixture = Fixture()
        val processor = fixture.processorWithConflicts(mutableListOf())

        // El usuario borró offline en t=200, pero el servidor recibió una
        // edición en t=500: el borrado pierde y el registro resucita.
        fixture.remoteDestinations.store(
            "d-1",
            Destination("d-1", "Server", QrAsset("x"), createdAt = 50, updatedAt = 500),
        )
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.DESTINATION,
                mutation = SyncMutationType.DELETE,
                entityId = "d-1",
                enqueuedAt = 200,
                nextAttemptAt = 200,
                baseUpdatedAt = 100,
                localUpdatedAt = 200,
            )
        )

        val result = processor.drainWithReport(now = 600)

        assertEquals(1, result.pulled)
        assertTrue(fixture.remoteDestinations.deleted.isEmpty())
        assertEquals("Server", fixture.destinations.get("d-1")?.name)
        assertTrue(fixture.queue.all().isEmpty())
    }

    @Test
    fun delete_wins_against_older_remote() = runTest {
        val fixture = Fixture()
        val processor = fixture.processorWithConflicts(mutableListOf())

        fixture.remoteDestinations.store(
            "d-1",
            Destination("d-1", "Old", QrAsset("x"), createdAt = 50, updatedAt = 100),
        )
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.DESTINATION,
                mutation = SyncMutationType.DELETE,
                entityId = "d-1",
                enqueuedAt = 5_000,
                nextAttemptAt = 5_000,
                baseUpdatedAt = 100,
                localUpdatedAt = 5_000,
            )
        )

        val result = processor.drainWithReport(now = 6_000)

        assertEquals(1, result.pushed)
        assertEquals(listOf("d-1"), fixture.remoteDestinations.deleted)
        assertTrue(fixture.queue.all().isEmpty())
    }

    @Test
    fun permanent_error_goes_to_dead_letter_and_stops_blocking() = runTest {
        val fixture = Fixture()
        fixture.remoteOperations.failWrites = true
        fixture.remoteOperations.failureMessage = "permission denied by RLS policy"
        val processor = fixture.processorWithConflicts(mutableListOf())

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

        val result = processor.drainWithReport(now = 200)

        assertEquals(0, result.processed)
        assertEquals(1, result.deadLettered)
        val quarantined = fixture.queue.all().single()
        assertEquals(SyncMutationState.DEAD_LETTER, quarantined.state)
        // La DLQ nunca se reclama, aunque pase el tiempo.
        assertTrue(fixture.queue.claim(now = Long.MAX_VALUE / 2).isEmpty())
    }

    @Test
    fun transient_failure_reports_next_retry() = runTest {
        val fixture = Fixture()
        fixture.remoteOperations.failWrites = true
        val processor = fixture.processorWithConflicts(mutableListOf())

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

        val result = processor.drainWithReport(now = 100)

        assertEquals(1, result.failed)
        assertEquals(100 + 2_000L, result.nextRetryAt)
    }

    @Test
    fun syncFromRemote_skips_pending_entities_until_drain_decides() = runTest {
        val store = MemorySyncQueueStore()
        val queue = LocalSyncQueue(store)
        val enqueuer = SyncMutationEnqueuer(queue)
        val local = FakeDestinationRepository()
        val remote = FakeRemoteDestinations()
        // Sin scope: sin pull automático en init.
        val repo = SyncDestinationRepository(
            local = local,
            remote = remote,
            enqueuer = enqueuer,
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined),
        )

        // Escritura local offline (el remoto falla → se encola con versión).
        remote.fail = true
        val mine = Destination("d-1", "Mine", QrAsset("x"), createdAt = 50, updatedAt = 200)
        repo.save(mine)

        // El servidor trae una versión claramente más nueva mientras seguimos offline.
        remote.fail = false
        remote.store("d-1", Destination("d-1", "Server", QrAsset("y"), createdAt = 50, updatedAt = 5_000))

        // El pull NO debe pisar mi edición pendiente.
        repo.syncFromRemote()
        assertEquals("Mine", local.get("d-1")?.name)

        // Al drenar, LWW decide: gana el servidor y baja.
        // (now real: el encolador usa el reloj del sistema para nextAttemptAt.)
        val processor = SyncMutationProcessor(
            queue = queue,
            contexts = FakeContextRepository(),
            destinations = local,
            operations = FakeOperationRepository(),
            comprobantes = FakeComprobanteRepository(),
            remoteContexts = FakeRemoteContexts(),
            remoteDestinations = remote,
            remoteOperations = FakeRemoteOperations(),
            remoteComprobantes = FakeRemoteComprobantes(),
            fileStore = MemoryFileStore(),
        )
        val result = processor.drainWithReport()
        assertEquals(1, result.pulled)
        assertEquals("Server", local.get("d-1")?.name)
        assertTrue(queue.all().isEmpty())
    }

    @Test
    fun pull_through_raw_local_terminates_and_second_drain_is_noop() = runTest {
        // Wiring de producción: el wrapper Sync escribe (encola) y el procesador
        // opera sobre el MISMO local crudo. Tras un pull, la cola debe quedar
        // vacía y el siguiente drain no debe generar trabajo nuevo.
        val store = MemorySyncQueueStore()
        val queue = LocalSyncQueue(store)
        val enqueuer = SyncMutationEnqueuer(queue)
        val rawLocal = FakeContextRepository()
        val remote = FakeRemoteContexts()
        val wrapper = SyncContextRepository(
            local = rawLocal,
            remote = remote,
            enqueuer = enqueuer,
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined),
        )
        val processor = Fixture().processorOn(
            queue = queue,
            contexts = rawLocal,
            remoteContexts = remote,
        )

        // Escritura de UI offline a través del wrapper (encola con versión).
        remote.fail = true
        wrapper.save(Context("c-1", "Mine", createdAt = 50, updatedAt = 200))

        // El servidor avanzó de forma claramente más nueva.
        remote.fail = false
        remote.stored["c-1"] = Context("c-1", "Server", createdAt = 50, updatedAt = 5_000)

        val first = processor.drainWithReport()
        assertEquals(1, first.pulled)
        assertEquals("Server", rawLocal.get("c-1")?.name)
        assertTrue(queue.all().isEmpty())

        val second = processor.drainWithReport()
        assertEquals(0, second.processed)
        assertTrue(queue.all().isEmpty())
    }

    @Test
    fun comprobante_delete_removes_remote_record_by_id_and_does_not_resurrect() = runTest {
        val fixture = Fixture()
        val processor = fixture.processorWithConflicts(mutableListOf())

        // El comprobante se borró localmente en t=5000; en el servidor sigue
        // la versión vieja (t=100): el borrado gana y no hay resurrección.
        fixture.remoteComprobantes.records["r-1"] = RemoteComprobanteRecord(
            Comprobante("r-1", "remote/r-1.png", createdAt = 50, updatedAt = 100),
            "user/r-1.png",
        )
        fixture.queue.enqueue(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.COMPROBANTE,
                mutation = SyncMutationType.DELETE,
                entityId = "r-1",
                enqueuedAt = 5_000,
                nextAttemptAt = 5_000,
                baseUpdatedAt = 100,
                localUpdatedAt = 5_000,
            )
        )

        val result = processor.drainWithReport(now = 6_000)

        assertEquals(1, result.pushed)
        assertEquals(listOf("r-1"), fixture.remoteComprobantes.deleted)
        assertNull(fixture.comprobantes.get("r-1"))
        assertTrue(fixture.queue.all().isEmpty())
    }

    private fun Fixture.processorOn(
        queue: LocalSyncQueue,
        contexts: ContextRepository,
        remoteContexts: RemoteContextRepository,
    ) = SyncMutationProcessor(
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

        fun processorWithConflicts(conflicts: MutableList<SyncConflict>) = SyncMutationProcessor(
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
            onConflict = { conflicts += it },
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
        private val state = MutableStateFlow(emptyList<Operation>())
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
        private val state = MutableStateFlow(emptyList<Destination>())
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
        private val state = MutableStateFlow(emptyList<Context>())
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
        private val state = MutableStateFlow(emptyList<Comprobante>())
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
        val stored = mutableMapOf<String, Context>()
        var fail = false
        override suspend fun observe(): List<Context> {
            if (fail) error("remote unavailable")
            return stored.values.toList()
        }
        override suspend fun save(context: Context) {
            if (fail) error("remote unavailable")
            stored[context.id] = context
        }
        override suspend fun update(context: Context) {
            if (fail) error("remote unavailable")
            stored[context.id] = context
        }
        override suspend fun delete(id: String) {
            if (fail) error("remote unavailable")
            stored.remove(id)
        }
    }

    private class FakeRemoteDestinations : RemoteDestinationRepository {
        val stored = mutableMapOf<String, Destination>()
        val deleted = mutableListOf<String>()
        var fail = false
        fun store(id: String, value: Destination) { stored[id] = value }
        override suspend fun observe(): List<Destination> {
            if (fail) error("remote unavailable")
            return stored.values.toList()
        }
        override suspend fun save(destination: Destination) {
            if (fail) error("remote unavailable")
            stored[destination.id] = destination
        }
        override suspend fun update(destination: Destination) {
            if (fail) error("remote unavailable")
            stored[destination.id] = destination
        }
        override suspend fun delete(id: String) {
            if (fail) error("remote unavailable")
            stored.remove(id)
            deleted += id
        }
    }

    private class FakeRemoteOperations : RemoteOperationRepository {
        var failWrites = false
        var failureMessage = "remote unavailable"
        val saved = mutableListOf<Operation>()
        private val store = mutableMapOf<String, Operation>()
        fun store(id: String, value: Operation) { store[id] = value }
        override suspend fun observe(): List<Operation> = store.values.toList() + saved
        override suspend fun save(operation: Operation) {
            if (failWrites) error(failureMessage)
            saved.removeAll { it.id == operation.id }
            saved += operation
        }
        override suspend fun update(operation: Operation) {
            if (failWrites) error(failureMessage)
            saved.removeAll { it.id == operation.id }
            saved += operation
        }
        override suspend fun delete(id: String) {
            if (failWrites) error(failureMessage)
            store.remove(id)
        }
    }

    private class FakeRemoteComprobantes : RemoteComprobanteRepository {
        val saved = mutableListOf<Pair<Comprobante, ByteArray>>()
        val records = mutableMapOf<String, RemoteComprobanteRecord>()
        val deleted = mutableListOf<String>()
        override suspend fun observe(): List<RemoteComprobanteRecord> = records.values.toList()
        override suspend fun save(comprobante: Comprobante, bytes: ByteArray) {
            saved.removeAll { it.first.id == comprobante.id }
            saved += comprobante to bytes.copyOf()
        }
        override suspend fun update(comprobante: Comprobante) = Unit
        override suspend fun delete(record: RemoteComprobanteRecord) {
            records.remove(record.comprobante.id)
            deleted += record.comprobante.id
        }
    }
}
