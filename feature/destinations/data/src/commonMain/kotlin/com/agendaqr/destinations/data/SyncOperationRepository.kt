package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncOperationRepository(
    private val local: OperationRepository,
    private val remote: RemoteOperationRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : OperationRepository {

    private val syncMutex = Mutex()

    init {
        scope.launch { syncFromRemote() }
    }

    override fun observe(): Flow<List<Operation>> = local.observe()

    override suspend fun get(id: String): Operation? = local.get(id)

    override suspend fun save(operation: Operation) {
        local.save(operation)
        runCatching { remote.save(operation) }
    }

    override suspend fun update(operation: Operation) {
        local.update(operation)
        runCatching { remote.update(operation) }
    }

    override suspend fun delete(id: String) {
        local.delete(id)
        runCatching { remote.delete(id) }
    }

    suspend fun syncFromRemote() {
        syncMutex.withLock {
            runCatching {
                remote.observe().forEach { remoteItem ->
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

fun createSyncedOperationRepository(): OperationRepository =
    SyncOperationRepository(
        local = LocalOperationRepository(storageKey = userScopedKey("agendaqr.operations.v1")),
        remote = createRemoteOperationRepository(),
    )

