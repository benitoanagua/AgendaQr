package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.nowMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SyncContextRepository(
    private val local: ContextRepository,
    private val remote: RemoteContextRepository,
    private val enqueuer: SyncMutationEnqueuer,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val conflictResolver: SyncConflictResolver = SyncConflictResolver(),
) : ContextRepository {
    init {
        scope.launch { syncFromRemote() }
    }

    override fun observe(): Flow<List<Context>> = local.observe()
    override suspend fun get(id: String): Context? = local.get(id)

    override suspend fun save(context: Context) {
        local.save(context)
        enqueuer.upsert(
            SyncResource.CONTEXT,
            context.id,
            context.updatedAt,
            runCatching { remote.get(context.id)?.updatedAt }.getOrNull(),
        )
    }

    override suspend fun update(context: Context) {
        local.update(context)
        enqueuer.upsert(
            SyncResource.CONTEXT,
            context.id,
            context.updatedAt,
            runCatching { remote.get(context.id)?.updatedAt }.getOrNull(),
        )
    }

    override suspend fun delete(id: String) {
        val deletedAt = local.get(id)?.updatedAt
        local.delete(id)
        enqueuer.delete(
            SyncResource.CONTEXT,
            id,
            deletedAt ?: nowMillis(),
            runCatching { remote.get(id)?.updatedAt }.getOrNull(),
        )
    }

    suspend fun syncFromRemote() {
        runCatching {
            val dirty = enqueuer.pendingIds(SyncResource.CONTEXT)
            remote.observe().forEach { remoteContext ->
                if (remoteContext.id in dirty) return@forEach
                val localContext = local.get(remoteContext.id)
                if (localContext == null) {
                    runCatching { local.save(remoteContext) }
                        .recoverCatching { local.update(remoteContext) }.getOrThrow()
                } else if (conflictResolver.shouldApplyRemote(localContext.updatedAt, remoteContext.updatedAt)) {
                    runCatching { local.save(remoteContext) }
                        .recoverCatching { local.update(remoteContext) }.getOrThrow()
                }
            }
        }
    }
}

fun createSyncedContextRepository(queue: LocalSyncQueue): ContextRepository =
    SyncContextRepository(
        local = LocalContextRepository(storageKey = userScopedKey("agendaqr.contexts.v1")),
        remote = createRemoteContextRepository(),
        enqueuer = SyncMutationEnqueuer(queue),
    )

/**
 * Variante que comparte el [local] con el [SyncMutationProcessor]: el procesador
 * debe operar sobre el repositorio crudo (sin re-encolado) para que aplicar un
 * snapshot ganador no genere una mutación nueva. Los wrappers Sync son la vía
 * de escritura de UI; el procesador es el único motor de sync.
 */
fun createSyncedContextRepository(
    queue: LocalSyncQueue,
    local: ContextRepository,
): ContextRepository =
    SyncContextRepository(
        local = local,
        remote = createRemoteContextRepository(),
        enqueuer = SyncMutationEnqueuer(queue),
    )
