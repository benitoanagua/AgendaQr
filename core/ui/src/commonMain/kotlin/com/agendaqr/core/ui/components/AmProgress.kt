package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AmColor

/** Progreso determinado: [fraction] en el rango 0..1. Estado puro e inmutable. */
data class AmProgress(val fraction: Float) {
    init {
        require(fraction in 0f..1f) { "AmProgress.fraction debe estar entre 0 y 1" }
    }

    val percent: Int get() = (fraction * 100).toInt()
}

/** Barra de progreso determinada equivalente a `s-progress-bar`. */
@Composable
fun AmProgressBar(
    progress: AmProgress,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress.fraction },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
                this.progressBarRangeInfo = ProgressBarRangeInfo(
                    progress.fraction,
                    0f..1f,
                    progress.percent,
                )
                liveRegion = LiveRegionMode.Polite
            },
        color = AmColor.Accent,
        trackColor = AmColor.SurfaceContainerHighest,
    )
}

/** Indicador circular indeterminado con anuncio accesible (equivalente a `s-spinner`). */
@Composable
fun AmSpinner(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            liveRegion = LiveRegionMode.Polite
        },
        color = AmColor.Accent,
        trackColor = AmColor.SurfaceContainerHighest,
    )
}

/** Progreso con etiqueta y mensaje, todos los textos inyectados por el llamador. */
@Composable
fun AmProgressBlock(
    progress: AmProgress,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(modifier = modifier) {
        AmText(text = label, size = com.agendaqr.core.ui.theme.AmType.Label)
        AmProgressBar(progress = progress, contentDescription = contentDescription)
        message?.let { AmText(text = it, size = com.agendaqr.core.ui.theme.AmType.Caption) }
    }
}
