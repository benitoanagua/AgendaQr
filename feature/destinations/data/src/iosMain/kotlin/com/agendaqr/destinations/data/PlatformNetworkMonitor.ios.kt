package com.agendaqr.destinations.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import platform.Network.NWPathMonitor
import platform.Network.NWPathStatusSatisfied
import platform.darwin.dispatch_queue_create

private class IosNetworkMonitor : NetworkMonitor {
    override fun observe(): Flow<Boolean> = callbackFlow {
        val monitor = NWPathMonitor()
        val queue = dispatch_queue_create("com.agendaqr.network-monitor", null)

        monitor.pathUpdateHandler = { path ->
            trySend(path.status == NWPathStatusSatisfied)
        }
        monitor.startWithQueue(queue)

        awaitClose {
            monitor.cancel()
        }
    }.distinctUntilChanged().flowOn(Dispatchers.Default)
}

actual fun platformNetworkMonitor(): NetworkMonitor = IosNetworkMonitor()
