package com.agendaqr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.aqPinShadowPlatform
import kotlin.math.roundToInt

/**
 * Clavo de progreso sobre [MoonProgressPin]
 * (`moon_design` 1.1.0, `widgets/progress_pin/progress_pin.dart` +
 * `progress_pin_painter.dart`).
 *
 * Superpone un clavo circular con el porcentaje y una flecha sobre la
 * barra de progreso [content] (típicamente el contenido de
 * [AqLinearProgress]), con un pulgar circular en la posición de
 * [progress].
 *
 * Geometría Moon (`progress_pin_theme.dart`):
 * - Pin circular de [AqLayout.ProgressPinWidth] (36 dp) con borde
 *   `goten`→[AqColor.OnBrandFill] de [AqLayout.ProgressPinBorderWidth]
 *   (2 dp), interior `popo`→[AqColor.Popo] y texto `body.text10`→
 *   [AqType.Body10] `goten`.
 * - Flecha de base [AqLayout.ProgressPinArrowWidth] (8 dp) y alto
 *   [AqLayout.ProgressPinArrowHeight] (6 dp), separada del pulgar por
 *   [AqLayout.ProgressPinDistance] (4 dp).
 * - Pulgar de [thumbSize] en `goten`; sombra `popo` de elevación
 *   [AqLayout.ProgressPinShadowElevation] (6 dp).
 * - [affectsHeight]: reserva el espacio del clavo sobre la barra
 *   (`pinAffectsHeight` de Moon); `false` lo dibuja como overlay.
 */
@Composable
fun AqProgressPin(
    progress: Float,
    pinText: String,
    modifier: Modifier = Modifier,
    barHeight: Dp = AqSize.X4s,
    thumbSize: Dp? = null,
    affectsHeight: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    require(progress in 0f..1f) { "AqProgressPin.progress debe estar entre 0 y 1" }
    val clamped = progress.coerceIn(0f, 1f)

    // Geometría del painter de Moon (`progress_pin_painter.dart`): el pin,
    // la flecha y el pulgar son paint puro sobre el contenido — el pulgar
    // nunca engorda el layout. Con [thumbSize] explícito el radio es
    // `thumbSizeValue/2`; sin él Moon usa `size.height/2` (la mitad del
    // alto real del contenido), lo que produce el "pulgar gigante" cuando
    // `MoonProgressPin` envuelve un hijo alto sin `pinStyle`.
    var contentHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val measuredHeight = if (contentHeightPx > 0) {
        with(density) { contentHeightPx.toDp() }
    } else {
        barHeight
    }
    val barZoneHeight = thumbSize?.let { maxOf(barHeight, it) } ?: barHeight
    // Referencia vertical: la zona de la barra. Con thumb explícito es la
    // altura forzada; sin él, el alto natural del contenido medido.
    val contentHeight = thumbSize?.let { barZoneHeight } ?: measuredHeight
    val thumbRadius = thumbSize?.let { it / 2 } ?: (contentHeight / 2)
    val thumbSizeDp = thumbRadius * 2

    // thumbTop = contentH/2 - thumbR; arrowTip = thumbTop - distance;
    // circleTop = arrowTip - arrowHeight - pinWidth.
    val thumbTop = contentHeight / 2 - thumbRadius
    val arrowTipY = thumbTop - AqLayout.ProgressPinDistance
    val arrowTopY = arrowTipY - AqLayout.ProgressPinArrowHeight
    val circleTopY = arrowTopY - AqLayout.ProgressPinWidth
    val pinZoneHeight = -circleTopY

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = pinText },
    ) {
        if (affectsHeight) {
            Spacer(modifier = Modifier.height(pinZoneHeight))
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (thumbSize != null) Modifier.height(barZoneHeight) else Modifier,
                )
                .onSizeChanged { contentHeightPx = it.height },
        ) {
            val widthPx = with(density) { maxWidth.toPx() }
            val pinWidthPx = with(density) { AqLayout.ProgressPinWidth.toPx() }
            val thumbSizePx = with(density) { thumbSizeDp.toPx() }
            val arrowWidthPx = with(density) { AqLayout.ProgressPinArrowWidth.toPx() }

            val pinX = (clamped * widthPx - pinWidthPx / 2f)
                .coerceIn(0f, (widthPx - pinWidthPx).coerceAtLeast(0f))
            val thumbX = (clamped * widthPx - thumbSizePx / 2f)
                .coerceIn(0f, (widthPx - thumbSizePx).coerceAtLeast(0f))

            // Sombra `drawShadow` de la silueta pin+flecha. Se dibuja desde
            // la zona visible: si el pin queda fuera de pantalla (pin
            // externo de `MoonProgressPin`) su propio nodo se descartaría
            // por visibilidad y la sombra no sangraría, a diferencia del
            // painter único de Moon.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .aqPinShadowPlatform(
                        pinCenterX = pinX + pinWidthPx / 2f,
                        pinCenterY = with(density) { circleTopY.toPx() } +
                            pinWidthPx / 2f,
                        pinRadius = pinWidthPx / 2f,
                        arrowHalfWidth = arrowWidthPx / 2f,
                        arrowBaseY = with(density) { arrowTopY.toPx() },
                        arrowHeight = with(density) {
                            AqLayout.ProgressPinArrowHeight.toPx()
                        },
                        blur = with(density) {
                            AqLayout.ProgressPinShadowBlur.toPx()
                        },
                        offsetY = with(density) {
                            AqLayout.ProgressPinShadowOffset.toPx()
                        },
                        alpha = AqLayout.ProgressPinShadowAlpha,
                    ),
            )

            // Barra (contenido del llamador), centrada verticalmente.
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .then(
                        if (thumbSize != null) Modifier.height(barHeight) else Modifier,
                    ),
            ) {
                content()
            }

            // Flecha del clavo (triángulo `goten`).
            AqProgressPinArrow(
                modifier = Modifier.offset {
                    IntOffset(
                        (pinX + pinWidthPx / 2f - arrowWidthPx / 2f).roundToInt(),
                        with(density) { arrowTopY.toPx().roundToInt() },
                    )
                },
            )

            // Clavo circular: borde goten + interior popo + texto goten.
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            pinX.roundToInt(),
                            with(density) { circleTopY.toPx().roundToInt() },
                        )
                    }
                    .size(AqLayout.ProgressPinWidth)
                    .border(
                        width = AqLayout.ProgressPinBorderWidth,
                        color = AqColor.OnBrandFill,
                        shape = AqSquircles.Circle,
                    )
                    .padding(AqLayout.ProgressPinBorderWidth)
                    .clip(AqSquircles.Circle)
                    .background(AqColor.Popo),
                contentAlignment = Alignment.Center,
            ) {
                AqText(
                    text = pinText,
                    size = AqType.Body10,
                    color = AqColor.OnBrandFill,
                )
            }

            // Pulgar `goten` en la posición del progreso.
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            thumbX.roundToInt(),
                            with(density) { thumbTop.toPx().roundToInt() },
                        )
                    }
                    .size(thumbSizeDp)
                    .clip(AqSquircles.Circle)
                    .background(AqColor.OnBrandFill),
            )
        }
    }
}

/** Triángulo de la flecha del clavo (base [AqLayout.ProgressPinArrowWidth],
 *  alto [AqLayout.ProgressPinArrowHeight], color `goten`). */
@Composable
private fun AqProgressPinArrow(modifier: Modifier = Modifier) {
    val arrowColor = AqColor.OnBrandFill
    Box(
        modifier = modifier.size(
            width = AqLayout.ProgressPinArrowWidth,
            height = AqLayout.ProgressPinArrowHeight,
        ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w / 2f, h)
                close()
            }
            drawPath(path, color = arrowColor)
        }
    }
}
