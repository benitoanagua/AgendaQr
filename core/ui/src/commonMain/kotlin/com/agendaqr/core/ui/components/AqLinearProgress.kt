package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType

/**
 * Barra de progreso determinada sobre [MoonLinearProgress]
 * (`moon_design` 1.1.0, `widgets/progress/linear_progress.dart` +
 * `linear_progress_theme.dart`/`_sizes.dart`).
 *
 * - [progress]: fracción 0..1 del avance.
 * - Tamaños Moon x6s/x5s/x4s/x3s/x2s (alturas 2/4/8/12/16 dp); el
 *   llamador pasa [height] y la geometría se resuelve por tabla
 *   (radios `surface*` de [AqCorner], pulgar y hueco de etiquetas).
 * - Colores `piccolo`→[AqColor.EmphasisFill], `beerus`→[AqColor.Border],
 *   texto de etiquetas `textPrimary`→[AqColor.TextPrimary].
 * - [showPin] superpone el clavo de [MoonProgressPin] vía
 *   [AqProgressPin]; [pinAffectsHeight] reserva su altura como en Moon.
 * - [showMinLabel]/[showMaxLabel] muestran etiquetas encima de la barra
 *   (`minLabel`/`maxLabel` personalizables; por defecto "0%"/"100%").
 * - Semántica de barra de progreso con [ProgressBarRangeInfo].
 */
@Composable
fun AqLinearProgress(
    progress: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    height: Dp = AqSize.X4s,
    color: Color = AqColor.EmphasisFill,
    trackColor: Color = AqColor.Border,
    showPin: Boolean = false,
    pinAffectsHeight: Boolean = true,
    showMinLabel: Boolean = false,
    showMaxLabel: Boolean = false,
    minLabel: (@Composable () -> Unit)? = null,
    maxLabel: (@Composable () -> Unit)? = null,
) {
    require(progress in 0f..1f) { "AqLinearProgress.progress debe estar entre 0 y 1" }
    val clamped = progress.coerceIn(0f, 1f)

    // Tabla Moon `linear_progress_sizes.dart`.
    val cornerRadius = when {
        height <= AqSize.X5s -> AqCorner.SurfaceXs
        height <= AqSize.X4s -> AqCorner.SurfaceSm
        height <= AqSize.X3s -> AqCorner.SurfaceMd
        else -> AqCorner.SurfaceLg
    }
    val thumbSize = if (height <= AqSize.X4s) AqSize.X3s else AqSize.X2s
    // `verticalGap` de `linear_progress_sizes.dart`: x6s/x5s → `sizes.x4s`,
    // x4s/x3s → 6 dp (literal de Moon, token propio), x2s → `sizes.x5s`.
    val labelGap = when {
        height <= AqSize.X5s -> AqLayout.ProgressLabelGap
        height <= AqSize.X3s -> AqLayout.ProgressLabelGapSmall
        else -> AqSize.X5s
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo(
                    clamped,
                    0f..1f,
                    (clamped * 100).toInt(),
                )
                liveRegion = LiveRegionMode.Polite
            },
    ) {
        if (showMinLabel || showMaxLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showMinLabel) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        minLabel?.invoke() ?: AqText(
                            text = "0%",
                            size = AqType.Body10,
                            color = AqColor.TextPrimary,
                        )
                    }
                }
                if (showMaxLabel) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        maxLabel?.invoke() ?: AqText(
                            text = "100%",
                            size = AqType.Body10,
                            color = AqColor.TextPrimary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(labelGap))
        }

        if (showPin) {
            AqProgressPin(
                progress = clamped,
                pinText = "${(clamped * 100).toInt()}%",
                barHeight = height,
                thumbSize = thumbSize,
                affectsHeight = pinAffectsHeight,
            ) {
                AqLinearTrack(
                    progress = clamped,
                    height = height,
                    cornerRadius = cornerRadius,
                    color = color,
                    trackColor = trackColor,
                )
            }
        } else {
            AqLinearTrack(
                progress = clamped,
                height = height,
                cornerRadius = cornerRadius,
                color = color,
                trackColor = trackColor,
            )
        }
    }
}
