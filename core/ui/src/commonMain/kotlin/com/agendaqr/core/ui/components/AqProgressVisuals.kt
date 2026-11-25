package com.agendaqr.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import kotlin.math.min

/**
 * Port de los painters de `moon_design` 1.1.0
 * (`widgets/common/progress_indicators/painters/`).
 *
 * - Circular: pista de círculo completo menos ε y progreso desde -90° con
 *   barrido `progress × (2π − ε)` (`_sweep`/`_startAngle` de Moon),
 *   `StrokeCap.round`. Indeterminado: arco de 270° en rotación (ciclo de
 *   Moon `_kIndeterminateCircularDuration` ≈ 1333 ms por paso).
 * - Lineal: pista y relleno redondeados; indeterminado: segmento del 30 %
 *   del ancho recorriéndola en 1800 ms (`_kIndeterminateLinearDuration`).
 */

private const val TwoPi = 360f
private const val SweepEpsilon = 0.001f
private const val IndeterminateCircularMs = 1333
private const val IndeterminateLinearMs = 1800

/** Arco circular: determinado si [progress] no es nulo, giro si lo es. */
@Composable
internal fun AqCircularArc(
    progress: Float?,
    size: Dp,
    strokeWidth: Dp,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
) {
    if (progress == null) {
        val transition = rememberInfiniteTransition(label = "aqCircularArc")
        val rotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(IndeterminateCircularMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "aqCircularArcRotation",
        )
        Canvas(modifier = modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = TwoPi - SweepEpsilon,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // Arco indeterminado de 270° que rota (par head/tail de Moon).
            drawArc(
                color = color,
                startAngle = -90f + (rotation % 360f),
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    } else {
        Canvas(modifier = modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = TwoPi - SweepEpsilon,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progress.coerceIn(0f, 1f) * (TwoPi - SweepEpsilon),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    }
}

/** Barra lineal: determinada si [progress] no es nulo, deslizante si lo es. */
@Composable
internal fun AqLinearTrack(
    progress: Float?,
    height: Dp,
    cornerRadius: Dp,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
) {
    if (progress == null) {
        val transition = rememberInfiniteTransition(label = "aqLinearTrack")
        val translation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(IndeterminateLinearMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "aqLinearTrackTranslation",
        )
        Canvas(modifier = modifier.fillMaxWidth().height(height)) {
            drawLinear(progress = null, translation = translation, color = color, trackColor = trackColor, cornerRadius = cornerRadius)
        }
    } else {
        Canvas(modifier = modifier.fillMaxWidth().height(height)) {
            drawLinear(progress = progress, translation = 0f, color = color, trackColor = trackColor, cornerRadius = cornerRadius)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLinear(
    progress: Float?,
    translation: Float,
    color: Color,
    trackColor: Color,
    cornerRadius: Dp,
) {
    val h = size.height
    val w = size.width
    val radius = min(cornerRadius.toPx(), h / 2f)
    val corner = CornerRadius(radius, radius)
    drawRoundRect(
        color = trackColor,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = corner,
    )
    if (progress == null) {
        // Indeterminado de Moon: dos segmentos (line1/line2 head-tail)
        // sobre un ciclo de 1800 ms con curvas Interval+Cubic.
        drawMoonIndeterminateBars(w, h, corner, color, translation)
    } else {
        val fill = w * progress.coerceIn(0f, 1f)
        if (fill > 0f) {
            drawRoundRect(
                color = color,
                topLeft = Offset.Zero,
                size = Size(fill, h),
                cornerRadius = corner,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Curvas del indeterminado lineal de Moon
// (`linear_progress_indicator_painter.dart`, ciclo de 1800 ms).
// ---------------------------------------------------------------------------

private const val IndeterminateCycle = 1800f

/** `Interval(start, end, curve)` de Flutter: 0 fuera, Bézier dentro. */
private class MoonInterval(
    private val start: Float,
    private val end: Float,
    private val cx1: Float,
    private val cy1: Float,
    private val cx2: Float,
    private val cy2: Float,
) {
    fun transform(v: Float): Float {
        val t = ((v - start) / (end - start)).coerceIn(0f, 1f)
        return cubicY(t)
    }

    private fun cubicY(t: Float): Float {
        // Resuelve x(s) = t por bisección y devuelve y(s).
        var lo = 0f
        var hi = 1f
        var s = t
        repeat(24) {
            s = (lo + hi) / 2f
            if (cubicX(s) < t) lo = s else hi = s
        }
        val u = s
        val it = 1f - u
        return 3f * it * it * u * cy1 + 3f * it * u * u * cy2 + u * u * u
    }

    private fun cubicX(s: Float): Float {
        val it = 1f - s
        return 3f * it * it * s * cx1 + 3f * it * s * s * cx2 + s * s * s
    }
}

private val Line1Head = MoonInterval(0f, 750f / IndeterminateCycle, 0.2f, 0f, 0.8f, 1f)
private val Line1Tail = MoonInterval(333f / IndeterminateCycle, (333f + 750f) / IndeterminateCycle, 0.4f, 0f, 1f, 1f)
private val Line2Head = MoonInterval(1000f / IndeterminateCycle, (1000f + 567f) / IndeterminateCycle, 0f, 0f, 0.65f, 1f)
private val Line2Tail = MoonInterval(1267f / IndeterminateCycle, (1267f + 533f) / IndeterminateCycle, 0.1f, 0f, 0.45f, 1f)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMoonIndeterminateBars(
    w: Float,
    h: Float,
    corner: CornerRadius,
    color: Color,
    animationValue: Float,
) {
    val x1 = w * Line1Tail.transform(animationValue)
    val width1 = w * Line1Head.transform(animationValue) - x1
    val x2 = w * Line2Tail.transform(animationValue)
    val width2 = w * Line2Head.transform(animationValue) - x2
    for ((x, width) in listOf(x1 to width1, x2 to width2)) {
        if (width <= 0f) continue
        drawRoundRect(
            color = color,
            topLeft = Offset(x, 0f),
            size = Size(width, h),
            cornerRadius = corner,
        )
    }
}
