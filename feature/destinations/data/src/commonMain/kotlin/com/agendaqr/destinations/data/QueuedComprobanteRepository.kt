package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import kotlinx.coroutines.flow.Flow

class QueuedComprobanteRepository(private val local: ComprobanteRepository, private val sync: SyncMutationEnqueuer) : ComprobanteRepository {
    override fun observe(): Flow<List<Comprobante>> = local.observe()
    override suspend fun get(id: String): Comprobante? = local.get(id)
    override suspend fun save(comprobante: Comprobante) { local.save(comprobante); sync.upsert(SyncResource.COMPROBANTE, comprobante.id) }
    override suspend fun update(comprobante: Comprobante) { local.update(comprobante); sync.upsert(SyncResource.COMPROBANTE, comprobante.id) }
    override suspend fun delete(id: String) { local.delete(id); sync.delete(SyncResource.COMPROBANTE, id) }
}
