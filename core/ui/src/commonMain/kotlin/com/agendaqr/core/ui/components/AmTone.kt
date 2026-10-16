package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AmColorScheme
import com.agendaqr.core.ui.theme.LocalAmColorScheme

/**
 * Tono semántico: la base compartida de los estados de
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
    // El contenedor informativo es `wathet`; el acento puro queda en
    // 4.39:1 sobre él, por lo que el CONTENIDO usa `accentText` (5.24:1):
    // el acento puro sobre wathet se reserva a glifos y bordes.
    AmTone.Info -> ToneColors(accentText, infoContainer)
}

@Composable
fun AmTone.content(): Color =
    LocalAmColorScheme.current.toneColors(this).content

@Composable
fun AmTone.container(): Color =
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
