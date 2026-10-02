package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.nowMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Local-first repository with best-effort remote synchronization.
 *
 * Local state remains immediately usable offline. Remote failures are enqueued
 * for retry with exponential backoff.
 */
class SyncDestinationRepository(
    private val local: DestinationRepository,
    private val remote: RemoteDestinationRepository,
    private val enqueuer: SyncMutationEnqueuer,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val conflictResolver: SyncConflictResolver = SyncConflictResolver(),
) : DestinationRepository {

    private val syncMutex = Mutex()

    init {
        scope.launch { syncFromRemote() }
    }

    override fun observe(): Flow<List<Destination>> = local.observe()

    override suspend fun get(id: String): Destination? = local.get(id)

    override suspend fun save(destination: Destination) {
        local.save(destination)
        runCatching { remote.save(destination) }
            .onFailure {
                enqueuer.upsert(
                    SyncResource.DESTINATION,
                    destination.id,
                    destination.updatedAt,
                    remoteVersionOf(destination.id),
                )
            }
    }

    override suspend fun update(destination: Destination) {
        local.update(destination)
        runCatching { remote.update(destination) }
            .onFailure {
                enqueuer.upsert(
                    SyncResource.DESTINATION,
                    destination.id,
                    destination.updatedAt,
                    remoteVersionOf(destination.id),
                )
            }
    }

    override suspend fun delete(id: String) {
        val deletedAt = local.get(id)?.updatedAt
        local.delete(id)
        runCatching { remote.delete(id) }
            .onFailure {
                enqueuer.delete(
                    SyncResource.DESTINATION,
                    id,
                    deletedAt ?: nowMillis(),
                    remoteVersionOf(id),
                )
            }
    }

    private suspend fun remoteVersionOf(id: String): Long? =
        runCatching { remote.get(id)?.updatedAt }.getOrNull()

    suspend fun syncFromRemote() {
        syncMutex.withLock {
            runCatching {
                // No pisar entidades con mutaciones pendientes: el drain con LWW
                // es quien decide al recuperar conectividad.
                val dirty = enqueuer.pendingIds(SyncResource.DESTINATION)
                val remoteItems = remote.observe()
                remoteItems.forEach { remoteItem ->
                    if (remoteItem.id in dirty) return@forEach
                    val localItem = local.get(remoteItem.id)
                    if (localItem == null) {
                        local.save(remoteItem)
                    } else if (conflictResolver.shouldApplyRemote(localItem.updatedAt, remoteItem.updatedAt)) {
                        local.update(remoteItem)
                    }
                }
            }
        }
    }
}

fun createSyncedDestinationRepository(
    queue: LocalSyncQueue = LocalSyncQueue(),
): DestinationRepository =
    SyncDestinationRepository(
        local = LocalDestinationRepository(storageKey = userScopedKey("agendaqr.destinations.v1")),
        remote = createRemoteDestinationRepository(),
        enqueuer = SyncMutationEnqueuer(queue),
    )

fun createSyncedDestinationRepository(
    enqueuer: SyncMutationEnqueuer,
): DestinationRepository =
    SyncDestinationRepository(
        local = LocalDestinationRepository(storageKey = userScopedKey("agendaqr.destinations.v1")),
        remote = createRemoteDestinationRepository(),
        enqueuer = enqueuer,
    )

/** Variante con [local] compartido con el [SyncMutationProcessor] (ver contexts). */
fun createSyncedDestinationRepository(
    enqueuer: SyncMutationEnqueuer,
    local: DestinationRepository,
): DestinationRepository =
    SyncDestinationRepository(
        local = local,
        remote = createRemoteDestinationRepository(),
        enqueuer = enqueuer,
    )

