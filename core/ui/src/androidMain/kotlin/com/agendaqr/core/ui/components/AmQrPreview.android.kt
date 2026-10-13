package com.agendaqr.core.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics

@Composable
actual fun AmQrPreview(encodedQr: String, modifier: Modifier, size: Dp) {
    val bytes = runCatching { Base64.decode(encodedQr, Base64.DEFAULT) }.getOrNull()
    val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }?.asImageBitmap()
    // Marco tonal (AmMediaFrame) SIN borde de reposo: el límite del
    // contenido es el bloque tonal.
    AmMediaFrame(modifier = modifier.size(size)) {
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = "QR", modifier = Modifier.fillMaxSize())
        } else {
            // Lienzo claro garantizado: el QR necesita zona clara para ser
            // escaneable en ambos temas (AmColor.QrCanvas).
            Box(Modifier.fillMaxSize().background(AmColor.QrCanvas))
        }
    }
}
