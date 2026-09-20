package com.agendaqr.core.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing

@Composable
actual fun XauxaQrPreview(encodedQr: String, modifier: Modifier) {
    val bytes = runCatching { Base64.decode(encodedQr, Base64.DEFAULT) }.getOrNull()
    val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }?.asImageBitmap()
    Box(
        modifier = modifier.size(XauxaMetrics.QrPreviewSize).border(XauxaMetrics.Border, XauxaColor.Border),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = "QR", modifier = Modifier.fillMaxSize())
        } else {
            Box(Modifier.fillMaxSize().background(XauxaColor.White))
        }
    }
}
