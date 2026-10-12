package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles


/**
 * Diálogos Xauxa (spec §12 V1.2): `AlertDialog` de M3 con los contratos
 * de foco/acciones del sistema.
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
            .heightIn(max = XauxaMetrics.DialogListMaxHeight)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        content()
    }
}

/**
 * Diálogo de confirmación con exactamente dos acciones.
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
    /**
     * La confirmación es DESTRUCTIVA (eliminar QR/actividad/comprobante).
     * Usa [XauxaDangerButton] y el foco inicial cae en la acción
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
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest ?: onDismiss,
        title = {
            Text(
                title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = XauxaColor.TextPrimary,
            )
        },
        text = {
            if (content != null) {
                content()
            } else {
                Text(
                    message.orEmpty(),
                    style = XauxaTextStyles.Body,
                    color = XauxaColor.TextSecondary,
                )
            }
        },
        confirmButton = {
            when {
                confirmAsText -> XauxaTextAction(label = confirmLabel, onClick = onConfirm)
                destructive -> XauxaDangerButton(label = confirmLabel, onClick = onConfirm)
                else -> XauxaPrimaryButton(label = confirmLabel, onClick = onConfirm)
            }
        },
        dismissButton = dismissLabel?.let {
            {
                XauxaTextAction(
                    label = it,
                    onClick = onDismiss,
                    modifier = if (destructive) {
                        Modifier.focusRequester(dismissFocus)
                    } else {
                        Modifier
                    },
                )
            }
        },
    )
}
