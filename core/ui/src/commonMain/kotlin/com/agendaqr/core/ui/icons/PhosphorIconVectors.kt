package com.agendaqr.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Iconos Phosphor vendorizados para slots de producto (ADR-0051).
 *
 * Fuente: paquete `phosphor-icons/core` **v2.0.8** (MIT, © Phosphor Icons),
 * archivos `assets/{bold,regular,light}/check(-bold|-light).svg` y
 * `x(-bold|-light).svg`. Solo se vendorizan los glifos necesarios (Check y
 * X); el atributo `d` se conserva literal — el parser de rutas de Compose
 * entiende los comandos relativos y arcos del SVG. El `fill="currentColor"`
 * del origen se sustituye por el tinte semántico de `AqIcon`, igual que en
 * the shared semantic icon entry point.
 *
 * Regla de peso por tamaño (ADR-0051): ≤ 20 dp Bold, 21–32 dp Regular,
 * ≥ 40 dp Light. Dentro del switch los iconos miden 12–14 dp → Bold.
 */
internal object PhosphorIconVectors {
    const val SourcePackage = "phosphor-icons/core"
    const val SourceVersion = "2.0.8"
    const val ViewBox = "0 0 256 256"

    val CheckBold: ImageVector by lazy { icon("PhosphorCheckBold", Paths.CheckBold) }
    val CheckRegular: ImageVector by lazy { icon("PhosphorCheckRegular", Paths.CheckRegular) }
    val CheckLight: ImageVector by lazy { icon("PhosphorCheckLight", Paths.CheckLight) }
    val XBold: ImageVector by lazy { icon("PhosphorXBold", Paths.XBold) }
    val XRegular: ImageVector by lazy { icon("PhosphorXRegular", Paths.XRegular) }
    val XLight: ImageVector by lazy { icon("PhosphorXLight", Paths.XLight) }

    private fun icon(name: String, data: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 256.dp,
            defaultHeight = 256.dp,
            viewportWidth = 256f,
            viewportHeight = 256f,
        ).addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            pathFillType = PathFillType.NonZero,
            fill = SolidColor(Color.Black),
        ).build()

    private object Paths {
        // assets/bold/check-bold.svg
        const val CheckBold =
            "M232.49,80.49l-128,128a12,12,0,0,1-17,0l-56-56a12,12,0,1,1,17-17L96,183,215.51,63.51a12,12,0,0,1,17,17Z"
        // assets/regular/check.svg
        const val CheckRegular =
            "M229.66,77.66l-128,128a8,8,0,0,1-11.32,0l-56-56a8,8,0,0,1,11.32-11.32L96,188.69,218.34,66.34a8,8,0,0,1,11.32,11.32Z"
        // assets/light/check-light.svg
        const val CheckLight =
            "M228.24,76.24l-128,128a6,6,0,0,1-8.48,0l-56-56a6,6,0,0,1,8.48-8.48L96,191.51,219.76,67.76a6,6,0,0,1,8.48,8.48Z"
        // assets/bold/x-bold.svg
        const val XBold =
            "M208.49,191.51a12,12,0,0,1-17,17L128,145,64.49,208.49a12,12,0,0,1-17-17L111,128,47.51,64.49a12,12,0,0,1,17-17L128,111l63.51-63.52a12,12,0,0,1,17,17L145,128Z"
        // assets/regular/x.svg
        const val XRegular =
            "M205.66,194.34a8,8,0,0,1-11.32,11.32L128,139.31,61.66,205.66a8,8,0,0,1-11.32-11.32L116.69,128,50.34,61.66A8,8,0,0,1,61.66,50.34L128,116.69l66.34-66.35a8,8,0,0,1,11.32,11.32L139.31,128Z"
        // assets/light/x-light.svg
        const val XLight =
            "M204.24,195.76a6,6,0,1,1-8.48,8.48L128,136.49,60.24,204.24a6,6,0,0,1-8.48-8.48L119.51,128,51.76,60.24a6,6,0,0,1,8.48-8.48L128,119.51l67.76-67.75a6,6,0,0,1,8.48,8.48L136.49,128Z"
    }
}

/**
 * Glifos Phosphor disponibles para slots de producto (ADR-0051): enmienda
 * acotada de ADR-0044 solo en iconografía — Moon sigue siendo la autoridad
 * en componentes, tokens y tipografía.
 */
enum class AqPhosphorIcon { Check, X }

/**
 * Peso del glifo Phosphor según el tamaño pedido (ADR-0051):
 * ≤ 20 dp Bold, 21–32 dp Regular, ≥ 40 dp Light. Vive en la capa de
 * vectores porque los umbrales son medidas intrínsecas del glifo, no
 * valores del sistema de espaciado.
 */
fun AqPhosphorIcon.vectorForSize(size: Dp): ImageVector {
    val bold = size <= 20.dp
    val regular = size in 21.dp..32.dp
    return when (this) {
        AqPhosphorIcon.Check -> when {
            bold -> PhosphorIconVectors.CheckBold
            regular -> PhosphorIconVectors.CheckRegular
            else -> PhosphorIconVectors.CheckLight
        }
        AqPhosphorIcon.X -> when {
            bold -> PhosphorIconVectors.XBold
            regular -> PhosphorIconVectors.XRegular
            else -> PhosphorIconVectors.XLight
        }
    }
}
