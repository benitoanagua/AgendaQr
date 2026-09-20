package com.agendaqr.destinations.presentation

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.agendaqr.destinations.presentation.IncomingComprobante

object AgendaQrAndroidImportLauncher {
    private lateinit var camera: ActivityResultLauncher<Void?>
    private lateinit var gallery: ActivityResultLauncher<String>
    private lateinit var multiple: ActivityResultLauncher<String>
    private lateinit var cameraPermission: ActivityResultLauncher<String>
    private lateinit var activity: Activity
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _results = MutableSharedFlow<QrImportResult>(extraBufferCapacity = 16)
    private val _receiptResults = MutableSharedFlow<IncomingComprobante>(extraBufferCapacity = 16)
    val results = _results.asSharedFlow()
    val receiptResults = _receiptResults.asSharedFlow()

    fun initialize(activity: Activity) {
        this.activity = activity
        cameraPermission = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) camera.launch(null)
        }
        camera = activity.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { decodeAndEmit(it) }
        }
        gallery = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let(::decodeAndEmit)
        }
        multiple = activity.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            decodeMultiple(uris)
        }
    }

    fun camera() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera.launch(null)
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    fun gallery() = gallery.launch("image/*")
    fun multiple() = multiple.launch("image/*")

    fun handleShare(activity: Activity, intent: android.content.Intent?) {
        when (intent?.action) {
            android.content.Intent.ACTION_SEND -> {
                intent.getParcelableExtra<Uri>(android.content.Intent.EXTRA_STREAM)?.let { decodeAndEmit(it, activity) }
            }
            android.content.Intent.ACTION_SEND_MULTIPLE -> {
                intent.getParcelableArrayListExtra<Uri>(android.content.Intent.EXTRA_STREAM)?.let { decodeMultiple(it, activity) }
            }
        }
    }

    private fun decodeMultiple(uris: List<Uri>) = decodeMultiple(uris, activity)

    private fun decodeMultiple(uris: List<Uri>, sourceActivity: Activity) {
        scope.launch {
            val assets = uris.mapNotNull { uri ->
                val bytes = sourceActivity.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@mapNotNull null
                decodeQrAsset(bytes, sourceActivity.contentResolver.getType(uri) ?: "image/png")
            }
            if (assets.isNotEmpty()) _results.emit(QrImportResult(assets))
        }
    }

    private fun decodeAndEmit(uri: Uri) = decodeAndEmit(uri, activity)

    private fun decodeAndEmit(uri: Uri, sourceActivity: Activity) {
        scope.launch {
            val bytes = sourceActivity.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@launch
            val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
            val asset = decodeQrAsset(bytes, mime)
            if (asset != null) _results.emit(QrImportResult(listOf(asset)))
            else _receiptResults.emit(IncomingComprobante(bytes, mime, extensionFor(mime)))
        }
    }

    private fun extensionFor(mime: String): String = when (mime.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "application/pdf" -> "pdf"
        "image/webp" -> "webp"
        else -> "png"
    }

    private fun decodeAndEmit(bitmap: Bitmap) {
        scope.launch {
            decodeQrBitmap(bitmap)?.let { bitmapToAsset(it, "image/png") }?.let { _results.emit(QrImportResult(listOf(it))) }
        }
    }
}
