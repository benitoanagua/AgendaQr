package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.XauxaShape


/**
 * Tono semántico Xauxa y anillo de foco: la base compartida de los componentes.
 * ((dividido por responsabilidad))
 */

/**
 * Tono semántico de Xauxa para componentes. Un solo acento decorativo de
 * marca (Brand); los demás colores tienen significado semántico (Regla 04,
 * ADR-0002: sin variación de acento por categoría). cada tono de
 * estado resuelve su par content/container desde el esquema
 * ([toneColors]); nada hereda de secondary/tertiary.
 */
enum class XauxaTone {
    Neutral,
    Success,
    Danger,
    Warning,
    Info,
}

/**
 * Colores de un tono semántico sobre un esquema función PURA a
 * partir de [com.agendaqr.core.ui.theme.XauxaColorScheme]; los pares
 * content/container cumplen >= 4.5:1 en ambos temas (XauxaSchemeTest).
 */
data class ToneColors(
    val content: androidx.compose.ui.graphics.Color,
    val container: androidx.compose.ui.graphics.Color,
)

/** Mapeo tono -> colores del esquema. Puro y testeable. */
fun com.agendaqr.core.ui.theme.XauxaColorScheme.toneColors(tone: XauxaTone): ToneColors = when (tone) {
    XauxaTone.Neutral -> ToneColors(textSecondary, surface2)
    XauxaTone.Success -> ToneColors(success, successContainer)
    XauxaTone.Danger -> ToneColors(danger, dangerBg)
    XauxaTone.Warning -> ToneColors(warning, warningContainer)
    XauxaTone.Info -> ToneColors(info, infoContainer)
}

@Composable
internal fun XauxaTone.content(): androidx.compose.ui.graphics.Color =
    com.agendaqr.core.ui.theme.LocalXauxaColorScheme.current.toneColors(this).content

@Composable
internal fun XauxaTone.container(): androidx.compose.ui.graphics.Color =
    com.agendaqr.core.ui.theme.LocalXauxaColorScheme.current.toneColors(this).container

/**
 * Glifo del tono para feedback de estado el
 * estado nunca solo por color, §11). Neutral no lleva: su texto ya es el
 * contenido y un glifo añadiría ruido.
 */
val XauxaTone.iconVector: androidx.compose.ui.graphics.vector.ImageVector?
    get() = when (this) {
        XauxaTone.Neutral -> null
        XauxaTone.Success -> XauxaIcons.Save // Check
        XauxaTone.Danger -> XauxaIcons.Cancel // X
        XauxaTone.Warning -> XauxaIcons.Warning
        XauxaTone.Info -> XauxaIcons.Info
    }

/**
 * realimentación de pulsación como overlay con el token
 * [com.agendaqr.core.ui.theme.XauxaOpacity.Pressed] sobre el contenido —
 * nunca un cambio de color local. Respeta reduced motion: con la opción
 * activa el cambio es inmediato (sin animación, §11); sin ella funde con
 * el tiempo corto del sistema (150 ms, M11).
 */
@Composable
fun Modifier.xauxaPressFeedback(
    source: MutableInteractionSource,
    /** Color del overlay. Default: [XauxaColor.TextPrimary] — negro sobre
     * tema claro, blanco sobre oscuro; siempre contrasta con el fondo del
     * tema (>= 7:1, XauxaFocusRingContrastTest). Sobre un BLOQUE de acento
     * (p. ej. tiles Metro) el llamador pasa el color OPUESTO al texto del
     * bloque ([XauxaAccent.pressOverlay]) para no bajar 4.5:1. */
    overlayColor: androidx.compose.ui.graphics.Color = XauxaColor.TextPrimary,
): Modifier {
    val pressed by source.collectIsPressedAsState()
    val reducedMotion = com.agendaqr.core.ui.motion.LocalReducedMotion.current
    val overlayAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) com.agendaqr.core.ui.theme.XauxaOpacity.Pressed else 0f,
        animationSpec = if (reducedMotion) {
            androidx.compose.animation.core.snap()
        } else {
            androidx.compose.animation.core.tween(
                com.agendaqr.core.ui.theme.XauxaMotion.DurationShortMs,
                easing = com.agendaqr.core.ui.theme.XauxaMotion.Easings.Standard,
            )
        },
        label = "xauxa_press_feedback",
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
 * Anillo de foco visible dedicado (invariante 10). Todo clickable
 * personalizado lo aplica sobre su propio [MutableInteractionSource].
 */
@Composable
fun Modifier.xauxaFocusRing(
    source: MutableInteractionSource,
    /** Forma del anillo (default XauxaShape). */
    shape: androidx.compose.ui.graphics.Shape = XauxaShape,
    /**
     * Color del anillo. Default: [XauxaColor.FocusRing] (marca) sobre las
     * superficies del tema (>= 3:1, XauxaFocusRingContrastTest). Sobre un
     * BLOQUE sólido de color (tile de acento, acción primaria de la app
     * bar) el anillo de marca desaparece (1:1): el llamador pasa el color
     * del CONTENIDO del bloque (p. ej. [tileFocusRingColor], OnBrand) para
     * garantizar >= 3:1 (contrato §7: se verifica sobre cada
     * tile/acento). */
    color: androidx.compose.ui.graphics.Color = XauxaColor.FocusRing,
): Modifier {
    val focused by source.collectIsFocusedAsState()
    return then(
        if (focused) Modifier.border(XauxaMetrics.Focus, color, shape)
        else Modifier,
    )
}
