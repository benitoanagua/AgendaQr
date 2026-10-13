package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor

@Composable
actual fun AmQrPreview(encodedQr: String, modifier: Modifier, size: Dp) {
    // iOS QR preview decoding is deferred until hardware validation.
    // Keep composable compile-ready without UIImage interop.
    // Marco tonal (AmMediaFrame) sin borde de reposo; lienzo claro
    // garantizado para el escaneo en ambos temas (AmColor.QrCanvas).
    AmMediaFrame(modifier = modifier.size(size)) {
        Box(Modifier.fillMaxSize().background(AmColor.QrCanvas))
    }
}
