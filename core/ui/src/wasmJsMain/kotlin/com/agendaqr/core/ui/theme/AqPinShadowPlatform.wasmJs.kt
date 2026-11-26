package com.agendaqr.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path

/**
 * wasmJs: misma aproximación que Android — degradado radial `popo`→
 * transparente sobre la silueta pin+flecha (sombra `drawShadow` de Flutter).
 * Todo el dibujo usa APIs comunes de Compose; no hay canvas nativo que
 * rasterice `BlurMaskFilter`.
 */
internal actual fun Modifier.aqPinShadowPlatform(
    pinCenterX: Float,
    pinCenterY: Float,
    pinRadius: Float,
    arrowHalfWidth: Float,
    arrowBaseY: Float,
    arrowHeight: Float,
    blur: Float,
    offsetY: Float,
    alpha: Float,
): Modifier = drawBehind {
    // Centro del degradado: silueta desplazada por el offset de la sombra.
    val cx = pinCenterX
    val cy = pinCenterY + offsetY
    // Radio del halo: silueta + margen de blur (≈ elevation de Flutter).
    val halo = pinRadius + blur + arrowHeight
    val brush = Brush.radialGradient(
        colors = listOf(
            AqColor.Popo.copy(alpha = alpha),
            AqColor.Popo.copy(alpha = alpha * 0.55f),
            AqColor.Transparent,
        ),
        center = Offset(cx, cy),
        radius = halo,
    )
    // Círculo del pin.
    drawOval(
        brush = brush,
        topLeft = Offset(cx - halo, cy - halo),
        size = Size(halo * 2f, halo * 2f),
    )
    // Flecha: path triangular relleno con el mismo degradado.
    val arrow = Path().apply {
        moveTo(cx - arrowHalfWidth, arrowBaseY + offsetY)
        lineTo(cx, arrowBaseY + arrowHeight + offsetY)
        lineTo(cx + arrowHalfWidth, arrowBaseY + offsetY)
        close()
    }
    drawPath(arrow, brush = brush)
}
