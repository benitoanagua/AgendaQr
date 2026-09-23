package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ImportPayloadStore
import java.util.concurrent.ConcurrentHashMap

private object AndroidImportPayloadStore : ImportPayloadStore {
    private val values = ConcurrentHashMap<String, ByteArray>()

    override suspend fun put(reference: String, bytes: ByteArray) {
        values[reference] = bytes.copyOf()
    }

    override suspend fun read(reference: String): ByteArray? =
        values[reference]?.copyOf()

    override suspend fun delete(reference: String) {
        values.remove(reference)
    }
}

actual fun platformImportPayloadStore(): ImportPayloadStore = AndroidImportPayloadStore
