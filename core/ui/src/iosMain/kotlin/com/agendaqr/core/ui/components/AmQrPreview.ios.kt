package com.agendaqr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import platform.CoreImage.CIFilter
import platform.CoreImage.CIImage
import platform.CoreImage.CIContext
import platform.Foundation.NSData
import platform.Foundation.NSString
import kotlinx.cinterop.ptr

@Composable
actual fun AmQrPreview(encodedQr: String, modifier: Modifier, size: Dp) {
    // Generar QR code usando Core Image desde el string base64
    // Primero decodificar el base64 para obtener el string original
    val bytes = platform.Foundation.NSData.dataWithBase64EncodedString(
        encodedQr,
        platform.Foundation.NSData.Base64DecodingOptions(0)
    )?.bytes?.toByteArray()
    
    val qrString = bytes?.let { String(it) } ?: encodedQr
    
    // Generar QR code usando Core Image
    val string = qrString
    val data = NSString(string).data(NSString.defaultCStringEncoding)
    val filter = CIFilter.filterWithName("CIQRCodeGenerator")
    filter.setValue(data, forKey: "inputMessage")
    filter.setValue("H", forKey: "inputCorrectionLevel")
    
    var outputImage: CIImage? = filter.outputImage
    if (outputImage != null) {
        // Escalar la imagen para que sea más nítida
        val scale = 4.0
        outputImage = outputImage.transformedBy(
            platform.CoreGraphics.CGAffineTransformMakeScale(scale, scale)
        )
    }
    
    val context = CIContext()
    var bitmap: ImageBitmap? = null
    
    if (outputImage != null) {
        val cgImage = context.createCGImage(outputImage!!, outputImage!!.extent())
        if (cgImage != null) {
            bitmap = cgImage!!.asImageBitmap()
        }
    }
    
    AmMediaFrame(modifier = modifier.size(size)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = "QR",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AmSpacing.Md)
                    .background(AmColor.QrCanvas),
            )
        } else {
            AmIcon(
                imageVector = AmIcons.QrCode,
                contentDescription = null,
                tint = AmColor.TextTertiary,
                size = AmMetrics.EmptyStateGlyphSize,
            )
        }
    }
}
