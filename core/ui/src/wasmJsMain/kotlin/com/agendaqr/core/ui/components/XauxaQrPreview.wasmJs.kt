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
    // Web QR preview decoding is deferred: there is no browser decoding path
    // yet, so this target honestly renders the placeholder instead of faking
    // a decoded QR. The catalog records the web render as PENDING.
    // M9: marco tonal (XauxaMediaFrame) sin borde de reposo; lienzo claro
    // garantizado para el escaneo en ambos temas (XauxaColor.QrCanvas).
    XauxaMediaFrame(modifier = modifier.size(XauxaMetrics.QrPreviewSize)) {
        Box(Modifier.fillMaxSize().background(XauxaColor.QrCanvas))
    }
}
