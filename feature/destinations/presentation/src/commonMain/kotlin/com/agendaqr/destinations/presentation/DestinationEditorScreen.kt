package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
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
import com.agendaqr.core.ui.components.XauxaHeading
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
    /** REINTENTAR del banner: re-ejecuta el guardado fallido en el VM. */
    onRetryError: () -> Unit = onClearError,
    /** U3/ADR-0003: crear contexto desde S08 sin perder el draft. */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
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
    DestinationEditorContent(
        initial, existing, onSave, onImportMany, contexts, onBack, isSaving, error, onClearError,
        onCreateContext, justCreatedContextId, onRetryError,
    )
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
    onCreateContext: (name: String, note: String?) -> Unit,
    justCreatedContextId: String?,
    onRetryError: () -> Unit,
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
    // U3/ADR-0003: crear contexto desde S08 lo deja seleccionado (el
    // selector vuelve a la lista con el nuevo elemento marcado).
    androidx.compose.runtime.LaunchedEffect(justCreatedContextId) {
        justCreatedContextId?.let { id -> selection = selection.select(id) }
    }

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
        XauxaHeading(
            text = if (existing == null) AppStrings.AgregarDestino else AppStrings.EditarDestino,
            size = XauxaType.Headline,
            fontWeight = FontWeight.Bold,
        )
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = { if (err.action == ErrorAction.Retry) onRetryError() else onClearError() },
                onDismiss = onClearError,
            )
        }
        XauxaTextInput(
            label = AppStrings.Nombre,
            value = name,
            onValueChange = { name = it },
            isRequired = true,
            isError = nameError,
            errorMessage = if (nameError) "El nombre es obligatorio" else null,
        )
        XauxaTextInput(label = AppStrings.Categoria, value = category, onValueChange = { category = it })
        XauxaTextInput(label = AppStrings.Nota, value = note, onValueChange = { note = it }, singleLine = false, minLines = 3)
        XauxaSecondaryButton(
            label = selection.contextId?.let { id -> AppStrings.ParaContextoElegido + (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico) } ?: AppStrings.ParaElegirContexto,
            onClick = { selection = selection.openPicker() },
        )
        XauxaText(
            AppStrings.shareFromAnotherAppLabel,
            size = XauxaType.Label,
            color = XauxaColor.TextSecondary,
        )
        QrImportControls(onResult = { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        })
        if (qr.encoded.isBlank()) {
            XauxaScannerViewport(
                scanning = false,
                hint = AppStrings.ImportaUnQrConCamara,
            )
            if (qrError) {
                XauxaText(AppStrings.FaltaElCodigoQrUsa, size = XauxaType.Caption, color = XauxaColor.Danger)
            }
        } else {
            XauxaQrPreview(qr.encoded)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaSecondaryButton(label = AppStrings.Volver, onClick = onBack, enabled = !isSaving)
            XauxaPrimaryButton(
                label = AppStrings.Guardar,
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
                onCreateContext = onCreateContext,
                justCreatedContextId = justCreatedContextId,
            )
        }
    }
}
