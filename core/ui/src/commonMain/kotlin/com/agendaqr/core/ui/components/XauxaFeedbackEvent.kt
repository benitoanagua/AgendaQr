package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * Ronda 2 (Áreas B/F) — punto ÚNICO de realimentación no visual del DS:
 * háptica ligera + anuncio accesible de estado, disparados por EVENTO
 * (un token que cambia), nunca por estado persistente (así no se repite
 * en recomposiciones).
 *
 * - `event`: null mientras no ocurre; el llamador pasa un token nuevo
 *   (contador, id de operación) cada vez que quiere anunciar.
 * - Háptica: `LongPress` del sistema (ligera); respeta los ajustes del
 *   sistema y no depende de reduced motion (no es movimiento visual).
 * - Anuncio: nodo con `liveRegion` cortés. El texto del anuncio es copy
 *   del llamador (AppStrings); nada de copy hardcodeado aquí.
 * - Verificación con TalkBack/VoiceOver: checklist manual (dispositivo).
 */
@Composable
fun XauxaFeedbackEvent(
    event: Any?,
    message: String,
    haptic: Boolean = true,
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(event) {
        if (event != null && haptic) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    // Nodo de anuncio: existe siempre (estable en el árbol), pero su
    // contenido cambia SOLO cuando cambia el evento — el lector anuncia
    // la transición, no cada recomposición. Un ÚNICO nodo con
    // clearAndSetSemantics: liveRegion + contentDescription juntos.
    Box(
        modifier = Modifier
            .size(XauxaSpacing.None)
            .clearAndSetSemantics {
                if (event != null) {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = message
                }
            },
    )
}
