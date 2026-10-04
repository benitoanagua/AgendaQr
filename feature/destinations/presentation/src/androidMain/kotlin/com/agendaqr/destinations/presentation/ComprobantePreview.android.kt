package com.agendaqr.destinations.presentation

import android.graphics.BitmapFactory
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaType

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    val bitmap = remember(bytes) {
        runCatching {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
    Box(
        modifier = modifier.fillMaxWidth().heightIn(min = XauxaMetrics.QrPreviewSize).border(XauxaMetrics.Border, XauxaColor.Border),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Comprobante",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        } else {
            XauxaText(
                if (mimeType?.contains("pdf", ignoreCase = true) == true) "PDF adjunto (sin vista previa)"
                else "Vista previa no disponible",
                size = XauxaType.Label,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}
