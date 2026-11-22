package com.agendaqr.core.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import android.graphics.Path.Direction
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Geometría de una esquina squircle (aproximación de Figma con curvas de
 * Bézier + arco circular central). Port de `ProcessedSquircleRadius`
 * (`moon_design` 1.1.0, `utils/squircle/processed_squircle_radius.dart`),
 * a su vez basado en «Desperately seeking squircles» (Figma) y en
 * `MartinRGB/Figma_Squircles_Approximation`.
 *
 * Invariantes verificadas en `AqSquircleGeometryTest`: `a = 2b` y
 * `p = arcLength + c + d + 3b`.
 */
internal data class AqSquircleGeometry(
    val cornerRadius: Float,
    val p: Float,
    val a: Float,
    val b: Float,
    val c: Float,
    val d: Float,
    val arcLength: Float,
    val alphaRad: Float,
    val betaRad: Float,
)

private fun degreesToRadians(degrees: Float): Float = degrees * (PI.toFloat() / 180f)

internal fun aqSquircleGeometry(
    radius: Float,
    smoothing: Float,
    width: Float,
    height: Float,
): AqSquircleGeometry {
    val maxRadius = min(width, height) / 2f
    val cornerRadius = min(radius, maxRadius)
    val p = min((1f + smoothing) * cornerRadius, maxRadius)

    val angleBeta: Float
    val angleAlpha: Float
    if (cornerRadius <= maxRadius / 2f) {
        angleBeta = 90f * (1f - smoothing)
        angleAlpha = 45f * smoothing
    } else {
        val diffRatio = (cornerRadius - maxRadius / 2f) / (maxRadius / 2f)
        angleBeta = 90f * (1f - smoothing * (1f - diffRatio))
        angleAlpha = 45f * smoothing * (1f - diffRatio)
    }
    val angleTheta = (90f - angleBeta) / 2f
    val p3ToP4 = cornerRadius * tan(degreesToRadians(angleTheta / 2f))
    val arcLength = sin(degreesToRadians(angleBeta / 2f)) * cornerRadius * sqrt(2f)
    val c = p3ToP4 * cos(degreesToRadians(angleAlpha))
    val d = c * tan(degreesToRadians(angleAlpha))
    val b = (p - arcLength - c - d) / 3f
    val a = 2f * b
    return AqSquircleGeometry(
        cornerRadius = cornerRadius,
        p = p,
        a = a,
        b = b,
        c = c,
        d = d,
        arcLength = arcLength,
        alphaRad = degreesToRadians(angleAlpha),
        betaRad = degreesToRadians(angleBeta),
    )
}

/**
 * Añade una esquina al [Path] en coordenadas (s, t): `s` se mide desde el
 * vértice hacia dentro por la arista de llegada (`e1`) y `t` por la de salida
 * (`e2`). Las cuatro esquinas comparten la misma secuencia de puntos.
 */
private fun Path.addSquircleCorner(
    vertex: Offset,
    e1: Offset,
    e2: Offset,
    g: AqSquircleGeometry,
    e1Length: Float,
    e2Length: Float,
    first: Boolean,
) {
    fun pt(s: Float, t: Float) = Offset(
        vertex.x + s * e1.x + t * e2.x,
        vertex.y + s * e1.y + t * e2.y,
    )

    val start = pt(min(g.p, e1Length / 2f), 0f)
    if (first) moveTo(start.x, start.y) else lineTo(start.x, start.y)

    if (g.cornerRadius <= 0f) {
        lineTo(vertex.x, vertex.y)
        val mid = pt(0f, e2Length / 2f)
        lineTo(mid.x, mid.y)
        return
    }

    val p = g.p
    val a = g.a
    val b = g.b
    val c = g.c
    val d = g.d

    val c1a = pt(p - a, 0f)
    val c1b = pt(p - a - b, 0f)
    val arcStartS = p - a - b - c
    val arcStart = pt(arcStartS, d)
    cubicTo(c1a.x, c1a.y, c1b.x, c1b.y, arcStart.x, arcStart.y)

    // Arco circular central como una cúbica (k = 4/3·tan(β/4)·r).
    val k = (4f / 3f) * tan(g.betaRad / 4f) * g.cornerRadius
    val sinA = sin(g.alphaRad)
    val cosA = cos(g.alphaRad)
    val arcEndS = arcStartS - g.arcLength
    val arcEndT = d + g.arcLength
    val arcC1 = pt(arcStartS - k * cosA, d + k * sinA)
    val arcC2 = pt(arcEndS + k * sinA, arcEndT - k * cosA)
    val arcEnd = pt(arcEndS, arcEndT)
    cubicTo(arcC1.x, arcC1.y, arcC2.x, arcC2.y, arcEnd.x, arcEnd.y)

    val c2a = pt(0f, p - a - b)
    val c2b = pt(0f, p - a)
    val end = pt(0f, min(p, e2Length / 2f))
    cubicTo(c2a.x, c2a.y, c2b.x, c2b.y, end.x, end.y)
}

/**
 * Forma squircle (esquinas superelípticas) para superficies y controles de la
 * capa UX (ADR-0039). Equivale a `MoonClipSquircleRect` / `MoonSquircleBorder`.
 */
class AqSquircleShape(
    private val cornerRadius: Dp,
    private val smoothing: Float = AqCorner.Smoothing,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return Outline.Rectangle(size.toRect())
        val radiusPx = with(density) { cornerRadius.toPx() }
        // Cuando el radio cubre la mitad del tamaño o más, la forma es un
        // círculo/ovalo completo (evita que la geometría squircle degeneré
        // y anule el área táctil al recortar).
        if (radiusPx >= min(width, height) / 2f) {
            return Outline.Generic(Path().apply { addArc(size.toRect(), 0f, 360f) })
        }
        val g = aqSquircleGeometry(radiusPx, smoothing, width, height)
        val path = Path().apply {
            addSquircleCorner(Offset(width, 0f), Offset(-1f, 0f), Offset(0f, 1f), g, width, height, first = true)
            addSquircleCorner(Offset(width, height), Offset(0f, -1f), Offset(-1f, 0f), g, height, width, first = false)
            addSquircleCorner(Offset(0f, height), Offset(1f, 0f), Offset(0f, -1f), g, width, height, first = false)
            addSquircleCorner(Offset(0f, 0f), Offset(0f, 1f), Offset(1f, 0f), g, height, width, first = false)
            close()
        }
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean =
        other is AqSquircleShape && other.cornerRadius == cornerRadius && other.smoothing == smoothing

    override fun hashCode(): Int = 31 * cornerRadius.hashCode() + smoothing.hashCode()
}

/** Formas squircle nombradas según la escala de [AqCorner]. */
object AqSquircles {
    val InteractiveXs: Shape = AqSquircleShape(AqCorner.InteractiveXs)
    val InteractiveSm: Shape = AqSquircleShape(AqCorner.InteractiveSm)
    val InteractiveMd: Shape = AqSquircleShape(AqCorner.InteractiveMd)
    val SurfaceXs: Shape = AqSquircleShape(AqCorner.SurfaceXs)
    val SurfaceSm: Shape = AqSquircleShape(AqCorner.SurfaceSm)
    val SurfaceMd: Shape = AqSquircleShape(AqCorner.SurfaceMd)
    val SurfaceLg: Shape = AqSquircleShape(AqCorner.SurfaceLg)
    /** Círculo/ovalo completo: radio `AqRadius.ExtraLarge` (16dp) se clampa
     *  al máximo del componente, produciendo una forma redonda en controles
     *  pequeños (botón de radio, insignia, punto) sin degenerar la
     *  geometría squircle. */
    val Circle: Shape = AqSquircleShape(AqRadius.ExtraLarge)
}
