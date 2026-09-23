package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
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
) : ContextRepository {
    init {
        scope.launch {
            runCatching {
                remote.observe().forEach { remoteContext ->
                    val localContext = local.get(remoteContext.id)
                    if (localContext == null || remoteContext.updatedAt > localContext.updatedAt) {
                        runCatching { local.save(remoteContext) }
                            .recoverCatching { local.update(remoteContext) }
                    }
                }
            }
        }
    }

    override fun observe(): Flow<List<Context>> = local.observe()
    override suspend fun get(id: String): Context? = local.get(id)

    override suspend fun save(context: Context) {
        local.save(context)
        enqueuer.upsert(SyncResource.CONTEXT, context.id)
    }

    override suspend fun update(context: Context) {
        local.update(context)
        enqueuer.upsert(SyncResource.CONTEXT, context.id)
    }

    override suspend fun delete(id: String) {
        local.delete(id)
        enqueuer.delete(SyncResource.CONTEXT, id)
    }
}

fun createSyncedContextRepository(queue: LocalSyncQueue): ContextRepository =
    SyncContextRepository(
        local = LocalContextRepository(storageKey = userScopedKey("agendaqr.contexts.v1")),
        remote = createRemoteContextRepository(),
        enqueuer = SyncMutationEnqueuer(queue),
    )
