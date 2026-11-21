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
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Modal sobre [MoonModal] (`moon_design` 1.1.0,
 * `lib/src/widgets/modal`). Superficie centrada con scrim, radio squircle
 * grande y cierre al pulsar el scrim o el botón de cierre.
 *
 * [title] y [content] los aporta el llamador (copy). [onDismissRequest] se
 * invoca al cerrar.
 */
@Composable
fun AqModal(
    show: Boolean = true,
    title: String,
    /** copy del llamador. */
    dismissLabel: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!show) return
    val scheme = LocalAqColorScheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.scrim),
        contentAlignment = Alignment.Center,
    ) {
        // Cierre al pulsar el scrim (fuera de la superficie modal).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismissRequest),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(AqSize.Lg)
                .background(scheme.surface, AqSquircles.SurfaceLg)
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        ) {
            Text(
                text = title,
                style = AqTextStyles.HeadingLg,
                color = scheme.textPrimary,
            )
            content()
            Row(
                modifier = Modifier
                    .padding(vertical = AqSize.Xs),
                horizontalArrangement = Arrangement.End,
            ) {
                AqTextAction(
                    label = dismissLabel,
                    onClick = onDismissRequest,
                    enabled = true,
                )
            }
        }
    }
}

/**
 * Hoja inferior sobre [MoonBottomSheet] / [showMoonModalBottomSheet]
 * (`moon_design` 1.1.0, `lib/src/widgets/bottom_sheet`). Superficie que
 * asciende desde el fondo con scrim, radio grande superior y cierre al
 * pulsar el scrim.
 */
@Composable
fun AqBottomSheet(
    show: Boolean = true,
    title: String,
    /** copy del llamador. */
    dismissLabel: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!show) return
    val scheme = LocalAqColorScheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.scrim),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Cierre al pulsar el scrim (fuera de la hoja).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismissRequest),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(scheme.surface, AqSquircles.InteractiveMd)
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        ) {
            Text(
                text = title,
                style = AqTextStyles.HeadingLg,
                color = scheme.textPrimary,
            )
            content()
            Row(
                modifier = Modifier
                    .padding(vertical = AqSize.Xs),
                horizontalArrangement = Arrangement.End,
            ) {
                AqTextAction(
                    label = dismissLabel,
                    onClick = onDismissRequest,
                    enabled = true,
                )
            }
        }
    }
}

/**
 * Cajón lateral sobre [MoonDrawer] (`moon_design` 1.1.0,
 * `lib/src/widgets/drawer`). Superficie lateral (izquierda por defecto) con
 * scrim y cierre al pulsar el scrim.
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
    val scheme = LocalAqColorScheme.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.scrim),
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
                .background(scheme.surface, AqSquircles.InteractiveMd),
        ) {
            Text(
                text = title,
                style = AqTextStyles.HeadingLg,
                color = scheme.textPrimary,
            )
            content()
            Row(
                modifier = Modifier
                    .padding(vertical = AqSize.Lg),
                horizontalArrangement = Arrangement.End,
            ) {
                AqTextAction(
                    label = dismissLabel,
                    onClick = onDismissRequest,
                    enabled = true,
                )
            }
        }
    }
}
