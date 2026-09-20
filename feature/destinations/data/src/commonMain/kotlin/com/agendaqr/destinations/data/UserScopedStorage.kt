package com.agendaqr.destinations.data

fun userScopedKey(prefix: String): String =
    prefix + "." + (AgendaQrSupabase.client.auth.currentUserOrNull()?.id
        ?: error("Authentication required"))

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class SyncResource {
    DESTINATION,
    OPERATION,
    COMPROBANTE,
}

enum class SyncMutationType {
    UPSERT,
    DELETE,
}

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

private val queueJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

class LocalSyncQueue(
    private val store: SyncQueueStore = platformSyncQueueStore(),
) {
    private val lock = kotlinx.coroutines.sync.Mutex()

    suspend fun enqueue(item: PendingSyncMutation) {
        lock.lock()
        try {
            val current = store.read().filterNot {
                it.resource == item.resource &&
                    it.entityId == item.entityId &&
                    it.mutation == item.mutation
            }
            store.write(current + item)
        } finally {
            lock.unlock()
        }
    }

    suspend fun remove(id: String) {
        lock.lock()
        try {
            store.write(store.read().filterNot { it.id == id })
        } finally {
            lock.unlock()
        }
    }

    suspend fun all(): List<PendingSyncMutation> = store.read()
}

fun encodeSyncQueue(items: List<PendingSyncMutation>): String =
    queueJson.encodeToString(items)

fun decodeSyncQueue(value: String): List<PendingSyncMutation> =
    runCatching { queueJson.decodeFromString<List<PendingSyncMutation>>(value) }
        .getOrDefault(emptyList())
