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
import androidx.compose.ui.focus.FocusRequester
import com.agendaqr.core.ui.components.AqFormFocus
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.rememberAqFormFocus
import com.agendaqr.destinations.domain.FieldLimits
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqMoonIconBadge
import com.agendaqr.core.ui.components.AqFormSection
import com.agendaqr.core.ui.components.AqMoonListRow
import com.agendaqr.core.ui.components.AqMultilineTextField
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqMoonStatusBanner
import com.agendaqr.core.ui.components.AqSwitch
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
    var favorite by rememberSaveable(existing.favorite) { mutableStateOf(existing.favorite) }
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
    // G1: orden visual de foco al primer error (solo los campos obligatorios:
    // nombre y código QR; categoría y nota son opcionales en el dominio).
    val formFocus = rememberAqFormFocus(FieldName)

    val nameError = submitted && name.isBlank()
    val qrError = submitted && qr.encoded.isBlank()
    // G1: Guardar siempre está habilitado salvo mientras guarda. La
    // validación se explica (errores de campo + foco), no se esconde
    // tras un botón deshabilitado (ADR-0032).
    val canSave = !isSaving

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
                    if (name.isBlank() || qr.encoded.isBlank()) {
                        // Foco al primer campo con error (orden visual: nombre).
                        // Si solo falta el QR, no hay campo de texto al que
                        // llevar el foco; el error de QR se muestra en el
                        // bloque de origen del código.
                        formFocus.focusFirstError(
                            errors = if (name.isBlank()) setOf(FieldName) else emptySet(),
                        )
                        return@AqBottomAction
                    }
                    // G2: límites de dominio — no se guarda si algún campo
                    // excede su tope (el input ya muestra el error).
                    if (name.length > FieldLimits.Nombre
                        || category.length > FieldLimits.Categoria
                        || note.length > FieldLimits.Nota) {
                        return@AqBottomAction
                    }
                    val now = com.agendaqr.destinations.domain.nowMillis()
                    onSave(
                        existing.copy(
                            name = name.trim(),
                            category = category.trim().ifBlank { null },
                            note = note.trim().ifBlank { null },
                            favorite = favorite,
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
            AqMoonStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        // Identidad del QR: nombre, categoría y nota bajo un encabezado
        // de sección (jerarquía H2); cada campo dibuja su propia caja.
        AqFormSection(AppStrings.Identidad) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.MdPlus),
            ) {
                AqTextField(
                    label = AppStrings.Nombre,
                    value = name,
                    onValueChange = { name = it },
                    focusRequester = formFocus.requesterFor(FieldName),
                    maxLength = FieldLimits.Nombre,
                    isError = nameError || name.length > FieldLimits.Nombre,
                    errorMessage = if (nameError) AppStrings.ElNombreEsObligatorio else null,
                )
                AqTextField(
                    label = AppStrings.Categoria,
                    value = category,
                    onValueChange = { category = it },
                    maxLength = FieldLimits.Categoria,
                )
                AqMultilineTextField(
                    label = AppStrings.Nota,
                    value = note,
                    onValueChange = { note = it },
                    maxLength = FieldLimits.Nota,
                )
                AqSwitch(
                    label = AppStrings.Favorito,
                    checked = favorite,
                    onCheckedChange = { favorite = it },
                )
            }
        }
        // Selección de contexto: fila seleccionable con chevron, no botón.
        // El estado seleccionado (contextId != null) se refleja con fondo y
        // borde de selección, no solo con el color del texto.
        AqMoonListRow(
            title = selection.contextId?.let { id ->
                AppStrings.ParaContextoElegido + (contexts.firstOrNull { it.id == id }?.name ?: AppStrings.ContextoGenerico)
            } ?: AppStrings.ParaElegirContexto,
            subtitle = AppStrings.PuedesSinContexto,
            onClick = { selection = selection.openPicker() },
            leading = {
                AqMoonIconBadge(
                    icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                    tone = AqTone.Info,
                )
            },
            showChevron = true,
            selected = selection.contextId != null,
        )
        // Origen del código: entradas de importación del QR del destino
        // (galería y cámara, secundarias) y vista previa solo cuando hay
        // contenido — sin rectángulo reservado en vacío (el error de envío
        // cubre el QR faltante). Guardar sigue siendo la única primaria.
        AqFormSection(AppStrings.OrigenDelCodigo) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
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

// G1: claves de foco al primer error (orden visual del formulario).
private const val FieldName = "nombre"
