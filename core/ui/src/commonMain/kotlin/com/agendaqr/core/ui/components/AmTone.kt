package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmColorScheme
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmMotion
import com.agendaqr.core.ui.theme.AmOpacity
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.LocalAmColorScheme

/**
 * Tono semántico de AgendaMobile: la base compartida de los estados de
 * los componentes. Un solo acento (Accent); los demás colores tienen
 * significado semántico. Cada tono resuelve su par content/container
 * desde el esquema ([toneColors]); nada hereda de slots secundarios.
 */
enum class AmTone {
    Neutral,
    Success,
    Danger,
    Warning,
    Info,
}

/**
 * Colores de un tono semántico: función PURA a partir de
 * [AmColorScheme]; los pares content/container cumplen >= 4.5:1 en ambos
 * temas (AmSchemeTest).
 */
data class ToneColors(
    val content: Color,
    val container: Color,
)

/** Mapeo tono -> colores del esquema. Puro y testeable. */
fun AmColorScheme.toneColors(tone: AmTone): ToneColors = when (tone) {
    AmTone.Neutral -> ToneColors(textSecondary, surfaceSecondary)
    AmTone.Success -> ToneColors(success, successContainer)
    AmTone.Danger -> ToneColors(danger, dangerContainer)
    AmTone.Warning -> ToneColors(warning, warningContainer)
    AmTone.Info -> ToneColors(info, infoContainer)
}

@Composable
internal fun AmTone.content(): Color =
    LocalAmColorScheme.current.toneColors(this).content

@Composable
internal fun AmTone.container(): Color =
    LocalAmColorScheme.current.toneColors(this).container

/**
 * Glifo del tono para feedback de estado (el estado nunca se comunica
 * solo por color). Neutral no lleva: su texto ya es el contenido y un
 * glifo añadiría ruido.
 */
val AmTone.iconVector: ImageVector?
    get() = when (this) {
        AmTone.Neutral -> null
        AmTone.Success -> AmIcons.CircleCheck
        AmTone.Danger -> AmIcons.Cancel
        AmTone.Warning -> AmIcons.Warning
        AmTone.Info -> AmIcons.Info
    }

/**
 * Realimentación de pulsación como overlay con el token
 * [AmOpacity.Pressed] sobre el contenido — nunca un cambio de color
 * local. Respeta reduced motion: con la opción activa el cambio es
 * inmediato (sin animación); sin ella funde con la duración corta del
 * sistema.
 */
@Composable
fun Modifier.amPressFeedback(
    source: MutableInteractionSource,
    /** Color del overlay. Default: [AmColor.TextPrimary] — siempre
     * contrasta con el fondo del tema (>= 7:1, AmFocusRingContrastTest). */
    overlayColor: Color = AmColor.TextPrimary,
): Modifier {
    val pressed by source.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val overlayAlpha by animateFloatAsState(
        targetValue = if (pressed) AmOpacity.Pressed else 0f,
        animationSpec = if (reducedMotion) {
            snap()
        } else {
            tween(
                AmMotion.DurationShortMs,
                easing = AmMotion.Easings.Standard,
            )
        },
        label = "am_press_feedback",
    )
    return drawWithContent {
        drawContent()
        if (overlayAlpha > 0f) {
            drawRect(
                color = overlayColor.copy(alpha = overlayAlpha),
                size = size,
            )
        }
    }
}

/**
 * Anillo de foco visible dedicado: todo clickable personalizado lo
 * aplica sobre su propio [MutableInteractionSource]. Implementación
 * canónica del foco visible (único archivo con `.border(` permitido por
 * el gate del sistema de diseño).
 */
@Composable
fun Modifier.amFocusRing(
    source: MutableInteractionSource,
    /** Forma del anillo. */
    shape: Shape = AmShapes.Small,
    /**
     * Color del anillo. Default: [AmColor.Focus] sobre las superficies
     * del tema (>= 3:1, AmFocusRingContrastTest).
     */
    color: Color = AmColor.Focus,
): Modifier {
    val focused by source.collectIsFocusedAsState()
    return then(
        if (focused) Modifier.border(AmMetrics.Focus, color, shape) else Modifier,
    )
}
