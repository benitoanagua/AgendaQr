package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaShape
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * Indicador de progreso de PUNTOS Xauxa (contrato §11):
 * sustituye a CircularProgressIndicator (Material) con un lenguaje plano
 * de bloques de color — sin rotación decorativa.
 *
 * Con reduced motion: representación ESTÁTICA informativa (un punto
 * encendido + liveRegion); sin bucles de animación.
 *
 * El tamaño del punto y el espaciado viven aquí como proporciones del
 * token [XauxaSpacing.Sm] — son parte del componente, no valores de
 * pantalla reutilizables fuera de él.
 */
@Composable
fun XauxaDotProgress(
    modifier: Modifier = Modifier,
    /** Color de los puntos (default Brand). */
    color: androidx.compose.ui.graphics.Color = XauxaColor.Brand,
    /** Nombre accesible del estado (copy del llamador). */
    contentDescription: String? = null,
) {
    val reducedMotion = LocalReducedMotion.current
    val semantics = if (contentDescription != null) {
        Modifier.clearAndSetSemantics {
            this.contentDescription = contentDescription
            liveRegion = LiveRegionMode.Polite
        }
    } else {
        Modifier
    }
    Row(
        modifier = modifier.then(semantics),
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (reducedMotion) {
            // Reduced motion: un punto encendido, estático — informativo.
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(XauxaSpacing.Sm).background(color, XauxaShape),
            )
        } else {
            // Tres puntos con parpadeo desfasado (una vuelta por ciclo).
            repeat(3) { index ->
                val alpha by animateFloatAsState(
                    targetValue = if (index == 0) 1f else 0.3f,
                    animationSpec = tween(
                        durationMillis = XauxaMotion.DurationShortMs,
                        delayMillis = index * 40,
                        easing = XauxaMotion.Easings.Standard,
                    ),
                    label = "xauxa_dot_$index",
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(XauxaSpacing.Sm)
                        .graphicsLayer { this.alpha = alpha }
                        .background(color, XauxaShape),
                )
            }
        }
    }
}
