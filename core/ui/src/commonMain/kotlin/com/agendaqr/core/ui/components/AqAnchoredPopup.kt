package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.agendaqr.core.ui.theme.AqSize
import kotlin.math.abs

/**
 * Utilidades de superficies flotantes ancladas fieles a Moon
 * (`widgets/dropdown`, `widgets/popover`, `widgets/tooltip` de
 * `moon_design` 1.1.0): posición centrada sobre el ancla, separación
 * `distanceToTarget` y `maxWidth` calculado desde la posición del ancla en
 * pantalla.
 */

/** Separación ancla↔superficie de Moon (`distanceToTarget` = `x4s`). */
internal val AqAnchorGap: Dp = AqSize.X4s

/** Margen horizontal que Moon descuenta del `maxWidth` (8 dp por lado). */
internal val AqAnchorMargin: Dp = AqSize.X2s

/**
 * `maxWidth` de Moon para posiciones `top`/`bottom` centradas:
 * `overlayWidth - |overlayWidth/2 - targetCenterX| * 2 - marginHorizontal`.
 * [anchorCenterXPx] son los límites del ancla en coordenadas de ventana
 * (px); null hasta el primer `onGloballyPositioned` (se usa el centro de
 * pantalla, mismo resultado que un ancla centrada).
 */
@Composable
internal fun aqAnchorMaxWidthDp(anchorCenterXPx: Float?): Dp {
    val density = LocalDensity.current
    val windowWidthPx = LocalWindowInfo.current.containerSize.width.toFloat()
    val center = anchorCenterXPx ?: (windowWidthPx / 2f)
    val marginPx = with(density) { AqAnchorMargin.toPx() }
    val maxPx = windowWidthPx - abs(windowWidthPx / 2f - center) * 2f - marginPx
    return with(density) { maxPx.coerceAtLeast(0f).toDp() }
}

/** Centro horizontal del ancla en coordenadas de ventana (px), para el popup. */
internal class AqAnchorPosition {
    // Estado observable: el Popup se compone antes del primer
    // `onGloballyPositioned` y debe recomponerse cuando llegue el centro
    // real para aplicar el `maxWidth` de Moon.
    var centerXPx by mutableStateOf<Float?>(null)
        private set

    fun update(left: Float, top: Float, right: Float, bottom: Float) {
        centerXPx = (left + right) / 2f
    }
}

/**
 * Posición estilo Moon para anclas `bottom`/`top` centradas: la superficie
 * se centra horizontalmente bajo (o sobre) el ancla con separación
 * [gapPx], sin limitarse a los bordes (Moon tampoco limita el seguidor).
 */
internal class AqCenteredAnchorPositionProvider(
    private val gapPx: Int,
    private val below: Boolean,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.center.x - popupContentSize.width / 2
        val y = if (below) {
            anchorBounds.bottom + gapPx
        } else {
            anchorBounds.top - gapPx - popupContentSize.height
        }
        return IntOffset(x, y)
    }
}

/**
 * Ancho "wrap-content con tope": el hijo se mide a su ancho intrínseco
 * máximo y se restringe a `constraints.maxWidth`. A diferencia de
 * `width(IntrinsicSize.Max)`, sí respeta el límite entrante — es lo que
 * permite que `widthIn(max = maxWidth)` de Moon limite la tarjeta sin
 * estirar el contenido cuando es más estrecho.
 */
internal fun Modifier.aqWrapIntrinsicWidth(): Modifier = layout { measurable, constraints ->
    val intrinsic = measurable.maxIntrinsicWidth(
        if (constraints.hasBoundedHeight) constraints.maxHeight else 0,
    )
    val target = intrinsic.coerceIn(constraints.minWidth, constraints.maxWidth)
    val placeable = measurable.measure(
        constraints.copy(minWidth = target, maxWidth = target),
    )
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

/**
 * Recuerda un [AqAnchorPosition] y devuelve el modifier que lo alimenta con
 * `onGloballyPositioned` (coordenadas de ventana en px).
 */
@Composable
internal fun rememberAqAnchor(): Pair<AqAnchorPosition, Modifier> {
    val position = remember { AqAnchorPosition() }
    val modifier = Modifier.onGloballyPositioned { coordinates ->
        val positionInWindow = coordinates.positionInWindow()
        position.update(
            left = positionInWindow.x,
            top = positionInWindow.y,
            right = positionInWindow.x + coordinates.size.width,
            bottom = positionInWindow.y + coordinates.size.height,
        )
    }
    return position to modifier
}
