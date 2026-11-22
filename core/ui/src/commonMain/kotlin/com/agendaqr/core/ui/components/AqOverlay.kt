package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Modal sobre [MoonModal] (`moon_design` 1.1.0,
 * `lib/src/widgets/modal`). Implementado sobre el diálogo de plataforma
 * (`androidx.compose.ui.window.Dialog`) para garantizar: z-order por encima
 * de las barras del sistema, cierre con back / predictive back mediante
 * [onDismissRequest], semántica de diálogo (`paneTitle`) y scrim clicable.
 *
 * El foco se atrapa dentro del modal: al mostrarse se solicita el foco en
 * su superficie de contenido y el scrim bloquea la interacción con lo que
 * queda detrás. Todo el copy llega del llamador.
 */
@Composable
fun AqModal(
    show: Boolean = true,
    title: String,
    /** copy del llamador. */
    dismissLabel: String,
    onDismissRequest: () -> Unit,
    /** Etiqueta opcional del botón de confirmación (primaria). */
    confirmLabel: String? = null,
    /** Invocada al pulsar la confirmación. */
    onConfirm: (() -> Unit)? = null,
    /** Si es verdadera, la confirmación se pinta como acción destructiva. */
    destructive: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!show) return
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LocalAqColorScheme.current.scrim)
                .clickable(onClick = onDismissRequest)
                .testTag("aq-modal-scrim"),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(AqSize.Lg)
                    .background(LocalAqColorScheme.current.surface, AqSquircles.SurfaceLg)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .testTag("aq-modal-pane")
                    .semantics { paneTitle = title },
            ) {
                Text(
                    text = title,
                    style = AqTextStyles.HeadingLg,
                    color = LocalAqColorScheme.current.textPrimary,
                )
                content()
                Row(
                    modifier = Modifier
                        .padding(vertical = AqSize.Xs)
                        .semantics { paneTitle = title },
                    horizontalArrangement = Arrangement.End,
                ) {
                    confirmLabel?.let { label ->
                        AqButton(
                            label = label,
                            onClick = { onConfirm?.invoke() },
                            variant = if (destructive) AqButtonVariant.Danger else AqButtonVariant.Text,
                            size = AqButtonSize.Small,
                        )
                    }
                    AqButton(
                        label = dismissLabel,
                        onClick = onDismissRequest,
                        variant = AqButtonVariant.Text,
                        size = AqButtonSize.Small,
                        modifier = Modifier.testTag("aq-modal-dismiss"),
                    )
                }
            }
        }
    }
}

/**
 * Cajón lateral sobre [MoonDrawer] (`moon_design` 1.1.0,
 * `lib/src/widgets/drawer`). Superficie lateral (izquierda por defecto) con
 * scrim y cierre al pulsar el scrim. Expone semántica de panel
 * (`paneTitle`) y descarte al tocar el scrim.
 */
@Composable
fun AqDrawer(
    show: Boolean = true,
    title: String,
    /** copy del llamador. */
    dismissLabel: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!show) return
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAqColorScheme.current.scrim),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Cierre al pulsar el scrim (fuera del cajón).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismissRequest),
        )
        Column(
            modifier = Modifier
                .width(AqMetrics.NavRailWidth)
                .fillMaxHeight()
                .background(LocalAqColorScheme.current.surface, AqSquircles.InteractiveMd)
                .semantics { paneTitle = title },
        ) {
            Text(
                text = title,
                style = AqTextStyles.HeadingLg,
                color = LocalAqColorScheme.current.textPrimary,
            )
            content()
            Row(
                modifier = Modifier
                    .padding(vertical = AqSize.Lg)
                    .semantics { paneTitle = title },
                horizontalArrangement = Arrangement.End,
            ) {
                AqButton(
                    label = dismissLabel,
                    onClick = onDismissRequest,
                    variant = AqButtonVariant.Text,
                    size = AqButtonSize.Small,
                    modifier = Modifier
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                        .testTag("aq-modal-dismiss"),
                )
            }
        }
    }
}
