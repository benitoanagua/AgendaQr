package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics

@Composable
actual fun AmQrPreview(encodedQr: String, modifier: Modifier, size: Dp) {
    // iOS QR preview decoding is deferred until hardware validation.
    // Keep composable compile-ready without UIImage interop.
    // Hasta que exista decodificación: estado explícito (glifo sobre el
    // marco tonal), NUNCA un rectángulo blanco sin contexto.
    AmMediaFrame(modifier = modifier.size(size)) {
        AmIcon(
            imageVector = AmIcons.QrCode,
            contentDescription = null,
            tint = AmColor.TextTertiary,
            size = AmMetrics.EmptyStateGlyphSize,
        )
    }
}
