package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
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
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqChip(
    text: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
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
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .heightIn(min = size.height)
            .background(if (pressed && enabled) scheme.surfaceActive else container, AqSquircles.InteractiveSm)
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
                contentDescription = if (selected) "$text, seleccionado" else text
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
        Text(
            text = text,
            style = AqTextStyles.Label.copy(fontSize = size.textSize),
            color = content,
            modifier = Modifier.weight(1f, fill = false),
        )
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
