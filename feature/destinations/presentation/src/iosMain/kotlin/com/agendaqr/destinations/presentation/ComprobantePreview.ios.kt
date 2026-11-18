package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.AqMediaFrame
import com.agendaqr.core.ui.components.AqText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    // La decodificación de imágenes en iOS queda diferida hasta validación
    // en hardware. Marcador honesto, igual que AqQrPreview en iOS.
    // M9: marco tonal (AqMediaFrame, core:ui) sin borde de reposo —
    // los bordes pertenecen al design system, no a las pantallas.
    AqMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(min = AqMetrics.QrPreviewSize),
    ) {
        Box(Modifier.fillMaxSize()) {
            AqText(
                AppStrings.ComprobanteAdjuntoVistaPreviaPendienteEnIos,
                size = AqType.Label,
                color = AqColor.TextSecondary,
            )
        }
    }
}
