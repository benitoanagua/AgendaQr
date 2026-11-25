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
 * el painter de Moon ([AqLinearTrack]): altura `x4s` (8 dp), radio
 * `surfaceSm`, color `piccolo`→[AqColor.EmphasisFill] sobre pista
 * `beerus`→[AqColor.Border].
 */
@Composable
fun AqProgressBar(
    progress: AqProgress,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    AqLinearTrack(
        progress = progress.fraction,
        height = AqSize.X4s,
        cornerRadius = AqCorner.SurfaceSm,
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
