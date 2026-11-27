package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqSpacing

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
    title: String? = null,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    /** Etiqueta de la acción de descarte (p. ej. "Cerrar", "Descartar"). */
    dismissDescription: String? = null,
    /** Cuando falso, se desactiva el gesto de arrastre hacia abajo. */
    dismissOnSwipeDown: Boolean = true,
    /** Estado de expansión controlado por el llamador. */
    expanded: Boolean = true,
    /** Muestra la píldora de arrastre superior (affordance móvil). */
    showHandle: Boolean = true,
    /** Alto fijo de la hoja (Moon `MoonBottomSheet.height`); nulo = abraza
     *  el contenido. */
    height: androidx.compose.ui.unit.Dp? = null,
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
                .pointerInput(onDismissRequest) {
                    detectTapGestures { onDismissRequest() }
                },
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .then(
                        if (height != null) {
                            Modifier.height(height)
                        } else {
                            Modifier
                        },
                    )
                    // Moon `MoonBottomSheet`: `ShapeDecoration` con
                    // `surfaceLg` superior — sin sombra ni elevación.
                    // Moon v1 «Bottom sheet»: `surfaceMd` superior (r12).
                    .background(AqColor.Background, AqSquircles.SurfaceTopMd)
                    .semantics { title?.let { paneTitle = it } }
                    .testTag("aq-bottom-sheet-pane")
                    .verticalScroll(rememberScrollState())
                    // Consume toques dentro del panel sin exponer un
                    // "clicable" sin nombre al árbol de accesibilidad.
                    .pointerInput(Unit) { detectTapGestures { } },
                ) {
                // Handle visual de arrastre: píldora angosta centrada
                // (Moon no define handle; es affordance móvil convencional).
                if (showHandle) {
                Box(
                    modifier = Modifier
                        .padding(vertical = AqSpacing.Md)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Área táctil ≥ 48 dp; la píldora visual es angosta.
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
                            .testTag("aq-bottom-sheet-handle"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(AqLayout.BottomSheetHandleWidth)
                                .height(AqLayout.BottomSheetHandleHeight)
                                .background(AqColor.Divider, AqSquircles.Circle),
                        )
                    }
                }
                }
                title?.let {
                    AqText(
                        text = it,
                        size = com.agendaqr.core.ui.theme.AqType.Title,
                        modifier = Modifier.padding(horizontal = AqSpacing.Lg),
                    )
                }
                if (isExpandedState.value) {
                    // Moon `MoonBottomSheet`: el `child` se compone pegado a
                    // los bordes de la hoja; el espaciado lo decide el
                    // llamador dentro de [content].
                    content()
                }
                dismissDescription?.let { label ->
                    AqButton(
                        label = label,
                        onClick = onDismissRequest,
                        variant = AqButtonVariant.Text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AqSpacing.Lg)
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                    )
                }
            }
        }
    }
}
