package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScannerViewport
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextField
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset

/**
 * Editor del QR (la rama "agregar" no existe: Edit(null) enruta a S02 en
 * el VM, así que esta pantalla siempre edita un destino existente; la
 * rama muerta se retiró en vez de reactivarla contra S02).
 */
@Composable
fun DestinationEditorScreen(
    existing: Destination,
    appHosts: AppHosts? = null,
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
    DestinationEditorContent(
        existing, appHosts, onSave, onImportMany, contexts, onBack, isSaving, error, onClearError,
        onCreateContext, justCreatedContextId, onRetryError,
    )
}

@Composable
private fun DestinationEditorContent(
    existing: Destination,
    appHosts: AppHosts? = null,
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
    var name by rememberSaveable(existing.name) { mutableStateOf(existing.name) }
    var category by rememberSaveable(existing.category) { mutableStateOf(existing.category.orEmpty()) }
    var note by rememberSaveable(existing.note) { mutableStateOf(existing.note.orEmpty()) }
    var qr by remember(existing.qr) { mutableStateOf(existing.qr) }
    // S08: selección de contexto con semántica explícita — Cancelar no
    // toca la selección; Quitar contexto es la única vía de limpiarla.
    var selection by rememberSaveable(existing.contextId, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing.contextId))
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

    // V1.4 (§12): "Editar QR" en la barra superior con flecha atrás; Guardar
    // es el botón principal FIJO abajo (acción real de guardado), nunca
    // recortado por el scroll. Mientras se guarda no hay vuelta (§3).
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.EditarQr,
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = {
            AmPrimaryButton(
                label = if (isSaving) AppStrings.Guardando else AppStrings.Guardar,
                onClick = {
                    submitted = true
                    if (name.isBlank() || qr.encoded.isBlank()) return@AmPrimaryButton
                    val now = com.agendaqr.destinations.domain.nowMillis()
                    onSave(
                        existing.copy(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = AmSpacing.ScreenMargin, vertical = AmSpacing.Sm),
            )
        },
    ) {
        error?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
                actionLabel = err.action.label,
                onAction = { if (err.action == ErrorAction.Retry) onRetryError() else onClearError() },
                onDismiss = onClearError,
                dismissLabel = AppStrings.Descartar,
            )
        }
        AmTextField(
            label = AppStrings.Nombre,
            value = name,
            onValueChange = { name = it },
            isError = nameError,
            errorMessage = if (nameError) AppStrings.ElNombreEsObligatorio else null,
        )
        AmTextField(label = AppStrings.Categoria, value = category, onValueChange = { category = it })
        AmTextField(label = AppStrings.Nota, value = note, onValueChange = { note = it }, singleLine = false, minLines = 3)
        AmSecondaryButton(
            label = selection.contextId?.let { id -> AppStrings.ParaContextoElegido + (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico) } ?: AppStrings.ParaElegirContexto,
            onClick = { selection = selection.openPicker() },
        )
        // Entradas de importación del QR del destino: galería (una o
        // varias imágenes) y cámara; la primaria de esta pantalla es Guardar.
        QrImportControls(onResult = { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        })
        if (qr.encoded.isBlank()) {
            AmScannerViewport(
                scanning = false,
                hint = AppStrings.ImportaUnQrConCamara,
            )
            if (qrError) {
                AmText(AppStrings.FaltaElCodigoQrUsa, size = AmType.Caption, color = AmColor.Danger)
            }
        } else {
            AmQrPreview(qr.encoded)
        }
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
