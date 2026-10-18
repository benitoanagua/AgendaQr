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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Ciclo de vida del coordinator: drenado oportunista al recuperar red y
 * supervivencia del scope compartido tras stop() (cambio de usuario).
 *
 * La recomposición Compose (`remember(userId)`) no es verificable en
 * commonTest —sin harness de UI— y queda como pendiente de prueba
 * instrumentada; aquí se cubre el contrato del coordinator del que depende.
 */
class SyncRecoveryCoordinatorTest {

    @Test
    fun drains_when_network_reports_online() = runTest {
        val fixture = Fixture()
        val monitor = FakeNetworkMonitor(initiallyOnline = false)
        val results = mutableListOf<DrainResult>()
        val coordinator = SyncRecoveryCoordinator(
            processor = fixture.processor,
            scope = backgroundScope,
            idleIntervalMillis = 3_600_000L,
            minRetryIntervalMillis = 1_000L,
            networkMonitor = monitor,
            onResult = { results += it },
        )

        coordinator.start()
        runCurrent()
        // Offline significa pausa real: no debe intentar drenar ni generar
        // resultados mientras no haya conectividad.
        assertTrue(results.isEmpty())

        fixture.enqueueOperationUpsert("op-1")
        runCurrent()
        assertEquals(listOf("op-1"), fixture.queue.all().map { it.entityId })
        assertTrue(results.isEmpty())

        // Al volver online, el ciclo se activa inmediatamente sin avanzar el
        // reloj virtual.
        monitor.setOnline(true)
        runCurrent()

        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("op-1"), fixture.remoteOperations.saved.map { it.id })
        assertTrue(results.any { it.processed == 1 })
        coordinator.stop()
    }

    @Test
    fun shared_scope_survives_stop_and_serves_a_new_coordinator() = runTest {
        val fixture = Fixture()
        val monitor = FakeNetworkMonitor(initiallyOnline = false)

        // "Primer usuario": arranca y se detiene (p. ej. cambio de sesión).
        val first = SyncRecoveryCoordinator(
            processor = fixture.processor,
            scope = backgroundScope,
            networkMonitor = monitor,
        )
        first.start()
        runCurrent()
        first.stop()

        // El scope compartido sigue vivo: "Reintentar ahora" debe funcionar.
        var probeRan = false
        backgroundScope.launch { probeRan = true }.join()
        assertTrue(probeRan)

        // Trabajo del "segundo usuario": un coordinator nuevo sobre el mismo
        // scope lo drena al reportarse online.
        fixture.enqueueOperationUpsert("op-1")

        // "Segundo usuario": un coordinator nuevo sobre el mismo scope drena.
        val second = SyncRecoveryCoordinator(
            processor = fixture.processor,
            scope = backgroundScope,
            networkMonitor = monitor,
        )
        second.start()
        runCurrent()
        monitor.setOnline(true)
        runCurrent()

        assertTrue(fixture.queue.all().isEmpty())
        assertEquals(listOf("op-1"), fixture.remoteOperations.saved.map { it.id })
        second.stop()
    }

    private class FakeNetworkMonitor(initiallyOnline: Boolean) : NetworkMonitor {
        private val state = MutableStateFlow(initiallyOnline)
        override fun observe(): Flow<Boolean> = state
        fun setOnline(online: Boolean) {
            state.value = online
        }
    }

    private class Fixture {
        val queue = LocalSyncQueue(MemoryStore())
        val operations = FakeOperations()
        val remoteOperations = FakeRemoteOperations()
        val processor = SyncMutationProcessor(
            queue = queue,
            contexts = NoopContexts(),
            destinations = NoopDestinations(),
            operations = operations,
            comprobantes = NoopComprobantes(),
            remoteContexts = NoopRemoteContexts(),
            remoteDestinations = NoopRemoteDestinations(),
            remoteOperations = remoteOperations,
            remoteComprobantes = NoopRemoteComprobantes(),
            fileStore = NoopFileStore(),
        )

        suspend fun enqueueOperationUpsert(id: String) {
            operations.save(Operation(id, OperationType.PAGO, occurredAt = 100, createdAt = 100))
            queue.enqueue(
                PendingSyncMutation(
                    id = "m-$id",
                    resource = SyncResource.OPERATION,
                    mutation = SyncMutationType.UPSERT,
                    entityId = id,
                    enqueuedAt = 0,
                    nextAttemptAt = 0,
                )
            )
        }

        private class MemoryStore : SyncQueueStore {
            private var items = emptyList<PendingSyncMutation>()
            override fun read(): List<PendingSyncMutation> = items
            override fun write(items: List<PendingSyncMutation>) {
                this.items = items
            }
        }
    }

    private class FakeOperations : OperationRepository {
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

    private class FakeRemoteOperations : RemoteOperationRepository {
        var failWrites = false
        val saved = mutableListOf<Operation>()
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
        }
    }

    private class NoopContexts : ContextRepository {
        private val empty = MutableStateFlow(emptyList<Context>())
        override fun observe(): Flow<List<Context>> = empty
        override suspend fun get(id: String): Context? = null
        override suspend fun save(context: Context) = Unit
        override suspend fun update(context: Context) = Unit
        override suspend fun delete(id: String) = Unit
    }

    private class NoopDestinations : DestinationRepository {
        private val empty = MutableStateFlow(emptyList<Destination>())
        override fun observe(): Flow<List<Destination>> = empty
        override suspend fun get(id: String): Destination? = null
        override suspend fun save(destination: Destination) = Unit
        override suspend fun update(destination: Destination) = Unit
        override suspend fun delete(id: String) = Unit
    }

    private class NoopComprobantes : ComprobanteRepository {
        private val empty = MutableStateFlow(emptyList<Comprobante>())
        override fun observe(): Flow<List<Comprobante>> = empty
        override suspend fun get(id: String): Comprobante? = null
        override suspend fun save(comprobante: Comprobante) = Unit
        override suspend fun update(comprobante: Comprobante) = Unit
        override suspend fun delete(id: String) = Unit
    }

    private class NoopRemoteContexts : RemoteContextRepository {
        override suspend fun observe(): List<Context> = emptyList()
        override suspend fun save(context: Context) = Unit
        override suspend fun update(context: Context) = Unit
        override suspend fun delete(id: String) = Unit
    }

    private class NoopRemoteDestinations : RemoteDestinationRepository {
        override suspend fun observe(): List<Destination> = emptyList()
        override suspend fun save(destination: Destination) = Unit
        override suspend fun update(destination: Destination) = Unit
        override suspend fun delete(id: String) = Unit
    }

    private class NoopRemoteComprobantes : RemoteComprobanteRepository {
        override suspend fun observe(): List<RemoteComprobanteRecord> = emptyList()
        override suspend fun save(comprobante: Comprobante, bytes: ByteArray) = Unit
        override suspend fun update(comprobante: Comprobante) = Unit
        override suspend fun delete(record: RemoteComprobanteRecord) = Unit
    }

    private class NoopFileStore : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String =
            "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) = Unit
    }
}
