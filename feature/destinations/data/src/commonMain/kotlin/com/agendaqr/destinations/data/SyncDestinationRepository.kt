package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
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
 * Local state remains immediately usable offline. Remote failures do not erase
 * the local record; a later repository initialization retries the pull.
 */
class SyncDestinationRepository(
    private val local: DestinationRepository,
    private val remote: RemoteDestinationRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
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
    }

    override suspend fun update(destination: Destination) {
        local.update(destination)
        runCatching { remote.update(destination) }
    }

    override suspend fun delete(id: String) {
        local.delete(id)
        runCatching { remote.delete(id) }
    }

    suspend fun syncFromRemote() {
        syncMutex.withLock {
            runCatching {
                val remoteItems = remote.observe()
                remoteItems.forEach { remoteItem ->
                    val localItem = local.get(remoteItem.id)
                    if (localItem == null) {
                        local.save(remoteItem)
                    } else if (remoteItem.updatedAt >= localItem.updatedAt) {
                        local.update(remoteItem)
                    }
                }
            }
        }
    }
}

fun createSyncedDestinationRepository(): DestinationRepository =
    SyncDestinationRepository(
        local = LocalDestinationRepository(storageKey = userScopedKey("agendaqr.destinations.v1")),
        remote = createRemoteDestinationRepository(),
    )

