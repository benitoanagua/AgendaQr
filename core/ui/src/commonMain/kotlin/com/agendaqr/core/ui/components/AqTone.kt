package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AqColorScheme
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Tono semántico: la base compartida de los estados de
 * los componentes. Un solo acento (Accent); los demás colores tienen
 * significado semántico. Cada tono resuelve su par content/container
 * desde el esquema ([toneColors]); nada hereda de slots secundarios.
 */
enum class AqTone {
    Neutral,
    Success,
    Danger,
    Warning,
    Info,
}

/**
 * Colores de un tono semántico: función PURA a partir de
 * [AqColorScheme]; los pares content/container cumplen >= 4.5:1 en ambos
 * temas (AqSchemeTest).
 */
data class ToneColors(
    val content: Color,
    val container: Color,
)

/** Mapeo tono -> colores del esquema. Puro y testeable. */
fun AqColorScheme.toneColors(tone: AqTone): ToneColors = when (tone) {
    AqTone.Neutral -> ToneColors(textSecondary, surfaceSecondary)
    AqTone.Success -> ToneColors(success, successContainer)
    AqTone.Danger -> ToneColors(danger, dangerContainer)
    AqTone.Warning -> ToneColors(warning, warningContainer)
    // El contenedor informativo es azul fintech claro; el CONTENIDO usa el
    // rol `info` (azul legible ≥ 4.5:1 sobre el contenedor y las superficies).
    AqTone.Info -> ToneColors(info, infoContainer)
}

@Composable
fun AqTone.content(): Color =
    LocalAqColorScheme.current.toneColors(this).content

@Composable
fun AqTone.container(): Color =
    LocalAqColorScheme.current.toneColors(this).container

/**
 * Glifo del tono para feedback de estado (el estado nunca se comunica
 * solo por color). Neutral no lleva: su texto ya es el contenido y un
 * glifo añadiría ruido.
 */
val AqTone.iconVector: ImageVector?
    get() = when (this) {
        AqTone.Neutral -> null
        AqTone.Success -> AqIcons.CircleCheck
        AqTone.Danger -> AqIcons.AlertCircle
        AqTone.Warning -> AqIcons.Warning
        AqTone.Info -> AqIcons.Info
    }
