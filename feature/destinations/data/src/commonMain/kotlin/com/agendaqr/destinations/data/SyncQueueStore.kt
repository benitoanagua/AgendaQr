package com.agendaqr.destinations.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class SyncResource { CONTEXT, DESTINATION, OPERATION, COMPROBANTE }
enum class SyncMutationType { UPSERT, DELETE }
enum class SyncMutationState { PENDING, PROCESSING, FAILED, DEAD_LETTER }

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
    val revision: Long = 0,
    /**
     * `updatedAt` del registro local en el momento de encolar. Sirve como
     * versión base para detectar divergencia (el servidor cambió desde que
     * se encoló Y el cliente también). Nulo en mutaciones antiguas.
     */
    val baseUpdatedAt: Long? = null,
    /** `updatedAt` local capturado al encolar (para LWW sin releer). Nulo = releer en drain. */
    val localUpdatedAt: Long? = null,
)

/**
 * Política de reintentos con backoff exponencial acotado y jitter.
 *
 * El delay del intento N (1-based) es `min(maxDelay, base * 2^(N-1)) + jitter`,
 * de modo que el primer reintento espera `base` (+ jitter). Con los valores
 * por defecto (base 2s) el primer reintento tras `fail(now)` cae en `now+2000`,
 * preservando el comportamiento histórico de la cola.
 */
data class SyncRetryPolicy(
    val baseDelayMillis: Long = 2_000L,
    val maxDelayMillis: Long = 60_000L,
    val maxAttempts: Int = 25,
    val jitterMillis: Long = 0L,
    val jitter: (bound: Long) -> Long = { 0L },
) {
    fun delayFor(attempt: Int): Long {
        require(attempt >= 1) { "attempt must be >= 1, was $attempt" }
        val shift = (attempt - 1).coerceAtMost(20)
        val exponential = baseDelayMillis * (1L shl shift)
        val bounded = exponential.coerceAtMost(maxDelayMillis)
        if (jitterMillis <= 0) return bounded
        val jitterValue = jitter(jitterMillis + 1).coerceIn(0, jitterMillis)
        return (bounded + jitterValue).coerceAtMost(maxDelayMillis + jitterMillis)
    }

    fun shouldDeadLetter(attempts: Int, transient: Boolean): Boolean =
        !transient || attempts >= maxAttempts
}

/**
 * Clasifica errores en transitorios (reintentables) o permanentes.
 *
 * Heurística por mensaje, deliberadamente conservadora: todo lo desconocido
 * se considera transitorio para no perder escrituras del usuario. Solo los
 * fallos de autenticación/autorización y validación de esquema se marcan
 * permanentes.
 */
object SyncErrorClassifier {
    private val permanentMarkers = listOf(
        "authentication required",
        "unauthorized",
        "jwt",
        "permission denied",
        "row-level security",
        "violates row-level",
        "check constraint",
        "not-null constraint",
        "invalid input syntax",
        "duplicate key",
    )
    private val notFoundMarkers = listOf("not found")

    fun isTransient(error: Throwable): Boolean = isTransient(error.message)

    fun isTransient(message: String?): Boolean {
        val normalized = message?.lowercase() ?: return true
        if (permanentMarkers.any { normalized.contains(it) }) return false
        // "not found" local (entidad borrada antes del push) no debe reintentarse
        // como fallo de red; el procesador lo trata como idempotente. Aquí se
        // mantiene transitorio por defecto y el procesador decide.
        if (notFoundMarkers.any { normalized.contains(it) }) return true
        return true
    }

    fun isPermanent(error: Throwable): Boolean = !isTransient(error)
}

interface SyncQueueStore {
    fun read(): List<PendingSyncMutation>
    fun write(items: List<PendingSyncMutation>)
    fun preserveCorrupt(raw: String, timestamp: Long) {}
}

expect fun platformSyncQueueStore(): SyncQueueStore

private val queueJson = Json { encodeDefaults = true; ignoreUnknownKeys = true }

class LocalSyncQueue(private val store: SyncQueueStore = platformSyncQueueStore()) {
    private val mutex = Mutex()

    suspend fun enqueue(item: PendingSyncMutation) = mutex.withLock {
        val current = store.read()
        val existing = current.firstOrNull { it.resource == item.resource && it.entityId == item.entityId }
        val next = if (existing == null) {
            current + item.copy(revision = item.revision.coerceAtLeast(1))
        } else current.map {
            if (it.id == existing.id) {
                // La última escritura gana: el payload nuevo resetea el estado
                // de reintento (contrato histórico de la cola), pero la versión
                // base se conserva del primer encolado para detectar
                // divergencia contra el servidor.
                item.copy(
                    id = existing.id,
                    revision = existing.revision + 1,
                    baseUpdatedAt = existing.baseUpdatedAt ?: item.baseUpdatedAt,
                )
            } else it
        }
        store.write(next)
    }

    suspend fun claim(now: Long): List<PendingSyncMutation> = mutex.withLock {
        val current = store.read()
        val claimed = current.filter {
            it.state != SyncMutationState.PROCESSING &&
                it.state != SyncMutationState.DEAD_LETTER &&
                it.nextAttemptAt <= now
        }
        store.write(current.map { if (it in claimed) it.copy(state = SyncMutationState.PROCESSING) else it })
        claimed.map { it.copy(state = SyncMutationState.PROCESSING) }
    }

    suspend fun complete(id: String, claimedRevision: Long) = mutex.withLock {
        val current = store.read()
        store.write(current.mapNotNull {
            if (it.id != id) return@mapNotNull it
            if (it.revision == claimedRevision) null
            else it.copy(state = SyncMutationState.PENDING, nextAttemptAt = it.enqueuedAt)
        })
    }

    suspend fun defer(id: String, now: Long) = mutex.withLock {
        store.write(store.read().map {
            if (it.id == id) it.copy(state = SyncMutationState.PENDING, nextAttemptAt = now) else it
        })
    }

    suspend fun fail(
        id: String,
        now: Long,
        error: String,
        maxDelayMillis: Long = 60_000L,
        policy: SyncRetryPolicy = SyncRetryPolicy(maxDelayMillis = maxDelayMillis),
    ) = mutex.withLock {
        store.write(store.read().map {
            if (it.id == id) {
                val attempts = it.attempts + 1
                val transient = SyncErrorClassifier.isTransient(error)
                if (policy.shouldDeadLetter(attempts, transient)) {
                    it.copy(
                        attempts = attempts,
                        state = SyncMutationState.DEAD_LETTER,
                        lastError = error.take(500),
                    )
                } else {
                    val delay = policy.copy(maxDelayMillis = maxDelayMillis).delayFor(attempts)
                    it.copy(attempts = attempts, state = SyncMutationState.FAILED, nextAttemptAt = now + delay, lastError = error.take(500))
                }
            } else it
        })
    }

    suspend fun resetProcessing() = mutex.withLock {
        store.write(store.read().map { if (it.state == SyncMutationState.PROCESSING) it.copy(state = SyncMutationState.PENDING) else it })
    }

    suspend fun all(): List<PendingSyncMutation> = mutex.withLock { store.read() }

    /** true si hay una mutación pendiente/reintentable para este recurso+entidad. */
    suspend fun hasPending(resource: SyncResource, entityId: String): Boolean = mutex.withLock {
        store.read().any {
            it.resource == resource && it.entityId == entityId &&
                it.state != SyncMutationState.DEAD_LETTER
        }
    }

    /** Ids con mutaciones pendientes por recurso (para que `syncFromRemote` no las pise). */
    suspend fun pendingIds(resource: SyncResource): Set<String> = mutex.withLock {
        store.read()
            .filter { it.resource == resource && it.state != SyncMutationState.DEAD_LETTER }
            .mapTo(mutableSetOf()) { it.entityId }
    }

    /** Mueve una mutación a DLQ manual (p. ej. desde UI tras mostrar el conflicto). */
    suspend fun deadLetter(id: String, error: String) = mutex.withLock {
        store.write(store.read().map {
            if (it.id == id) it.copy(state = SyncMutationState.DEAD_LETTER, lastError = error.take(500))
            else it
        })
    }

    /** Reencola una mutación de DLQ para reintentarla (soporte / debug). */
    suspend fun redrive(id: String, now: Long) = mutex.withLock {
        store.write(store.read().map {
            if (it.id == id) it.copy(state = SyncMutationState.PENDING, attempts = 0, nextAttemptAt = now, lastError = null)
            else it
        })
    }
}

fun encodeSyncQueue(items: List<PendingSyncMutation>) = queueJson.encodeToString(items)
class SyncQueueCorruptionException(message: String, cause: Throwable) : IllegalStateException(message, cause)

fun decodeSyncQueue(value: String): List<PendingSyncMutation> =
    try {
        queueJson.decodeFromString<List<PendingSyncMutation>>(value)
    } catch (error: Throwable) {
        throw SyncQueueCorruptionException("Persisted sync queue is corrupt", error)
    }

// Queue invariants are intentionally covered at repository level before platform integration.
