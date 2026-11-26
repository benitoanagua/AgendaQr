package com.agendaqr.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape

/**
 * Fallback iOS: `Modifier.shadow` por capa (elevación = blur). La geometría
 * exacta de `BoxShadow` solo está disponible en Android; en iOS la sombra es
 * una aproximación documentada (ver ADR de fidelidad visual).
 */
internal actual fun Modifier.aqShadowPlatform(
    layers: List<AqShadowLayer>,
    shape: Shape,
): Modifier {
    var result = this
    layers.forEach { layer ->
        val color = AqPalette.popo.copy(alpha = layer.alpha)
        result = result.shadow(
            elevation = layer.blur,
            shape = shape,
            clip = false,
            ambientColor = color,
            spotColor = color,
        )
    }
    return result
}
