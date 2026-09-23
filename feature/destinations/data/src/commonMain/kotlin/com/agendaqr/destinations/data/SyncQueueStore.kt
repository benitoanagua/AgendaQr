package com.agendaqr.destinations.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class SyncResource { CONTEXT, DESTINATION, OPERATION, COMPROBANTE }
enum class SyncMutationType { UPSERT, DELETE }
enum class SyncMutationState { PENDING, PROCESSING, FAILED }

@Serializable
data class PendingSyncMutation(
    val id: String,
    val resource: SyncResource,
    val mutation: SyncMutationType,
    val entityId: String,
    val enqueuedAt: Long,
    val attempts: Int = 0,
    val state: SyncMutationState = SyncMutationState.PENDING,
    val nextAttemptAt: Long = enqueuedAt,
    val lastError: String? = null,
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
        val current = store.read()
        val existing = current.firstOrNull { it.resource == item.resource && it.entityId == item.entityId }
        val next = if (existing == null) current + item else current.map {
            if (it.id == existing.id) item.copy(id = existing.id) else it
        }
        store.write(next)
    }

    suspend fun claim(now: Long): List<PendingSyncMutation> = mutex.withLock {
        val current = store.read()
        val claimed = current.filter { it.state != SyncMutationState.PROCESSING && it.nextAttemptAt <= now }
        store.write(current.map { if (it in claimed) it.copy(state = SyncMutationState.PROCESSING) else it })
        claimed.map { it.copy(state = SyncMutationState.PROCESSING) }
    }

    suspend fun complete(id: String) = mutex.withLock {
        store.write(store.read().filterNot { it.id == id })
    }

    suspend fun fail(id: String, now: Long, error: String, maxDelayMillis: Long = 60_000L) = mutex.withLock {
        store.write(store.read().map {
            if (it.id == id) {
                val attempts = it.attempts + 1
                val delay = (1L shl attempts.coerceAtMost(6)) * 1_000L
                it.copy(attempts = attempts, state = SyncMutationState.FAILED, nextAttemptAt = now + delay.coerceAtMost(maxDelayMillis), lastError = error.take(500))
            } else it
        })
    }

    suspend fun resetProcessing() = mutex.withLock {
        store.write(store.read().map { if (it.state == SyncMutationState.PROCESSING) it.copy(state = SyncMutationState.PENDING) else it })
    }

    suspend fun all(): List<PendingSyncMutation> = mutex.withLock { store.read() }
}

fun encodeSyncQueue(items: List<PendingSyncMutation>) = queueJson.encodeToString(items)
fun decodeSyncQueue(value: String): List<PendingSyncMutation> =
    runCatching { queueJson.decodeFromString<List<PendingSyncMutation>>(value) }.getOrDefault(emptyList())

// Queue invariants are intentionally covered at repository level before platform integration.
