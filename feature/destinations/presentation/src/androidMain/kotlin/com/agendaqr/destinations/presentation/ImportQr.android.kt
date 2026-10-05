package com.agendaqr.destinations.presentation

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import android.content.Context
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.security.MessageDigest
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.data.createImportPayloadStore

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
    val content = QrFrameAnalyzer.decodeArgb(pixels, bitmap.width, bitmap.height)
    bitmap.recycle()
    // Sin decodificación no hay QR demostrable: la imagen queda pendiente
    // de revisión por otra vía (contrato de clasificación conservadora).
    return content?.let {
        QrAsset(
            encoded = Base64.encodeToString(bytes, Base64.NO_WRAP),
            mimeType = mimeType,
            content = it,
        )
    }
}

object AgendaQrAndroidImportLauncher {
    private var gallery: ActivityResultLauncher<String>? = null
    private var multiple: ActivityResultLauncher<String>? = null
    private var activity: Activity? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val payloadStore = createImportPayloadStore()
    /**
     * In-app acquisition (Galería/Cámara launched from Añadir or the editor).
     * The launching screen owns a live collector, so no retention is needed.
     */
    private val _results = MutableSharedFlow<QrImportResult>(extraBufferCapacity = 16)
    val results = _results.asSharedFlow()

    /**
     * External share intents can arrive while the app is cold (no collector
     * composed yet) or even signed out. These channels RETAIN the last value
     * until the signed-in app consumes it; consumption clears the slot so a
     * later recomposition never replays an already handled import.
     */
    private val _sharedQrResults = MutableStateFlow<QrImportResult?>(null)
    val sharedQrResults: Flow<QrImportResult> =
        _sharedQrResults.filterNotNull().onEach { _sharedQrResults.value = null }

    private val _receiptResults = MutableStateFlow<IncomingComprobante?>(null)
    val receiptResults: Flow<IncomingComprobante> =
        _receiptResults.filterNotNull().onEach { _receiptResults.value = null }

    private val _batchResults = MutableStateFlow<ImportBatch?>(null)
    val batchResults: Flow<ImportBatch> =
        _batchResults.filterNotNull().onEach { _batchResults.value = null }

    /** Import failures the user must see (unreadable/revoked shared URIs). */
    private val _importErrors = MutableStateFlow<String?>(null)
    val importErrors: Flow<String> =
        _importErrors.filterNotNull().onEach { _importErrors.value = null }

    fun initialize(compActivity: ComponentActivity) {
        this.activity = compActivity
        // T8 — S03: la captura con cámara ya no es `TakePicturePreview`
        // (cámara del sistema): es CameraX + análisis ZXing continuo dentro
        // de la app (CameraQrCaptureOverlay), con detección automática.
        gallery = compActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { decodeAndEmit(it) }
        }
        multiple = compActivity.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            decodeMultiple(uris)
        }
    }

    fun gallery() { gallery?.launch("image/*") }
    fun multiple() { multiple?.launch("image/*") }

    /**
     * Share entry point (ACTION_SEND / ACTION_SEND_MULTIPLE). Single files go
     * to exactly one semantic surface — S09 review for a QR, the receipt
     * dialog for a comprobante, S12 for multiples — instead of double-feeding
     * the batch screen plus a second review, which allowed the same file to
     * be saved twice.
     */
    fun handleShare(activity: Activity, intent: android.content.Intent?) {
        when (intent?.action) {
            android.content.Intent.ACTION_SEND -> {
                intent.getParcelableExtra<Uri>(android.content.Intent.EXTRA_STREAM)?.let { importSharedUri(it, activity) }
            }
            android.content.Intent.ACTION_SEND_MULTIPLE -> {
                intent.getParcelableArrayListExtra<Uri>(android.content.Intent.EXTRA_STREAM)?.let { decodeMultiple(it, activity) }
            }
        }
    }

    private fun importSharedUri(uri: Uri, sourceActivity: Activity) {
        scope.launch {
            // A shared URI can be unreadable (revoked grant, missing file,
            // storage restrictions); that must surface as a recoverable
            // error, never crash the process.
            val bytes = runCatching { readUriBytes(uri, sourceActivity) }.getOrNull()
            if (bytes == null) {
                _importErrors.value = "No se pudo abrir el archivo compartido."
                return@launch
            }
            val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
            val asset = decodeQrAsset(bytes, mime)
            if (asset != null) {
                _sharedQrResults.value = QrImportResult(listOf(asset))
            } else {
                _receiptResults.value = IncomingComprobante(bytes, mime, extensionFor(mime))
            }
        }
    }

    private fun readUriBytes(uri: Uri, sourceActivity: Activity): ByteArray? =
        sourceActivity.contentResolver.openInputStream(uri)?.use { it.readBytes() }

    private fun decodeMultiple(uris: List<Uri>) {
        activity?.let { decodeMultiple(uris, it) }
    }

    private fun decodeMultiple(uris: List<Uri>, sourceActivity: Activity) {
        scope.launch {
            val candidates = mutableListOf<ImportCandidate>()
            uris.forEach { uri ->
                // Unreadable URIs are isolated per candidate and never block
                // the valid ones (multi-import contract).
                val bytes = runCatching { readUriBytes(uri, sourceActivity) }.getOrNull() ?: return@forEach
                val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
                val fingerprint = sha256(bytes)
                val asset = decodeQrAsset(bytes, mime)
                if (asset != null) {
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
                    val payloadRef = "import-payload-$fingerprint"
                    payloadStore.put(payloadRef, bytes)
                    candidates += ImportCandidate(
                        id = "import-$fingerprint",
                        kind = classifyNonQr(mime),
                        fingerprint = fingerprint,
                        mimeType = mime,
                        extension = extension,
                        payloadRef = payloadRef,
                    )
                }
            }
            // Multi-import is a single surface (S12): recognized QRs and
            // pendings both live in the batch. Emitting the extra single-QR
            // channel here made a stale "Revisar QR" reappear after the batch
            // was already saved, offering a second save of the same file.
            if (candidates.isNotEmpty()) {
                _batchResults.value = ImportBatch(candidates)
            } else {
                _importErrors.value = "No se pudo abrir ninguno de los archivos compartidos."
            }
        }
    }

    private fun decodeAndEmit(uri: Uri) {
        activity?.let { decodeAndEmit(uri, it) }
    }

    private fun decodeAndEmit(uri: Uri, sourceActivity: Activity) {
        scope.launch {
            val bytes = runCatching { readUriBytes(uri, sourceActivity) }.getOrNull()
            if (bytes == null) {
                _importErrors.value = "No se pudo abrir el archivo seleccionado."
                return@launch
            }
            val mime = sourceActivity.contentResolver.getType(uri) ?: "image/png"
            val asset = decodeQrAsset(bytes, mime)
            if (asset != null) {
                _results.emit(QrImportResult(listOf(asset)))
            } else {
                // Single non-QR pick: the receipt dialog owns the flow and the
                // bytes travel with it, so no payload slot is kept behind.
                _receiptResults.value = IncomingComprobante(bytes, mime, extensionFor(mime))
            }
        }
    }

    private fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun extensionFor(mime: String): String = when (mime.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "application/pdf" -> "pdf"
        "image/webp" -> "webp"
        else -> "png"
    }

    private fun classifyNonQr(mime: String): ImportKind {
        // MIME alone cannot prove that an image/PDF is a receipt.
        // Keep it pending review until actual receipt recognition exists.
        return ImportKind.DESCONOCIDO
    }

}
