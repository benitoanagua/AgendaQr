package com.agendaqr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Resultado de la decodificación diferida de una miniatura de comprobante.
 *
 * - [Success]: [bitmap] con la imagen decodada del archivo.
 * - [Failure]: no hay vista previa decodable (archivo ausente, formato no
 *   compatible o fallo de decodificación). Se muestra un ícono semántico:
 *   [AqIcons.QrCode] para imágenes y [AqIcons.Attachment] para PDF/archivos.
 * - [Loading]: se está decodando el archivo. Nunca se muestra un ícono: se
 *   usa un esqueleto estático ([AqThumbnailSkeleton]), respetando
 *   «reducir movimiento» por construcción (no hay animación continua).
 */
data class ThumbnailResult(
    val state: ThumbnailState,
    val bitmap: ImageBitmap? = null,
)

enum class ThumbnailState { Loading, Success, Failure }

/**
 * Tamaño cuadrado de la miniatura de comprobante en filas de lista.
 * Coherente en Android e iOS: [AqMetrics.ReceiptThumbnailSize] (56 dp).
 */
val ThumbnailSize: Dp = AqMetrics.ReceiptThumbnailSize

/**
 * Decodifica una miniatura ligera de un archivo de disco, fuera del hilo
 * principal. Implementación por plataforma:
 * - Android: `BitmapFactory` con `inSampleSize` contra el tamaño real en px
 *   (densidad), `ARGB_8888` y caché en memoria acotada ([LruCache]).
 * - iOS: `ImageIO` downsampling (`CGImageSourceCreateThumbnailAtIndex`).
 * Devuelve `null` cuando el archivo no existe o no es una imagen
 * decodificable.
 */
expect suspend fun decodeThumbnailBitmap(file: String): ImageBitmap?

/**
 * Marco de miniatura compartido por Android, iOS, vistas de detalle y
 * previsualizaciones: superficie [AqColor.SurfaceContainerHighest], radio
 * [AqShapes.Medium], contenido centrado. Los valores visuales son 100 %
 * tokens Polaris; sin valores propios.
 */
@Composable
fun AqThumbnailFrame(
    modifier: Modifier = Modifier,
    size: Dp = ThumbnailSize,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.size(size).background(AqColor.SurfaceContainerHighest, AqShapes.Medium),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Ícono de fallback para miniatura sin vista previa. Imagen → [AqIcons.QrCode]
 * (decisión de producto: el comprobante sin miniatura se representa con el
 * glifo de QR, nunca con el glifo «ALT»); PDF / archivo →
 * [AqIcons.Attachment]. Tinte [AqColor.TextTertiary], tamaño
 * [AqMetrics.IconSizeCompact].
 *
 * El ícono lleva un `testTag` estable (`aq-thumbnail-fallback-qr` /
 * `aq-thumbnail-fallback-attachment`) para probar el estado sin inspeccionar
 * el vector.
 */
@Composable
fun AqThumbnailFallback(
    isPdf: Boolean = false,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val imageVector = if (isPdf) AqIcons.Attachment else AqIcons.QrCode
    val tag = if (isPdf) "aq-thumbnail-fallback-attachment" else "aq-thumbnail-fallback-qr"
    Box(modifier = modifier.testTag(tag), contentAlignment = Alignment.Center) {
        AqIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            size = AqMetrics.IconSizeCompact,
            tint = AqColor.TextTertiary,
        )
    }
}

/**
 * Esqueleto de carga de la miniatura. Bloque estático sin movimiento
 * decorativo continuo: respeta «reducir movimiento» por construcción (nunca
 * anima). Fondo [AqColor.Surface] — distinto del marco
 * [SurfaceContainerHighest] en ambos temas, por lo que la carga se percibe
 * también en tema claro.
 */
@Composable
fun AqThumbnailSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(AqColor.Surface, AqShapes.Medium),
    )
}

/**
 * Renderiza el resultado de una miniatura de comprobante: imagen recortada
 * ([ContentScale.Crop]), ícono de fallback (sin vista previa) o esqueleto
 * (carga). Idéntico en Android e iOS.
 *
 * [contentDescription] solo se anuncia en estado [Failure] (el ícono es el
 * único indicio de estado). En [Success] la imagen es decorativa: el
 * título de la fila ya describe el archivo, por lo que va en `null`.
 */
@Composable
fun AqThumbnail(
    result: ThumbnailResult,
    mimeType: String?,
    modifier: Modifier = Modifier,
    size: Dp = ThumbnailSize,
    contentDescription: String? = null,
) {
    AqThumbnailFrame(modifier = modifier, size = size) {
        when (result.state) {
            ThumbnailState.Success -> {
                result.bitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(AqShapes.Medium),
                    )
                }
            }
            ThumbnailState.Failure -> {
                val isPdf = mimeType?.contains("pdf", ignoreCase = true) == true
                AqThumbnailFallback(
                    isPdf = isPdf,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            ThumbnailState.Loading -> {
                AqThumbnailSkeleton(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * Miniatura real del comprobante guardado en disco (implementación común).
 *
 * - Imágenes compatibles (JPG/PNG/WebP): miniatura decodada con muestreo,
 *   recortada sin deformar.
 * - PDF y archivos no visualizables: ícono de tipo de archivo.
 * - Error / sin vista previa: ícono semántico (QR para imagen, adjunto
 *   para PDF), nunca el glifo «ALT».
 * - Carga: esqueleto estático, nunca un ícono.
 *
 * El archivo se lee de forma diferida y ligera, sin cargar el contenido
 * completo en memoria. La decodificación se cancela al salir de la
 * composición ([LaunchedEffect] por [file]) y no hay fugas de [ImageBitmap]:
 * la caché de Android recicla los bitmaps al desalojarlos.
 */
@Composable
fun ComprobanteThumbnail(
    file: String,
    mimeType: String?,
    modifier: Modifier = Modifier,
    size: Dp = ThumbnailSize,
    /**
     * Descripción de accesibilidad del estado de fallo (cuando el ícono de
     * fallback es el único indicio de estado). El llamador de `feature/`
     * la inyecta según el mimeType (p. ej. «Vista previa no disponible» /
     * «PDF adjunto (sin vista previa)»).
     */
    contentDescription: String? = null,
) {
    val state = remember { mutableStateOf<ThumbnailResult>(ThumbnailResult(ThumbnailState.Loading)) }
    LaunchedEffect(file) {
        state.value = ThumbnailResult(ThumbnailState.Loading)
        val bitmap = withContext(Dispatchers.Default) { decodeThumbnailBitmap(file) }
        state.value = ThumbnailResult(
            state = if (bitmap == null) ThumbnailState.Failure else ThumbnailState.Success,
            bitmap = bitmap,
        )
    }
    AqThumbnail(state.value, mimeType, modifier, size, contentDescription)
}
