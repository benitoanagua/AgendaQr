package com.agendaqr.core.ui.components

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Decodifica bytes de imagen (PNG/JPEG/WebP) a un mapa de bits con
 * `BitmapFactory`. Devuelve `null` si los bytes no son una imagen válida;
 * el llamador ([decodeQrImageBitmap]) convierte ese null en el estado
 * explícito "sin vista previa".
 */
actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? {
    if (bytes.isEmpty()) return null
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
}
