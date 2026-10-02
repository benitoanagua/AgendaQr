package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.nowMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncOperationRepository(
    private val local: OperationRepository,
    private val remote: RemoteOperationRepository,
    private val enqueuer: SyncMutationEnqueuer,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val conflictResolver: SyncConflictResolver = SyncConflictResolver(),
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
            .onFailure {
                enqueuer.upsert(
                    SyncResource.OPERATION,
                    operation.id,
                    operation.updatedAt,
                    runCatching { remote.get(operation.id)?.updatedAt }.getOrNull(),
                )
            }
    }

    override suspend fun update(operation: Operation) {
        local.update(operation)
        runCatching { remote.update(operation) }
            .onFailure {
                enqueuer.upsert(
                    SyncResource.OPERATION,
                    operation.id,
                    operation.updatedAt,
                    runCatching { remote.get(operation.id)?.updatedAt }.getOrNull(),
                )
            }
    }

    override suspend fun delete(id: String) {
        val deletedAt = local.get(id)?.updatedAt
        local.delete(id)
        runCatching { remote.delete(id) }
            .onFailure {
                enqueuer.delete(
                    SyncResource.OPERATION,
                    id,
                    deletedAt ?: nowMillis(),
                    runCatching { remote.get(id)?.updatedAt }.getOrNull(),
                )
            }
    }

    suspend fun syncFromRemote() {
        syncMutex.withLock {
            runCatching {
                val dirty = enqueuer.pendingIds(SyncResource.OPERATION)
                remote.observe().forEach { remoteItem ->
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

fun createSyncedOperationRepository(
    queue: LocalSyncQueue = LocalSyncQueue(),
): OperationRepository =
    SyncOperationRepository(
        local = LocalOperationRepository(storageKey = userScopedKey("agendaqr.operations.v1")),
        remote = createRemoteOperationRepository(),
        enqueuer = SyncMutationEnqueuer(queue),
    )

fun createSyncedOperationRepository(
    enqueuer: SyncMutationEnqueuer,
): OperationRepository =
    SyncOperationRepository(
        local = LocalOperationRepository(storageKey = userScopedKey("agendaqr.operations.v1")),
        remote = createRemoteOperationRepository(),
        enqueuer = enqueuer,
    )

/** Variante con [local] compartido con el [SyncMutationProcessor] (ver contexts). */
fun createSyncedOperationRepository(
    enqueuer: SyncMutationEnqueuer,
    local: OperationRepository,
): OperationRepository =
    SyncOperationRepository(
        local = local,
        remote = createRemoteOperationRepository(),
        enqueuer = enqueuer,
    )

