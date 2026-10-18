package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

/**
 * Decodifica bytes de imagen (PNG/JPEG/WebP) a un mapa de bits con Skia.
 * Mismo contrato que la implementación Android: los bytes almacenados en el
 * dominio se interpretan como IMAGEN, nunca como texto para regenerar un QR.
 * Devuelve `null` si los bytes no son una imagen decodificable.
 */
actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? {
    if (bytes.isEmpty()) return null
    return runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
}
