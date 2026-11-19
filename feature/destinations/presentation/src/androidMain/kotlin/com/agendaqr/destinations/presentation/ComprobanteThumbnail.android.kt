package com.agendaqr.destinations.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI

private const val ThumbnailTargetPx = 96

internal enum class ThumbnailState {
    Loading, Success, Failure
}

internal data class ThumbnailResult(
    val state: ThumbnailState,
    val bitmap: Bitmap? = null,
)

/**
 * Decodifica una miniatura ligera del archivo de comprobante sin cargar el
 * contenido completo en memoria. Usa [BitmapFactory.Options.inSampleSize]
 * para acercarse al tamaño objetivo antes de asignar el bitmap.
 */
private suspend fun decodeThumbnail(file: String): Bitmap? = withContext(Dispatchers.Default) {
    try {
        val path = File(URI(file)).absolutePath
        // Primera pasada: solo dimensiones.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds) ?: return@withContext null
        val sample = computeInSampleSize(bounds.outWidth, bounds.outHeight, ThumbnailTargetPx, ThumbnailTargetPx)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        BitmapFactory.decodeFile(path, options)
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

/**
 * Componente con estado que carga y muestra la miniatura del comprobante.
 */
@Composable
private fun ComprobanteThumbnailLoader(
    file: String,
    mimeType: String?,
    modifier: Modifier,
    size: Dp,
) {
    val state = remember {
        mutableStateOf<ThumbnailResult>(ThumbnailResult(ThumbnailState.Loading))
    }
    LaunchedEffect(file) {
        state.value = ThumbnailResult(ThumbnailState.Loading)
        val bitmap = decodeThumbnail(file)
        state.value = ThumbnailResult(
            state = if (bitmap == null) ThumbnailState.Failure else ThumbnailState.Success,
            bitmap = bitmap,
        )
    }
    val result = state.value
    Box(modifier = modifier.size(size)) {
        when (result.state) {
            ThumbnailState.Success -> {
                result.bitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(AqShapes.Medium),
                    )
                }
            }
            ThumbnailState.Failure -> {
                fallbackBox(mimeType)
            }
            ThumbnailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AqColor.SurfaceContainerHighest, AqShapes.Medium),
                    contentAlignment = Alignment.Center,
                ) {
                    AqIcon(
                        imageVector = AqIcons.ImageOff,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = AqColor.TextTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun fallbackBox(mimeType: String?) {
    val isPdf = mimeType?.contains("pdf", ignoreCase = true) == true
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AqColor.SurfaceContainerHighest, AqShapes.Medium),
        contentAlignment = Alignment.Center,
    ) {
        AqIcon(
            imageVector = if (isPdf) AqIcons.Attachment else AqIcons.ImageOff,
            contentDescription = null,
            size = AqMetrics.IconSizeCompact,
            tint = AqColor.TextTertiary,
        )
    }
}

/**
 * Miniatura real del comprobante (implementación Android).
 *
 * - Imágenes compatibles (JPG/PNG/WebP): bitmap decodado con muestreo.
 * - PDF y archivos no visualizables: ícono de tipo de archivo.
 * - Error / sin vista previa: ícono de fallback.
 */
@Composable
actual fun ComprobanteThumbnail(
    file: String,
    mimeType: String?,
    modifier: Modifier,
    size: Dp,
) {
    ComprobanteThumbnailLoader(file, mimeType, modifier, size)
}
