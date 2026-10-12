package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusBanner
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
    // V1.2 (§4 S12): barra superior con título y atrás; "Guardar
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
                AmPrimaryButton(
                    label = AppStrings.GuardarReconocidos,
                    onClick = { onAction(ImportBatchAction.SaveRecognized) },
                    enabled = state !is ImportBatchUiState.Saving,
                    isLoading = state is ImportBatchUiState.Saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = AmSpacing.ScreenMargin, vertical = AmSpacing.Sm),
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
                AmLoading(message = AppStrings.savingRecognizedItems)
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                AmStatusBanner(AppStrings.batchSavedKeepReviewNotice, tone = AmTone.Success)
            }
            is ImportBatchUiState.Error -> {
                AmStatusBanner(
                    state.error.display(),
                    tone = AmTone.Danger,
                    actionLabel = state.error.action.label,
                    onAction = { onAction(ImportBatchAction.SaveRecognized) },
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

@Composable
private fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    AmText(AppStrings.checkMark + AppStrings.Reconocidos + ": " + batch.uniqueRecognized.size, color = AmColor.TextPrimary)
    AmText(AppStrings.dupMark + AppStrings.PosiblesDuplicados + ": " + batch.duplicates.size, color = AmColor.TextSecondary)
    AmText(AppStrings.pendingMark + AppStrings.PendientesDeRevision + ": " + batch.unknown.size, color = AmColor.TextSecondary)

    // Contrato S12: "Revisar N pendientes" como acción secundaria; "Guardar
    // reconocidos" vive fijo abajo (botón principal del scaffold).
    if (batch.pendingItems().isNotEmpty()) {
        AmSecondaryButton(reviewPendingLabel(batch), onClick = { onAction(ImportBatchAction.ReviewPending) }, enabled = !isSaving)
    }
}

@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    AmText(AppStrings.ElementosQueNecesitanRevision, color = AmColor.TextPrimary)
    batch.pendingItems().forEach { candidate ->
        PendingItemCard(candidate, batch, onAction, isSaving)
    }
}

@Composable
private fun PendingItemCard(
    candidate: ImportCandidate,
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    val isDuplicate = batch.duplicates.any { it.id == candidate.id }
    // §12 V1.2: pendiente como fila ListItem; las acciones de fila bajo el
    // contenido (etiquetas largas).
    AmListRow(
        title = pendingItemName(candidate),
        subtitle = when {
            isDuplicate -> AppStrings.elementoYaGuardado
            candidate.kind == ImportKind.DESCONOCIDO -> AppStrings.elementoSinClasificar
            else -> AppStrings.elementoPendienteRevision
        },
        meta = {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                when {
                    isDuplicate -> Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                        AmTextAction(label = AppStrings.VerExistente, onClick = { onAction(ImportBatchAction.ViewExisting(candidate.id)) })
                        AmTextAction(label = AppStrings.GuardarDeTodosModos, onClick = { onAction(ImportBatchAction.SaveDuplicateAnyway(candidate.id)) })
                    }
                    else -> Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                        AmTextAction(label = AppStrings.ReintentarClasificacion, onClick = { onAction(ImportBatchAction.RetryCandidate(candidate.id)) })
                    }
                }
                // El descarte siempre está disponible: abandona la intención
                // con este elemento y libera su payload temporal.
                AmTextAction(
                    label = AppStrings.Descartar,
                    onClick = { onAction(ImportBatchAction.DiscardCandidate(candidate.id)) },
                )
            }
        },
    )
}
