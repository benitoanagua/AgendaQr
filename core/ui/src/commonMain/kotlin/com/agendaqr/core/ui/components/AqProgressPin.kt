package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStroke

/**
 * Indicador de progreso circular reducido sobre [MoonProgressPin]
 * (`moon_design` 1.1.0, `widgets/progress_pin`).
 *
 * Pequeño indicador circular usado para mostrar avances puntuales sin
 * ocupar todo el ancho (diferente de [AqLinearProgress]).
 *
 * - [progress]: fracción 0..1.
 * - Tamaño por defecto x2s (16 dp, [AqSize]).
 * - Grosor de trazo: 1 dp ([AqStroke]).
 * - Semántica con [RangeInfo].
 */
@Composable
fun AqProgressPin(
    progress: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = AqSize.X2s,
    color: Color = AqColor.EmphasisFill,
) {
    require(progress in 0f..1f) { "AqProgressPin.progress debe estar entre 0 y 1" }
    Box(
        modifier = modifier
            .size(size)
            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            progress = { progress },
            color = color,
            trackColor = AqColor.SurfaceContainerHighest,
            strokeWidth = AqStroke.Default,
        )
    }
}
