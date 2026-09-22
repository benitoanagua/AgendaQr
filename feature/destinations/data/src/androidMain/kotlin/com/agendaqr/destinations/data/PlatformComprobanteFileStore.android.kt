package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import io.github.jan.supabase.auth.auth
import java.io.File

private object AndroidComprobanteFileStore : ComprobanteFileStore {
    private fun directory(): File {
        val userId = AgendaQrSupabase.client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for receipt storage")
        return File(File(AgendaQrAndroidStorage.requireFilesDir(), "comprobantes"), userId)
            .apply { mkdirs() }
    }

    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        require(bytes.isNotEmpty()) { "Receipt file cannot be empty." }
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
