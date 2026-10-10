package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics

@Composable
actual fun XauxaQrPreview(encodedQr: String, modifier: Modifier) {
    // iOS QR preview decoding is deferred until hardware validation.
    // Keep composable compile-ready without UIImage interop.
    // M9: marco tonal (XauxaMediaFrame) sin borde de reposo; lienzo claro
    // garantizado para el escaneo en ambos temas (XauxaColor.QrCanvas).
    XauxaMediaFrame(modifier = modifier.size(XauxaMetrics.QrPreviewSize)) {
        Box(Modifier.fillMaxSize().background(XauxaColor.QrCanvas))
    }
}
