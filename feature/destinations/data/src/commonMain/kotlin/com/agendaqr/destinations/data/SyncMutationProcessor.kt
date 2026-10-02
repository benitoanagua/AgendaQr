package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.OperationRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import io.github.jan.supabase.storage.storage

/**
 * Resultado observable de un [SyncMutationProcessor.drainWithReport].
 */
data class DrainResult(
    /** Mutaciones convergidas (subidas, bajadas o idempotentes). */
    val processed: Int = 0,
    /** Mutaciones cuyo valor local se impuso en el servidor. */
    val pushed: Int = 0,
    /** Mutaciones donde el servidor ganó y se aplicó localmente. */
    val pulled: Int = 0,
    /** Conflictos detectados (incluye divergencias reales LWW). */
    val conflicts: List<SyncConflict> = emptyList(),
    /** Mutaciones que fallaron con error transitorio y siguen en cola. */
    val failed: Int = 0,
    /** Mutaciones movidas a DLQ (error permanente o sin reintentos). */
    val deadLettered: Int = 0,
    /** Mutaciones omitidas por dependencias bloqueadas. */
    val skipped: Int = 0,
    /** Próximo reintento programado, si hay fallos pendientes. */
    val nextRetryAt: Long? = null,
)

/**
 * Drena la cola offline aplicando Last-Write-Wins antes de cada push.
 *
 * Flujo por mutación:
 * 1. Lee el estado local y remoto puntual (`get`, con caché dentro del drain).
 * 2. Resuelve con [SyncConflictResolver]: si el servidor es más nuevo, el
 *    snapshot remoto se aplica localmente y la mutación se completa sin push.
 * 3. Si el cliente gana (o no hay remoto), hace push idempotente.
 * 4. Ante fallo, clasifica con [SyncErrorClassifier]: transitorio → backoff
 *    exponencial; permanente o sin reintentos → `DEAD_LETTER`.
 *
 * La firma histórica `drain(now): Int` se conserva para compatibilidad.
 *
 * @param contexts Repositorio LOCAL crudo (p. ej. `LocalContextRepository`),
 * compartido por instancia con el wrapper Sync correspondiente. El procesador
 * escribe snapshots ganadores directamente; si recibiera el wrapper Sync, cada
 * pull re-encolaría una mutación y la cola nunca convergería. Misma regla para
 * [destinations], [operations] y [comprobantes].
 */
class SyncMutationProcessor @OptIn(ExperimentalTime::class) constructor(
    private val queue: LocalSyncQueue,
    private val contexts: ContextRepository,
    private val destinations: DestinationRepository,
    private val operations: OperationRepository,
    private val comprobantes: ComprobanteRepository,
    private val remoteContexts: RemoteContextRepository,
    private val remoteDestinations: RemoteDestinationRepository,
    private val remoteOperations: RemoteOperationRepository,
    private val remoteComprobantes: RemoteComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
    private val conflictResolver: SyncConflictResolver = SyncConflictResolver(),
    private val retryPolicy: SyncRetryPolicy = SyncRetryPolicy(),
    private val maxDelayMillis: Long = 60_000L,
    private val onConflict: (SyncConflict) -> Unit = {},
    private val downloadRemoteBytes: suspend (remotePath: String) -> ByteArray? = { path ->
        runCatching {
            AgendaQrSupabase.client.storage
                .from("comprobantes")
                .downloadAuthenticated(path)
        }.getOrNull()
    },
) {
    private val mutex = Mutex()

    suspend fun drain(now: Long = Clock.System.now().toEpochMilliseconds()): Int =
        drainWithReport(now).processed

    suspend fun drainWithReport(now: Long = Clock.System.now().toEpochMilliseconds()): DrainResult =
        mutex.withLock {
            queue.resetProcessing()
            var pushed = 0
            var pulled = 0
            var failed = 0
            var deadLettered = 0
            var skipped = 0
            val conflicts = mutableListOf<SyncConflict>()
            // Caché del drain: evita N+1 `observe()` contra Supabase.
            val remoteCache = mutableMapOf<Pair<SyncResource, String>, Any?>()

            val beforeClaim = queue.all()
            val claimed = queue.claim(now)
            val blockedResources = beforeClaim
                .filter { it.state != SyncMutationState.PROCESSING && it.nextAttemptAt > now }
                .mapTo(mutableSetOf()) { it.resource }
            val failedParents = mutableSetOf<SyncResource>()
            val dependencyOrder = listOf(
                SyncResource.CONTEXT,
                SyncResource.DESTINATION,
                SyncResource.OPERATION,
                SyncResource.COMPROBANTE,
            )

            claimed
                .sortedWith(compareBy<PendingSyncMutation> { it.resource.ordinal }.thenBy { it.enqueuedAt })
                .forEach { mutation ->
                    val parentResources = dependencyOrder.takeWhile { it != mutation.resource }.toSet()
                    if (parentResources.any { it in blockedResources || it in failedParents }) {
                        queue.defer(mutation.id, now)
                        skipped++
                        return@forEach
                    }
                    val outcome = runCatching {
                        applyMutation(mutation, remoteCache)
                    }.getOrElse { error ->
                        failMutation(mutation, now, error)
                        failedParents += mutation.resource
                        failed++
                        if (queue.all().firstOrNull { it.id == mutation.id }?.state == SyncMutationState.DEAD_LETTER) {
                            deadLettered++
                        }
                        return@forEach
                    }
                    queue.complete(mutation.id, mutation.revision)
                    when (outcome.decision) {
                        SyncConflictDecision.PUSH_LOCAL -> pushed++
                        SyncConflictDecision.PULL_REMOTE -> pulled++
                    }
                    // Solo se reporta cuando hubo algo que decidir: el servidor
                    // ganó, o ambos lados avanzaron (divergencia real resuelta
                    // por LWW). Un push limpio sin base divergente no es conflicto.
                    if (outcome.decision == SyncConflictDecision.PULL_REMOTE || outcome.diverged) {
                        conflicts += outcome.conflict
                        onConflict(outcome.conflict)
                    }
                }
            val nextRetryAt = queue.all()
                .filter { it.state == SyncMutationState.FAILED }
                .minOfOrNull { it.nextAttemptAt }
            DrainResult(
                processed = pushed + pulled,
                pushed = pushed,
                pulled = pulled,
                conflicts = conflicts,
                failed = failed,
                deadLettered = deadLettered,
                skipped = skipped,
                nextRetryAt = nextRetryAt,
            )
        }

    private data class ApplyOutcome(
        val decision: SyncConflictDecision,
        val conflict: SyncConflict,
        val diverged: Boolean,
    )

    @Suppress("UNCHECKED_CAST")
    private suspend fun applyMutation(
        mutation: PendingSyncMutation,
        remoteCache: MutableMap<Pair<SyncResource, String>, Any?>,
    ): ApplyOutcome {
        suspend fun cachedRemote(): Any? {
            val key = mutation.resource to mutation.entityId
            if (!remoteCache.containsKey(key)) {
                remoteCache[key] = when (mutation.resource) {
                    SyncResource.CONTEXT -> remoteContexts.get(mutation.entityId)
                    SyncResource.DESTINATION -> remoteDestinations.get(mutation.entityId)
                    SyncResource.OPERATION -> remoteOperations.get(mutation.entityId)
                    SyncResource.COMPROBANTE -> remoteComprobantes.get(mutation.entityId)
                }
            }
            return remoteCache[key]
        }

        return when (mutation.resource) {
            SyncResource.CONTEXT -> {
                val local = contexts.get(mutation.entityId)
                if (mutation.mutation == SyncMutationType.DELETE) {
                    applyDelete(
                        mutation = mutation,
                        localUpdatedAt = null,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Context)?.updatedAt,
                        push = { remoteContexts.delete(mutation.entityId) },
                        resurrect = {
                            val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Context)
                                ?: error("Remote vanished during delete resolution: " + mutation.entityId)
                            runCatching { contexts.save(remote) }.recoverCatching { contexts.update(remote) }.getOrThrow()
                        },
                    )
                } else {
                    if (local == null) {
                        val remote = cachedRemote() as? com.agendaqr.destinations.domain.Context
                        if (remote == null) {
                            // Ambos lados sin el registro: convergido, nada que subir.
                            return ApplyOutcome(
                                SyncConflictDecision.PUSH_LOCAL,
                                conflictResolver.resolve(mutation.resource, mutation.entityId, null, null, mutation.baseUpdatedAt),
                                false,
                            )
                        }
                        runCatching { contexts.save(remote) }.recoverCatching { contexts.update(remote) }.getOrThrow()
                        val conflict = conflictResolver.resolve(
                            mutation.resource, mutation.entityId, null, remote.updatedAt, mutation.baseUpdatedAt,
                        )
                        return ApplyOutcome(SyncConflictDecision.PULL_REMOTE, conflict, conflict.diverged)
                    }
                    val conflict = conflictResolver.resolve(
                        resource = mutation.resource,
                        entityId = mutation.entityId,
                        localUpdatedAt = mutation.localUpdatedAt ?: local.updatedAt,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Context)?.updatedAt,
                        baseUpdatedAt = mutation.baseUpdatedAt,
                    )
                    if (conflict.decision == SyncConflictDecision.PUSH_LOCAL) {
                        remoteContexts.save(local)
                    } else {
                        val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Context)
                            ?: error("Remote context vanished: " + mutation.entityId)
                        // Igualdad exacta ⇒ ya convergido: no reescribir.
                        if (local.updatedAt != remote.updatedAt) {
                            runCatching { contexts.update(remote) }.recoverCatching { contexts.save(remote) }.getOrThrow()
                        }
                    }
                    ApplyOutcome(conflict.decision, conflict, conflict.diverged)
                }
            }
            SyncResource.DESTINATION -> {
                val local = destinations.get(mutation.entityId)
                if (mutation.mutation == SyncMutationType.DELETE) {
                    applyDelete(
                        mutation = mutation,
                        localUpdatedAt = null,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Destination)?.updatedAt,
                        push = { remoteDestinations.delete(mutation.entityId) },
                        resurrect = {
                            val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Destination)
                                ?: error("Remote vanished during delete resolution: " + mutation.entityId)
                            runCatching { destinations.save(remote) }.recoverCatching { destinations.update(remote) }.getOrThrow()
                        },
                    )
                } else {
                    if (local == null) {
                        val remote = cachedRemote() as? com.agendaqr.destinations.domain.Destination
                        if (remote == null) {
                            return ApplyOutcome(
                                SyncConflictDecision.PUSH_LOCAL,
                                conflictResolver.resolve(mutation.resource, mutation.entityId, null, null, mutation.baseUpdatedAt),
                                false,
                            )
                        }
                        runCatching { destinations.save(remote) }.recoverCatching { destinations.update(remote) }.getOrThrow()
                        val conflict = conflictResolver.resolve(
                            mutation.resource, mutation.entityId, null, remote.updatedAt, mutation.baseUpdatedAt,
                        )
                        return ApplyOutcome(SyncConflictDecision.PULL_REMOTE, conflict, conflict.diverged)
                    }
                    val conflict = conflictResolver.resolve(
                        resource = mutation.resource,
                        entityId = mutation.entityId,
                        localUpdatedAt = mutation.localUpdatedAt ?: local.updatedAt,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Destination)?.updatedAt,
                        baseUpdatedAt = mutation.baseUpdatedAt,
                    )
                    if (conflict.decision == SyncConflictDecision.PUSH_LOCAL) {
                        remoteDestinations.save(local)
                    } else {
                        val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Destination)
                            ?: error("Remote destination vanished: " + mutation.entityId)
                        if (local.updatedAt != remote.updatedAt) {
                            destinations.update(remote)
                        }
                    }
                    ApplyOutcome(conflict.decision, conflict, conflict.diverged)
                }
            }
            SyncResource.OPERATION -> {
                val local = operations.get(mutation.entityId)
                if (mutation.mutation == SyncMutationType.DELETE) {
                    applyDelete(
                        mutation = mutation,
                        localUpdatedAt = null,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Operation)?.updatedAt,
                        push = { remoteOperations.delete(mutation.entityId) },
                        resurrect = {
                            val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Operation)
                                ?: error("Remote vanished during delete resolution: " + mutation.entityId)
                            runCatching { operations.save(remote) }.recoverCatching { operations.update(remote) }.getOrThrow()
                        },
                    )
                } else {
                    if (local == null) {
                        val remote = cachedRemote() as? com.agendaqr.destinations.domain.Operation
                        if (remote == null) {
                            return ApplyOutcome(
                                SyncConflictDecision.PUSH_LOCAL,
                                conflictResolver.resolve(mutation.resource, mutation.entityId, null, null, mutation.baseUpdatedAt),
                                false,
                            )
                        }
                        runCatching { operations.save(remote) }.recoverCatching { operations.update(remote) }.getOrThrow()
                        val conflict = conflictResolver.resolve(
                            mutation.resource, mutation.entityId, null, remote.updatedAt, mutation.baseUpdatedAt,
                        )
                        return ApplyOutcome(SyncConflictDecision.PULL_REMOTE, conflict, conflict.diverged)
                    }
                    val conflict = conflictResolver.resolve(
                        resource = mutation.resource,
                        entityId = mutation.entityId,
                        localUpdatedAt = mutation.localUpdatedAt ?: local.updatedAt,
                        remoteUpdatedAt = (cachedRemote() as? com.agendaqr.destinations.domain.Operation)?.updatedAt,
                        baseUpdatedAt = mutation.baseUpdatedAt,
                    )
                    if (conflict.decision == SyncConflictDecision.PUSH_LOCAL) {
                        remoteOperations.save(local)
                    } else {
                        val remote = (cachedRemote() as? com.agendaqr.destinations.domain.Operation)
                            ?: error("Remote operation vanished: " + mutation.entityId)
                        if (local.updatedAt != remote.updatedAt) {
                            operations.update(remote)
                        }
                    }
                    ApplyOutcome(conflict.decision, conflict, conflict.diverged)
                }
            }
            SyncResource.COMPROBANTE -> applyComprobante(mutation, cachedRemote())
        }
    }

    private suspend fun applyComprobante(
        mutation: PendingSyncMutation,
        cachedRemote: Any?,
    ): ApplyOutcome {
        val remote = cachedRemote as? RemoteComprobanteRecord
        if (mutation.mutation == SyncMutationType.DELETE) {
            return applyDelete(
                mutation = mutation,
                localUpdatedAt = null,
                remoteUpdatedAt = remote?.comprobante?.updatedAt,
                push = {
                    val record = remote ?: remoteComprobantes.get(mutation.entityId)
                    if (record != null) remoteComprobantes.delete(record)
                },
                resurrect = {
                    val record = remote ?: error("Remote vanished during delete resolution: " + mutation.entityId)
                    val bytes = downloadRemoteBytes(record.remoteFilePath)
                        ?: error("Remote receipt bytes unavailable: " + mutation.entityId)
                    val localFile = fileStore.save(
                        record.comprobante.id,
                        bytes,
                        record.comprobante.extension
                            ?: record.comprobante.file.substringAfterLast('.', "bin"),
                    )
                    val resurrected = record.comprobante.copy(file = localFile)
                    runCatching { comprobantes.save(resurrected) }
                        .recoverCatching { comprobantes.update(resurrected) }.getOrThrow()
                },
            )
        }
        val local = comprobantes.get(mutation.entityId)
        if (local == null) {
            if (remote == null) {
                return ApplyOutcome(
                    SyncConflictDecision.PUSH_LOCAL,
                    conflictResolver.resolve(mutation.resource, mutation.entityId, null, null, mutation.baseUpdatedAt),
                    false,
                )
            }
            val bytes = downloadRemoteBytes(remote.remoteFilePath)
                ?: error("Remote receipt bytes unavailable: " + mutation.entityId)
            val localFile = fileStore.save(
                remote.comprobante.id,
                bytes,
                remote.comprobante.extension ?: remote.comprobante.file.substringAfterLast('.', "bin"),
            )
            val pulled = remote.comprobante.copy(file = localFile)
            runCatching { comprobantes.save(pulled) }.recoverCatching { comprobantes.update(pulled) }.getOrThrow()
            val conflict = conflictResolver.resolve(
                mutation.resource, mutation.entityId, null, remote.comprobante.updatedAt, mutation.baseUpdatedAt,
            )
            return ApplyOutcome(SyncConflictDecision.PULL_REMOTE, conflict, conflict.diverged)
        }
        val conflict = conflictResolver.resolve(
            resource = mutation.resource,
            entityId = mutation.entityId,
            localUpdatedAt = mutation.localUpdatedAt ?: local.updatedAt,
            remoteUpdatedAt = remote?.comprobante?.updatedAt,
            baseUpdatedAt = mutation.baseUpdatedAt,
        )
        if (conflict.decision == SyncConflictDecision.PUSH_LOCAL) {
            val bytes = fileStore.read(local.file)
                ?: error("Local receipt file not found: " + local.file)
            remoteComprobantes.save(local, bytes)
        } else {
            val record = remote ?: error("Remote receipt vanished: " + mutation.entityId)
            // Igualdad exacta ⇒ convergido: evita descargar bytes de nuevo.
            if (local.updatedAt != record.comprobante.updatedAt) {
                val bytes = downloadRemoteBytes(record.remoteFilePath)
                    ?: error("Remote receipt bytes unavailable: " + mutation.entityId)
                val localFile = fileStore.save(
                    record.comprobante.id,
                    bytes,
                    record.comprobante.extension ?: record.comprobante.file.substringAfterLast('.', "bin"),
                )
                val pulled = record.comprobante.copy(file = localFile)
                runCatching { comprobantes.update(pulled) }.recoverCatching { comprobantes.save(pulled) }.getOrThrow()
            }
        }
        return ApplyOutcome(conflict.decision, conflict, conflict.diverged)
    }

    private suspend inline fun applyDelete(
        mutation: PendingSyncMutation,
        localUpdatedAt: Long?,
        remoteUpdatedAt: Long?,
        crossinline push: suspend () -> Unit,
        crossinline resurrect: suspend () -> Unit,
    ): ApplyOutcome {
        val conflict = conflictResolver.resolve(
            resource = mutation.resource,
            entityId = mutation.entityId,
            localUpdatedAt = localUpdatedAt,
            remoteUpdatedAt = remoteUpdatedAt,
            baseUpdatedAt = mutation.baseUpdatedAt,
            deleteAt = mutation.localUpdatedAt ?: mutation.enqueuedAt,
        )
        if (conflict.decision == SyncConflictDecision.PUSH_LOCAL) {
            // El borrado siempre se propaga (delete remoto idempotente); si el
            // remoto ya no existe, el push es no-op y converge igual.
            push()
        } else {
            resurrect()
        }
        return ApplyOutcome(conflict.decision, conflict, conflict.diverged)
    }

    private suspend fun failMutation(mutation: PendingSyncMutation, now: Long, error: Throwable) {
        queue.fail(mutation.id, now, error.message ?: "Synchronization failed", maxDelayMillis, retryPolicy)
    }
}
