package com.agendaqr.destinations.data

class MemorySyncQueueStore : SyncQueueStore {
    private var items = listOf<PendingSyncMutation>()
    override fun read() = items
    override fun write(items: List<PendingSyncMutation>) {
        this.items = items
    }
}
