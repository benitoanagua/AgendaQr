package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqStroke

/**
 * Botón de radio sobre [MoonRadio] (`moon_design` 1.1.0, `widgets/radio`).
 *
 * Geometría de `radio_painter.dart`: caja de 16×16 dp, círculo exterior de
 * radio 8 con trazo de 1 dp y punto interior de radio 4 que crece con la
 * fracción de animación. Colores (`radio_theme.dart`): activo `piccolo`→
 * [AqColor.BrandFill], inactivo `trunks`→[AqColor.TextSecondary]; el trazo y
 * el punto comparten el color interpolado.
 *
 * - Mutuamente excluyente dentro de un grupo (el llamador gestiona el estado
 *   de selección de cada opción). Moon lo implementa como `Toggleable` con
 *   `toggleable = false` por defecto: pulsar el seleccionado no lo desmarca.
 * - [onClick]: `null` cuando la fila contenedora ya es la superficie
 *   seleccionable (el indicador queda inerte).
 * - Deshabilitado: opacidad 0.6 sobre el glifo (MoonOpacities.disabled).
 * - Área táctil de 40×40 dp (`tapAreaSizeValue` de Moon `radio.dart`).
 * - [contentDescription]: nombre accesible del control.
 */
@Composable
fun AqRadio(
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val position by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqRadio",
    )
    val dotSize by animateDpAsState(
        targetValue = AqLayout.RadioInnerRadius * 2 * position,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqRadioDot",
    )
    val glyphColor = lerpRadioColor(AqColor.TextSecondary, AqColor.BrandFill, position)

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AqSize.Md,
                minHeight = AqSize.Md,
            )
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
        Canvas(
            modifier = Modifier
                .size(AqLayout.CheckboxGlyphSize)
                .alpha(if (enabled) 1f else AqStateAlpha.Disabled),
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            // Círculo exterior: trazo de 1 dp, color interpolado por `position`.
            drawCircle(
                color = glyphColor,
                radius = size.minDimension / 2f - AqStroke.Default.toPx() / 2f,
                center = center,
                style = Stroke(width = AqStroke.Default.toPx()),
            )
            // Punto interior: radio 4 dp × position (0 = oculto).
            if (dotSize > com.agendaqr.core.ui.theme.AqLayout.Zero) {
                drawCircle(
                    color = glyphColor,
                    radius = dotSize.toPx() / 2f,
                    center = center,
                )
            }
        }
    }
}

@Composable
private fun lerpRadioColor(from: Color, to: Color, fraction: Float): Color =
    androidx.compose.ui.graphics.lerp(from, to, fraction.coerceIn(0f, 1f))
