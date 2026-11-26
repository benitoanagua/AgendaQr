package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType

/** Progreso determinado: [fraction] en el rango 0..1. Estado puro e inmutable. */
data class AqProgress(val fraction: Float) {
    init {
        require(fraction in 0f..1f) { "AqProgress.fraction debe estar entre 0 y 1" }
    }

    val percent: Int get() = (fraction * 100).toInt()
}

/**
 * Barra de progreso determinada equivalente a `s-progress-bar`, pintada con
 * el painter de Moon ([AqLinearTrack]): altura `x4s` (8 dp) por defecto —
 * el spec Moon v1 «Progress bar» cubre las alturas lineales 2/4/8/12/16 dp
 * (6xs…2xs) y las pasa el llamador con [height] —, radio según la escala de
 * [AqLinearLoader], color `piccolo`→[AqColor.EmphasisFill] sobre pista
 * `beerus`→[AqColor.Border]. La variante `pin` del spec es [AqProgressPin].
 */
@Composable
fun AqProgressBar(
    progress: AqProgress,
    contentDescription: String,
    modifier: Modifier = Modifier,
    /** Alto de la barra (Moon v1 «Progress bar» lineal: 2/4/8/12/16 dp). */
    height: Dp = AqSize.X4s,
) {
    AqLinearTrack(
        progress = progress.fraction,
        height = height,
        cornerRadius = when {
            height <= AqSize.X5s -> AqCorner.SurfaceXs
            height <= AqSize.X4s -> AqCorner.SurfaceSm
            height <= AqSize.X3s -> AqCorner.SurfaceMd
            else -> AqCorner.SurfaceLg
        },
        color = AqColor.EmphasisFill,
        trackColor = AqColor.Border,
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.progressBarRangeInfo = ProgressBarRangeInfo(
                    progress.fraction,
                    0f..1f,
                    progress.percent,
                )
                liveRegion = LiveRegionMode.Polite
            },
    )
}

/** Progreso con etiqueta y mensaje, todos los textos inyectados por el llamador. */
@Composable
fun AqProgressBlock(
    progress: AqProgress,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(modifier = modifier) {
        AqText(text = label, size = com.agendaqr.core.ui.theme.AqType.Label)
        AqProgressBar(progress = progress, contentDescription = contentDescription)
        message?.let { AqText(text = it, size = com.agendaqr.core.ui.theme.AqType.Caption) }
    }
}
