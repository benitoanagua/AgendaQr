package com.agendaqr.destinations.presentation

import android.graphics.BitmapFactory
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaMediaFrame
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    val bitmap = remember(bytes) {
        runCatching {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
    // M9: marco tonal (XauxaMediaFrame, core:ui) SIN borde de reposo — el
    // borde histórico de 1 dp no cumplía el límite perceptible y vivía en
    // feature/ (los bordes pertenecen al design system, no a las pantallas).
    XauxaMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(min = XauxaMetrics.QrPreviewSize),
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = AppStrings.Comprobante,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        } else {
            Box(Modifier.fillMaxSize()) {
                XauxaText(
                    if (mimeType?.contains("pdf", ignoreCase = true) == true) AppStrings.PdfAdjuntoSinVistaPrevia
                    else AppStrings.VistaPreviaNoDisponible,
                    size = XauxaType.Label,
                    color = com.agendaqr.core.ui.theme.XauxaColor.TextSecondary,
                )
            }
        }
    }
}
