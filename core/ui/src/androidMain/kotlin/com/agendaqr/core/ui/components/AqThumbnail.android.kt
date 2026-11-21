package com.agendaqr.core.ui.components

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.ui.graphics.asImageBitmap
import com.agendaqr.core.ui.theme.AqMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import kotlin.math.roundToInt

private const val MinTargetPx = 96

/** Caché en memoria acotada por ruta de archivo: evita redecodificar al hacer scroll. */
private val ThumbnailCache: LruCache<String, androidx.compose.ui.graphics.ImageBitmap> = LruCache(48)

actual suspend fun decodeThumbnailBitmap(file: String): androidx.compose.ui.graphics.ImageBitmap? =
    withContext(Dispatchers.Default) {
        try {
            ThumbnailCache.get(file)?.let { return@withContext it }
            val path = File(URI(file)).absolutePath
            // Primera pasada: solo dimensiones.
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds) ?: return@withContext null
            // inSampleSize contra el tamaño real en px de la miniatura (densidad),
            // no contra un px fijo.
            val scale = Resources.getSystem().displayMetrics.density.coerceAtLeast(1f)
            val targetPx = (AqMetrics.ReceiptThumbnailSize.value * scale).roundToInt().coerceAtLeast(MinTargetPx)
            val sample = computeInSampleSize(bounds.outWidth, bounds.outHeight, targetPx, targetPx)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            BitmapFactory.decodeFile(path, options)?.let { bitmap ->
                val imageBitmap = bitmap.asImageBitmap()
                ThumbnailCache.put(file, imageBitmap)
                return@withContext imageBitmap
            } ?: return@withContext null
        } catch (_: Exception) {
            null
        }
    }

private fun computeInSampleSize(
    width: Int,
    height: Int,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize.coerceAtLeast(1)
}
