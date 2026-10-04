package com.agendaqr.destinations.presentation

import androidx.compose.foundation.border
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    // La decodificación de imágenes en iOS queda diferida hasta validación
    // en hardware. Marcador honesto, igual que XauxaQrPreview en iOS.
    Box(
        modifier = modifier.fillMaxWidth().heightIn(min = XauxaMetrics.QrPreviewSize).border(XauxaMetrics.Border, XauxaColor.Border),
        contentAlignment = Alignment.Center,
    ) {
        XauxaText("Comprobante adjunto (vista previa pendiente en iOS)", size = XauxaType.Label, color = XauxaColor.TextSecondary)
    }
}
