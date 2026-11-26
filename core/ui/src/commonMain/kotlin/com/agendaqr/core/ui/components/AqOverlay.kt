package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
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
    title: String? = null,
    /** copy del llamador. */
    dismissLabel: String? = null,
    onDismissRequest: () -> Unit,
    /** Etiqueta opcional del botón de confirmación (primaria). */
    confirmLabel: String? = null,
    /** Cuando es verdadera, el botón de confirmación se pinta como texto (no primario). */
    confirmAsText: Boolean = false,
    /** Invocada al pulsar la confirmación. */
    onConfirm: (() -> Unit)? = null,
    /** Si es verdadera, la confirmación se pinta como acción destructiva. */
    destructive: Boolean = false,
    /** Cierre del diálogo (alias de [onDismissRequest]). */
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    if (!show) return
    val dismissRequest = onDismiss ?: onDismissRequest
    // Al mostrarse el modal, el foco inicial va al botón de descarte
    // (Cancelar) para que un lector de pantalla lo anuncie primero
    // (T-05: el botón de salida es el punto de entrada del diálogo).
    val focusManager = LocalFocusManager.current
    // Al mostrarse el modal, el foco inicial va al primer nodo enfocable
    // (el botón de descarte "Cancelar") para que un lector de pantalla lo
    // anuncie primero (T-05).
    LaunchedEffect(show) {
        if (show) {
            focusManager.moveFocus(FocusDirection.Next)
        }
    }
    Dialog(
        onDismissRequest = dismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(LocalAqColorScheme.current.scrim)
                .clickable(onClick = dismissRequest)
                // `paneTitle` va en la raíz del diálogo (nodo que ya fusiona
                // descendientes por `clickable`): dentro del panel quedaría
                // absorbido por el merge y `PaneTitle` es una propiedad no
                // fusionable → crash de semántica.
                .then(
                    if (title != null) {
                        Modifier.semantics { paneTitle = title }
                    } else {
                        Modifier
                    },
                )
                .testTag("aq-modal-scrim"),
            contentAlignment = Alignment.Center,
        ) {
            // D-02: el panel no puede superar el alto disponible; el contenido
            // hace scroll y las acciones quedan siempre visibles abajo.
            val paneMaxHeight = maxHeight * 0.9f
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = paneMaxHeight)
                    .testTag("aq-modal-pane")
                    // Primero el fondo y después el relleno interno: antes el
                    // padding iba delante y actuaba como margen externo, así el
                    // contenido quedaba pegado al borde del panel.
                    // Moon v1 «Modal»: radio 12 y padding interno 8.
                    .background(LocalAqColorScheme.current.background, AqSquircles.SurfaceMd)
                    .padding(AqSize.X4s)
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                verticalArrangement = Arrangement.spacedBy(AqSize.X3s),
            ) {
                title?.let {
                    AqText(
                        text = it,
                        size = AqType.Title,
                        color = LocalAqColorScheme.current.textPrimary,
                        modifier = Modifier,
                        semanticHeading = true,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                ) {
                    content()
                }
                if (confirmLabel != null || dismissLabel != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        confirmLabel?.let { label ->
                            AqButton(
                                label = label,
                                onClick = { onConfirm?.invoke() },
                                variant = if (confirmAsText) AqButtonVariant.Text else if (destructive) AqButtonVariant.Danger else AqButtonVariant.Text,
                                size = AqButtonSize.Small,
                            )
                        }
                        dismissLabel?.let { label ->
                            AqButton(
                                label = label,
                                onClick = dismissRequest,
                                variant = AqButtonVariant.Text,
                                size = AqButtonSize.Small,
                                modifier = Modifier
                                    .testTag("aq-modal-dismiss")
                                    .focusable(),
                            )
                        }
                    }
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
    title: String? = null,
    /** copy del llamador. */
    dismissLabel: String? = null,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!show) return
    // Esquinas interiores redondeadas (Moon v1 Drawer r12).
    val drawerShape = RoundedCornerShape(
        topEnd = AqCorner.SurfaceMd,
        bottomEnd = AqCorner.SurfaceMd,
    )
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
                // Moon `drawer.dart`: `min(theme.width=448, ancho de pantalla)`.
                .fillMaxWidth()
                .widthIn(max = AqLayout.DrawerWidth)
                .fillMaxHeight()
                // Moon v1 «Drawer»: radio 12 en las esquinas interiores
                // (las que miran al contenido; `end` se invierte en RTL).
                .clip(drawerShape)
                .aqShadow(AqShadow.Lg, drawerShape)
                .background(LocalAqColorScheme.current.background)
                .testTag("aq-drawer-pane")
                .then(
                    if (title != null) {
                        Modifier.semantics { paneTitle = title }
                    } else {
                        Modifier
                    },
                ),
        ) {
            title?.let {
                AqText(
                    text = it,
                    size = AqType.Title,
                    color = LocalAqColorScheme.current.textPrimary,
                    semanticHeading = true,
                )
            }
            content()
            if (dismissLabel != null) {
                Row(
                    modifier = Modifier.padding(vertical = AqSize.Lg),
                    horizontalArrangement = Arrangement.End,
                ) {
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
