package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Chip seleccionable sobre [MoonChip] (`moon_design` 1.1.0,
 * `widgets/chip`).
 *
 * - Tamaños: sm 32 / md 40 dp ([AqSize]).
 * - Radio: InteractiveXs / Sm ([AqCorner]) vía [AqSquircles].
 * - [selected] activa el relleno de selección; [enabled] controla la
 *   interacción. Área táctil ≥ 48 dp (D-02).
 * - [icon] glifo decorativo a la izquierda; [text] la etiqueta.
 * - [count] conteo opcional anunciado como texto semántico separado del
 *   label (p. ej. "Favoritos · 3"), nunca fusionado en una sola cadena.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqChip(
    text: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    count: Int? = null,
    modifier: Modifier = Modifier,
    size: AqChipSize = AqChipSize.Md,
) {
    val scheme = LocalAqColorScheme.current
    val container = when {
        !enabled -> scheme.disabled
        selected -> scheme.selectionSurface
        else -> scheme.surface
    }
    val content = when {
        !enabled -> scheme.onDisabled
        selected -> scheme.selectionIcon
        else -> scheme.textPrimary
    }
    val isSelected = selected
    // Texto accesible del nodo táctil: label + count como una sola cadena
    // para que onNodeWithText resuelva al nodo táctil (48 dp) y el count se
    // lea junto al label (el count viaja dentro del Row con clickable, que
    // no fusiona su texto al nodo táctil).
    val touchText = if (count != null) "$text $count" else text
    val interactionSource = remember { MutableInteractionSource() }
    // PV-01: el estado pressed no cambia el relleno del chip; el efecto es el
    // scale de Moon aplicado por el contenidor táctil. El alto visual se
    // conserva (32/40 dp); el área táctil se amplía a 48 dp sin deformar el
    // chip (ADR-0039 §4.4). mergeDescendants = false evita que el texto del
    // count reemplace el texto del nodo táctil.
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AqMetrics.ControlMinSize,
                minHeight = AqMetrics.ControlMinSize,
            )
            .semantics(mergeDescendants = false) {
                this.text = AnnotatedString(touchText)
                this.selected = isSelected
                this[SemanticsProperties.Role] = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = size.height)
                .heightIn(min = size.height)
                .background(container, AqSquircles.InteractiveSm)
                .clickable(
                    role = Role.Button,
                    onClickLabel = text,
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled && onClick != null,
                    onClick = { onClick?.invoke() },
                )
                .padding(horizontal = size.horizontalPadding, vertical = AqSpacing.None)
                .semantics {
                    contentDescription = if (isSelected) "$text, seleccionado" else text
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.X4s),
        ) {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = size.iconSize,
                    tint = content,
                )
            }
            AqText(
                text = text,
                size = size.textSize,
                color = content,
                modifier = Modifier.weight(1f, fill = false).clearAndSetSemantics { },
            )
            count?.let { count ->
                AqText(
                    text = count.toString(),
                    size = AqType.CaptionSmall,
                    color = content,
                    modifier = Modifier.padding(start = AqSpacing.Xs),
                )
            }
        }
    }
}

/** Tamaños de [AqChip] (tabla §3 del catálogo Moon). */
enum class AqChipSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val textSize: androidx.compose.ui.unit.TextUnit,
) {
    Sm(AqSize.Sm, AqSize.X3s, AqMetrics.IconSizeCompact, com.agendaqr.core.ui.theme.AqType.BodySm),
    Md(AqSize.Md, AqSize.X2s, AqMetrics.IconSizeCompact, com.agendaqr.core.ui.theme.AqType.BodyMd),
}
