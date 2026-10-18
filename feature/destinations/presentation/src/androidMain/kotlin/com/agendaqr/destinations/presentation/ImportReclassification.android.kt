package com.agendaqr.destinations.presentation

import android.util.Base64
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset

actual suspend fun reclassifyImportCandidate(
    candidate: ImportCandidate,
    bytes: ByteArray,
): ImportCandidate {
    // La clasificación no-QR es deliberadamente conservadora (Android: QR
    // demostrado por decodificación ZXing; el resto permanece pendiente de
    // revisión), igual que en la importación original.
    val mime = candidate.mimeType ?: "image/png"
    val bitmap = runCatching {
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull() ?: return candidate
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = com.google.zxing.RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
    val binary = com.google.zxing.BinaryBitmap(com.google.zxing.common.HybridBinarizer(source))
    val decoded = try {
        com.google.zxing.MultiFormatReader().decode(binary)
        true
    } catch (_: com.google.zxing.ReaderException) {
        false
    } finally {
        bitmap.recycle()
    }
    return if (decoded) {
        candidate.copy(
            kind = ImportKind.QR,
            qrAsset = QrAsset(encoded = Base64.encodeToString(bytes, Base64.NO_WRAP), mimeType = mime),
        )
    } else {
        candidate
    }
}
