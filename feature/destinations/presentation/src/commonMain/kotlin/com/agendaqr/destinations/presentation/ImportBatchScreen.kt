package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmDialog
import com.agendaqr.core.ui.components.AmFeedbackEvent
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmMetaRow
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmTextActionVariant
import com.agendaqr.core.ui.components.AmBottomAction
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmStatusPill
import com.agendaqr.core.ui.components.AmCard
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.components.AmLoading
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind

/**
 * Vista previa del elemento existente con el que coincide un duplicado
 * ("Ver existente"). No navega: el usuario decide sin perder la
 * revisión del lote.
 */
data class ExistingImportPreview(
    val qrAsset: com.agendaqr.destinations.domain.QrAsset? = null,
    val comprobanteBytes: ByteArray? = null,
    val comprobanteMimeType: String? = null,
)

@Composable
fun ImportBatchScreen(
    state: ImportBatchUiState,
    onAction: (ImportBatchAction) -> Unit,
    existing: ExistingImportPreview? = null,
    appHosts: AppHosts? = null,
) {
    // guardado del lote anunciado en la TRANSICIÓN al
    // estado Saved (por evento, no por estado).
    var savedEvent by remember { mutableStateOf<Any?>(null) }
    var wasSaving by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state) {
        val savingNow = state is ImportBatchUiState.Saving
        if (wasSaving && state is ImportBatchUiState.Saved) {
            savedEvent = Any()
        }
        wasSaving = state is ImportBatchUiState.Saving || state is ImportBatchUiState.Saved
    }
    AmFeedbackEvent(event = savedEvent, message = AppStrings.GuardadoOk)
    // V1.4 (§4 S12): barra superior con título y atrás; "Guardar
    // reconocidos" como botón principal FIJO abajo, solo cuando hay
    // elementos reconocibles; "Revisar N pendientes" en el cuerpo.
    // Mientras se guarda NO hay vuelta (el lote se perdería); el Back del
    // estado de revisión regresa al RESULTADO (flujo padre), no al origen.
    val currentBatch = when (state) {
        is ImportBatchUiState.Result -> state.batch
        is ImportBatchUiState.Review -> state.batch
        is ImportBatchUiState.Saving -> state.batch
        is ImportBatchUiState.Saved -> state.batch
        is ImportBatchUiState.Error -> state.batch
        else -> null
    }
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.ResultadoDeImportacion,
                onBack = if (state is ImportBatchUiState.Saving) null else ({ onAction(ImportBatchAction.Back) }),
                backLabel = if (state is ImportBatchUiState.Review) AppStrings.VolverAResultado else AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (currentBatch?.canSaveRecognized() == true) {
            {
                AmBottomAction(
                    label = if (state is ImportBatchUiState.Error) {
                        AppStrings.reintentarGuardado
                    } else {
                        val recognized = currentBatch?.uniqueRecognized?.size ?: 0
                        if (recognized > 0) guardarReconocidosLabel(recognized) else AppStrings.GuardarReconocidos
                    },
                    onClick = { onAction(ImportBatchAction.SaveRecognized) },
                    enabled = state !is ImportBatchUiState.Saving,
                    isLoading = state is ImportBatchUiState.Saving,
                )
            }
        } else {
            null
        },
    ) {
        when (state) {
            // Idle es alcanzable por restauración tras muerte de proceso
            // (stack restaurado con S12 arriba y holder nuevo): el estado
            // lleva una acción real ("Elegir imágenes"), no una pantalla
            // vacía.
            ImportBatchUiState.Idle -> {
                AmText(AppStrings.TraeVariosElementosAAgenda, color = AmColor.TextSecondary)
                ImportBatchPickerButton(label = AppStrings.ElegirImagenes)
            }
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Saving -> {
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                AmStatusBanner(
                    title = AppStrings.GuardadoOk,
                    message = AppStrings.batchSavedKeepReviewNotice,
                    tone = AmTone.Success,
                )
                state.batch.let { batch ->
                    if (batch.pendingItems().isNotEmpty()) {
                        AmSecondaryButton(
                            label = reviewPendingLabel(batch),
                            onClick = { onAction(ImportBatchAction.ReviewPending) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            is ImportBatchUiState.Error -> {
                // El banner no lleva acción: el botón fijo inferior
                // "Reintentar guardado" es la única fuente de la acción.
                AmStatusBanner(
                    title = state.error.what,
                message = state.error.dataStatus,
                    tone = AmTone.Danger,
                )
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
            }
        }
    }

    existing?.let { preview ->
        AmDialog(
            title = AppStrings.ElementoExistente,
            confirmLabel = AppStrings.Cerrar,
            onConfirm = { onAction(ImportBatchAction.CloseExisting) },
            onDismiss = { onAction(ImportBatchAction.CloseExisting) },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                    when {
                        preview.qrAsset != null -> AmQrPreview(preview.qrAsset.encoded)
                        preview.comprobanteBytes != null ->
                            ComprobantePreview(preview.comprobanteBytes, preview.comprobanteMimeType)
                        else -> AmText(AppStrings.NoPudimosMostrarElElemento, color = AmColor.TextSecondary)
                    }
                }
            },
        )
    }
}

/**
 * Resumen de importación con jerarquía clara:
 * 1. Reconocidos (listos para guardar) — tono de éxito
 * 2. Posibles duplicados — tono de advertencia
 * 3. Pendientes de revisión — tono neutro
 */
@Composable
private fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    // Tarjeta resumen como filas etiqueta-valor: cada estado del lote con
    // su conteo real alineado a la derecha (singular honesto por conteo).
    AmCard {
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            if (batch.uniqueRecognized.isNotEmpty()) {
                AmMetaRow(
                    label = AppStrings.Reconocidos,
                    value = batch.uniqueRecognized.size.toString(),
                    icon = AmIcons.CircleCheck,
                )
            }
            if (batch.duplicates.isNotEmpty()) {
                AmMetaRow(
                    label = AppStrings.PosiblesDuplicados,
                    value = batch.duplicates.size.toString(),
                    icon = AmIcons.Warning,
                )
            }
            if (batch.unknown.isNotEmpty()) {
                AmMetaRow(
                    label = AppStrings.PendientesDeRevision,
                    value = batch.unknown.size.toString(),
                    icon = AmIcons.Info,
                )
            }
        }
    }

    // Contrato S12: "Revisar N pendientes" como acción secundaria a ancho
    // completo; "Guardar reconocidos" vive fijo abajo (botón principal del
    // scaffold).
    if (batch.pendingItems().isNotEmpty()) {
        AmSecondaryButton(
            reviewPendingLabel(batch),
            onClick = { onAction(ImportBatchAction.ReviewPending) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        )
    }
}

/**
 * Revisión de pendientes: cada elemento como unidad visual reconocible
 * con acciones diferenciadas (principal vs secundarias).
 */
@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    AmSectionHeader(text = AppStrings.ElementosQueNecesitanRevision + " (" + batch.pendingItems().size + ")")
    // Pendientes como tarjetas independientes (nivel 3 de superficies):
    // cada una con su contenido + acciones; separadas a 8 dp.
    Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
        batch.pendingItems().forEachIndexed { index, candidate ->
            PendingItemCard(candidate, batch, onAction, isSaving, index)
        }
    }
}

/**
 * Tarjeta de pendiente con jerarquía de acciones:
 * - Principal: "Reintentar clasificación" (para desconocidos) o "Ver existente" (para duplicados)
 * - Secundaria: "Guardar de todos modos" (solo duplicados)
 * - Terciaria: "Descartar"
 */
@Composable
private fun PendingItemCard(
    candidate: ImportCandidate,
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
    index: Int = 0,
) {
    val isDuplicate = batch.duplicates.any { it.id == candidate.id }
    // El descarte de un elemento SIN clasificar pierde su contenido
    // retenido (no recuperable dentro del flujo: habría que re-importar),
    // así que pide confirmación; el de un duplicado no pierde nada (el
    // original existe) y se ejecuta directo.
    var showDiscardConfirm by remember(candidate.id) { mutableStateOf(false) }
    com.agendaqr.core.ui.components.AmCard {
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            // Cabecera: icono de estado + nombre útil + estado.
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                if (isDuplicate) {
                    AmIcon(
                        imageVector = AmIcons.Warning,
                        contentDescription = null,
                        tint = com.agendaqr.core.ui.theme.AmColor.Warning,
                    )
                } else {
                    AmIcon(
                        imageVector = AmIcons.Info,
                        contentDescription = null,
                        tint = com.agendaqr.core.ui.theme.AmColor.Info,
                    )
                }
                Column {
                    AmText(text = pendingItemName(candidate, index), fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                    AmText(
                        text = if (candidate.displayName?.isNotBlank() == true) {
                            (candidate.extension?.let { AppStrings.ArchivoPrefijo + it + " · " } ?: "") + when (candidate.kind) {
                                ImportKind.QR -> "QR"
                                ImportKind.COMPROBANTE -> "comprobante"
                                ImportKind.DESCONOCIDO -> "sin clasificar"
                            }
                        } else {
                            when {
                                isDuplicate -> AppStrings.elementoYaGuardado
                                candidate.kind == ImportKind.DESCONOCIDO -> AppStrings.elementoSinClasificar
                                else -> AppStrings.elementoPendienteRevision
                            }
                        },
                        size = AmType.BodySmall,
                        color = AmColor.TextSecondary,
                    )
                }
            }
            // Acción principal de ancho completo (tonal).
            when {
                isDuplicate -> AmSecondaryButton(
                    label = AppStrings.VerExistente,
                    onClick = { onAction(ImportBatchAction.ViewExisting(candidate.id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                else -> AmSecondaryButton(
                    label = AppStrings.ReintentarClasificacion,
                    onClick = { onAction(ImportBatchAction.RetryCandidate(candidate.id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Acciones secundarias alineadas al final, mismo orden siempre:
            // [Guardar de todos modos] [Descartar], Descartar a la derecha.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                if (isDuplicate) {
                    AmTextAction(
                        label = AppStrings.GuardarDeTodosModos,
                        onClick = { onAction(ImportBatchAction.SaveDuplicateAnyway(candidate.id)) },
                        variant = AmTextActionVariant.Neutral,
                        contentPadding = PaddingValues(horizontal = AmSpacing.None, vertical = AmSpacing.None),
                    )
                }
                AmTextAction(
                    label = AppStrings.Descartar,
                    onClick = {
                        if (isDuplicate) {
                            onAction(ImportBatchAction.DiscardCandidate(candidate.id))
                        } else {
                            showDiscardConfirm = true
                        }
                    },
                    variant = AmTextActionVariant.Danger,
                    contentPadding = PaddingValues(horizontal = AmSpacing.None, vertical = AmSpacing.None),
                )
            }
        }
    }
    if (showDiscardConfirm) {
        AmDialog(
            title = AppStrings.Descartar,
            message = AppStrings.descartarElementoWarning,
            // Etiqueta específica (no "Descartar" genérico): nombra la
            // consecuencia exacta y desambigua frente a la acción de fila.
            confirmLabel = AppStrings.descartarElementoConfirmar,
            destructive = true,
            onConfirm = {
                showDiscardConfirm = false
                onAction(ImportBatchAction.DiscardCandidate(candidate.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDiscardConfirm = false },
        )
    }
}
