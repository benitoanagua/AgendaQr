package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes

/**
 * Miniatura real del comprobante (implementación iOS).
 *
 * La decodificación de imágenes en iOS queda diferida hasta validación en
 * hardware. Mientras tanto muestra un marcador honesto (icono de tipo de
 * archivo) sin inventar una miniatura del contenido real.
 */
@Composable
actual fun ComprobanteThumbnail(
    file: String,
    mimeType: String?,
    modifier: Modifier,
    size: Dp,
) {
    val isPdf = mimeType?.contains("pdf", ignoreCase = true) == true
    Box(
        modifier = modifier.size(size)
            .background(AqColor.SurfaceContainerHighest, AqShapes.Medium),
        contentAlignment = Alignment.Center,
    ) {
        AqIcon(
            imageVector = if (isPdf) AqIcons.Attachment else AqIcons.ImageOff,
            contentDescription = null,
            size = AqMetrics.IconSizeCompact,
            tint = AqColor.TextTertiary,
        )
    }
}
