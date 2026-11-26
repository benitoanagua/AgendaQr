package com.agendaqr.core.ui.theme

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas

/**
 * Sombras reales estilo `BoxShadow` de Flutter: cada capa se dibuja sobre el
 * canvas nativo con `BlurMaskFilter` (difuminado gaussiano), desplazamiento
 * vertical y dispersión negativos contraídos, igual que hace el engine de
 * Flutter para `BoxShadow(color, blurRadius, offset, spreadRadius)`.
 *
 * El radio del `BlurMaskFilter` usa la misma escala que el engine de Flutter
 * (`blurSigma = 0.57735 * blurRadius + 0.5`), que a efectos visuales equivale
 * al `blurRadius` en px.
 */
internal actual fun Modifier.aqShadowPlatform(
    layers: List<AqShadowLayer>,
    shape: Shape,
): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val canvas = drawContext.canvas.nativeCanvas
    layers.forEach { layer ->
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(
                (layer.alpha * 255).toInt().coerceIn(0, 255),
                0,
                0,
                0,
            )
            maskFilter = BlurMaskFilter(
                (layer.blur.toPx() * 0.57735f + 0.5f).coerceAtLeast(1f),
                BlurMaskFilter.Blur.NORMAL,
            )
        }
        // CSS/Flutter `spreadRadius`: negativo contrae la silueta de la sombra.
        val spreadPx = (-layer.spread).toPx()
        canvas.save()
        canvas.translate(0f, layer.offsetY.toPx())
        when (outline) {
            is Outline.Rectangle -> {
                val r = outline.rect
                canvas.drawRect(
                    RectF(
                        r.left + spreadPx,
                        r.top + spreadPx,
                        r.right - spreadPx,
                        r.bottom - spreadPx,
                    ),
                    paint,
                )
            }
            is Outline.Rounded -> {
                val rr = outline.roundRect
                val path = android.graphics.Path().apply {
                    addRoundRect(
                        RectF(
                            rr.left + spreadPx,
                            rr.top + spreadPx,
                            rr.right - spreadPx,
                            rr.bottom - spreadPx,
                        ),
                        floatArrayOf(
                            rr.topLeftCornerRadius.x, rr.topLeftCornerRadius.y,
                            rr.topRightCornerRadius.x, rr.topRightCornerRadius.y,
                            rr.bottomRightCornerRadius.x, rr.bottomRightCornerRadius.y,
                            rr.bottomLeftCornerRadius.x, rr.bottomLeftCornerRadius.y,
                        ),
                        android.graphics.Path.Direction.CW,
                    )
                }
                canvas.drawPath(path, paint)
            }
            is Outline.Generic -> {
                // Las squircles llegan como `Outline.Generic`: no hay forma
                // barata de contraer un `Path`; la dispersión negativa se
                // aproxima omitiéndola (el blur ya la difumina).
                canvas.drawPath(outline.path.asAndroidPath(), paint)
            }
        }
        canvas.restore()
    }
}
