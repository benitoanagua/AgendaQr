package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize

/**
 * Progreso circular determinado sobre [MoonCircularProgress]
 * (`moon_design` 1.1.0, `widgets/progress`).
 *
 * - [progress]: fracción 0..1 del avance.
 * - Tamaños Moon: x2s (16 dp), xs (24 dp), sm (32 dp), md (40 dp), lg (48 dp).
 * - El área táctil se amplía a 48 dp incluso cuando el indicador visual es
 *   más pequeño (D-02).
 * - Semántica de barra de progreso para lectores de pantalla.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = AqSize.Md,
    color: Color = AqColor.EmphasisFill,
) {
    require(progress in 0f..1f) { "AqCircularProgress.progress debe estar entre 0 y 1" }
    Box(
        modifier = modifier
            .size(size)
            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            progress = { progress },
            color = color,
            trackColor = AqColor.SurfaceContainerHighest,
        )
    }
}
