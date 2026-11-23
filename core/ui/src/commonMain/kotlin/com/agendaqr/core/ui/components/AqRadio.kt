package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Botón de radio sobre [MoonRadio] (`moon_design` 1.1.0, `widgets/radio`).
 *
 * Círculo exterior con un punto interior relleno cuando está seleccionado.
 * Mutuamente excluyente dentro de un grupo (el llamador gestiona el estado
 * de selección de cada opción).
 *
 * - [selected]: estado de selección.
 * - [onClick]: invocada al pulsar (solo si [enabled]).
 * - [enabled]: desactiva la interacción y aplica estilo deshabilitado.
 * - Área táctil ≥ 48 dp (D-02).
 * - [contentDescription]: nombre accesible del control.
 */
@Composable
fun AqRadio(
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    size: Dp = AqSize.Md,
) {
    val scheme = LocalAqColorScheme.current
    val interactionSource = remember { MutableInteractionSource() }
    // PV-01: el estado pressed no cambia el color del borde del radio; el
    // efecto es el scale de Moon. El borde funcional usa `functionalBorder`
    // (PV-03) y no se tiñe al pulsar.
    val outerColor = when {
        !enabled -> scheme.onDisabled
        selected -> scheme.emphasisFill
        else -> scheme.border
    }
    val fillColor = when {
        !enabled -> scheme.onDisabled
        selected -> scheme.emphasisFill
        else -> AqColor.Transparent
    }
    val shape: Shape = AqSquircles.Circle
    Box(
        modifier = modifier
            .size(size)
            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize)
            .border(AqStroke.Default, outerColor, shape)
            .clickable(
                role = Role.RadioButton,
                onClickLabel = contentDescription ?: "radio",
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onClick != null,
                onClick = { onClick?.invoke() },
            )
            .testTag("aq-radio")
            .semantics {
                this.contentDescription = contentDescription ?: "radio"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected || !enabled) {
            Box(
                modifier = Modifier
                    .size(size * 0.5f)
                    .background(fillColor, shape)
                    .clip(shape),
            )
        }
    }
}
