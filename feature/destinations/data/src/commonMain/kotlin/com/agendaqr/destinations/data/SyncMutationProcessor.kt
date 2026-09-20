package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.ComprobanteRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncMutationProcessor(
    private val queue: LocalSyncQueue,
    private val destinations: DestinationRepository,
    private val operations: OperationRepository,
    private val comprobantes: ComprobanteRepository,
) {
    private val mutex = Mutex()

    suspend fun drain(): Int = mutex.withLock {
        var processed = 0
        queue.all().forEach { mutation ->
            val result = runCatching {
                when (mutation.resource) {
                    SyncResource.DESTINATION -> Unit
                    SyncResource.OPERATION -> Unit
                    SyncResource.COMPROBANTE -> Unit
                }
            }
            if (result.isSuccess) {
                queue.remove(mutation.id)
                processed++
            }
        }
        processed
    }
}
