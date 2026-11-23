package com.agendaqr.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
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
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.core.ui.theme.AqRadius
import com.agendaqr.core.ui.theme.AqSquircles
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Contenedor de recarga vertical por arrastre (Pull to refresh).
 * Nivel 3 (patrón nativo móvil estilizado con tokens Moon).
 * Muestra el spinner canónico de Moon cuando el usuario tira hacia abajo.
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
    val reducedMotion = LocalReducedMotion.current
    val triggerDistancePx = with(density) { (AqMetrics.ControlMinSize + AqSpacing.Lg).toPx() }
    val offsetY = remember { Animatable(0f) }
    // Duración de las transiciones de arrastre; se anula con movimiento
    // reducido (contrato §7: eliminar la transición).
    val pullSpec: TweenSpec<Float>? = if (reducedMotion) null else tween(
        durationMillis = AqMotion.DurationMediumMs,
        easing = AqMotion.Easings.Standard,
    )
    /** Desplaza el offset al target; con movimiento reducido es un snap
     *  inmediato en lugar de una animación interpolada. */
    suspend fun pullTo(target: Float) {
        if (pullSpec == null) {
            offsetY.snapTo(target)
        } else {
            offsetY.animateTo(targetValue = target, animationSpec = pullSpec)
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && offsetY.value != 0f) {
            coroutineScope.launch {
                pullTo(0f)
            }
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
                            pullTo(triggerDistancePx)
                        }
                        onRefresh()
                    } else {
                        coroutineScope.launch {
                            pullTo(0f)
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
                    .aqShadow(AqShadow.Sm, AqSquircles.Circle)
                    .background(AqColor.Surface)
                    .padding(AqSpacing.Md),
                contentAlignment = Alignment.Center,
            ) {
                AqCircularLoader(
                    loaderSize = AqMetrics.IconSize,
                    contentDescription = refreshingLabel,
                )
            }
        }
    }
}
