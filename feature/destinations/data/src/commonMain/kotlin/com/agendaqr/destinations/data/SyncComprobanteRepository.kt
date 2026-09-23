package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncComprobanteRepository(
    private val local: ComprobanteRepository,
    private val remote: RemoteComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
    private val enqueuer: SyncMutationEnqueuer,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : ComprobanteRepository {

    private val syncMutex = Mutex()

    init {
        scope.launch { syncFromRemote() }
    }

    override fun observe(): Flow<List<Comprobante>> = local.observe()
    override suspend fun get(id: String): Comprobante? = local.get(id)

    override suspend fun save(comprobante: Comprobante) {
        local.save(comprobante)
        runCatching {
            val bytes = fileStore.read(comprobante.file)
                ?: error("Local receipt file not found: " + comprobante.file)
            remote.save(comprobante, bytes)
        }.onFailure { enqueuer.upsert(SyncResource.COMPROBANTE, comprobante.id) }
    }

    override suspend fun update(comprobante: Comprobante) {
        local.update(comprobante)
        runCatching { remote.update(comprobante) }
            .onFailure { enqueuer.upsert(SyncResource.COMPROBANTE, comprobante.id) }
    }

    override suspend fun delete(id: String) {
        local.delete(id)
        runCatching {
            remote.observe().firstOrNull { it.comprobante.id == id }?.let { remote.delete(it) }
        }.onFailure { enqueuer.delete(SyncResource.COMPROBANTE, id) }
    }

    suspend fun syncFromRemote() {
        syncMutex.withLock {
            runCatching {
                remote.observe().forEach { record ->
                    val bytes = AgendaQrSupabase.client.storage
                        .from("comprobantes")
                        .downloadAuthenticated(record.remoteFilePath)
                    val localFile = fileStore.save(
                        record.comprobante.id,
                        bytes,
                        record.comprobante.extension ?: record.comprobante.file.substringAfterLast('.', "bin"),
                    )
                    val localReceipt = record.comprobante.copy(file = localFile)
                    val existing = local.get(localReceipt.id)
                    if (existing == null) local.save(localReceipt)
                    else if (localReceipt.updatedAt >= existing.updatedAt) local.update(localReceipt)
                }
            }
        }
    }
}

fun createSyncedComprobanteRepository(
    fileStore: ComprobanteFileStore,
    queue: LocalSyncQueue = LocalSyncQueue(),
): ComprobanteRepository =
    SyncComprobanteRepository(
        local = LocalComprobanteRepository(storageKey = userScopedKey("agendaqr.comprobantes.v1")),
        remote = createRemoteComprobanteRepository(),
        fileStore = fileStore,
        enqueuer = SyncMutationEnqueuer(queue),
    )

fun createSyncedComprobanteRepository(
    fileStore: ComprobanteFileStore,
    enqueuer: SyncMutationEnqueuer,
): ComprobanteRepository =
    SyncComprobanteRepository(
        local = LocalComprobanteRepository(storageKey = userScopedKey("agendaqr.comprobantes.v1")),
        remote = createRemoteComprobanteRepository(),
        fileStore = fileStore,
        enqueuer = enqueuer,
    )

