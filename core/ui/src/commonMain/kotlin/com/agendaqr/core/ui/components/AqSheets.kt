package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqElevation
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.aqElevation

/**
 * Hoja inferior modal equivalente a un bottom sheet nativo (D-04).
 * Usa el modal del sistema con scrim semántico, superficie elevada con
 * tokens y targets táctiles de 48 dp. Todo el copy llega del llamador.
 *
 * Estados:
 * - [expanded] controla si la hoja está expandida (muestra el título,
 *   handle y todo el contenido) o colapsada (solo título y handle).
 * - [dismissOnSwipeDown] activa el gesto de arrastre hacia abajo para
 *   descartar la hoja (patrón táctil móvil de Moon).
 *
 * El scrim se puede tocar para descartar. El contenido es desplazable
 * verticalmente cuando no cabe en pantalla.
 */
@Composable
fun AqBottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    /** Etiqueta de la acción de descarte (p. ej. "Cerrar", "Descartar"). */
    dismissDescription: String,
    /** Cuando falso, se desactiva el gesto de arrastre hacia abajo. */
    dismissOnSwipeDown: Boolean = true,
    /** Estado de expansión controlado por el llamador. */
    expanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    // Umbral equivalente a cuatro áreas táctiles mínimas del sistema.
    val dismissThresholdPx = with(density) { (AqMetrics.ControlMinSize * 4).toPx() }
    val isExpandedState = rememberUpdatedState(expanded)
    val onDismissState = rememberUpdatedState(onDismissRequest)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AqColor.Scrim)
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .aqElevation(AqElevation.Shadow3, AqSquircles.SurfaceLg)
                    .background(AqColor.Surface, AqSquircles.SurfaceLg)
                    .semantics { paneTitle = title }
                    .testTag("aq-bottom-sheet-pane")
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = AqSpacing.Md)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Handle visual de arrastre (indica que la hoja es manejable).
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize)
                            .then(
                                if (dismissOnSwipeDown && isExpandedState.value) {
                                    Modifier.pointerInput(dismissThresholdPx) {
                                        var downwardDistance = 0f
                                        detectVerticalDragGestures(
                                            onDragStart = { downwardDistance = 0f },
                                            onVerticalDrag = { change, dragAmount ->
                                                downwardDistance += dragAmount
                                                change.consume()
                                            },
                                            onDragEnd = {
                                                if (downwardDistance >= dismissThresholdPx) {
                                                    onDismissState.value()
                                                }
                                                downwardDistance = 0f
                                            },
                                            onDragCancel = { downwardDistance = 0f },
                                        )
                                    }
                                } else {
                                    Modifier
                                },
                            )
                            .testTag("aq-bottom-sheet-handle")
                            .width(AqSpacing.Xxxl)
                            .background(AqColor.Divider, AqSquircles.SurfaceLg),
                    )
                }
                AqText(
                    text = title,
                    size = com.agendaqr.core.ui.theme.AqType.Title,
                    modifier = Modifier.padding(horizontal = AqSpacing.Lg),
                )
                if (isExpandedState.value) {
                    Spacer(Modifier.height(AqSpacing.Md))
                    Box(modifier = Modifier.padding(horizontal = AqSpacing.Lg)) { content() }
                    Spacer(Modifier.height(AqSpacing.Lg))
                }
                AqButton(
                    label = dismissDescription,
                    onClick = onDismissRequest,
                    variant = AqButtonVariant.Text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AqSpacing.Lg)
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
                Spacer(Modifier.height(AqSpacing.Lg))
            }
        }
    }
}
