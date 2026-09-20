package com.agendaqr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun XauxaQrPreview(encodedQr: String, modifier: Modifier) {
    val image = runCatching {
        val data = NSData.create(base64EncodedString = encodedQr, options = 0u)
        data?.let { UIImage(data = it) }
    }.getOrNull()
    Box(
        modifier = modifier.size(XauxaMetrics.QrPreviewSize).border(XauxaMetrics.Border, XauxaColor.Border),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            Image(bitmap = image.toComposeImageBitmap(), contentDescription = "QR", modifier = Modifier.fillMaxSize())
        } else {
            Box(Modifier.fillMaxSize().background(XauxaColor.White))
        }
    }
}
