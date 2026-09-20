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
