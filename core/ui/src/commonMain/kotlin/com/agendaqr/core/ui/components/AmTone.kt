package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AmColorScheme
import com.agendaqr.core.ui.theme.LocalAmColorScheme

/**
 * Tono semántico compartido de la interfaz: la base compartida de los estados de
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
