package com.agendaqr.destinations.data

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SyncMutationEnqueuer(
    private val queue: LocalSyncQueue,
    private val idFactory: () -> String = { Clock.System.now().toEpochMilliseconds().toString() },
) {
    suspend fun upsert(resource: SyncResource, entityId: String) =
        enqueue(resource, SyncMutationType.UPSERT, entityId, localUpdatedAt = null, baseUpdatedAt = null)

    suspend fun delete(resource: SyncResource, entityId: String) =
        enqueue(resource, SyncMutationType.DELETE, entityId, localUpdatedAt = null, baseUpdatedAt = null)

    /**
     * Variante con versiones: [localUpdatedAt] es el `updatedAt` del registro
     * tras la escritura local y [baseUpdatedAt] la versión conocida del
     * servidor (si se conoce). Permiten a [SyncConflictResolver] detectar
     * divergencia real en el drain.
     */
    suspend fun upsert(
        resource: SyncResource,
        entityId: String,
        localUpdatedAt: Long,
        baseUpdatedAt: Long?,
    ) = enqueue(resource, SyncMutationType.UPSERT, entityId, localUpdatedAt, baseUpdatedAt)

    suspend fun delete(
        resource: SyncResource,
        entityId: String,
        deleteAt: Long,
        baseUpdatedAt: Long?,
    ) = enqueue(resource, SyncMutationType.DELETE, entityId, deleteAt, baseUpdatedAt)

    private suspend fun enqueue(
        resource: SyncResource,
        mutation: SyncMutationType,
        entityId: String,
        localUpdatedAt: Long?,
        baseUpdatedAt: Long?,
    ) {
        val now = Clock.System.now().toEpochMilliseconds()
        queue.enqueue(
            PendingSyncMutation(
                id = idFactory(),
                resource = resource,
                mutation = mutation,
                entityId = entityId,
                enqueuedAt = now,
                nextAttemptAt = now,
                baseUpdatedAt = baseUpdatedAt,
                localUpdatedAt = localUpdatedAt,
            )
        )
    }

    /** Para que `syncFromRemote` no pise entidades con mutaciones pendientes. */
    suspend fun hasPending(resource: SyncResource, entityId: String): Boolean =
        queue.hasPending(resource, entityId)

    suspend fun pendingIds(resource: SyncResource): Set<String> =
        queue.pendingIds(resource)
}
