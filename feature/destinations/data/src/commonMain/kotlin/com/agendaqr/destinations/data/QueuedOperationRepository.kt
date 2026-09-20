package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import kotlinx.coroutines.flow.Flow

class QueuedOperationRepository(private val local: OperationRepository, private val sync: SyncMutationEnqueuer) : OperationRepository {
    override fun observe(): Flow<List<Operation>> = local.observe()
    override suspend fun get(id: String): Operation? = local.get(id)
    override suspend fun save(operation: Operation) { local.save(operation); sync.upsert(SyncResource.OPERATION, operation.id) }
    override suspend fun update(operation: Operation) { local.update(operation); sync.upsert(SyncResource.OPERATION, operation.id) }
    override suspend fun delete(id: String) { local.delete(id); sync.delete(SyncResource.OPERATION, id) }
}
