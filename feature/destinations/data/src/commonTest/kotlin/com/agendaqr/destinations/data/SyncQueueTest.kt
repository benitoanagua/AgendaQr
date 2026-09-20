package com.agendaqr.destinations.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncQueueTest {
    @Test
    fun enqueueDeduplicatesByResourceAndEntity() = runTest {
        val store = FakeQueueStore()
        val queue = LocalSyncQueue(store)

        queue.enqueue(mutation("1", SyncResource.OPERATION, SyncMutationType.UPSERT, "op-1"))
        queue.enqueue(mutation("2", SyncResource.OPERATION, SyncMutationType.DELETE, "op-1"))

        val items = queue.all()
        assertEquals(1, items.size)
        assertEquals(SyncMutationType.DELETE, items.single().mutation)
        assertEquals("1", items.single().id)
    }

    @Test
    fun failedMutationUsesBoundedExponentialBackoff() = runTest {
        val store = FakeQueueStore()
        val queue = LocalSyncQueue(store)
        queue.enqueue(mutation("1", SyncResource.DESTINATION, SyncMutationType.UPSERT, "d-1"))

        queue.claim(1000)
        queue.fail("1", now = 1000, error = "network")

        val failed = queue.all().single()
        assertEquals(SyncMutationState.FAILED, failed.state)
        assertEquals(3000, failed.nextAttemptAt)
        assertEquals(1, failed.attempts)
        assertEquals("network", failed.lastError)
    }

    @Test
    fun processingItemsAreRecoveredAsPending() = runTest {
        val store = FakeQueueStore()
        val queue = LocalSyncQueue(store)
        queue.enqueue(mutation("1", SyncResource.OPERATION, SyncMutationType.UPSERT, "op-1"))

        queue.claim(1000)
        assertEquals(SyncMutationState.PROCESSING, queue.all().single().state)

        queue.resetProcessing()

        assertEquals(SyncMutationState.PENDING, queue.all().single().state)
        assertTrue(queue.all().single().nextAttemptAt <= 1000)
    }

    private fun mutation(
        id: String,
        resource: SyncResource,
        type: SyncMutationType,
        entityId: String,
    ) = PendingSyncMutation(
        id = id,
        resource = resource,
        mutation = type,
        entityId = entityId,
        enqueuedAt = 0,
        nextAttemptAt = 0,
    )
}

private class FakeQueueStore : SyncQueueStore {
    private var items = emptyList<PendingSyncMutation>()
    override fun read(): List<PendingSyncMutation> = items
    override fun write(items: List<PendingSyncMutation>) {
        this.items = items
    }
}

private fun <T> runTest(block: suspend () -> T): T =
    kotlinx.coroutines.test.runTest { block() }
