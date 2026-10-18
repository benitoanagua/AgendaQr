package com.agendaqr.destinations.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

/**
 * Fixture de QR REAL para capturas y pruebas.
 *
 * El dominio ([com.agendaqr.destinations.domain.QrAsset]) almacena BYTES DE
 * IMAGEN codificados en Base64. Este fixture produce exactamente eso: un PNG
 * legible generado con el codificador del proyecto (`com.google.zxing`, el
 * mismo que usa la importación y la cámara), **no** una imagen ficticia.
 *
 * Toda captura que muestre un QR lo mostrará como un código válido y
 * legible; [decodeQrPngBase64] permite además verificar la lectura con el
 * decodificador disponible en el proyecto.
 */
object QrTestFixture {

    const val CONTENT = "https://agendaqr.app/d-1"

    private const val SIZE_PX = 480

    /** PNG Base64 de un QR que codifica [CONTENT] (con zona de silencio). */
    val base64Png: String by lazy { encodeQrPngBase64(CONTENT) }

    /** Píxeles ARGB (negro sobre blanco) de un QR real. */
    fun argbPixels(content: String, sizePx: Int = SIZE_PX): IntArray {
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val pixels = IntArray(matrix.width * matrix.height)
        for (y in 0 until matrix.height) {
            for (x in 0 until matrix.width) {
                pixels[y * matrix.width + x] =
                    if (matrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        return pixels
    }

    /** PNG Base64 del QR de [content], con el mismo pipeline que la app. */
    fun encodeQrPngBase64(content: String, sizePx: Int = SIZE_PX): String {
        val pixels = argbPixels(content, sizePx)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, sizePx, 0, 0, sizePx, sizePx)
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    /** Decodifica un PNG Base64 con el lector ZXing; `null` si no es legible. */
    fun decodeQrPngBase64(encoded: String): String? {
        val bytes = runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull() ?: return null
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        return decodeBitmap(bitmap)
    }

    /** Decodifica un mapa de bits Android con el lector ZXing. */
    fun decodeBitmap(bitmap: Bitmap): String? {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return QrFrameAnalyzer.decodeArgb(pixels, bitmap.width, bitmap.height)
    }
}
