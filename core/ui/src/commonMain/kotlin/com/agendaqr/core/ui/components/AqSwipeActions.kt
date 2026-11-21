package com.agendaqr.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Modelo de acción de deslizamiento revelada al arrastrar una fila de lista.
 */
data class AqSwipeAction(
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val onAction: () -> Unit,
)

/**
 * Contenedor de fila con acciones contextuales al deslizar (swipe actions).
 * Nivel 3 (patrón nativo móvil estilizado con tokens Polaris).
 * Expone automáticamente `customActions` en semántica para accesibilidad TalkBack.
 */
@Composable
fun AqSwipeActions(
    actions: List<AqSwipeAction>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (actions.isEmpty() || !enabled) {
        Box(modifier = modifier) {
            content()
        }
        return
    }

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val reducedMotion = LocalReducedMotion.current
    val actionWidthPx = with(density) { AqMetrics.ControlMinSize.toPx() }
    val maxSwipePx = actionWidthPx * actions.size
    val offsetX = remember { Animatable(0f) }
    // Duración de las transiciones de deslizamiento; se anula con
    // movimiento reducido (contrato §7: eliminar la transición).
    val swipeSpec: TweenSpec<Float>? = if (reducedMotion) null else tween(
        durationMillis = AqMotion.DurationMediumMs,
        easing = AqMotion.Easings.Standard,
    )
    /** Desplaza el offset al target; con movimiento reducido es un snap
     *  inmediato en lugar de una animación interpolada. */
    suspend fun swipeTo(target: Float) {
        if (swipeSpec == null) {
            offsetX.snapTo(target)
        } else {
            offsetX.animateTo(targetValue = target, animationSpec = swipeSpec)
        }
    }

    val accessibilityModifier = Modifier.semantics(mergeDescendants = true) {
        customActions = actions.map { action ->
            CustomAccessibilityAction(label = action.label) {
                action.onAction()
                true
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
            .then(accessibilityModifier),
    ) {
        // Capa de fondo: acciones reveladas a la derecha
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(AqColor.SurfaceSecondary),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions.forEach { action ->
                    val resolvedBg = action.backgroundColor ?: AqColor.DangerFill
                    val resolvedFg = action.contentColor ?: AqColor.InverseOnSurface

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(AqMetrics.ControlMinSize)
                            .background(resolvedBg)
                            .clickable {
                                coroutineScope.launch {
                                    swipeTo(0f)
                                }
                                action.onAction()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        AqIcon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = resolvedFg,
                            size = AqMetrics.IconSizeCompact,
                        )
                    }
                }
            }
        }

        // Capa superior: contenido deslizable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .background(AqColor.Surface)
                .draggable(
                    orientation = Orientation.Horizontal,
                    enabled = enabled,
                    state = rememberDraggableState { delta ->
                        val target = (offsetX.value + delta).coerceIn(-maxSwipePx, 0f)
                        coroutineScope.launch {
                            offsetX.snapTo(target)
                        }
                    },
                    onDragStopped = {
                        val threshold = -maxSwipePx / 2f
                        val target = if (offsetX.value < threshold) -maxSwipePx else 0f
                        coroutineScope.launch {
                            swipeTo(target)
                        }
                    },
                ),
        ) {
            content()
        }
    }
}
