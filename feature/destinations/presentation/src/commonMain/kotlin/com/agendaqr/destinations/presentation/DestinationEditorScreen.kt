package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScannerViewport
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaType
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
        existing, onSave, onImportMany, contexts, onBack, isSaving, error, onClearError,
        onCreateContext, justCreatedContextId, onRetryError,
    )
}

@Composable
private fun DestinationEditorContent(
    existing: Destination,
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

    // V1.1 (M7/T7): Guardar como acción principal de la app bar; Volver
    // pasa a la flecha de la barra (sin desbordar en dos líneas).
    XauxaScreenScaffold(
        scrollable = true,
        bottomBar = {
            XauxaAppBar(
                actions = listOf(
                    XauxaAppBarAction(
                        label = if (isSaving) AppStrings.Guardando else AppStrings.Guardar,
                        icon = XauxaIcons.Save,
                        primary = true,
                        enabled = canSave,
                        onClick = {
                            submitted = true
                            if (name.isBlank() || qr.encoded.isBlank()) return@XauxaAppBarAction
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
                    ),
                ),
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
    ) {
        XauxaPageTitle(
            text = AppStrings.EditarQr,
        )
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = { if (err.action == ErrorAction.Retry) onRetryError() else onClearError() },
                onDismiss = onClearError,
                dismissLabel = AppStrings.Descartar,
            )
        }
        XauxaTextInput(
            label = AppStrings.Nombre,
            value = name,
            onValueChange = { name = it },
            isRequired = true,
            isError = nameError,
            errorMessage = if (nameError) AppStrings.ElNombreEsObligatorio else null,
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
