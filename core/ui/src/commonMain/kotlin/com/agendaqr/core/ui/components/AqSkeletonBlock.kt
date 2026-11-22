package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Esqueleto de bloque sobre piezas Moon ([AqLinearLoader] + formas Moon).
 * Sustituye a [AqSkeleton].
 *
 * - [lines]: número de líneas de esqueleto.
 * - [lineHeight]: alto de cada línea (por defecto [AqSize.Sm]).
 * - [gap]: hueco entre líneas (por defecto [AqSize.X2s]).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSkeletonBlock(
    modifier: Modifier = Modifier,
    lines: Int = 3,
    lineHeight: Dp = AqSize.Sm,
    gap: Dp = AqSize.X2s,
) {
    require(lines > 0) { "AqSkeletonBlock.lines debe ser mayor que 0" }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        repeat(lines) { index ->
            val last = index == lines - 1
            val width = if (last) 0.4f else 1f
            Box(
                modifier = Modifier
                    .fillMaxWidth(width)
                    .height(lineHeight)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .background(AqColor.SurfaceContainerHigh, AqSquircles.SurfaceXs),
            )
        }
    }
}

/**
 * Indicador de carga lineal para esqueleto (reusa [AqLinearLoader] como base
 * de forma Moon). Útil como barra de esqueleto única.
 */
@Composable
fun AqSkeletonLine(
    height: Dp = AqSize.X4s,
    progress: Float = 1f,
    color: Color = AqColor.SurfaceContainerHigh,
) {
    AqLinearLoader(
        loaderHeight = height,
        progress = progress,
        color = color,
    )
}
