package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqTextInputSize
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.FieldLimits

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
    save = { selection -> listOf(selection.contextId, selection.pickerOpen) },
    restore = { values ->
        ContextSelection(
            contextId = values.getOrNull(0) as? String,
            pickerOpen = values.getOrNull(1) as? Boolean == true,
        )
    },
)

internal fun saveContextSelection(selection: ContextSelection): List<Any> =
    listOf(selection.contextId, selection.pickerOpen)

internal fun restoreContextSelection(values: List<Any>): ContextSelection =
    ContextSelection(
        contextId = values.getOrNull(0) as? String,
        pickerOpen = values.getOrNull(1) as? Boolean == true,
    )

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
    val toastHost: com.agendaqr.core.ui.components.AqToastHostState? = null
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showCreateFields by remember { mutableStateOf(false) }
    AqModal(
        title = AppStrings.ElegirContexto,
        dismissLabel = AppStrings.Cancelar,
        onDismissRequest = onCancel,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                if (contexts.isNotEmpty()) {
                    AqSectionTitle(text = AppStrings.ContextosExistentes)
                    contexts.forEach { ctx ->
                        AqMenuItem(
                            title = ctx.name,
                            subtitle = ctx.note.orEmpty(),
                            selected = selection.contextId == ctx.id,
                            onClick = { onSelect(ctx.id) },
                            leading = {
                                AqTag(text = ctx.id.take(1).uppercase())
                            },
                        )
                    }
                }
                AqSectionTitle(text = AppStrings.CrearContexto)
                AqTextInput(
                    label = AppStrings.LabelNombre,
                    value = name,
                    onValueChange = { name = it },
                    size = AqTextInputSize.Md,
                )
                AqTextInput(
                    label = AppStrings.LabelNota,
                    value = note,
                    onValueChange = { note = it },
                    size = AqTextInputSize.Md,
                )
                AqButton(
                    label = AppStrings.Crear,
                    onClick = {
                        onCreateContext(name, note)
                    },
                    variant = AqButtonVariant.Primary,
                )
            }
        },
    )
}
