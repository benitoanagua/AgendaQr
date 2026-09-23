package com.agendaqr.destinations.presentation

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.ImportBatch
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.ReaderException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

fun QrAsset.toShareUri(context: Context): Uri {
    val bytes = Base64.decode(encoded, Base64.DEFAULT)
    val ext = when (mimeType.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "application/pdf" -> "pdf"
        "image/webp" -> "webp"
        else -> "png"
    }
    val file = File(context.cacheDir, "shared_qr.$ext")
    file.writeBytes(bytes)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

fun decodeQrAsset(bytes: ByteArray, mimeType: String): QrAsset? {
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
    val binary = BinaryBitmap(HybridBinarizer(source))
    return try {
        MultiFormatReader().decode(binary)
        QrAsset(encoded = Base64.encodeToString(bytes, Base64.NO_WRAP), mimeType = mimeType)
    } catch (_: ReaderException) {
        null
    } finally {
        bitmap.recycle()
    }
}

fun decodeQrBitmap(bitmap: Bitmap): QrAsset? {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val bytes = outputStream.toByteArray()
    return decodeQrAsset(bytes, "image/png")
}

fun bitmapToAsset(asset: QrAsset, mimeType: String): QrAsset = asset

object AgendaQrAndroidImportLauncher {
    private var camera: ActivityResultLauncher<Void?>? = null
    private var gallery: ActivityResultLauncher<String>? = null
    private var multiple: ActivityResultLauncher<String>? = null
    private var cameraPermission: ActivityResultLauncher<String>? = null
    private var activity: Activity? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _results = MutableSharedFlow<QrImportResult>(extraBufferCapacity = 16)
    private val _receiptResults = MutableSharedFlow<IncomingComprobante>(extraBufferCapacity = 16)
    private val _batchResults = MutableSharedFlow<ImportBatch>(extraBufferCapacity = 16)
    val results = _results.asSharedFlow()
    val receiptResults = _receiptResults.asSharedFlow()
    val batchResults = _batchResults.asSharedFlow()

    fun initialize(compActivity: ComponentActivity) {
        this.activity = compActivity
        cameraPermission = compActivity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) camera?.launch(null)
        }
        camera = compActivity.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { decodeAndEmit(it) }
        }
        gallery = compActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { decodeAndEmit(it) }
        }
        multiple = compActivity.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            decodeMultiple(uris)
        }
    }

    fun camera() {
        val current = activity ?: return
        if (ActivityCompat.checkSelfPermission(current, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera?.launch(null)
        } else {
            cameraPermission?.launch(Manifest.permission.CAMERA)
        }
    }

    fun gallery() { gallery?.launch("image/*") }
    fun multiple() { multiple?.launch("image/*") }

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

    private fun decodeMultiple(uris: List<Uri>) {
        activity?.let { decodeMultiple(uris, it) }
    }

    private fun decodeMultiple(uris: List<Uri>, sourceActivity: Activity) {
        scope.launch {
            val assets = mutableListOf<QrAsset>()
            val candidates = mutableListOf<ImportCandidate>()
            uris.forEach { uri ->
                val bytes = sourceActivity.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@forEach
                val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
                val fingerprint = sha256(bytes)
                val asset = decodeQrAsset(bytes, mime)
                if (asset != null) {
                    assets += asset
                    candidates += ImportCandidate(
                        id = "import-$fingerprint",
                        kind = ImportKind.QR,
                        fingerprint = fingerprint,
                        mimeType = mime,
                        extension = extensionFor(mime),
                        qrAsset = asset,
                    )
                } else {
                    val extension = extensionFor(mime)
                    _receiptResults.emit(IncomingComprobante(bytes, mime, extension))
                    candidates += ImportCandidate(
                        id = "import-$fingerprint",
                        kind = ImportKind.COMPROBANTE,
                        fingerprint = fingerprint,
                        mimeType = mime,
                        extension = extension,
                    )
                }
            }
            if (assets.isNotEmpty()) _results.emit(QrImportResult(assets))
            if (candidates.isNotEmpty()) _batchResults.emit(ImportBatch(candidates))
        }
    }

    private fun decodeAndEmit(uri: Uri) {
        activity?.let { decodeAndEmit(uri, it) }
    }

    private fun decodeAndEmit(uri: Uri, sourceActivity: Activity) {
        scope.launch {
            val bytes = sourceActivity.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@launch
            val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
            val asset = decodeQrAsset(bytes, mime)
            val fingerprint = sha256(bytes)
            if (asset != null) {
                _results.emit(QrImportResult(listOf(asset)))
                _batchResults.emit(
                    ImportBatch(
                        listOf(
                            ImportCandidate(
                                id = "import-$fingerprint",
                                kind = ImportKind.QR,
                                fingerprint = fingerprint,
                                mimeType = mime,
                                extension = extensionFor(mime),
                                qrAsset = asset,
                            )
                        )
                    )
                )
            } else {
                val extension = extensionFor(mime)
                _receiptResults.emit(IncomingComprobante(bytes, mime, extension))
                _batchResults.emit(
                    ImportBatch(
                        listOf(
                            ImportCandidate(
                                id = "import-$fingerprint",
                                kind = ImportKind.COMPROBANTE,
                                fingerprint = fingerprint,
                                mimeType = mime,
                                extension = extension,
                            )
                        )
                    )
                )
            }
        }
    }

    private fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun extensionFor(mime: String): String = when (mime.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "application/pdf" -> "pdf"
        "image/webp" -> "webp"
        else -> "png"
    }

    private fun decodeAndEmit(bitmap: Bitmap) {
        scope.launch {
            decodeQrBitmap(bitmap)?.let { _results.emit(QrImportResult(listOf(it))) }
        }
    }
}
