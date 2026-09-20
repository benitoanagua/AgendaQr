package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.Foundation.NSDataWritingAtomic
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private object IosComprobanteFileStore : ComprobanteFileStore {
    private fun directory(): NSURL {
        val documents = NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask,
        ).firstOrNull() as NSURL
        val directory = documents.URLByAppendingPathComponent("comprobantes")!!
        NSFileManager.defaultManager.createDirectoryAtURL(
            directory,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        return directory
    }

    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        require(bytes.isNotEmpty()) { "Receipt file cannot be empty." }
        val file = directory().URLByAppendingPathComponent("$id.$extension")!!
        NSData.create(bytes = bytes.refTo(0), length = bytes.size.toULong())
            .writeToURL(file, NSDataWritingAtomic)
        return file.absoluteString!!
    }

    override suspend fun read(file: String): ByteArray? {
        val url = NSURL.URLWithString(file) ?: return null
        val data = NSData.dataWithContentsOfURL(url) ?: return null
        return data.toByteArray()
    }

    override suspend fun delete(file: String) {
        val url = NSURL.URLWithString(file) ?: return
        NSFileManager.defaultManager.removeItemAtURL(url, error = null)
    }
}

private fun NSData.toByteArray(): ByteArray =
    ByteArray(length.toInt()).also {
        if (it.isNotEmpty()) {
            getBytes(it.refTo(0), length)
        }
    }

actual fun platformComprobanteFileStore(): ComprobanteFileStore =
    IosComprobanteFileStore
