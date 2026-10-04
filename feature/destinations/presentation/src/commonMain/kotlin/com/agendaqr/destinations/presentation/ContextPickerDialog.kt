package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Context

/**
 * Semántica del selector de contexto (S08) — T4.
 *
 * Contrato congelado:
 * - S08: "elegir contexto para el flujo padre. Al terminar, vuelve al flujo
 *   original conservando el draft."
 * - §3: "Back conserva el trabajo del flujo padre"; "Cancelar abandona la
 *   intención actual".
 *
 * Reglas que este modelo hace explícitas y testeables:
 * - **Elegir** un elemento confirma y cierra: [select].
 * - **Cancelar** cierra SIN tocar la selección previa: [cancelPicker].
 *   (El descarte anterior, "Sin contexto", borraba la selección sin pedirlo.)
 * - **Quitar contexto** es la única vía de limpiar la selección y solo
 *   existe si hay una: [removeContext]/[canRemove].
 *
 * El draft del flujo padre (campos del editor) vive fuera de este modelo:
 * el selector solo decide sobre `contextId` y su propia visibilidad, así el
 * draft sobrevive abrir/cerrar el selector.
 */
internal data class ContextSelection(
    val contextId: String? = null,
    val pickerOpen: Boolean = false,
) {
    fun openPicker(): ContextSelection = copy(pickerOpen = true)

    /** Cancelar: cierra sin tocar la selección. */
    fun cancelPicker(): ContextSelection = copy(pickerOpen = false)

    /** Elegir: confirma y cierra con el contexto elegido. */
    fun select(id: String): ContextSelection = copy(pickerOpen = false, contextId = id)

    /** Quitar contexto: limpia la selección (acción explícita). */
    fun removeContext(): ContextSelection = copy(pickerOpen = false, contextId = null)

    /** "Quitar contexto" solo se ofrece si hay un contexto elegido. */
    val canRemove: Boolean get() = contextId != null
}

/** Serializa la selección para el bundle (lista de primitivos). */
internal fun saveContextSelection(selection: ContextSelection): List<Any> =
    listOf(selection.pickerOpen, selection.contextId.orEmpty())

/** Inversa de [saveContextSelection]. */
internal fun restoreContextSelection(values: List<Any>): ContextSelection {
    val open = values[0] as Boolean
    val id = values[1] as String
    return ContextSelection(contextId = id.takeIf(String::isNotEmpty), pickerOpen = open)
}

/** Persistencia del draft de contexto ante recreaciones (rotación, etc.). */
internal val ContextSelectionSaver: Saver<ContextSelection, Any> = listSaver(
    save = { selection -> saveContextSelection(selection) },
    restore = { values -> restoreContextSelection(values) },
)

/**
 * Selector de contexto compartido por los editores (S08).
 *
 * Título "Seleccionar contexto" (el "¿A cuál corresponde?" era copy de la
 * ambigüedad de comprobantes, S11). Acciones: [Cancelar] y, solo si hay
 * contexto elegido, [Quitar contexto]; elegir una fila confirma y cierra.
 */
@Composable
internal fun ContextPickerDialog(
    contexts: List<Context>,
    selection: ContextSelection,
    onSelect: (String) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
) {
    XauxaDialog(
        title = AppStrings.SeleccionarContexto,
        confirmLabel = AppStrings.Cancelar,
        onConfirm = onCancel,
        dismissLabel = if (selection.canRemove) "Quitar contexto" else null,
        onDismiss = onRemove,
        // Back del sistema / toque fuera: cancelar, nunca quitar la selección
        // por accidente (§3 "Cancelar abandona la intención actual").
        onDismissRequest = onCancel,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                if (contexts.isEmpty()) {
                    XauxaText(AppStrings.SinContextosDisponibles, color = XauxaColor.TextSecondary)
                } else {
                    contexts.forEach { context ->
                        XauxaListRow(
                            title = context.name,
                            subtitle = context.note?.takeIf { it.isNotBlank() },
                            tone = if (context.id == selection.contextId) XauxaTone.Info else XauxaTone.Neutral,
                            onClick = { onSelect(context.id) },
                        )
                    }
                }
            }
        },
    )
}
