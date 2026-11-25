package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqTouchTarget
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Etiqueta sobre [MoonTag] (`moon_design` 1.1.0, `widgets/tag` +
 * `tag_sizes.dart`). Insignia pequeña y no seleccionable usada para
 * identificación de datos (estado de comprobante, tipo de moneda, etc.).
 * Nunca comunica «Verificado» ni «Pagado» (ADR-0025, D-14).
 *
 * - Tamaños: x2s 16 / xs 24 / sm 32 dp de alto (`sizes.x2s/xs/sm`).
 * - Radio: interactiveXs / interactiveXs / interactiveSm (`borders`).
 * - Relleno horizontal: x4s / x4s / x2s; hueco interno x6s / x6s / x4s.
 * - Glifo inicial: x3s / x2s / x2s (`iconSizeValue`).
 * - Texto: `body.text9` (9 sp) en x2s, `body.text12` (12 sp) en xs/sm.
 * - Colores Moon: fondo `goku`→[AqColor.Background], texto `textPrimary`,
 *   icono `iconPrimary`. [tone] es la extensión semántica de AgendaQr
 *   (estado del dato); `Neutral` reproduce la paleta Moon.
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
    icon: ImageVector? = null,
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
                .height(size.height)
                .background(colors.container, size.shape)
                .padding(horizontal = size.horizontalPadding)
                .semantics {
                    this.contentDescription = text
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(size.gap),
        ) {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = size.iconSize,
                    tint = colors.content,
                )
            }
            AqText(
                text = text,
                size = size.textSize,
                color = colors.content,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}

/** Tamaños de [AqTag] (Moon `tag_sizes.dart`). */
enum class AqTagSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val gap: Dp,
    val iconSize: Dp,
    val textSize: TextUnit,
    val shape: Shape,
) {
    X2s(
        AqLayout.TagHeightX2s,
        AqLayout.TagPaddingHorizontalX2s,
        AqLayout.TagGapX2s,
        AqLayout.TagIconSizeX2s,
        AqType.Body9,
        AqSquircles.InteractiveXs,
    ),
    Xs(
        AqLayout.TagHeightXs,
        AqLayout.TagPaddingHorizontalXs,
        AqLayout.TagGapXs,
        AqLayout.TagIconSizeXs,
        AqType.BodyXs,
        AqSquircles.InteractiveXs,
    ),
    Sm(
        AqLayout.TagHeightSm,
        AqLayout.TagPaddingHorizontalSm,
        AqLayout.TagGapSm,
        AqLayout.TagIconSizeSm,
        AqType.BodyXs,
        AqSquircles.InteractiveSm,
    ),
}
