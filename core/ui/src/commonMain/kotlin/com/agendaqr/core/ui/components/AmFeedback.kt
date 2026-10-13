package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/**
 * Feedback de proceso: skeleton, paginación, marco de
 * medios y encuadre del scanner.
 */

/** Skeleton estático sin movimiento decorativo continuo. */
@Composable
fun AmSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AmSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = AmSkeletonTitleFraction)
                .heightIn(min = AmSpacing.Xl)
                .background(AmColor.SurfaceSecondary, AmShapes.Small),
        )
        repeat(lines.coerceAtLeast(1)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AmSpacing.Sm)
                    .background(AmColor.SurfaceSecondary, AmShapes.Small),
            )
        }
    }
}

private const val AmSkeletonTitleFraction = 0.6f

/**
 * Footer de "Cargar más". La paginación se compone de este footer: no
 * existe un componente de paginación duplicado.
 */
@Composable
fun AmLoadMoreFooter(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    endReached: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    /** copy desde el llamador (el DS no hardcodea copy). */
    loadMoreLabel: String = "",
    loadingLabel: String = "",
    endLabel: String = "",
) {
    if (onLoadMore != null) require(loadMoreLabel.isNotBlank()) { "loadMoreLabel requerido" }
    Box(
        modifier = modifier.fillMaxWidth().padding(AmSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            endReached -> Text(endLabel, fontSize = AmType.Caption, color = AmColor.TextSecondary)
            isLoading -> Row(
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AmSpacing.Xxl),
                    color = AmColor.AccentText,
                )
                Text(loadingLabel, fontSize = AmType.Label, color = AmColor.TextSecondary)
            }
            onLoadMore != null -> AmSecondaryButton(label = loadMoreLabel, onClick = onLoadMore)
        }
    }
}

/**
 * Encuadre del scanner: guía visual ACTIVA de captura — cuatro esquinas
 * de acento dibujadas en canvas (patrón contemporáneo de encuadre QR).
 * La cámara de producto lo compone SOBRE su preview real (sin fondo
 * propio, para no tapar la imagen de la cámara).
 */
@Composable
fun AmScannerFrame(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    val density = LocalDensity.current
    val cornerLength = with(density) { AmMetrics.ScannerCornerLength.toPx() }
    val stroke = with(density) { AmMetrics.BorderStrong.toPx() }
    Box(
        modifier = modifier
            .size(AmMetrics.QrPreviewSize)
            .amScannerCorners(
                color = AmColor.Accent,
                cornerLength = cornerLength,
                stroke = stroke,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content?.invoke()
    }
}

private fun Modifier.amScannerCorners(
    color: Color,
    cornerLength: Float,
    stroke: Float,
): Modifier = drawBehind {
    val w = size.width
    val h = size.height
    val len = cornerLength.coerceAtMost(w / 2f).coerceAtMost(h / 2f)
    val half = stroke / 2f
    val segments = listOf(
        // Esquina superior izquierda.
        Offset(half, len) to Offset(half, half),
        Offset(half, half) to Offset(len, half),
        // Esquina superior derecha.
        Offset(w - len, half) to Offset(w - half, half),
        Offset(w - half, half) to Offset(w - half, len),
        // Esquina inferior derecha.
        Offset(w - half, h - len) to Offset(w - half, h - half),
        Offset(w - half, h - half) to Offset(w - len, h - half),
        // Esquina inferior izquierda.
        Offset(len, h - half) to Offset(half, h - half),
        Offset(half, h - half) to Offset(half, h - len),
    )
    segments.forEach { (start, end) ->
        drawLine(color, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

/**
 * Marco de medios (QR, comprobante): contenedor tonal
 * [AmColor.SurfaceSecondary] con la forma grande de tokens. El límite
 * del contenido es el bloque tonal; el fondo interno del contenido (p.
 * ej. el lienzo claro del QR, [AmColor.QrCanvas]) lo aporta quien lo
 * dibuja.
 */
@Composable
fun AmMediaFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(AmColor.SurfaceSecondary, AmShapes.Large),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
