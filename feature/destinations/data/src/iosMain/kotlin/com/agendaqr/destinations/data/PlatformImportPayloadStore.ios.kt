package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ImportPayloadStore

private object IosImportPayloadStore : ImportPayloadStore {
    private val values = mutableMapOf<String, ByteArray>()

    override suspend fun put(reference: String, bytes: ByteArray) {
        values[reference] = bytes.copyOf()
    }

    override suspend fun read(reference: String): ByteArray? =
        values[reference]?.copyOf()

    override suspend fun delete(reference: String) {
        values.remove(reference)
    }
}

actual fun platformImportPayloadStore(): ImportPayloadStore = IosImportPayloadStore
