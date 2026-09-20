package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import kotlinx.coroutines.flow.Flow

class QueuedDestinationRepository(private val local: DestinationRepository, private val sync: SyncMutationEnqueuer) : DestinationRepository {
    override fun observe(): Flow<List<Destination>> = local.observe()
    override suspend fun get(id: String): Destination? = local.get(id)
    override suspend fun save(destination: Destination) { local.save(destination); sync.upsert(SyncResource.DESTINATION, destination.id) }
    override suspend fun update(destination: Destination) { local.update(destination); sync.upsert(SyncResource.DESTINATION, destination.id) }
    override suspend fun delete(id: String) { local.delete(id); sync.delete(SyncResource.DESTINATION, id) }
}
