package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Diálogos y notificaciones Xauxa: confirmación, toast e inline result.
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

/**
 * Lista dentro de un diálogo: filas de selección (S08, ¿a cuál
 * corresponde?) con scroll cuando la lista es larga (§11). Sin cambio de
 * jerarquía: el orden de las filas y las acciones del diálogo no cambian.
 */
@Composable
fun XauxaDialogList(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = XauxaMetrics.DialogListMaxHeight)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        content()
    }
}

/**
 * Diálogo de confirmación destructiva con exactamente dos acciones.
 * Sin loops ni sombras: superficie plana con borde.
 *
 * [onDismissRequest] cubre las vías de cierre que NO son el botón
 * dismiss (Back del sistema, toque fuera). Por defecto equivale a
 * [onDismiss]; un diálogo cuya acción dismiss sea destructiva (p. ej.
 * "Quitar contexto") debe pasar aquí la acción conservativa (Cancelar)
 * para que el Back nunca destruya estado por accidente.
 */
@Composable
fun XauxaDialog(
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
     * lista, §1 "una acción principal"), el confirmar se pinta como
     * acción de texto en vez de botón primario.
     */
    confirmAsText: Boolean = false,
) {
    androidx.compose.material3.AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest ?: onDismiss,
        shape = RectangleShape,
        containerColor = XauxaColor.Surface,
        title = {
            Text(
                title,
                fontSize = XauxaType.Title,
                fontWeight = FontWeight.SemiBold,
                color = XauxaColor.TextPrimary,
            )
        },
        text = {
            if (content != null) {
                content()
            } else {
                Text(
                    message.orEmpty(),
                    fontSize = XauxaType.Body,
                    color = XauxaColor.TextSecondary,
                )
            }
        },
        confirmButton = {
            if (confirmAsText) XauxaTextAction(label = confirmLabel, onClick = onConfirm)
            else XauxaPrimaryButton(label = confirmLabel, onClick = onConfirm)
        },
        dismissButton = dismissLabel?.let {
            { XauxaTextAction(label = it, onClick = onDismiss) }
        },
    )
}

/** Notificación con acción y descarte. Sin auto-cierre en loop: el llamador decide. */
@Composable
fun XauxaToast(
    message: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone = XauxaTone.Neutral,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(tone.container())
            // Fase 1: región viva cortés — el toast se anuncia (antes
            // solo se veía).
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.width(XauxaMetrics.BorderStrong).fillMaxHeight().background(tone.content()))
        // §11: icono por tono — el estado nunca solo por color.
        tone.iconVector?.let { glyph ->
            XauxaIcon(imageVector = glyph, contentDescription = null, tint = tone.content())
        }
        Text(
            message,
            modifier = Modifier.weight(XauxaToneWeight),
            fontSize = XauxaType.Label,
            color = XauxaColor.TextPrimary,
        )
        if (actionLabel != null && onAction != null) {
            XauxaTextAction(label = actionLabel, onClick = onAction)
        }
        if (onDismiss != null) {
            XauxaIconButton(contentDescription = "Descartar notificación", onClick = onDismiss) {
                Text("×", fontSize = XauxaType.Title, color = XauxaColor.TextSecondary)
            }
        }
    }
}

/** Banner de resultado embebido: marcador semántico + contenido + acciones. */
@Composable
fun XauxaInlineResult(
    title: String,
    modifier: Modifier = Modifier,
    meta: String? = null,
    tone: XauxaTone = XauxaTone.Info,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(tone.container())
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.width(XauxaMetrics.BorderStrong).fillMaxHeight().background(tone.content()))
        // §11: icono por tono — el estado nunca solo por color.
        tone.iconVector?.let { glyph ->
            XauxaIcon(imageVector = glyph, contentDescription = null, tint = tone.content())
        }
        Column(modifier = Modifier.weight(XauxaToneWeight)) {
            Text(title, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary)
            if (meta != null) {
                Text(meta, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            }
        }
        actions?.invoke()
    }
}
