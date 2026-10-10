package com.agendaqr.destinations.presentation

import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual fun shareComprobante(bytes: ByteArray, extension: String, mimeType: String?) {
    if (bytes.isEmpty()) return
    val current = AgendaQrAndroidShareLauncher.requireActivity()
    val safeExtension = extension.trim().trimStart('.').takeIf { it.isNotBlank() } ?: "bin"
    val file = File(current.cacheDir, "shared_comprobante." + safeExtension)
    file.writeBytes(bytes)
    val uri = FileProvider.getUriForFile(current, current.packageName + ".fileprovider", file)
    val type = mimeType?.takeIf { it.isNotBlank() } ?: "application/octet-stream"
    val intent = Intent(Intent.ACTION_SEND).apply {
        this.type = type
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    current.startActivity(Intent.createChooser(intent, null))
}
