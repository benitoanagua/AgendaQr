package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.aqShadow
import kotlinx.coroutines.delay

/**
 * Toast efímero sobre [MoonToast] (`moon_design` 1.1.0,
 * `widgets/toast`). Mensaje breve que aparece en la parte inferior de la
 * pantalla y se descarta solo tras [durationMs] (por defecto la curva de
 * 200 ms de Moon). No interrumpe el flujo.
 *
 * - [message]: texto del toast.
 * - [actionLabel] / [onAction]: acción opcional (p. ej. "Deshacer").
 * - [icon]: glifo decorativo opcional.
 * - [durationMs]: tiempo de vida en ms; 0 lo mantiene visible hasta que el
 *   llamador cambie [show] a falso.
 * - [onDismissRequest]: se invoca al expirar [durationMs] o al descartar.
 * - Superficie `goku`→`goku`/`darkGoku`, radio `surfaceSm`, sombra
 *   `shadows.lg`, padding `x2s`, gaps `x2s`/`x3s` (Moon `toast_theme`).
 * - Objetivo táctil ≥ 48 dp en la acción (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqToast(
    show: Boolean = true,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    icon: ImageVector? = null,
    /** Variante de color de Moon (`MoonToastVariant`): `Inverted` usa la
     *  paleta oscura aunque el tema sea claro. */
    variant: AqToastVariant = AqToastVariant.Original,
    durationMs: Long = AqMotion.ToastDurationMs.toLong(),
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val inverted = variant == AqToastVariant.Inverted
    val toastBackground = if (inverted) DarkAqColorScheme.background else scheme.background
    val toastText = if (inverted) DarkAqColorScheme.textPrimary else scheme.textPrimary
    val toastAccent = if (inverted) DarkAqColorScheme.link else scheme.link
    if (!show) return
    val onDismissState = rememberUpdatedState(onDismissRequest)
    LaunchedEffect(show, durationMs) {
        if (show && durationMs > 0) {
            delay(durationMs)
            onDismissState.value?.invoke()
        }
    }
    // Moon `MoonToast`: el elemento medido es `margin(contentPadding) +
    // tarjeta(padding contentPadding + contenido)`. El margen queda fuera
    // del fondo pero dentro de la caja etiquetada para que las métricas
    // del comparador cubran la misma superficie que el golden Moon.
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        // El tag envuelve margen + tarjeta: las bounds semánticas cubren la
        // misma superficie que el `Container(margin+padding)` de MoonToast.
        Box(modifier = Modifier.testTag("aq-toast-card")) {
            Box(modifier = Modifier.padding(AqSpacing.Lg)) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                        .aqShadow(AqShadow.Lg, AqSquircles.SurfaceSm)
                        .background(toastBackground, AqSquircles.SurfaceSm)
                        .padding(AqSize.X2s),
                    contentAlignment = Alignment.Center,
                ) {
            Row(
                // Moon `MoonToast`: la tarjeta abraza su contenido
                // (`Row(mainAxisSize.min)`); el ancho solo se fija si el
                // llamador pasa `width`. La etiqueta se acota para no
                // desbordar en mensajes largos.
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AqSize.X2s),
            ) {
                icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = toastText,
                    )
                }
                AqText(
                    text = message,
                    size = AqType.Body,
                    color = toastText,
                    modifier = Modifier
                        .widthIn(max = AqMetrics.DialogMinWidth)
                        .semantics {
                            contentDescription = message
                            liveRegion = LiveRegionMode.Polite
                        },
                )
                actionLabel?.let { label ->
                    AqText(
                        text = label,
                        size = AqType.Label,
                        fontWeight = AqWeight.SemiBold,
                        color = toastAccent,
                        modifier = Modifier
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = label,
                                onClick = { onAction?.invoke() },
                                enabled = onAction != null,
                            ),
                    )
                }
                }
                }
            }
        }
    }
}

/** Variante de color del toast (`MoonToastVariant` de Moon). */
enum class AqToastVariant { Original, Inverted }
