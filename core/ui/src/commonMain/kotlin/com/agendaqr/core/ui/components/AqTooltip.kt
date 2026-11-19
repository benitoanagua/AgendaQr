package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Tooltip móvil: etiqueta flotante tonal que se muestra cuando [isVisible] es
 * verdadero.
 *
 * Patrón de interacción móvil (Moon): los controles solo-icono sin etiqueta
 * visible exponen su nombre accesible como tooltip. El llamador controla
 * [isVisible] (p. ej. tras una pulsación larga detectada en la pantalla), de
   modo que el tooltip nunca interfiere con la interacción del contenido
 * envuelto ni roba eventos de puntero a controles anidados.
 *
 * - El contenido envuelto conserva su área táctil (≥ 48 dp impuesta por el
 *   llamador, p. ej. [AqIconButton]).
 * - El tooltip se posiciona como superposición sobre el contenido, sin
 *   recortar ni desplazar al contenido de fondo.
 * - Usa el bloque tonal y tipografía del sistema; el par
 *   contenido/contenedor cumple AA ([AqSchemeTest]).
 *
 * Estados: Default (oculto) y Visible (etiqueta mostrada). No es interactivo.
 */
@Composable
fun AqTooltip(
    label: String,
    isVisible: Boolean = false,
    modifier: Modifier = Modifier,
    /** Glifo decorativo opcional a la izquierda de la etiqueta. */
    icon: ImageVector? = null,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    // Ancho acotado: el tooltip nunca ocupa toda la pantalla en pantallas
    // estrechas; se ajusta al texto con un tope razonable.
    val tooltipMaxWidth = with(density) { (AqMetrics.DialogMinWidth.value * 0.55f).dp }
    val isVisibleState = rememberUpdatedState(isVisible)

    Box(
        modifier = modifier,
    ) {
        content()
        if (isVisibleState.value) {
            TooltipBubble(
                label = label,
                icon = icon,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = AqSpacing.Xs)
                    .testTag("aq-tooltip"),
                maxWidth = tooltipMaxWidth,
            )
        }
    }
}

/**
 * Burbuja del tooltip: bloque tonal pequeño con la etiqueta, glifo opcional.
 */
@Composable
private fun TooltipBubble(
    label: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    maxWidth: Dp,
) {
    Box(
        modifier = modifier
            .clip(AqShapes.Medium)
            .background(AqColor.SurfaceContainerHigh, AqShapes.Medium)
            .widthIn(max = maxWidth),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = AqColor.TextSecondary,
                )
            }
            AqText(
                text = label,
                size = AqType.CaptionSmall,
                color = AqColor.TextPrimary,
                maxLines = 2,
            )
        }
    }
}
