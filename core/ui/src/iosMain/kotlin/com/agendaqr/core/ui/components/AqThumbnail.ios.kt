package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.FilterMipmap
import org.jetbrains.skia.FilterMode
import org.jetbrains.skia.Image
import org.jetbrains.skia.MipmapMode
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

private const val MinTargetPx = 96

/**
 * iOS: miniatura de un archivo local, fuera del hilo principal
 * ([Dispatchers.Default]). Mismo contrato que Android: `null` ⇒ estado
 * Failure (fallback semántico); nunca lanza.
 *
 * Pipeline: `file://` o ruta → `NSData.dataWithContentsOfFile` → bytes →
 * `Image.makeFromEncoded` (Skia: JPG/PNG/WebP/GIF/BMP) → reescalado a
 * [MinTargetPx] px de lado mayor con filtro lineal → `ImageBitmap`.
 * Se usan los mismos patrones de interop ya presentes en el repo
 * (`PlatformComprobanteFileStore.ios.kt`, `AqQrPreview.ios.kt`) en lugar de
 * `ImageIO`, cuyo puente `CFURLRef`/`CFDictionaryRef` no admite `NSURL`
 * ni `Map` de Kotlin directamente.
 *
 * Un PDF o una ruta inválida no son decodificables por Skia ⇒ `null`
 * (igual que `BitmapFactory` en Android).
 *
 * ## Estado de validación
 * NO VALIDADO en ejecución: no hay macOS/Xcode ni simulador en CI. Checklist
 * manual en simulador (comprobante en bandeja "sin asociar"):
 *  1. JPG grande (≥ 12 MP) → miniatura nítida, sin lag al hacer scroll.
 *  2. PNG con transparencia → miniatura sin fondo negro.
 *  3. WebP → miniatura (si Skia no lo decodifica, Failure con ícono QR).
 *  4. PDF → Failure con ícono Attachment (`aq-thumbnail-fallback-attachment`).
 *  5. Ruta inexistente / archivo vacío / `file://` mal formado → Failure,
 *     sin crash ni log de excepción sin capturar.
 *  6. Valor recibido con y sin prefijo `file://` → ambos resuelven.
 */
@OptIn(ExperimentalForeignApi::class)
actual suspend fun decodeThumbnailBitmap(file: String): ImageBitmap? = withContext(Dispatchers.Default) {
    try {
        val path = file.removePrefix("file://")
        val data = NSData.dataWithContentsOfFile(path) ?: return@withContext null
        val length = data.length.toInt()
        if (length == 0) return@withContext null
        val bytes = ByteArray(length)
        bytes.usePinned { pinned -> memcpy(pinned.addressOf(0), data.bytes, data.length) }

        val source = Image.makeFromEncoded(bytes)
        if (source.width <= 0 || source.height <= 0) return@withContext null
        val (w, h) = thumbnailScaledSize(source.width, source.height, MinTargetPx)
        if (w == source.width && h == source.height) return@withContext source.toComposeImageBitmap()
        val surface = Surface.makeRasterN32Premul(w, h)
        surface.canvas.drawImageRect(
            source,
            Rect.makeWH(source.width.toFloat(), source.height.toFloat()),
            Rect.makeWH(w.toFloat(), h.toFloat()),
            FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),
            null,
            true,
        )
        surface.makeImageSnapshot().toComposeImageBitmap()
    } catch (_: Throwable) {
        null
    }
}
