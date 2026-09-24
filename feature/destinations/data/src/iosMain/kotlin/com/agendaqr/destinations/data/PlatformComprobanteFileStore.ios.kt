package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import io.github.jan.supabase.auth.auth
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathDomainMask
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSSearchPathDirectory

private object IosComprobanteFileStore : ComprobanteFileStore {
    private val fileManager = NSFileManager.defaultManager

    private fun userRoot(): String {
        val userId = AgendaQrSupabase.client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for receipt storage")
        val base = NSSearchPathForDirectoriesInDomains(
            NSSearchPathDirectory.NSApplicationSupportDirectory,
            NSSearchPathDomainMask.NSUserDomainMask,
            true,
        ).first() as String
        val root = base + "/AgendaQr/comprobantes/" + userId
        fileManager.createDirectoryAtPath(root, true, null, null)
        return root
    }

    private fun safeExtension(extension: String): String =
        extension.trim().lowercase().replace(Regex("[^a-z0-9]"), "").ifBlank { "bin" }

    private fun path(id: String, extension: String): String {
        val safeId = id.replace(Regex("[^A-Za-z0-9._-]"), "_")
        return userRoot() + "/" + safeId + "." + safeExtension(extension)
    }

    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        require(bytes.isNotEmpty()) { "Receipt file cannot be empty." }
        val target = path(id, extension)
        val data = NSData.create(bytes = bytes, length = bytes.size.toULong())
        require(data.writeToFile(target, atomically = true)) {
            "Unable to persist receipt file."
        }
        return "file://" + target
    }

    override suspend fun read(file: String): ByteArray? {
        val path = file.removePrefix("file://")
        return NSData.dataWithContentsOfFile(path)?.toByteArray()
    }

    override suspend fun delete(file: String) {
        val path = file.removePrefix("file://")
        if (fileManager.fileExistsAtPath(path)) {
            fileManager.removeItemAtPath(path, null)
        }
    }
}

actual fun platformComprobanteFileStore(): ComprobanteFileStore =
    IosComprobanteFileStore
