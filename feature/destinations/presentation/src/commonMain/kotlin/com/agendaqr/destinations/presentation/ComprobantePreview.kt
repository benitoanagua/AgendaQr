package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Muestra el contenido de un comprobante ya cargado en memoria.
 * Las imágenes se decodifican por target; Android e iOS renderizan PDF con
 * sus APIs nativas, manteniendo un fallback textual si el documento falla.
 */
@Composable
expect fun ComprobantePreview(
    bytes: ByteArray,
    mimeType: String?,
    modifier: Modifier = Modifier,
)

