package com.agendaqr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import kotlin.io.encoding.Base64

/**
 * Decodifica los BYTES DE IMAGEN de una plataforma a un mapa de bits.
 *
 * Android usa `BitmapFactory`; iOS usa Skia (`Image.makeFromEncoded`).
 * Ninguna plataforma vuelve a generar un QR a partir de texto: el dominio
 * ([com.agendaqr.destinations.domain.QrAsset.encoded]) declara almacenar
 * "base64 encoded image bytes" y ambas interpretan esos bytes como imagen.
 */
expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap?

/**
 * Decodifica la representación Base64 del QR almacenado en el dominio a un
 * mapa de bits. Devuelve `null` si el contenido falta, no es Base64 válido
 * o no es una imagen decodificable; nunca reintenta la decodificación como
 * texto ni genera un QR nuevo.
 */
fun decodeQrImageBitmap(encoded: String): ImageBitmap? {
    if (encoded.isBlank()) return null
    val bytes = runCatching { Base64.decode(encoded) }.getOrNull() ?: return null
    if (bytes.isEmpty()) return null
    return runCatching { decodeImageBitmap(bytes) }.getOrNull()
}

/**
 * Vista previa del QR a partir de su representación codificada en el
 * dominio. El QR conserva proporción 1:1 (nunca se estira) y se dibuja
 * sobre [AmColor.QrCanvas] para garantizar el contraste de escaneo en
 * ambos temas.
 *
 * Si el contenido no puede decodificarse, se muestra un estado explícito
 * (glifo "sin imagen" + [unavailableMessage] opcional): NUNCA un bloque que
 * parezca un QR válido. El copy es responsabilidad del llamador (ADR-0008).
 */
@Composable
fun AmQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
    /** Lado del marco cuadrado. El QR conserva proporción 1:1. */
    size: Dp = AmMetrics.QrPreviewSize,
    /** Texto del estado "sin vista previa" (copy del llamador). */
    unavailableMessage: String? = null,
) {
    val bitmap = remember(encodedQr) { decodeQrImageBitmap(encodedQr) }
    AmMediaFrame(modifier = modifier.size(size)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "QR",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AmSpacing.Md)
                    .background(AmColor.QrCanvas),
            )
        } else {
            Column(
                modifier = Modifier.padding(AmSpacing.Lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmIcon(
                    imageVector = AmIcons.ImageOff,
                    contentDescription = null,
                    tint = AmColor.TextTertiary,
                    size = AmMetrics.EmptyStateGlyphSize,
                )
                unavailableMessage?.let { message ->
                    AmText(
                        message,
                        color = AmColor.TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
