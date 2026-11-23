package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqTouchTarget
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Etiqueta sobre [MoonTag] (`moon_design` 1.1.0, `widgets/tag`).
 *
 * Insignia pequeña y no seleccionable usada para identificación de datos
 * (estado de comprobante, tipo de moneda, etc.). Nunca comunica
 * «Verificado» ni «Pagado» (ADR-0025, D-14).
 *
 * - Tamaños: x2s 16 / xs 24 / sm 32 dp ([AqLayout.TagHeight*]).
 * - Radio: Xs / Xs / Sm ([AqCorner]) vía [AqSquircles].
 * - Relleno horizontal: 8 / 8 / 8 dp ([AqLayout.TagPaddingHorizontal*]).
 * - [tone] resuelve relleno y contenido desde los tonos de Moon.
 * - Área táctil ≥ 48 dp sin cambiar el aspecto visual ([AqTouchTarget],
 *   ADR-0039 §4.4).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqTag(
    text: String,
    tone: AqTone = AqTone.Neutral,
    size: AqTagSize = AqTagSize.Xs,
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val colors = scheme.toneColors(tone)
    // El alto visual se conserva (16/24/32 dp); el área táctil se amplía a 48
    // dp sin deformar la etiqueta ([AqTouchTarget], ADR-0039 §4.4).
    AqTouchTarget(
        minTouchSize = AqMetrics.ControlMinSize,
        text = text,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = size.height)
                .background(colors.container, AqSquircles.InteractiveXs)
                .padding(horizontal = size.horizontalPadding, vertical = AqSize.X2s)
                .semantics {
                    this.contentDescription = text
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSize.X2s),
        ) {
            Text(
                text = text,
                style = AqTextStyles.Tag.copy(fontSize = size.textSize),
                color = colors.content,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}

/** Tamaños de [AqTag] (tabla §3 del catálogo Moon). */
enum class AqTagSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val textSize: androidx.compose.ui.unit.TextUnit,
) {
    X2s(AqLayout.TagHeightX2s, AqLayout.TagPaddingHorizontalX2s, com.agendaqr.core.ui.theme.AqType.CaptionSmall),
    Xs(AqLayout.TagHeightXs, AqLayout.TagPaddingHorizontalXs, com.agendaqr.core.ui.theme.AqType.CaptionSmall),
    Sm(AqLayout.TagHeightSm, AqLayout.TagPaddingHorizontalSm, com.agendaqr.core.ui.theme.AqType.BodyXs),
}
