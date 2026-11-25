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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
    thumbSize: Dp = AqSize.X3s,
    affectsHeight: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    require(progress in 0f..1f) { "AqProgressPin.progress debe estar entre 0 y 1" }
    val clamped = progress.coerceIn(0f, 1f)

    // Geometría vertical (Dp) derivada del painter de Moon:
    // thumbTop = barZoneH/2 - thumbR; arrowTip = thumbTop - distance;
    // circleBottom = arrowTip - arrowHeight; circleTop = circleBottom - pinWidth.
    val barZoneHeight = maxOf(barHeight, thumbSize)
    val thumbRadius = thumbSize / 2
    val thumbTop = barZoneHeight / 2 - thumbRadius
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
                .height(barZoneHeight),
        ) {
            val density = LocalDensity.current
            val widthPx = with(density) { maxWidth.toPx() }
            val pinWidthPx = with(density) { AqLayout.ProgressPinWidth.toPx() }
            val thumbSizePx = with(density) { thumbSize.toPx() }
            val arrowWidthPx = with(density) { AqLayout.ProgressPinArrowWidth.toPx() }

            val pinX = (clamped * widthPx - pinWidthPx / 2f)
                .coerceIn(0f, (widthPx - pinWidthPx).coerceAtLeast(0f))
            val thumbX = (clamped * widthPx - thumbSizePx / 2f)
                .coerceIn(0f, (widthPx - thumbSizePx).coerceAtLeast(0f))

            // Barra (contenido del llamador), centrada verticalmente.
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(barHeight),
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
                    .shadow(
                        elevation = AqLayout.ProgressPinShadowElevation,
                        shape = AqSquircles.Circle,
                        ambientColor = AqColor.Popo,
                        spotColor = AqColor.Popo,
                    )
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
                    .size(thumbSize)
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
