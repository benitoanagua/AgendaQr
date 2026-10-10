package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.XauxaMediaFrame
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    // La decodificación de imágenes en iOS queda diferida hasta validación
    // en hardware. Marcador honesto, igual que XauxaQrPreview en iOS.
    // M9: marco tonal (XauxaMediaFrame, core:ui) sin borde de reposo —
    // los bordes pertenecen al design system, no a las pantallas.
    XauxaMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(min = XauxaMetrics.QrPreviewSize),
    ) {
        Box(Modifier.fillMaxSize()) {
            XauxaText(
                AppStrings.ComprobanteAdjuntoVistaPreviaPendienteEnIos,
                size = XauxaType.Label,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}
