package com.agendaqr.destinations.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncQueueUserIsolationTest {

    @Test
    fun queues_with_different_storage_keys_are_isolated() = runTest {
        val storeA = KeyedMemorySyncQueueStore("agendaqr.sync.queue.v1.user-a")
        val storeB = KeyedMemorySyncQueueStore("agendaqr.sync.queue.v1.user-b")
        val queueA = LocalSyncQueue(storeA)
        val queueB = LocalSyncQueue(storeB)

        queueA.enqueue(mutation("1", SyncResource.OPERATION, SyncMutationType.UPSERT, "op-1"))
        queueB.enqueue(mutation("2", SyncResource.DESTINATION, SyncMutationType.UPSERT, "d-1"))

        assertEquals(1, queueA.all().size)
        assertEquals("op-1", queueA.all().single().entityId)
        assertEquals(1, queueB.all().size)
        assertEquals("d-1", queueB.all().single().entityId)
    }

    @Test
    fun durable_queue_survives_recreation() = runTest {
        val backing = mutableMapOf<String, String>()
        val delegate = MapDestinationStore(backing)
        val key = "agendaqr.sync.queue.v1.user-a"
        fun store() = object : SyncQueueStore {
            override fun read(): List<PendingSyncMutation> =
                decodeSyncQueue(delegate.read(key) ?: "[]")
            override fun write(items: List<PendingSyncMutation>) {
                delegate.write(key, encodeSyncQueue(items))
            }
        }

        val queue1 = LocalSyncQueue(store())
        queue1.enqueue(mutation("1", SyncResource.COMPROBANTE, SyncMutationType.UPSERT, "r-1"))

        val queue2 = LocalSyncQueue(store())
        assertEquals(1, queue2.all().size)
        assertEquals("r-1", queue2.all().single().entityId)

        queue2.claim(1000)
        assertEquals(1, queue2.all().single().state.let { listOf(it) }.size)

        val queue3 = LocalSyncQueue(store())
        queue3.resetProcessing()
        assertEquals(1, queue3.all().size)
        assertEquals(SyncMutationState.PENDING, queue3.all().single().state)
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

    private class KeyedMemorySyncQueueStore(private val key: String) : SyncQueueStore {
        private val backing = mutableMapOf<String, String>()
        override fun read(): List<PendingSyncMutation> =
            decodeSyncQueue(backing[key] ?: "[]")
        override fun write(items: List<PendingSyncMutation>) {
            backing[key] = encodeSyncQueue(items)
        }
    }

    private class MapDestinationStore(private val backing: MutableMap<String, String>) : DestinationStore {
        override fun read(key: String): String? = backing[key]
        override fun write(key: String, value: String) { backing[key] = value }
    }
}
