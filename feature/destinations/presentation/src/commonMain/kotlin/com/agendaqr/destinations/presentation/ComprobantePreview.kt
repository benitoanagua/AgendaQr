package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
