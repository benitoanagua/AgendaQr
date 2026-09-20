package com.agendaqr.destinations.presentation

import android.app.Activity
import android.content.Intent
import com.agendaqr.destinations.domain.QrAsset

object AgendaQrAndroidShareLauncher {
    private var activity: Activity? = null

    fun initialize(activity: Activity) {
        this.activity = activity
    }

    fun share(asset: QrAsset) {
        val current = requireNotNull(activity) { "AgendaQrAndroidShareLauncher must be initialized by the host." }
        val uri = asset.toShareUri(current)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = asset.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        current.startActivity(Intent.createChooser(intent, null))
    }
}

actual fun shareQr(asset: QrAsset) {
    AgendaQrAndroidShareLauncher.share(asset)
}
