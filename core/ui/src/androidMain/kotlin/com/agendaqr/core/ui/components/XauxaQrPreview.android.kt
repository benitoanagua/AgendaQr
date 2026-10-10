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
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics

@Composable
actual fun XauxaQrPreview(encodedQr: String, modifier: Modifier) {
    val bytes = runCatching { Base64.decode(encodedQr, Base64.DEFAULT) }.getOrNull()
    val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }?.asImageBitmap()
    // M9: marco tonal (XauxaMediaFrame) SIN borde de reposo — el borde
    // histórico de 1 dp (XauxaColor.Border, 1,88:1 en claro) no cumplía el
    // límite perceptible del control.
    XauxaMediaFrame(modifier = modifier.size(XauxaMetrics.QrPreviewSize)) {
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = "QR", modifier = Modifier.fillMaxSize())
        } else {
            // Lienzo claro garantizado: el QR necesita zona clara para ser
            // escaneable en ambos temas (XauxaColor.QrCanvas).
            Box(Modifier.fillMaxSize().background(XauxaColor.QrCanvas))
        }
    }
}
