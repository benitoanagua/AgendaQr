package com.agendaqr.core.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing

@Composable
actual fun AmQrPreview(encodedQr: String, modifier: Modifier, size: Dp) {
    val bytes = runCatching { Base64.decode(encodedQr, Base64.DEFAULT) }.getOrNull()
    val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }?.asImageBitmap()
    // Marco tonal (AmMediaFrame) SIN borde de reposo: el límite del
    // contenido es el bloque tonal.
    AmMediaFrame(modifier = modifier.size(size)) {
        if (bitmap != null) {
            // Lienzo claro garantizado tras la imagen: el QR necesita zona
            // clara para ser escaneable en ambos temas (AmColor.QrCanvas),
            // con aire interno para que el marco tonal se lea como marco.
            Image(
                bitmap = bitmap,
                contentDescription = "QR",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AmSpacing.Md)
                    .background(AmColor.QrCanvas),
            )
        } else {
            // Decodificación fallida o sin contenido: estado explícito
            // (glifo sobre el tono del marco), NUNCA un rectángulo blanco
            // sin contexto.
            AmIcon(
                imageVector = AmIcons.QrCode,
                contentDescription = null,
                tint = AmColor.TextTertiary,
                size = AmMetrics.EmptyStateGlyphSize,
            )
        }
    }
}
