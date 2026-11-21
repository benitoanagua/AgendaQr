package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqComposition
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Feedback de proceso: skeleton, paginación, marco de
 * medios y encuadre del scanner.
 */

/** Skeleton estático sin movimiento decorativo continuo. Los bloques
 * usan [AqColor.Surface] (distinto del fondo en ambos temas) para que la
 * carga se perciba también en tema claro. */
@Composable
fun AqSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AqSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = AqComposition.SkeletonTitleWidthFraction)
                .heightIn(min = AqSpacing.Xl)
                .background(AqColor.Surface, AqShapes.Small),
        )
        repeat(lines.coerceAtLeast(1)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AqSpacing.Sm)
                    .background(AqColor.Surface, AqShapes.Small),
            )
        }
    }
}

/**
 * Footer de "Cargar más". La paginación se compone de este footer: no
 * existe un componente de paginación duplicado.
 */
@Composable
fun AqLoadMoreFooter(
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
        modifier = modifier.fillMaxWidth().padding(AqSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            endReached -> Text(endLabel, fontSize = AqType.Caption, color = AqColor.TextSecondary)
            isLoading -> Row(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AqSpacing.Xxl),
                    color = AqColor.EmphasisFill,
                )
                Text(loadingLabel, fontSize = AqType.Label, color = AqColor.TextSecondary)
            }
            onLoadMore != null -> AqSecondaryButton(label = loadMoreLabel, onClick = onLoadMore)
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
fun AqScannerFrame(
    modifier: Modifier = Modifier,
    /** Lado del encuadre; por defecto el de la vista previa de QR. */
    size: Dp = AqMetrics.QrPreviewSize,
    content: (@Composable () -> Unit)? = null,
) {
    val density = LocalDensity.current
    val cornerLength = with(density) { AqMetrics.ScannerCornerLength.toPx() }
    val stroke = with(density) { AqMetrics.BorderStrong.toPx() }
    Box(
        modifier = modifier
            .size(size)
            .aqScannerCorners(
                color = AqColor.EmphasisFill,
                cornerLength = cornerLength,
                stroke = stroke,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content?.invoke()
    }
}

private fun Modifier.aqScannerCorners(
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
 * Marco de medios (QR, comprobante): superficie [AqColor.Surface] con el
 * radio de tarjeta de la referencia (12, `radius-300`) — el límite del contenido es el
 * bloque tonal en ambos temas, sin bordes decorativos (el gate solo
 * admite E-01/E-02). El fondo interno del contenido (p. ej. el lienzo
 * claro del QR, [AqColor.QrCanvas]) lo aporta quien lo dibuja.
 */
@Composable
fun AqMediaFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(AqColor.Surface, AqShapes.Large),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Alerta persistente sobre [MoonAlert] / [MoonAlert.filled] /
 * [MoonAlert.outlined] (`moon_design` 1.1.0, `lib/src/widgets/alert`).
 * Superficie con radio squircle, borde opcional, ícono leading, etiqueta y
 * contenido opcional. El tono nunca se comunica solo por color: siempre
 * acompaña a un ícono y a un texto.
 *
 * [show] controla la visibilidad. [tone] define el par content/container.
 * [dismiss] es la acción de cierre (≥ 48 dp, con descripción accesible).
 */
@Composable
fun AqAlert(
    show: Boolean = true,
    tone: com.agendaqr.core.ui.components.AqTone = com.agendaqr.core.ui.components.AqTone.Info,
    /** copy del llamador. */
    label: String,
    /** Contenido opcional debajo de la etiqueta. */
    content: (@Composable () -> Unit)? = null,
    /** Acción de cierre; su descripción accesible la aporta el llamador. */
    dismiss: (() -> Unit)? = null,
    dismissLabel: String = "",
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
) {
    if (!show) return
    val toneColors = LocalAqColorScheme.current.toneColors(tone)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(AqSize.X2s)
            .background(toneColors.container, AqSquircles.InteractiveSm)
            .then(
                if (showBorder) {
                    Modifier.border(AqStroke.Default, toneColors.container, AqSquircles.InteractiveSm)
                } else {
                    Modifier
                },
            )
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X3s),
    ) {
        tone.iconVector?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqMetrics.IconSizeCompact,
                tint = toneColors.content,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(AqSize.X6s)) {
            Text(
                text = label,
                style = AqTextStyles.Body,
                color = toneColors.content,
            )
            content?.invoke()
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dismiss?.let {
                AqTextAction(
                    label = dismissLabel,
                    onClick = dismiss,
                    enabled = true,
                )
            }
        }
    }
}

/**
 * Indicador transitorio sobre [MoonToast] (`moon_design` 1.1.0,
 * `lib/src/widgets/toast`). Banner de recorrido corto. El host y la cola de
 * toasts se construyen sobre `AqFeedbackEvent` (flujo de eventos honestos);
 * este componente es la pieza visual.
 */
@Composable
fun AqToast(
    visible: Boolean = true,
    tone: com.agendaqr.core.ui.components.AqTone = com.agendaqr.core.ui.components.AqTone.Info,
    /** copy del llamador. */
    message: String,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    val toneColors = LocalAqColorScheme.current.toneColors(tone)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(AqSize.X2s)
            .background(toneColors.container, AqSquircles.InteractiveSm)
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X3s),
    ) {
        tone.iconVector?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqMetrics.IconSizeCompact,
                tint = toneColors.content,
            )
        }
        Text(
            text = message,
            style = AqTextStyles.Body,
            color = toneColors.content,
        )
    }
}

/**
 * Etiqueta flotante sobre [MoonTooltip] (`moon_design` 1.1.0,
 * `lib/src/widgets/tooltip`). Se muestra cuando [visible] es verdadero,
 * junto al objetivo [anchor]. El copy y la visibilidad los aporta el
 * llamador.
 */
@Composable
fun AqTooltip(
    visible: Boolean = true,
    /** copy del llamador. */
    label: String,
    /** Objetivo al que se ancla la etiqueta (proporciona el anclaje). */
    anchor: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    val toneColors = LocalAqColorScheme.current.toneColors(com.agendaqr.core.ui.components.AqTone.Info)
    Box(
        modifier = modifier
            .background(toneColors.container, AqSquircles.InteractiveSm)
            .padding(AqSize.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AqTextStyles.Caption,
            color = toneColors.content,
        )
    }
    anchor?.invoke()
}

/** Tipo de mensaje de feedback (re-exportado para claridad en esta capa). */
typealias AqFeedbackTone = com.agendaqr.core.ui.components.AqTone

