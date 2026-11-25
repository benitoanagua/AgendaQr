package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqComposition
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Feedback de proceso: skeleton, paginación, marco de
 * medios y encuadre del scanner.
 */

/**
 * Encuadre del scanner: guía visual ACTIVA de captura — cuatro esquinas
 * de acento dibujadas en canvas (patrón contemporáneo de encuadre QR).
 * La cámara de producto lo compone SOBRE su preview real (sin fondo
 * propio, para no tapar la imagen de la cámara).
 */
@Composable
fun AqScannerFrame(
    modifier: Modifier = Modifier,
    /** Lado del encuadre; por defecto el de la vista previa de QR. */
    size: Dp = AqMetrics.QrPreviewSize,
    content: (@Composable () -> Unit)? = null,
) {
    val density = LocalDensity.current
    val cornerLength = with(density) { AqMetrics.ScannerCornerLength.toPx() }
    val stroke = with(density) { AqMetrics.BorderStrong.toPx() }
    Box(
        modifier = modifier
            .size(size)
            .aqScannerCorners(
                color = AqColor.EmphasisFill,
                cornerLength = cornerLength,
                stroke = stroke,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content?.invoke()
    }
}

private fun Modifier.aqScannerCorners(
    color: Color,
    cornerLength: Float,
    stroke: Float,
): Modifier = drawBehind {
    val w = size.width
    val h = size.height
    val len = cornerLength.coerceAtMost(w / 2f).coerceAtMost(h / 2f)
    val half = stroke / 2f
    val segments = listOf(
        // Esquina superior izquierda.
        Offset(half, len) to Offset(half, half),
        Offset(half, half) to Offset(len, half),
        // Esquina superior derecha.
        Offset(w - len, half) to Offset(w - half, half),
        Offset(w - half, half) to Offset(w - half, len),
        // Esquina inferior derecha.
        Offset(w - half, h - len) to Offset(w - half, h - half),
        Offset(w - half, h - half) to Offset(w - len, h - half),
        // Esquina inferior izquierda.
        Offset(len, h - half) to Offset(half, h - half),
        Offset(half, h - half) to Offset(half, h - len),
    )
    segments.forEach { (start, end) ->
        drawLine(color, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

/**
 * Marco de medios (QR, comprobante): superficie [AqColor.Surface] con el
 * radio de tarjeta de la referencia (12, `radius-300`) — el límite del contenido es el
 * bloque tonal en ambos temas, sin bordes decorativos (el gate solo
 * admite E-01/E-02). El fondo interno del contenido (p. ej. el lienzo
 * claro del QR, [AqColor.QrCanvas]) lo aporta quien lo dibuja.
 */
@Composable
fun AqMediaFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(AqColor.Surface, AqSquircles.SurfaceMd),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
