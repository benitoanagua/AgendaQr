package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSize

/**
 * Indicador de carga circular indeterminado sobre [MoonCircularLoader]
 * (`moon_design` 1.1.0, `widgets/loaders/circular_loader` +
 * `circular_loader_theme.dart`/`_sizes.dart`).
 *
 * - Tamaños Moon x2s/xs/sm/md/lg (16/24/32/40/48 dp); trazo `x6s` (2 dp)
 *   hasta sm y `x5s` (4 dp) en md/lg; `StrokeCap.round`.
 * - Color `piccolo`→[AqColor.EmphasisFill]; pista `backgroundColor`
 *   (transparente por defecto, como en Moon).
 * - Giro indeterminado del painter de Moon (arco de 270° rotando).
 */
@Composable
fun AqCircularLoader(
    loaderSize: Dp = AqSize.Md,
    strokeWidth: Dp? = null,
    color: Color = AqColor.EmphasisFill,
    backgroundColor: Color = AqColor.Transparent,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val effectiveStrokeWidth = strokeWidth ?: if (loaderSize <= AqSize.Sm) AqSize.X6s else AqSize.X5s
    Box(
        modifier = modifier
            .size(loaderSize)
            .semantics {
                this.contentDescription = contentDescription ?: ""
            },
        contentAlignment = Alignment.Center,
    ) {
        AqCircularArc(
            progress = null,
            size = loaderSize,
            strokeWidth = effectiveStrokeWidth,
            color = color,
            trackColor = backgroundColor,
        )
    }
}

/**
 * Indicador de carga lineal indeterminado sobre [MoonLinearLoader]
 * (`moon_design` 1.1.0, `widgets/loaders/linear_loader` +
 * `linear_loader_theme.dart`/`_sizes.dart`).
 *
 * - Alturas Moon x6s/x5s/x4s/x3s/x2s (2/4/8/12/16 dp) con radios
 *   `surfaceXs`/`surfaceSm`/`surfaceMd`/`surfaceLg` ([AqCorner]).
 * - Color `piccolo`→[AqColor.EmphasisFill]; `backgroundColor`
 *   transparente por defecto (Moon `linear_loader_colors.dart`).
 * - [progress]: `null` = indeterminado (Moon); un valor 0..1 dibuja la
 *   barra determinada (extensión usada por el esqueleto).
 */
@Composable
fun AqLinearLoader(
    loaderHeight: Dp = AqSize.X4s,
    progress: Float? = null,
    color: Color = AqColor.EmphasisFill,
    backgroundColor: Color = AqColor.Transparent,
    contentDescription: String? = null,
) {
    val radius = when {
        loaderHeight <= AqSize.X5s -> AqCorner.SurfaceXs
        loaderHeight <= AqSize.X4s -> AqCorner.SurfaceSm
        loaderHeight <= AqSize.X3s -> AqCorner.SurfaceMd
        else -> AqCorner.SurfaceLg
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(loaderHeight)
            .semantics {
                this.contentDescription = contentDescription ?: ""
            },
    ) {
        AqLinearTrack(
            progress = progress,
            height = loaderHeight,
            cornerRadius = radius,
            color = color,
            trackColor = backgroundColor,
        )
    }
}
