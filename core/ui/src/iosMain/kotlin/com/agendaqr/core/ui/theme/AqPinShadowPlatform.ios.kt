package com.agendaqr.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path

/**
 * Fallback iOS: la silueta pin+flecha se pinta con alfa reducida y sin
 * desenfoque (Compose común no expone `BlurMaskFilter`); la geometría y el
 * desplazamiento sí coinciden con `drawShadow` de Flutter.
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
    val cy = pinCenterY + offsetY
    val path = Path().apply {
        addOval(
            Rect(
                pinCenterX - pinRadius,
                cy - pinRadius,
                pinCenterX + pinRadius,
                cy + pinRadius,
            ),
        )
        moveTo(pinCenterX - arrowHalfWidth, arrowBaseY + offsetY)
        lineTo(pinCenterX, arrowBaseY + arrowHeight + offsetY)
        lineTo(pinCenterX + arrowHalfWidth, arrowBaseY + offsetY)
        close()
    }
    drawPath(path, color = AqColor.Popo.copy(alpha = alpha))
}
