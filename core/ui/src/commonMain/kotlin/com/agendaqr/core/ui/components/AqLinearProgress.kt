package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Barra de progreso determinada sobre [MoonLinearProgress]
 * (`moon_design` 1.1.0, `widgets/progress`).
 *
 * - [progress]: fracción 0..1 del avance.
 * - Altura: [height] (por defecto x4s / 8 dp, [AqSize]).
 * - Radio: SurfaceXs ([AqCorner]) vía [AqSquircles].
 * - Color de relleno y pista desde Polaris.
 * - Semántica de barra de progreso con [RangeInfo].
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqLinearProgress(
    progress: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    height: Dp = AqSize.X4s,
    color: Color = AqColor.EmphasisFill,
) {
    require(progress in 0f..1f) { "AqLinearProgress.progress debe estar entre 0 y 1" }
    val shape: Shape = AqSquircles.SurfaceXs
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(height).clip(shape),
            progress = { progress },
            color = color,
            trackColor = AqColor.SurfaceContainerHighest,
        )
    }
}
