package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Interruptor sobre [MoonSwitch] (`moon_design` 1.1.0, `widgets/switch`).
 * Control binario de efecto inmediato (ajustes). No usa `Switch` de Material
 * 3: se compone con piezas Moon y tokens.
 *
 * - Tamaños: x2s 16 / xs 24 / sm 32 dp de alto ([AqLayout.SwitchHeight*]).
 * - Pista: 32 / 44 / 60 dp de ancho ([AqLayout.SwitchTrackWidth*]).
 * - Pulgar: 12 / 16 / 20 dp de diámetro ([AqLayout.SwitchThumb*]). sm estaba
 *   *ver fuente* en el catálogo §3 y se transcribe desde moon_design 1.1.0.
 * - Radio: píldora completa (Circle).
 * - Área táctil ≥ 48 dp sin cambiar el aspecto ([AqTouchTarget],
 *   ADR-0039 §4.4). El nodo accesible expone `Role.Switch` y `selected`.
 * - [state.enabled] controla la interacción; [description] copy del llamador.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AqFieldState = AqFieldState(),
    description: String? = null,
    size: AqSwitchSize = AqSwitchSize.Sm,
) {
    val scheme = LocalAqColorScheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val trackColor = when {
        !state.enabled -> scheme.disabled
        checked -> scheme.selectionFill
        else -> scheme.border
    }
    val thumbColor = when {
        !state.enabled -> scheme.onDisabled
        checked -> scheme.selectionIcon
        else -> scheme.surface
    }

    // Nodo táctil de 48 dp (mínimo en ambos ejes) que centra el interruptor
    // visual y expone la semántica de switch. El aspecto visual (pista +
    // pulgar + etiqueta) conserva su tamaño natural; solo crece el área
    // táctil cuando es inferior a 48 dp (ADR-0039 §4.4).
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AqMetrics.ControlMinSize,
                minHeight = AqMetrics.ControlMinSize,
            )
            .clickable(
                enabled = state.enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics {
                contentDescription = description ?: label
                selected = checked
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.size(size.trackWidth, size.height),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.X4s),
        ) {
            Box(
                modifier = Modifier
                    .size(size.trackWidth, size.height)
                    .background(trackColor, AqSquircles.Circle)
                    .padding(size.thumb / 2),
                contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                Box(
                    modifier = Modifier
                        .size(size.thumb)
                        .background(thumbColor, AqSquircles.Circle),
                )
            }
            Column(modifier = Modifier) {
                AqText(
                    text = label,
                    size = AqType.Body,
                    fontWeight = com.agendaqr.core.ui.theme.AqWeight.Medium,
                    color = if (state.enabled) AqColor.TextPrimary else AqColor.OnDisabled,
                )
                if (description != null) {
                    AqText(
                        text = description,
                        size = AqType.Caption,
                        color = if (state.enabled) AqColor.TextSecondary else AqColor.OnDisabled,
                    )
                }
            }
        }
    }
}

/** Tamaños de [AqSwitch] (tabla §3 del catálogo Moon). */
enum class AqSwitchSize(
    val height: Dp,
    val trackWidth: Dp,
    val thumb: Dp,
) {
    X2s(AqLayout.SwitchHeightX2s, AqLayout.SwitchTrackWidthX2s, AqLayout.SwitchThumbX2s),
    Xs(AqLayout.SwitchHeightXs, AqLayout.SwitchTrackWidthXs, AqLayout.SwitchThumbXs),
    Sm(AqLayout.SwitchHeightSm, AqLayout.SwitchTrackWidthSm, AqLayout.SwitchThumbSm),
}
