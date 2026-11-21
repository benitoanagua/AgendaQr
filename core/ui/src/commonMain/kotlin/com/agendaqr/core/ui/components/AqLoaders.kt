package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Indicador de carga circular sobre [MoonCircularLoader]
 * (`moon_design` 1.1.0, `lib/src/widgets/loaders/circular_loader.dart`).
 *
 * Tamaños de Moon: x2s (16 dp, trazo 2 dp), xs (24 dp, 2 dp), sm (32 dp,
 * 2 dp), md (40 dp, 4 dp) y lg (48 dp, 4 dp). El color se toma de Polaris;
 * la geometría, de Moon. Es indeterminado (giro continuo).
 */
@Composable
fun AqCircularLoader(
    loaderSize: Dp = AqSize.Md,
    strokeWidth: Dp? = null,
    color: Color = AqColor.EmphasisFill,
    contentDescription: String? = null,
) {
    val effectiveStrokeWidth = strokeWidth ?: when {
        loaderSize <= AqSize.Sm -> AqSize.X6s
        else -> AqSize.X5s
    }
    val description = contentDescription
    Box(
        modifier = Modifier
            .size(width = loaderSize, height = loaderSize)
            .semantics {
                this.contentDescription = description ?: ""
            },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(width = loaderSize, height = loaderSize),
            color = color,
            strokeWidth = effectiveStrokeWidth,
        )
    }
}

/**
 * Indicador de carga lineal sobre [MoonLinearLoader]
 * (`moon_design` 1.1.0, `lib/src/widgets/loaders/linear_loader.dart`).
 *
 * Tamaños de Moon: x6s (2 dp, radio 4), x5s (4 dp, 4 dp), x4s (8 dp, 8 dp),
 * x3s (12 dp, 12 dp), x2s (16 dp, 16 dp). El color de Polaris; la geometría,
 * de Moon. [progress] en 0..1; usa 1f para indeterminado visual (línea
 * completa) o el valor real de avance.
 */
@Composable
fun AqLinearLoader(
    loaderHeight: Dp = AqSize.X4s,
    progress: Float = 1f,
    color: Color = AqColor.EmphasisFill,
    contentDescription: String? = null,
) {
    val shape: Shape = when {
        loaderHeight <= AqSize.X4s -> AqSquircles.SurfaceXs
        loaderHeight <= AqSize.X3s -> AqSquircles.SurfaceMd
        else -> AqSquircles.SurfaceLg
    }
    val description = contentDescription
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(loaderHeight)
            .semantics {
                this.contentDescription = description ?: ""
            },
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(loaderHeight).clip(shape),
            progress = progress,
            color = color,
        )
    }
}
