package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.testTag
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Ítem del carrusel horizontal de favoritos (S01).
 */
data class AqCarouselItem(val id: String, val label: String? = null)

/**
 * Carrusel horizontal de tarjetas justificado por el carrusel de favoritos
 * de Inicio (S01). La siguiente tarjeta asoma como indicio visual, sin
 * instrucción de deslizamiento (UX V1: los gestos nunca son la única forma
 * de acción; el scroll horizontal es suficiente).
 *
 * - Ítems con ancho fijo y objetivo táctil ≥ 48 dp.
 * - Espaciado entre ítems con tokens del sistema.
 * - Primer y último ítem con margen de pantalla para alinearse al borde
 *   y que el siguiente asome.
 * - Sin animación decorativa; el scroll es nativo de la plataforma.
 */
@Composable
fun AqCarousel(
    items: List<AqCarouselItem>,
    itemWidth: Dp = AqMetrics.FavCardWidth,
    itemSpacing: Dp = AqSpacing.Md,
    modifier: Modifier = Modifier,
    content: @Composable (AqCarouselItem) -> Unit,
) {
    require(items.isNotEmpty()) { "AqCarousel necesita al menos un ítem" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        items.forEachIndexed { index, item ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(itemWidth)
                    .height(AqMetrics.ControlMinSize)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .then(
                        if (index == 0) {
                            Modifier.padding(start = AqSpacing.ScreenMargin)
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (index == items.lastIndex) {
                            Modifier.padding(end = AqSpacing.ScreenMargin)
                        } else {
                            Modifier
                        },
                    )
                    .testTag("aq-carousel-item-${item.id}"),
            ) {
                content(item)
            }
        }
    }
}

/**
 * Hairline del sistema: separador sutil entre filas/controles.
 */
@Composable
fun AqHairline(
    modifier: Modifier = Modifier,
    insetStart: Dp = AqSpacing.None,
) {
    Box(
        modifier = if (insetStart > AqSpacing.None) {
            Modifier.padding(start = insetStart)
        } else {
            Modifier
        }.then(modifier)
            .fillMaxWidth()
            .height(AqMetrics.DividerHeight)
            .background(AqColor.Divider),
    )
}
