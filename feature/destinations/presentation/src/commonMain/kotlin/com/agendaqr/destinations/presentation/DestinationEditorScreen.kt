package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqDockAction
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextArea
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.FieldLimits
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.newEntityId
import com.agendaqr.destinations.domain.nowMillis
import com.agendaqr.destinations.presentation.UserFacingError

/** Editor de destino (crear/editar). Editar devuelve el [Destination]
 *  actualizado por [onSave]; la persistencia la decide el llamador.
 *  Validación al enviar (nombre y QR obligatorios), límites de dominio y
 *  contexto con el mismo picker que el editor de actividades. */
@Composable
fun DestinationEditorScreen(
    existing: Destination?,
    contexts: List<Context> = emptyList(),
    appHosts: AppHosts? = null,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit = {},
    onBack: () -> Unit = {},
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
    onRetryError: () -> Unit = {},
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
) {
    val isNew = existing == null
    var name by rememberSaveable(existing?.name) { mutableStateOf(existing?.name.orEmpty()) }
    var category by rememberSaveable(existing?.category) { mutableStateOf(existing?.category.orEmpty()) }
    var note by rememberSaveable(existing?.note) { mutableStateOf(existing?.note.orEmpty()) }
    var favorite by rememberSaveable(existing?.favorite) { mutableStateOf(existing?.favorite ?: false) }
    var qr by remember(existing?.qr) { mutableStateOf(existing?.qr ?: QrAsset(encoded = "")) }
    var selection by rememberSaveable(existing?.contextId, stateSaver = ContextSelectionSaver) {
        mutableStateOf(ContextSelection(contextId = existing?.contextId))
    }
    var submitted by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(justCreatedContextId) {
        justCreatedContextId?.let { id -> selection = selection.select(id) }
    }

    val nameError = submitted && name.isBlank()
    val qrError = submitted && qr.encoded.isBlank()
    val overLimits = name.length > FieldLimits.Nombre ||
        category.length > FieldLimits.Categoria ||
        note.length > FieldLimits.Nota
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = if (isNew) AppStrings.CrearDestino else AppStrings.EditarDestino,
        onBack = if (isSaving) null else onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        scrollable = true,
        contentSpacing = AqSpacing.Md,
        bottomAction = {
            AqDockAction(
                label = if (isSaving) AppStrings.Guardando else if (isNew) AppStrings.Guardar else AppStrings.GuardarCambios,
                onClick = {
                    submitted = true
                    if (name.isBlank() || qr.encoded.isBlank() || overLimits) return@AqDockAction
                    val now = nowMillis()
                    val base = existing
                    val updated = if (base != null) {
                        base.copy(
                            name = name.trim(),
                            category = category.trim().ifBlank { null },
                            note = note.trim().ifBlank { null },
                            favorite = favorite,
                            qr = qr,
                            contextId = selection.contextId,
                            updatedAt = now,
                        )
                    } else {
                        Destination(
                            id = newEntityId("destination"),
                            name = name.trim(),
                            qr = qr,
                            category = category.trim().ifBlank { null },
                            note = note.trim().ifBlank { null },
                            favorite = favorite,
                            contextId = selection.contextId,
                            createdAt = now,
                            updatedAt = now,
                        )
                    }
                    onSave(updated)
                },
                enabled = !isSaving,
                isLoading = isSaving,
            )
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = onRetryError,
                onClear = onClearError,
            )
            AqAlert(
                tone = AqTone.Danger,
                message = err.display(),
                confirmLabel = bannerAction?.label,
                onConfirm = bannerAction?.onClick,
                dismissLabel = bannerDismiss?.label ?: AppStrings.Cerrar,
                onDismiss = bannerDismiss?.onClick ?: onClearError,
            )
        }
        AqTextInput(
            label = AppStrings.LabelNombre,
            value = name,
            onValueChange = { name = it },
            maxLength = FieldLimits.Nombre,
            isError = nameError || name.length > FieldLimits.Nombre,
            errorMessage = if (nameError) AppStrings.ElNombreEsObligatorio else null,
        )
        AqTextInput(
            label = AppStrings.Categoria,
            value = category,
            onValueChange = { category = it },
            maxLength = FieldLimits.Categoria,
        )
        AqTextArea(
            label = AppStrings.LabelNota,
            value = note,
            onValueChange = { note = it },
            maxLength = FieldLimits.Nota,
            // Moon v1 «Textarea»: altura base 132 dp (default del componente).
        )
        AqSwitch(
            label = AppStrings.MarcarComoFavorito,
            checked = favorite,
            onCheckedChange = { favorite = it },
            // Fila de ajuste: etiqueta a la izquierda, control a la derecha.
            labelFirst = true,
        )
        AqMenuItem(
            title = selection.contextId?.let { id ->
                AppStrings.ParaContextoElegido +
                    (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
            } ?: AppStrings.ParaElegirContexto,
            subtitle = AppStrings.PuedesSinContexto,
            onClick = { selection = selection.openPicker() },
            leading = {
                AqIcon(
                    imageVector = AqIcons.Context,
                    contentDescription = null,
                    size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
                    tint = AqColor.TextSecondary,
                )
            },
            selected = selection.contextId != null,
        )
        QrImportControls(onResult = { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        })
        if (qr.encoded.isNotBlank()) {
            AqQrPreview(qr.encoded)
        } else if (qrError) {
            AqText(
                text = AppStrings.FaltaElCodigoQrUsa,
                size = AqType.Caption,
                color = AqColor.Danger,
            )
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
