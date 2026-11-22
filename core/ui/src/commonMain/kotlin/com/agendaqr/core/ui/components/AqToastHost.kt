package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Entrada de una notificación efímera en la cola del host.
 *
 * - [message]: texto del toast.
 * - [actionLabel] / [onAction]: acción opcional (p. ej. "Deshacer").
 * - [durationMs]: tiempo de vida en ms (0 = se mantiene hasta descartar).
 */
data class AqToastItem(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val durationMs: Long = AqMotion.ToastDurationMs,
)

/**
 * Estado del host de toasts. Único por sesión; las superficies lo crean con
 * `remember { AqToastHostState() }` y lo pasan a [AqToastHost].
 *
 * Las notificaciones se encolan mediante [show] y se muestran una tras otra;
 * el gap entre toasts es el publicado por Moon (200 ms) y cada una vive
 * [AqMotion.ToastDurationMs] por defecto (decisión de producto, ADR-0041).
 */
@Stable
class AqToastHostState {
    val items = mutableStateOf(emptyList<AqToastItem>())

    /** Encola una notificación efímera; el host la renderiza en turno. */
    fun show(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        durationMs: Long = AqMotion.ToastDurationMs,
    ) {
        items.value = items.value + listOf(
            AqToastItem(message = message, actionLabel = actionLabel, onAction = onAction, durationMs = durationMs),
        )
    }

    /** Retira una notificación ya mostrada (al expirar o al descartar). */
    fun dismiss(item: AqToastItem) {
        items.value = items.value.filter { it !== item }
    }
}

/**
 * Host de toasts sobre [MoonToast] (`moon_design` 1.1.0, `widgets/toast`).
 * Sustituye a [AqSnackbarHost] y [AqFeedbackHost].
 *
 * - Muestra las notificaciones encoladas en [host] una tras otra.
 * - Gap de 200 ms entre toasts (Moon).
 * - Superficie en la parte inferior, centrada, con radio squircle.
 * - Todo el copy llega del llamador vía [AqToastHostState.show].
 */
@Composable
fun AqToastHost(
    host: AqToastHostState,
    modifier: Modifier = Modifier,
) {
    val items by host.items
    if (items.isEmpty()) {
        return
    }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AqToastHostGap),
        ) {
            items.forEach { item ->
                AqToast(
                    show = true,
                    message = item.message,
                    actionLabel = item.actionLabel,
                    onAction = item.onAction,
                    onDismissRequest = { host.dismiss(item) },
                    durationMs = item.durationMs,
                )
            }
        }
    }
}

/** Gap entre toasts según Moon (200 ms, transcrito como distancia vertical). */
private val AqToastHostGap: androidx.compose.ui.unit.Dp = AqSpacing.Sm
