package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
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
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize

/**
 * Progreso circular determinado sobre [MoonCircularProgress]
 * (`moon_design` 1.1.0, `widgets/progress/circular_progress` +
 * `circular_progress_theme.dart`/`_sizes.dart`).
 *
 * - Tamaños Moon x2s/xs/sm/md/lg (16/24/32/40/48 dp); trazo `x6s` (2 dp)
 *   hasta sm y `x5s` (4 dp) en md/lg.
 * - Color `piccolo`→[AqColor.EmphasisFill] sobre pista `beerus`→
 *   [AqColor.Border],
 *   barrido desde -90° con `StrokeCap.round` (painter de Moon).
 * - [progress]: fracción 0..1; [size]: diámetro visual (cualquier dp del
 *   llamador; el trazo se resuelve por la tabla Moon).
 * - Semántica de barra de progreso; el área táctil se amplía a 48 dp
 *   (D-02, ADR-0039 §4.4).
 */
@Composable
fun AqCircularProgress(
    progress: Float,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = AqSize.Md,
    color: Color = AqColor.EmphasisFill,
    trackColor: Color = AqColor.Border,
) {
    require(progress in 0f..1f) { "AqCircularProgress.progress debe estar entre 0 y 1" }
    val strokeWidth = if (size <= AqSize.Sm) AqSize.X6s else AqSize.X5s
    Box(
        modifier = modifier
            .size(size)
            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize),
        contentAlignment = Alignment.Center,
    ) {
        AqCircularArc(
            progress = progress,
            size = size,
            strokeWidth = strokeWidth,
            color = color,
            trackColor = trackColor,
            modifier = Modifier
                .semantics {
                    contentDescription?.let { this.contentDescription = it }
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        progress,
                        0f..1f,
                        (progress * 100).toInt(),
                    )
                },
        )
    }
}
