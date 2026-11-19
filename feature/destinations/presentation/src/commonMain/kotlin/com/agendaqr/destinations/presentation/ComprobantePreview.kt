package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqMetrics

/**
 * Muestra el contenido de un comprobante ya cargado en memoria.
 * Android decodifica imágenes; otros targets muestran un marcador honesto.
 */
@Composable
expect fun ComprobantePreview(
    bytes: ByteArray,
    mimeType: String?,
    modifier: Modifier = Modifier,
)

/**
 * Miniatura real de un comprobante guardado en disco.
 *
 * - Imágenes (JPG/PNG/WebP): muestra una miniatura decodada del archivo,
 *   recortada y sin deformar.
 * - PDF: representación de documento.
 * - Otros / error: icono de tipo de archivo.
 *
 * El archivo se lee de forma diferida y ligera (muestreo), sin cargar el
 * contenido completo en memoria. No modifica la identificación ni el
 * almacenamiento del comprobante.
 */
@Composable
expect fun ComprobanteThumbnail(
    file: String,
    mimeType: String?,
    modifier: Modifier = Modifier,
    size: Dp = AqMetrics.ReceiptThumbnailSize,
)
