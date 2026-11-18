package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextField
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
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
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = AppStrings.EditarQr,
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        // Grupos funcionales (formulario · contexto · imagen · vista previa)
        // separados a 16 dp (Lg), el paso canónico entre organismos.
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        bottomAction = {
            AqBottomAction(
                label = if (isSaving) AppStrings.Guardando else AppStrings.Guardar,
                onClick = {
                    submitted = true
                    if (name.isBlank() || qr.encoded.isBlank()) return@AqBottomAction
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
            )
        },
    ) {
        error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = onRetryError,
                onClear = onClearError,
            )
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        // Formulario: cada campo dibuja su propia caja y borde (superficie
        // de campo). No hay AqListBody envolviendo: evitaría una superficie
        // duplicada sobre cada AqTextField. La separación etiqueta-campo (4
        // dp) la gestiona AqTextField; la separación entre campos es 12 dp.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.MdPlus),
        ) {
            AqTextField(
                label = AppStrings.Nombre,
                value = name,
                onValueChange = { name = it },
                isError = nameError,
                errorMessage = if (nameError) AppStrings.ElNombreEsObligatorio else null,
            )
            AqTextField(label = AppStrings.Categoria, value = category, onValueChange = { category = it })
            AqTextField(label = AppStrings.Nota, value = note, onValueChange = { note = it }, singleLine = false)
        }
        // Selección de contexto: fila seleccionable con chevron, no botón.
        // El estado seleccionado (contextId != null) se refleja con fondo y
        // borde de selección, no solo con el color del texto.
        AqListRow(
            title = selection.contextId?.let { id ->
                AppStrings.ParaContextoElegido + (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
            } ?: AppStrings.ParaElegirContexto,
            subtitle = AppStrings.PuedesSinContexto,
            onClick = { selection = selection.openPicker() },
            leading = {
                AqIconBadge(
                    icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                    tone = AqTone.Info,
                )
            },
            showChevron = true,
            selected = selection.contextId != null,
        )
        // Sección de imagen: entradas de importación del QR del destino
        // (galería y cámara) y vista previa solo cuando hay contenido —
        // sin rectángulo reservado en vacío (el error de envío cubre el
        // QR faltante).
        QrImportControls(onResult = { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        })
        if (qr.encoded.isNotBlank()) {
            AqQrPreview(qr.encoded)
        } else if (qrError) {
            AqText(AppStrings.FaltaElCodigoQrUsa, size = AqType.Caption, color = AqColor.Danger)
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
