package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaDialogList
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.destinations.domain.Context

/**
 * Semántica del selector de contexto (S08) — 
 * - **Elegir** un elemento confirma y cierra: [ContextSelection.select].
 * - **Cancelar** cierra SIN tocar la selección previa.
 * - **Quitar contexto** es la única vía de limpiar la selección.
 *
 * U3/ADR-0003 (opción C): el selector ofrece **crear contexto** sin salir
 * del draft: el paso de creación (Nombre obligatorio + Nota) vive en el
 * mismo diálogo y al crear vuelve a la lista con el nuevo elemento
 * seleccionado — el flujo padre nunca sale de composición.
 */
internal data class ContextSelection(
    val contextId: String? = null,
    val pickerOpen: Boolean = false,
) {
    fun openPicker(): ContextSelection = copy(pickerOpen = true)
    fun cancelPicker(): ContextSelection = copy(pickerOpen = false)
    fun select(id: String): ContextSelection = copy(pickerOpen = false, contextId = id)
    fun removeContext(): ContextSelection = copy(pickerOpen = false, contextId = null)
    val canRemove: Boolean get() = contextId != null
}

internal val ContextSelectionSaver: Saver<ContextSelection, Any> = listSaver(
    save = { selection -> saveContextSelection(selection) },
    restore = { values -> restoreContextSelection(values) },
)

/** Serializa la selección para el bundle (lista de primitivos). */
internal fun saveContextSelection(selection: ContextSelection): List<Any> =
    listOf(selection.pickerOpen, selection.contextId.orEmpty())

/** Inversa de [saveContextSelection]. */
internal fun restoreContextSelection(values: List<Any>): ContextSelection {
    val open = values[0] as Boolean
    val id = values[1] as String
    return ContextSelection(contextId = id.takeIf(String::isNotEmpty), pickerOpen = open)
}

/** Estado del paso de creación dentro del selector (S08 → crear → volver). */
internal enum class ContextPickerStep { List, Create }

/**
 * Selector compartido (S08). [onCreateContext] persiste (VM) y reporta el
 * id nuevo; el selector lo selecciona y vuelve a la lista.
 */
@Composable
internal fun ContextPickerDialog(
    contexts: List<Context>,
    selection: ContextSelection,
    onSelect: (String) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    onCreateContext: (name: String, note: String?) -> Unit,
    justCreatedContextId: String? = null,
) {
    var step by rememberSaveable { mutableStateOf(ContextPickerStep.List) }
    var name by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    // sobrevive a recreación.
    var submitted by rememberSaveable { mutableStateOf(false) }

    // El contexto recién creado queda seleccionado y marcado en la lista.
    val selectedId = justCreatedContextId ?: selection.contextId

    when (step) {
        ContextPickerStep.Create -> XauxaDialog(
            title = AppStrings.CrearContexto,
            confirmLabel = AppStrings.Crear,
            onConfirm = {
                submitted = true
                if (name.isNotBlank()) {
                    onCreateContext(name, note.takeIf { it.isNotBlank() })
                    // El paso vuelve a la lista al crear (ADR: vuelve a la
                    // lista con el nuevo elemento marcado).
                    step = ContextPickerStep.List
                }
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { step = ContextPickerStep.List },
            // Back/fuera vuelve a la lista: nunca destruye el draft.
            onDismissRequest = { step = ContextPickerStep.List },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    XauxaTextInput(
                        label = AppStrings.Nombre,
                        value = name,
                        onValueChange = { name = it },
                        isRequired = true,
                        isError = submitted && name.isBlank(),
                        errorMessage = if (submitted && name.isBlank()) AppStrings.ElNombreEsObligatorio else null,
                    )
                    XauxaTextInput(
                        label = AppStrings.Nota,
                        value = note,
                        onValueChange = { note = it },
                        singleLine = false,
                        minLines = 2,
                    )
                }
            },
        )

        ContextPickerStep.List -> XauxaDialog(
            title = AppStrings.SeleccionarContexto,
            confirmLabel = AppStrings.Cancelar,
            // La acción principal es elegir de la lista (§1); Cancelar y
            // Quitar contexto son salidas secundarias.
            confirmAsText = true,
            onConfirm = onCancel,
            dismissLabel = if (selection.canRemove) AppStrings.QuitarContexto else null,
            onDismiss = onRemove,
            // Back del sistema / toque fuera: cancelar, nunca quitar la
            // selección por accidente (§3).
            onDismissRequest = onCancel,
            content = {
                XauxaDialogList {
                    // U3/ADR-0003 (opción C): crear sin salir del draft.
                    // Arriba de la lista: el slot de texto del diálogo
                    // recorta el fondo en pantallas bajas.
                    XauxaTextAction(
                        label = AppStrings.CrearContexto,
                        onClick = {
                            submitted = false
                            name = ""
                            note = ""
                            step = ContextPickerStep.Create
                        },
                    )
                    if (contexts.isEmpty()) {
                        XauxaText(
                            text = AppStrings.SinContextosDisponibles,
                            color = XauxaColor.TextSecondary,
                        )
                    } else {
                        contexts.forEach { context ->
                            XauxaListRow(
                                title = context.name,
                                subtitle = context.note?.takeIf { it.isNotBlank() },
                                // M4: el acento derivado identifica el
                                // contexto; Cancelar/Quitar/Back intactos.
                                accent = accentFor(context.id).background,
                                tone = if (context.id == selectedId) XauxaTone.Info else XauxaTone.Neutral,
                                onClick = { onSelect(context.id) },
                            )
                        }
                    }
                }
            },
        )
    }
}

/**
 * Formulario de creación para S06 vacío (opción B/C del ADR): mismo paso
 * mínimo, mismas acciones. Con ≥1 contexto S06 NO ofrece creación (no se
 * inventa jerarquía en S06 lleno).
 */
@Composable
internal fun ContextCreationDialog(
    onConfirmCreate: (name: String, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    // sobrevive a recreación.
    var submitted by rememberSaveable { mutableStateOf(false) }
    XauxaDialog(
        title = AppStrings.CrearContexto,
        confirmLabel = AppStrings.Crear,
        onConfirm = {
            submitted = true
            if (name.isNotBlank()) onConfirmCreate(name, note.takeIf { it.isNotBlank() })
        },
        dismissLabel = AppStrings.Cancelar,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaTextInput(
                    label = AppStrings.Nombre,
                    value = name,
                    onValueChange = { name = it },
                    isRequired = true,
                    isError = submitted && name.isBlank(),
                    errorMessage = if (submitted && name.isBlank()) AppStrings.ElNombreEsObligatorio else null,
                )
                XauxaTextInput(
                    label = AppStrings.Nota,
                    value = note,
                    onValueChange = { note = it },
                    singleLine = false,
                    minLines = 2,
                )
            }
        },
    )
}
