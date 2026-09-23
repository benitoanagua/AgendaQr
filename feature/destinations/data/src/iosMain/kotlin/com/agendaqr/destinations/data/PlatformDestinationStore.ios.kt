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


private const val SYNC_QUEUE_PREFIX = "agendaqr.sync.queue.v1"

private class PlatformSyncQueueStore : SyncQueueStore {
    private val delegate = platformDestinationStore()
    private fun key(): String = userScopedKey(SYNC_QUEUE_PREFIX)
    override fun read(): List<PendingSyncMutation> = decodeSyncQueue(delegate.read(key()) ?: "[]")
    override fun write(items: List<PendingSyncMutation>) { delegate.write(key(), encodeSyncQueue(items)) }
}

actual fun platformSyncQueueStore(): SyncQueueStore = PlatformSyncQueueStore()
