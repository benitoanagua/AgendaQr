package com.agendaqr.destinations.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private class IosNetworkMonitor : NetworkMonitor {
    // TODO: implement NWPathMonitor via cinterop when iOS validation is enabled.
    // For V1, assume online and rely on queue pending as offline signal.
    override fun observe(): Flow<Boolean> = flowOf(true)
}

actual fun platformNetworkMonitor(): NetworkMonitor = IosNetworkMonitor()
