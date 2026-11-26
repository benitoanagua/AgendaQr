package com.agendaqr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Tooltip móvil: etiqueta flotante tonal que se muestra cuando [isVisible] es
 * verdadero.
 *
 * Patrón de interacción móvil (Moon): los controles solo-icono sin etiqueta
 * visible exponen su nombre accesible como tooltip. [onLongClick] permite
 * que el llamador actualice [isVisible] desde la pulsación larga del área
 * envuelta. El estado sigue perteneciendo al llamador y los taps normales
 * continúan llegando al contenido.
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
    /** Callback opcional de long press; el llamador controla el estado visible. */
    onLongClick: (() -> Unit)? = null,
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
        modifier = modifier.then(
            if (onLongClick != null) {
                Modifier.pointerInput(onLongClick) {
                    awaitEachGesture {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial,
                        )
                        val cancelled = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val pointer = event.changes.firstOrNull { it.id == down.id }
                                    ?: return@withTimeoutOrNull true
                                if (!pointer.pressed || (pointer.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                                    return@withTimeoutOrNull true
                                }
                            }
                        }
                        if (cancelled == null) onLongClick()
                    }
                }
            } else {
                Modifier
            },
        ),
    ) {
        content()
        if (isVisibleState.value) {
            TooltipBubble(
                label = label,
                icon = icon,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = AqSpacing.Xs),
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
    Column(modifier = modifier) {
        // El tag envuelve la burbuja completa (sombra + fondo + padding);
        // la flecha queda fuera, como en las bounds del golden Moon.
        Box(modifier = Modifier.testTag("aq-tooltip")) {
            Box(
                modifier = Modifier
                    .aqShadow(AqShadow.Sm, AqSquircles.InteractiveXs)
                    .background(AqColor.Background, AqSquircles.InteractiveXs)
                    .padding(AqSize.X3s)
                    .widthIn(max = maxWidth),
                contentAlignment = Alignment.Center,
            ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
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
                size = AqType.BodyXs,
                color = AqColor.TextPrimary,
                maxLines = 2,
            )
        }
            }
        }
        // Flecha de Moon (`arrowBaseWidth` x2s, `arrowLength` x4s):
        // triángulo bajo la burbuja apuntando al objetivo.
        val arrowColor = AqColor.Background
        Canvas(
            modifier = Modifier
                .padding(top = AqSize.X5s)
                .width(AqSize.X2s)
                .height(AqSize.X4s),
        ) {
            val w = size.width
            val h = size.height
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w / 2f, h)
                    close()
                },
                color = arrowColor,
            )
        }
    }
}
