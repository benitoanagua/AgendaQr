package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Vista previa de un código QR a partir de su representación codificada.
 * El decodificado es por plataforma (Android: BitmapFactory; iOS:
 * pendiente de validación de hardware).
 */
@Composable
expect fun AmQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
)
