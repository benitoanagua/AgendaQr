package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmMetrics

/**
 * Vista previa de un código QR a partir de su representación codificada.
 * El decodificado es por plataforma (Android: BitmapFactory; iOS:
 * pendiente de validación de hardware).
 */
@Composable
expect fun AmQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
    /** Lado del marco cuadrado. El QR conserva proporción 1:1 (aspecto
     * garantizado por el decodificador/imagen); nunca se estira. */
    size: Dp = AmMetrics.QrPreviewSize,
)
