package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles

/**
 * Superposiciones de AgendaMobile: los diálogos confirman (dos acciones
 * claras) y resuelven selecciones acotadas — la distribución de patrones
 * de las apps móviles contemporáneas.
 *
 * NOTA (ADR-0013, riesgos): el patrón "hoja inferior para selectores"
 * (Ant ActionSheet / Vant Picker) se evaluó con `ModalBottomSheet` de M3
 * y se retiró: la ventana de la hoja no despacha acciones semánticas bajo
 * Robolectric (el contrato de pruebas UI de este repositorio), por lo
 * que el flujo S08 quedaba inverificable en el gate. Reevaluar cuando el
 * entorno de pruebas lo soporte.
 */

/**
 * Lista dentro de un diálogo: filas de selección con scroll cuando
 * la lista es larga. Sin cambio de jerarquía: el orden de las filas y las
 * acciones del contenedor no cambian.
 */
@Composable
fun AmDialogList(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(max = AmMetrics.DialogListMaxHeight)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
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
fun AmDialog(
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
     * lista), el confirmar se pinta como acción de texto en vez de
     * botón primario.
     */
    confirmAsText: Boolean = false,
    /**
     * La confirmación es DESTRUCTIVA (eliminar QR/actividad/comprobante).
     * Usa [AmDangerButton] y el foco inicial cae en la acción
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
                color = AmColor.TextPrimary,
            )
        },
        text = {
            if (content != null) {
                content()
            } else {
                Text(
                    message.orEmpty(),
                    style = AmTextStyles.Body,
                    color = AmColor.TextSecondary,
                )
            }
        },
        confirmButton = {
            when {
                confirmAsText -> AmTextAction(label = confirmLabel, onClick = onConfirm)
                destructive -> AmDangerButton(label = confirmLabel, onClick = onConfirm)
                else -> AmPrimaryButton(label = confirmLabel, onClick = onConfirm)
            }
        },
        dismissButton = dismissLabel?.let {
            {
                AmTextAction(
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
