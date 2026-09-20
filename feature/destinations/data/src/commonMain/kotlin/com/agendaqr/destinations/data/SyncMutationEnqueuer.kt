package com.agendaqr.destinations.data

import kotlin.time.Clock

class SyncMutationEnqueuer(
    private val queue: LocalSyncQueue,
    private val idFactory: () -> String = { Clock.System.now().toEpochMilliseconds().toString() },
) {
    suspend fun upsert(resource: SyncResource, entityId: String) =
        enqueue(resource, SyncMutationType.UPSERT, entityId)

    suspend fun delete(resource: SyncResource, entityId: String) =
        enqueue(resource, SyncMutationType.DELETE, entityId)

    private suspend fun enqueue(resource: SyncResource, mutation: SyncMutationType, entityId: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        queue.enqueue(PendingSyncMutation(idFactory(), resource, mutation, entityId, now, nextAttemptAt = now))
    }
}
