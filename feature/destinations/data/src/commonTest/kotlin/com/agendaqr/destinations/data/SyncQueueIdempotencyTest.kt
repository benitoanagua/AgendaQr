package com.agendaqr.destinations.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncQueueIdempotencyTest {
    @Test
    fun latest_mutation_wins_for_same_entity_after_multiple_retries() = runTest {
        val store = MemoryQueueStore()
        val queue = LocalSyncQueue(store)

        queue.enqueue(mutation("first", SyncMutationType.UPSERT, "op-1", enqueuedAt = 10))
        queue.claim(10)
        queue.fail("first", now = 10, error = "offline")

        queue.enqueue(mutation("second", SyncMutationType.UPSERT, "op-1", enqueuedAt = 20))

        val item = queue.all().single()
        assertEquals("first", item.id)
        assertEquals(SyncMutationType.UPSERT, item.mutation)
        assertEquals(0, item.attempts)
        assertEquals(SyncMutationState.PENDING, item.state)
        assertEquals(20, item.enqueuedAt)
        assertEquals(20, item.nextAttemptAt)
        assertEquals(null, item.lastError)
    }

    @Test
    fun delete_supersedes_pending_upsert_without_creating_a_second_queue_item() = runTest {
        val store = MemoryQueueStore()
        val queue = LocalSyncQueue(store)

        queue.enqueue(mutation("upsert", SyncMutationType.UPSERT, "destination-1", enqueuedAt = 100))
        queue.enqueue(mutation("delete", SyncMutationType.DELETE, "destination-1", enqueuedAt = 101))

        val items = queue.all()

        assertEquals(1, items.size)
        assertEquals(SyncMutationType.DELETE, items.single().mutation)
        assertEquals("upsert", items.single().id)
        assertEquals(101, items.single().nextAttemptAt)
    }

    @Test
    fun failed_mutation_is_not_claimed_before_backoff_but_is_claimed_after_it() = runTest {
        val store = MemoryQueueStore()
        val queue = LocalSyncQueue(store)

        queue.enqueue(mutation("1", SyncMutationType.UPSERT, "context-1", enqueuedAt = 1))
        queue.claim(1)
        queue.fail("1", now = 1, error = "offline")

        assertTrue(queue.claim(2).isEmpty())
        assertEquals(SyncMutationState.FAILED, queue.all().single().state)

        val claimed = queue.claim(3001)
        assertEquals(listOf("1"), claimed.map { it.id })
        assertEquals(SyncMutationState.PROCESSING, queue.all().single().state)
    }

    @Test
    fun durable_json_round_trip_preserves_retry_state() {
        val original = mutation(
            id = "m-1",
            type = SyncMutationType.UPSERT,
            entityId = "receipt-1",
            enqueuedAt = 50,
        ).copy(
            attempts = 3,
            state = SyncMutationState.FAILED,
            nextAttemptAt = 58,
            lastError = "timeout",
        )

        val restored = decodeSyncQueue(encodeSyncQueue(listOf(original))).single()

        assertEquals(original, restored)
    }

    private fun mutation(
        id: String,
        type: SyncMutationType,
        entityId: String,
        enqueuedAt: Long,
    ) = PendingSyncMutation(
        id = id,
        resource = when {
            entityId.startsWith("op-") -> SyncResource.OPERATION
            entityId.startsWith("destination-") -> SyncResource.DESTINATION
            entityId.startsWith("context-") -> SyncResource.CONTEXT
            else -> SyncResource.COMPROBANTE
        },
        mutation = type,
        entityId = entityId,
        enqueuedAt = enqueuedAt,
        nextAttemptAt = enqueuedAt,
    )
}

private class MemoryQueueStore : SyncQueueStore {
    private var items = emptyList<PendingSyncMutation>()

    override fun read(): List<PendingSyncMutation> = items

    override fun write(items: List<PendingSyncMutation>) {
        this.items = items
    }
}
