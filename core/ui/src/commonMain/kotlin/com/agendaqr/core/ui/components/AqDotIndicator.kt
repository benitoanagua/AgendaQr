package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Indicador de puntos sobre [MoonDotIndicator] (`moon_design` 1.1.0,
 * `widgets/dot_indicator`). Serie de puntos para paginación o pasos, con
 * el punto activo resaltado.
 *
 * - [total]: número total de puntos.
 * - [selected]: índice del punto activo (0-based).
 * - Cada punto conserva el objetivo táctil ≥ 48 dp (D-02).
 * - Todo el copy (etiquetas de acceso) llega del llamador.
 */
@Composable
fun AqDotIndicator(
    total: Int,
    selected: Int = 0,
    modifier: Modifier = Modifier,
) {
    require(total > 0) { "AqDotIndicator.total debe ser mayor que 0" }
    require(selected in 0 until total) { "AqDotIndicator.selected fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = "Página ${selected + 1} de $total"
            },
        horizontalArrangement = Arrangement.spacedBy(AqSize.X2s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val active = index == selected
            Box(
                modifier = Modifier
                    .size(if (active) AqSize.Sm else AqSize.Xs)
                    .heightIn(min = AqSize.Xs)
                    .background(
                        if (active) AqColor.EmphasisFill else AqColor.TextTertiary,
                        AqSquircles.Circle,
                    ),
            )
            if (index < total - 1) {
                Spacer(modifier = Modifier.width(AqSize.X2s))
            }
        }
    }
}
