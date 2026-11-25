package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import kotlinx.coroutines.delay

/**
 * Ítem del carrusel de [AqCarousel].
 */
data class AqCarouselItem(val id: String, val label: String? = null)

/**
 * Carrusel sobre [MoonCarousel] (`moon_design` 1.1.0,
 * `widgets/carousel/carousel.dart` + `carousel_theme.dart`).
 *
 * - [itemExtent]: ancho máximo de cada ítem (Moon `itemExtent`, requerido).
 * - [gap]: hueco entre ítems; por defecto `sizes.x2s` (16 dp), como en
 *   `carousel_properties.dart`.
 * - [isCentered]: el ítem seleccionado queda centrado en el viewport
 *   (defecto Moon `true`); se aproxima con `contentPadding` simétrico
 *   cuando el ítem es más estrecho que el viewport.
 * - [loop]: al llegar al final vuelve al primero (salto documentado; Moon
 *   usa índices virtuales infinitos).
 * - [autoPlay]: avanza un ítem cada [autoPlayDelayMs] (defecto Moon 3 s)
 *   con la transición de plataforma (~[AqMotion.CarouselTransitionDurationMs]
 *   ms, aproximación de `fastOutSlowIn`).
 * - [onIndexChanged]: índice del primer ítem visible, como
 *   `onIndexChanged` de Moon.
 * - [vertical]: `axisDirection` de Moon (`Axis.vertical` cuando es true).
 */
@Composable
fun AqCarousel(
    items: List<AqCarouselItem>,
    modifier: Modifier = Modifier,
    itemExtent: Dp = AqMetrics.FavCardWidth,
    gap: Dp = AqLayout.CarouselGap,
    isCentered: Boolean = true,
    loop: Boolean = false,
    autoPlay: Boolean = false,
    autoPlayDelayMs: Long = AqMotion.CarouselAutoPlayDelayMs,
    onIndexChanged: ((Int) -> Unit)? = null,
    vertical: Boolean = false,
    content: @Composable (AqCarouselItem) -> Unit,
) {
    require(items.isNotEmpty()) { "AqCarousel necesita al menos un ítem" }
    require(itemExtent > AqLayout.Zero) { "AqCarousel.itemExtent debe ser positivo" }
    val listState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState, onIndexChanged) {
        if (onIndexChanged == null) return@LaunchedEffect
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { onIndexChanged(it) }
    }
    LaunchedEffect(autoPlay, items.size, listState, loop) {
        if (!autoPlay || items.size < 2) return@LaunchedEffect
        while (true) {
            delay(autoPlayDelayMs)
            val current = listState.firstVisibleItemIndex
            val next = if (loop) {
                (current + 1) % items.size
            } else {
                (current + 1).coerceAtMost(items.size - 1)
            }
            if (next == current) return@LaunchedEffect
            listState.animateScrollToItem(next)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val centering = if (isCentered) {
            if (vertical) {
                PaddingValues(vertical = (maxHeight - itemExtent).coerceAtLeast(AqLayout.Zero) / 2)
            } else {
                PaddingValues(horizontal = (maxWidth - itemExtent).coerceAtLeast(AqLayout.Zero) / 2)
            }
        } else {
            PaddingValues(AqLayout.Zero)
        }
        if (vertical) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = snapBehavior,
                contentPadding = centering,
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                    Box(
                        modifier = Modifier
                            .height(itemExtent)
                            .testTag("aq-carousel-item-${item.id}"),
                    ) {
                        content(item)
                    }
                }
            }
        } else {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = snapBehavior,
                contentPadding = centering,
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                    Box(
                        modifier = Modifier
                            .width(itemExtent)
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .testTag("aq-carousel-item-${item.id}"),
                    ) {
                        content(item)
                    }
                }
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
