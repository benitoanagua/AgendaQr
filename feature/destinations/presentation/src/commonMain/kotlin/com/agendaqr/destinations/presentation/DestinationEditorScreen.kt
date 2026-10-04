package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScannerViewport
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun DestinationEditorScreen(
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    contexts: List<Context> = emptyList(),
    onBack: () -> Unit,
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
) {
    val initial = remember(existing) {
        existing ?: Destination(
            id = com.agendaqr.destinations.domain.newEntityId("destination"),
            name = "",
            qr = QrAsset(encoded = ""),
            createdAt = com.agendaqr.destinations.domain.nowMillis(),
            updatedAt = com.agendaqr.destinations.domain.nowMillis(),
        )
    }
    DestinationEditorContent(initial, existing, onSave, onImportMany, contexts, onBack, isSaving, error, onClearError)
}

@Composable
private fun DestinationEditorContent(
    initial: Destination,
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    contexts: List<Context>,
    onBack: () -> Unit,
    isSaving: Boolean,
    error: UserFacingError?,
    onClearError: () -> Unit,
) {
    var name by rememberSaveable(initial.name) { mutableStateOf(initial.name) }
    var category by rememberSaveable(existing?.category) { mutableStateOf(existing?.category.orEmpty()) }
    var note by rememberSaveable(existing?.note) { mutableStateOf(existing?.note.orEmpty()) }
    var qr by remember(existing?.qr) { mutableStateOf(existing?.qr ?: QrAsset(encoded = "")) }
    // S08: selección de contexto con semántica explícita — Cancelar no
    // toca la selección; Quitar contexto es la única vía de limpiarla.
    var selection by rememberSaveable(existing?.contextId, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing?.contextId))
    }
    var submitted by remember { mutableStateOf(false) }

    val nameError = submitted && name.isBlank()
    val qrError = submitted && qr.encoded.isBlank()
    val canSave = name.isNotBlank() && qr.encoded.isNotBlank() && !isSaving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text(
            if (existing == null) "Agregar destino" else "Editar destino",
            modifier = Modifier.semantics { heading() },
            fontSize = XauxaType.Headline,
            fontWeight = FontWeight.Bold,
            color = XauxaColor.TextPrimary,
        )
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = onClearError,
                onDismiss = onClearError,
            )
        }
        XauxaTextInput(
            label = "Nombre",
            value = name,
            onValueChange = { name = it },
            isRequired = true,
            isError = nameError,
            errorMessage = if (nameError) "El nombre es obligatorio" else null,
        )
        XauxaTextInput(label = "Categoría", value = category, onValueChange = { category = it })
        XauxaTextInput(label = "Nota", value = note, onValueChange = { note = it }, singleLine = false, minLines = 3)
        XauxaSecondaryButton(
            label = selection.contextId?.let { id -> "Para: " + (contexts.firstOrNull { it.id == id }?.name ?: "Contexto") } ?: "Para: elegir contexto (opcional)",
            onClick = { selection = selection.openPicker() },
        )
        Text(
            "Desde otra app: comparte una imagen o PDF con Agenda QR.",
            fontSize = XauxaType.Label,
            color = XauxaColor.TextSecondary,
        )
        QrImportControls { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        }
        if (qr.encoded.isBlank()) {
            XauxaScannerViewport(
                scanning = false,
                hint = "Importa un QR con Cámara o Galería para previsualizarlo aquí",
            )
            if (qrError) {
                Text("Falta el código QR: usa Cámara o Galería", fontSize = XauxaType.Caption, color = XauxaColor.Danger)
            }
        } else {
            XauxaQrPreview(qr.encoded)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaSecondaryButton(label = "Volver", onClick = onBack, enabled = !isSaving)
            XauxaPrimaryButton(
                label = "Guardar",
                onClick = {
                    submitted = true
                    if (name.isBlank() || qr.encoded.isBlank()) return@XauxaPrimaryButton
                    val now = com.agendaqr.destinations.domain.nowMillis()
                    onSave(
                        initial.copy(
                            name = name.trim(),
                            category = category.trim().ifBlank { null },
                            note = note.trim().ifBlank { null },
                            qr = qr,
                            contextId = selection.contextId,
                            updatedAt = now,
                        ),
                    )
                },
                enabled = canSave,
                isLoading = isSaving,
            )
        }
        if (selection.pickerOpen) {
            ContextPickerDialog(
                contexts = contexts,
                selection = selection,
                onSelect = { id -> selection = selection.select(id) },
                onRemove = { selection = selection.removeContext() },
                onCancel = { selection = selection.cancelPicker() },
            )
        }
    }
}
