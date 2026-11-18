package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.Constraints
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles

/** Dialogs confirm decisions and host bounded choices in a scrollable list. */

/**
 * Lista dentro de un diálogo: filas de selección con scroll cuando
 * la lista es larga. Sin cambio de jerarquía: el orden de las filas y las
 * acciones del contenedor no cambian.
 */
@Composable
fun AqDialogList(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(max = AqMetrics.DialogListMaxHeight)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        content()
    }
}

/**
 * Acción del pie de un diálogo: texto a ancho completo sobre la zona
 * interactiva de 48 dp. [emphasized] marca la acción de confirmar
 * (negrita + acento, o peligro si es destructiva); las demás acciones
 * van en texto normal primario.
 */
@Composable
private fun AqDialogAction(
    label: String,
    emphasized: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .background(
                if (pressed) AqColor.SurfaceActive else AqColor.Transparent,
                AqShapes.Small,
            )
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                interactionSource = interactionSource,
                // Las acciones de diálogo son TDButton: color activo, sin ripple.
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AqTextStyles.Body,
            fontWeight = if (emphasized) AqWeight.Bold else null,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}
/**
 * Diálogo de confirmación con exactamente dos acciones.
 *
 * Réplica del Dialog de la referencia: popup con radio de tarjeta (12,
 * Anexo A), título 18 bold centrado, contenido 14 y FILA DE ACCIONES A
 * ANCHO COMPLETO separada del contenido por hairline (y entre sí por
 * hairline vertical); la acción de confirmar va en negrita con el acento
 * (peligro si [destructive]) y el descarte en texto normal primario.
 * Con una sola acción ([dismissLabel] nulo) la fila es una única
 * acción a ancho completo.
 *
 * [onDismissRequest] cubre las vías de cierre que NO son el botón
 * dismiss (Back del sistema, toque fuera). Por defecto equivale a
 * [onDismiss]; un diálogo cuya acción dismiss sea destructiva (p. ej.
 * "Quitar contexto") debe pasar aquí la acción conservativa (Cancelar)
 * para que el Back nunca destruya estado por accidente.
 */
@Composable
fun AqDialog(
    title: String,
    message: String? = null,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    /**
     * Cuando la acción principal vive en el contenido (elegir de una
     * lista), el confirmar se pinta como acción de texto sin énfasis
     * (mismo peso que el descarte).
     */
    confirmAsText: Boolean = false,
    /**
     * La confirmación es DESTRUCTIVA (eliminar QR/actividad/comprobante).
     * El confirmar se tiñe de peligro y el foco inicial cae en la acción
     * conservativa (dismiss) para que el primer toque no dispare lo
     * irreversible.
     */
    destructive: Boolean = false,
) {
    val dismissFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    if (destructive) {
        LaunchedEffect(Unit) {
            runCatching { dismissFocus.requestFocus() }
        }
    }
    Dialog(
        onDismissRequest = onDismissRequest ?: onDismiss,
        // Ancho propio del componente: el mínimo/ancho objetivo lo fija
        // el Dialog de la referencia, no la plataforma.
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints {
            // Ancho objetivo: 75 % de la ventana con tope de contenido;
            // el mínimo del popup prevalece (como el min-width del CSS de
            // la referencia en pantallas estrechas).
            val dialogWidth = minOf(
                maxWidth * AqMetrics.DialogMaxWidthFraction,
                AqMetrics.ContentMaxWidth,
            ).coerceAtLeast(AqMetrics.DialogMinWidth)
            Surface(
                modifier = modifier.width(dialogWidth),
                shape = AqShapes.ExtraLarge,
                color = AqColor.Surface,
            ) {
                Column {
                    Text(
                        title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AqSpacing.Xl, start = AqSpacing.Lg, end = AqSpacing.Lg)
                            .semantics { heading() },
                        style = AqTextStyles.Title,
                        fontWeight = AqWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = AqColor.TextPrimary,
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Md)
                            .fillMaxWidth(),
                    ) {
                        if (content != null) {
                            content()
                        } else {
                            Text(
                                message.orEmpty(),
                                style = AqTextStyles.Label,
                                color = AqColor.TextSecondary,
                            )
                        }
                    }
                    // Pie: hairline + fila de acciones a ancho completo
                    // (Dialog de la referencia), alto táctil de 48 dp.
                    HorizontalDivider(
                        thickness = AqMetrics.DividerHeight,
                        color = AqColor.Divider,
                    )
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                    ) {
                        val density = LocalDensity.current
                        val textMeasurer = rememberTextMeasurer()
                        val halfTextWidth = with(density) {
                            (maxWidth / 2 - AqSpacing.Md * 2).roundToPx().coerceAtLeast(0)
                        }
                        val labelsNeedStack = dismissLabel != null && (
                            textMeasurer.measure(
                                text = dismissLabel,
                                style = AqTextStyles.Body,
                                maxLines = 1,
                                constraints = Constraints(maxWidth = halfTextWidth),
                            ).hasVisualOverflow ||
                                textMeasurer.measure(
                                    text = confirmLabel,
                                    style = AqTextStyles.Body,
                                    maxLines = 1,
                                    constraints = Constraints(maxWidth = halfTextWidth),
                                ).hasVisualOverflow
                            )
                        if (labelsNeedStack) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                            ) {
                                AqDialogAction(
                                    label = dismissLabel.orEmpty(),
                                    emphasized = false,
                                    color = AqColor.TextPrimary,
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (destructive) {
                                                Modifier.focusRequester(dismissFocus)
                                            } else {
                                                Modifier
                                            },
                                        ),
                                )
                                HorizontalDivider(
                                    thickness = AqMetrics.DividerHeight,
                                    color = AqColor.Divider,
                                )
                                AqDialogAction(
                                    label = confirmLabel,
                                    emphasized = !confirmAsText,
                                    color = if (destructive) AqColor.Danger else AqColor.Link,
                                    onClick = onConfirm,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                            ) {
                                if (dismissLabel != null) {
                                    AqDialogAction(
                                        label = dismissLabel,
                                        emphasized = false,
                                        color = AqColor.TextPrimary,
                                        onClick = onDismiss,
                                        modifier = Modifier
                                            .weight(1f)
                                            .then(
                                                if (destructive) {
                                                    Modifier.focusRequester(dismissFocus)
                                                } else {
                                                    Modifier
                                                },
                                            ),
                                    )
                                    VerticalDivider(
                                        thickness = AqMetrics.DividerHeight,
                                        color = AqColor.Divider,
                                    )
                                }
                                AqDialogAction(
                                    label = confirmLabel,
                                    emphasized = !confirmAsText,
                                    color = if (destructive) AqColor.Danger else AqColor.Link,
                                    onClick = onConfirm,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
