package com.agendaqr.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqElevation
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.aqElevation
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Contenedor de recarga vertical por arrastre (Pull to refresh).
 * Nivel 3 (patrón nativo móvil estilizado con tokens Polaris).
 * Muestra el spinner canónico de Polaris cuando el usuario tira hacia abajo.
 */
@Composable
fun AqPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    refreshingLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val triggerDistancePx = with(density) { (AqMetrics.ControlMinSize + AqSpacing.Lg).toPx() }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && offsetY.value != 0f) {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = AqMotion.DurationMediumMs,
                    easing = AqMotion.Easings.Standard,
                ),
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Vertical,
                enabled = enabled && !isRefreshing,
                state = rememberDraggableState { delta ->
                    val target = (offsetY.value + delta * 0.5f).coerceIn(0f, triggerDistancePx * 1.5f)
                    coroutineScope.launch {
                        offsetY.snapTo(target)
                    }
                },
                onDragStopped = {
                    if (offsetY.value >= triggerDistancePx) {
                        coroutineScope.launch {
                            offsetY.animateTo(
                                targetValue = triggerDistancePx,
                                animationSpec = tween(
                                    durationMillis = AqMotion.DurationMediumMs,
                                    easing = AqMotion.Easings.Standard,
                                ),
                            )
                        }
                        onRefresh()
                    } else {
                        coroutineScope.launch {
                            offsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = AqMotion.DurationMediumMs,
                                    easing = AqMotion.Easings.Standard,
                                ),
                            )
                        }
                    }
                },
            ),
    ) {
        // Contenido principal desplazado suavemente durante el arrastre
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, offsetY.value.roundToInt()) },
        ) {
            content()
        }

        // Indicador flotante circular centrado en la parte superior
        if (offsetY.value > 0f || isRefreshing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset {
                        val indicatorOffset = if (isRefreshing) {
                            triggerDistancePx
                        } else {
                            offsetY.value
                        }
                        IntOffset(0, (indicatorOffset - with(density) { AqMetrics.ControlMinSize.toPx() }).roundToInt())
                    }
                    .aqElevation(AqElevation.Shadow2, AqShapes.Pill)
                    .clip(AqShapes.Pill)
                    .background(AqColor.Surface)
                    .padding(AqSpacing.Md),
                contentAlignment = Alignment.Center,
            ) {
                AqSpinner(
                    contentDescription = refreshingLabel,
                    modifier = Modifier.size(AqMetrics.IconSize),
                )
            }
        }
    }
}
