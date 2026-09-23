package com.agendaqr.destinations.data

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun observe(): Flow<Boolean>
}

expect fun platformNetworkMonitor(): NetworkMonitor

class SyncQueueObserver(
    private val queue: LocalSyncQueue,
    private val networkMonitor: NetworkMonitor = platformNetworkMonitor(),
) {
    fun observeQueue(): Flow<List<PendingSyncMutation>> = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(queue.all())
            kotlinx.coroutines.delay(2000)
        }
    }

    fun observeNetwork(): Flow<Boolean> = networkMonitor.observe()
}
