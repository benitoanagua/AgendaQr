package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Indicador de puntos sobre [MoonDotIndicator] (`moon_design` 1.1.0,
 * `widgets/dot_indicator`). Serie de puntos para paginación o pasos.
 *
 * - Tamaño `sizes.x4s` (8 dp) y hueco `sizes.x4s` (8 dp) por punto
 *   (`dot_indicator_theme.dart`).
 * - Colores: seleccionado `piccolo`→[AqColor.BrandFill], no seleccionado
 *   `beerus`→[AqColor.Border].
 * - Transición de color de 200 ms `easeInOutCubic` por punto (Moon anima un
 *   `ColorTween` premultiplicado por cada punto; aquí cada punto interpola su
 *   propio color con la misma curva).
 * - [total]: número total de puntos; [selected]: índice activo (0-based).
 * - [contentDescription]: copy accesible del grupo; lo aporta el llamador.
 */
@Composable
fun AqDotIndicator(
    total: Int,
    selected: Int = 0,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    require(total > 0) { "AqDotIndicator.total debe ser mayor que 0" }
    require(selected in 0 until total) { "AqDotIndicator.selected fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            },
        horizontalArrangement = Arrangement.spacedBy(AqLayout.DotIndicatorGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val isSelected = index == selected
            val dotColor by animateColorAsState(
                targetValue = if (isSelected) AqColor.BrandFill else AqColor.Border,
                animationSpec = tween(
                    durationMillis = AqMotion.DurationMediumMs,
                    easing = AqMotion.Easings.InOutCubic,
                ),
                label = "aqDot$index",
            )
            Box(
                modifier = Modifier
                    .size(AqLayout.DotIndicatorSize)
                    .background(dotColor, AqSquircles.Circle),
            )
        }
    }
}
