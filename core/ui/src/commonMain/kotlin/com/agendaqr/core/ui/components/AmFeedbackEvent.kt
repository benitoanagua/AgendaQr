package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import com.agendaqr.core.ui.theme.AmSpacing

/**
 * Host de feedback transitorio: un [SnackbarHostState] que las
 * superficies montan en el slot `snackbarHost` de su Scaffold mediante
 * [AmSnackbarHost]. Los eventos transitorios (sincronización completada,
 * eliminado, favorito) se publican aquí vía [AmFeedbackEvent]; el host es
 * compartido por la sesión para que el Snackbar lo muestre la superficie
 * activa.
 */
@Stable
class AmFeedbackHost {
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
}

/** Monta el Snackbar del host en el slot del Scaffold. */
@Composable
fun AmSnackbarHost(host: AmFeedbackHost) {
    SnackbarHost(hostState = host.snackbarHostState)
}

/**
 * Punto ÚNICO de realimentación no visual del DS: háptica ligera +
 * anuncio accesible de estado, disparados por EVENTO (un token que
 * cambia), nunca por estado persistente (así no se repite en
 * recomposiciones). Con [feedbackHost] el evento también se publica como
 * Snackbar transitorio (con acción opcional); el anuncio accesible y el
 * háptico se conservan siempre.
 *
 * - `event`: null mientras no ocurre; el llamador pasa un token nuevo
 *   (contador, id de operación) cada vez que quiere anunciar.
 * - Háptica: `LongPress` del sistema (ligera); respeta los ajustes del
 *   sistema y no depende de reduced motion (no es movimiento visual).
 * - Anuncio: nodo con `liveRegion` cortés. El texto del anuncio es copy
 *   del llamador (AppStrings); nada de copy hardcodeado aquí.
 */
@Composable
fun AmFeedbackEvent(
    event: Any?,
    message: String,
    haptic: Boolean = true,
    /** Host del Snackbar: cuando se provee, el evento también se publica ahí. */
    feedbackHost: AmFeedbackHost? = null,
    /** Acción opcional del Snackbar (copy + callback del llamador). */
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(event, feedbackHost) {
        if (event != null) {
            if (haptic) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            if (feedbackHost != null) {
                val result = feedbackHost.snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = false,
                    duration = SnackbarDuration.Short,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onAction?.invoke()
                }
            }
        }
    }
    // Nodo de anuncio: existe siempre (estable en el árbol), pero su
    // contenido cambia SOLO cuando cambia el evento — el lector anuncia
    // la transición, no cada recomposición. Un ÚNICO nodo con
    // clearAndSetSemantics: liveRegion + contentDescription juntos.
    Box(
        modifier = Modifier
            .size(AmSpacing.None)
            .clearAndSetSemantics {
                if (event != null) {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = message
                }
            },
    )
}
