package com.agendaqr.destinations.presentation

import android.graphics.BitmapFactory
import com.agendaqr.core.ui.components.AmMediaFrame
import com.agendaqr.core.ui.components.AmText
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
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    val bitmap = remember(bytes) {
        runCatching {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
    // M9: marco tonal (AmMediaFrame, core:ui) — el límite es el bloque
    // tonal Surface (los bordes viven solo en E-01/E-02 del gate).
    AmMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(min = AmMetrics.QrPreviewSize),
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
                AmText(
                    if (mimeType?.contains("pdf", ignoreCase = true) == true) AppStrings.PdfAdjuntoSinVistaPrevia
                    else AppStrings.VistaPreviaNoDisponible,
                    size = AmType.Label,
                    color = com.agendaqr.core.ui.theme.AmColor.TextSecondary,
                )
            }
        }
    }
}
