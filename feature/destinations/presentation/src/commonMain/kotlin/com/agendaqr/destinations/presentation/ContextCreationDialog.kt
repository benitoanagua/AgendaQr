package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqTextInput

/**
 * Diálogo de creación de contexto (S06/S08). Solicita nombre y nota
 * opcional, y confirma mediante [onConfirmCreate].
 */
@Composable
internal fun ContextCreationDialog(
    onConfirmCreate: (name: String, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    AqModal(
        title = AppStrings.CrearContexto,
        dismissLabel = AppStrings.Cancelar,
        confirmLabel = AppStrings.Crear,
        onDismissRequest = onDismiss,
        onConfirm = { onConfirmCreate(name, note.takeIf { it.isNotBlank() }) },
    ) {
        AqTextInput(
            label = AppStrings.Nombre,
            value = name,
            onValueChange = { name = it },
        )
        AqTextInput(
            label = AppStrings.Nota,
            value = note,
            onValueChange = { note = it },
        )
    }
}
