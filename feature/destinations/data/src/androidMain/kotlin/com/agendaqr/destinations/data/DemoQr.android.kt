package com.agendaqr.destinations.data

import android.graphics.Bitmap
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

/**
 * QR real en Android: mismo pipeline que el fixture de pruebas y la
 * importación (ZXing → ARGB → PNG → Base64), 480 px con zona de silencio.
 */
actual fun demoQrPngBase64(content: String): String {
    val sizePx = 480
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(sizePx * sizePx) { index ->
        if (matrix.get(index % sizePx, index / sizePx)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
    }
    bitmap.setPixels(pixels, 0, sizePx, 0, 0, sizePx, sizePx)
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
}
