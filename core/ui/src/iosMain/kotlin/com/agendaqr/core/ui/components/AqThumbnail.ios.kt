package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGImageSourceCreateThumbnailAtIndex
import platform.CoreGraphics.CGImageSourceCreateWithURL
import platform.CoreGraphics.kCGImageSourceCreateThumbnailFromImageAlways
import platform.CoreGraphics.kCGImageSourceThumbnailMaxPixelSize
import platform.Foundation.NSURL
import platform.UIKit.UIImage

private const val MinTargetPx = 96

/**
 * iOS: decodificación de miniatura con ImageIO downsampling.
 *
 * Usa `CGImageSourceCreateThumbnailAtIndex` con
 * `kCGImageSourceThumbnailMaxPixelSize` (máximo 96 px de lado) y
 * `kCGImageSourceCreateThumbnailFromImageAlways = true` para forzar la
 * creación de la miniatura aunque el archivo no incruste una, fuera del
 * hilo principal ([Dispatchers.Default]).
 *
 * @note NO VALIDADO en este entorno: no hay macOS/Xcode ni simulador iOS
 *   para compilar el target `iosMain` ni ejecutar pruebas. El código sigue
 *   los bindings de Kotlin/Native y el mismo contrato de estados que
 *   Android ([Loading], [Success], [Failure]), pero la validación real
 *   queda pendiente de hardware (ver reporte final, Etapa 2).
 */
actual suspend fun decodeThumbnailBitmap(file: String): ImageBitmap? = withContext(Dispatchers.Default) {
    try {
        val url = NSURL.fileURLWithPath(file)
        val source = CGImageSourceCreateWithURL(url, null) ?: return@withContext null
        val options = mapOf<String, Any>(
            kCGImageSourceThumbnailMaxPixelSize to MinTargetPx,
            kCGImageSourceCreateThumbnailFromImageAlways to true,
        )
        val cgImage = CGImageSourceCreateThumbnailAtIndex(source, 0, options) ?: return@withContext null
        return@withContext UIImage(cgImage)?.toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
}
