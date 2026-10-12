package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.AmMediaFrame
import com.agendaqr.core.ui.components.AmText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    // La decodificación de imágenes en iOS queda diferida hasta validación
    // en hardware. Marcador honesto, igual que AmQrPreview en iOS.
    // M9: marco tonal (AmMediaFrame, core:ui) sin borde de reposo —
    // los bordes pertenecen al design system, no a las pantallas.
    AmMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(min = AmMetrics.QrPreviewSize),
    ) {
        Box(Modifier.fillMaxSize()) {
            AmText(
                AppStrings.ComprobanteAdjuntoVistaPreviaPendienteEnIos,
                size = AmType.Label,
                color = AmColor.TextSecondary,
            )
        }
    }
}
