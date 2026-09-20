package com.agendaqr.destinations.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class SyncResource { DESTINATION, OPERATION, COMPROBANTE }
enum class SyncMutationType { UPSERT, DELETE }

@Serializable
data class PendingSyncMutation(
    val id: String,
    val resource: SyncResource,
    val mutation: SyncMutationType,
    val entityId: String,
    val enqueuedAt: Long,
)

interface SyncQueueStore {
    fun read(): List<PendingSyncMutation>
    fun write(items: List<PendingSyncMutation>)
}

expect fun platformSyncQueueStore(): SyncQueueStore

private val queueJson = Json { encodeDefaults = true; ignoreUnknownKeys = true }

class LocalSyncQueue(private val store: SyncQueueStore = platformSyncQueueStore()) {
    private val mutex = Mutex()

    suspend fun enqueue(item: PendingSyncMutation) = mutex.withLock {
        val next = store.read().filterNot {
            it.resource == item.resource && it.entityId == item.entityId
        } + item
        store.write(next)
    }

    suspend fun remove(id: String) = mutex.withLock {
        store.write(store.read().filterNot { it.id == id })
    }

    suspend fun all(): List<PendingSyncMutation> = mutex.withLock { store.read() }
}

fun encodeSyncQueue(items: List<PendingSyncMutation>) = queueJson.encodeToString(items)
fun decodeSyncQueue(value: String): List<PendingSyncMutation> =
    runCatching { queueJson.decodeFromString<List<PendingSyncMutation>>(value) }.getOrDefault(emptyList())
