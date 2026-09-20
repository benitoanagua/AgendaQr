package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.ComprobanteRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

class SyncMutationProcessor(
    private val queue: LocalSyncQueue,
    private val destinations: DestinationRepository,
    private val operations: OperationRepository,
    private val comprobantes: ComprobanteRepository,
    private val remoteDestinations: RemoteDestinationRepository,
    private val remoteOperations: RemoteOperationRepository,
    private val remoteComprobantes: RemoteComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
) {
    private val mutex = Mutex()

    suspend fun drain(now: Long = Clock.System.now().toEpochMilliseconds()): Int = mutex.withLock {
        queue.resetProcessing()
        var processed = 0
        queue.claim(now).forEach { mutation ->
            runCatching {
                when (mutation.resource) {
                    SyncResource.DESTINATION -> {
                        if (mutation.mutation == SyncMutationType.UPSERT) {
                            destinations.get(mutation.entityId)?.let(remoteDestinations::save)
                                ?: error("Destination not found: " + mutation.entityId)
                        } else remoteDestinations.delete(mutation.entityId)
                    }
                    SyncResource.OPERATION -> {
                        if (mutation.mutation == SyncMutationType.UPSERT) {
                            operations.get(mutation.entityId)?.let(remoteOperations::save)
                                ?: error("Operation not found: " + mutation.entityId)
                        } else remoteOperations.delete(mutation.entityId)
                    }
                    SyncResource.COMPROBANTE -> processComprobante(mutation)
                }
            }.onSuccess {
                queue.complete(mutation.id)
                processed++
            }.onFailure {
                queue.fail(mutation.id, now, it.message ?: "Synchronization failed")
            }
        }
        processed
    }

    private suspend fun processComprobante(mutation: PendingSyncMutation) {
        when (mutation.mutation) {
            SyncMutationType.UPSERT -> {
                val receipt = comprobantes.get(mutation.entityId)
                    ?: error("Receipt not found: " + mutation.entityId)
                val bytes = fileStore.read(receipt.file)
                    ?: error("Local receipt file not found: " + receipt.file)
                remoteComprobantes.save(receipt, bytes)
            }
            SyncMutationType.DELETE -> {
                remoteComprobantes.observe()
                    .firstOrNull { it.comprobante.id == mutation.entityId }
                    ?.let(remoteComprobantes::delete)
            }
        }
    }
}
