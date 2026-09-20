package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DestinationRepository
import platform.Foundation.NSUserDefaults

private object IosDestinationStore : DestinationStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun read(key: String): String? = defaults.stringForKey(key)
    override fun write(key: String, value: String) { defaults.setObject(value, forKey = key) }
}

actual fun platformDestinationStore(): DestinationStore = IosDestinationStore

actual fun createDestinationRepository(): DestinationRepository = createSyncedDestinationRepository()


private const val SYNC_QUEUE_KEY = "agendaqr.sync.queue.v1"

private class PlatformSyncQueueStore : SyncQueueStore {
    private val delegate = platformDestinationStore()
    override fun read(): List<PendingSyncMutation> = decodeSyncQueue(delegate.read(SYNC_QUEUE_KEY) ?: "[]")
    override fun write(items: List<PendingSyncMutation>) { delegate.write(SYNC_QUEUE_KEY, encodeSyncQueue(items)) }
}

actual fun platformSyncQueueStore(): SyncQueueStore = PlatformSyncQueueStore()
