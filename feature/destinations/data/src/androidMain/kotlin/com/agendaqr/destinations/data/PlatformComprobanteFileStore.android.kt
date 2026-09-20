package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import java.io.File

private object AndroidComprobanteFileStore : ComprobanteFileStore {
    private fun directory(): File =
        File(AgendaQrAndroidStorage.requireFilesDir(), "comprobantes").apply { mkdirs() }

    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        val file = File(directory(), "$id.$extension")
        file.writeBytes(bytes)
        return file.toURI().toString()
    }

    override suspend fun read(file: String): ByteArray? =
        runCatching { File(java.net.URI(file)).takeIf { it.exists() }?.readBytes() }.getOrNull()

    override suspend fun delete(file: String) {
        runCatching { File(java.net.URI(file)).delete() }
    }
}

actual fun platformComprobanteFileStore(): ComprobanteFileStore =
    AndroidComprobanteFileStore
