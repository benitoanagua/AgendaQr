package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaShapes
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Feedback y estados Xauxa (spec §12 V1.2): skeleton, paginación y
 * encuadre del scanner.
 */

/** Skeleton estático sin movimiento decorativo continuo. */
@Composable
fun XauxaSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.fillMaxWidth(fraction = XauxaSkeletonTitleFraction).heightIn(min = XauxaSpacing.Xl).background(XauxaColor.Surface2, XauxaShapes.ExtraSmall))
        repeat(lines.coerceAtLeast(1)) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = XauxaSpacing.Sm).background(XauxaColor.Surface2, XauxaShapes.ExtraSmall))
        }
    }
}

private const val XauxaSkeletonTitleFraction = 0.6f

/**
 * Footer de "Cargar más". La paginación se compone de este footer
 * (inv. 23): no existe un componente de paginación duplicado.
 */
@Composable
fun XauxaLoadMoreFooter(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    endReached: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    /** copy desde el llamador (core/ui no hardcodea copy). */
    loadMoreLabel: String = "",
    loadingLabel: String = "",
    endLabel: String = "",
) {
    if (onLoadMore != null) require(loadMoreLabel.isNotBlank()) { "loadMoreLabel requerido" }
    Box(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            endReached -> Text(endLabel, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            isLoading -> Row(
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(XauxaSpacing.Xxl),
                    color = XauxaColor.BrandText,
                )
                Text(loadingLabel, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            onLoadMore != null -> XauxaSecondaryButton(label = loadMoreLabel, onClick = onLoadMore)
        }
    }
}

/**
 * Viewport de cámara/escáner. El encuadre es preview real; el escaneo real
 * requiere cámara y queda por plataforma (ver contrato).
 */
@Composable
fun XauxaScannerViewport(
    modifier: Modifier = Modifier,
    scanning: Boolean = false,
    /** copy del llamador; el DS no hardcodea. */
    hint: String = "",
    /** Texto del estado "escaneando". */
    scanningLabel: String = "",
) {
    if (scanning) require(scanningLabel.isNotBlank()) { "scanningLabel requerido" }
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Bloque tonal que enmarca la guía de captura (S03 congelado).
            .background(XauxaColor.Surface2)
            .padding(XauxaSpacing.Xxxl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            XauxaScannerFrame {
                Text(
                    if (scanning) scanningLabel else hint,
                    fontSize = XauxaType.Caption,
                    color = XauxaColor.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(XauxaSpacing.Sm),
                )
            }
        }
    }
}

/**
 * Encuadre del scanner (excepción E-03): marco funcional de marca de 2 dp
 * ([XauxaMetrics.BorderStrong] + [XauxaColor.Brand]) — S03 congelado
 * ("marco con borde de marca"), guía visual ACTIVA de captura, no
 * decoración. Forma parte del contrato de [XauxaScannerViewport]: la
 * cámara de S03 lo compone SOBRE su preview real (sin fondo propio,
 * para no tapar la imagen de la cámara).
 */
@Composable
fun XauxaScannerFrame(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(XauxaMetrics.QrPreviewSize)
            .border(BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand), XauxaShapes.Medium),
        contentAlignment = Alignment.Center,
    ) {
        content?.invoke()
    }
}
