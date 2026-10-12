package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
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
import com.agendaqr.core.ui.components.XauxaSnackbarHost
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
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
    XauxaFeedbackEvent(event = savedEvent, message = AppStrings.GuardadoOk)
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
    XauxaScreenScaffold(
        scrollable = true,
        topBar = {
            XauxaTopBar(
                title = AppStrings.ResultadoDeImportacion,
                onBack = if (state is ImportBatchUiState.Saving) null else ({ onAction(ImportBatchAction.Back) }),
                backLabel = if (state is ImportBatchUiState.Review) AppStrings.VolverAResultado else AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { XauxaSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (currentBatch?.canSaveRecognized() == true) {
            {
                XauxaPrimaryButton(
                    label = AppStrings.GuardarReconocidos,
                    onClick = { onAction(ImportBatchAction.SaveRecognized) },
                    enabled = state !is ImportBatchUiState.Saving,
                    isLoading = state is ImportBatchUiState.Saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = XauxaSpacing.ScreenMargin, vertical = XauxaSpacing.Sm),
                )
            }
        } else {
            null
        },
    ) {
        when (state) {
            ImportBatchUiState.Idle -> {
                XauxaText(AppStrings.TraeVariosElementosAAgenda, color = XauxaColor.TextSecondary)
            }
            ImportBatchUiState.Importing -> XauxaLoading(message = AppStrings.Importando)
            ImportBatchUiState.Analyzing -> XauxaLoading(message = AppStrings.Analizando)
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Saving -> {
                XauxaLoading(message = AppStrings.savingRecognizedItems)
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                XauxaStatusBanner(AppStrings.batchSavedKeepReviewNotice, tone = XauxaTone.Success)
            }
            is ImportBatchUiState.Error -> {
                XauxaStatusBanner(
                    state.error.display(),
                    tone = XauxaTone.Danger,
                    actionLabel = state.error.action.label,
                    onAction = { onAction(ImportBatchAction.SaveRecognized) },
                )
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
            }
        }
    }

    existing?.let { preview ->
        XauxaDialog(
            title = AppStrings.ElementoExistente,
            confirmLabel = AppStrings.Cerrar,
            onConfirm = { onAction(ImportBatchAction.CloseExisting) },
            onDismiss = { onAction(ImportBatchAction.CloseExisting) },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    when {
                        preview.qrAsset != null -> XauxaQrPreview(preview.qrAsset.encoded)
                        preview.comprobanteBytes != null ->
                            ComprobantePreview(preview.comprobanteBytes, preview.comprobanteMimeType)
                        else -> XauxaText(AppStrings.NoPudimosMostrarElElemento, color = XauxaColor.TextSecondary)
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
    XauxaText(AppStrings.checkMark + AppStrings.Reconocidos + ": " + batch.uniqueRecognized.size, color = XauxaColor.TextPrimary)
    XauxaText(AppStrings.dupMark + AppStrings.PosiblesDuplicados + ": " + batch.duplicates.size, color = XauxaColor.TextSecondary)
    XauxaText(AppStrings.pendingMark + AppStrings.PendientesDeRevision + ": " + batch.unknown.size, color = XauxaColor.TextSecondary)

    // Contrato S12: "Revisar N pendientes" como acción secundaria; "Guardar
    // reconocidos" vive fijo abajo (botón principal del scaffold).
    if (batch.pendingItems().isNotEmpty()) {
        XauxaSecondaryButton(reviewPendingLabel(batch), onClick = { onAction(ImportBatchAction.ReviewPending) }, enabled = !isSaving)
    }
}

@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    XauxaText(AppStrings.ElementosQueNecesitanRevision, color = XauxaColor.TextPrimary)
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
    XauxaTile {
        Column(
            modifier = Modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            XauxaText(pendingItemName(candidate), fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            XauxaText(
                when {
                    isDuplicate -> "! Parece que este elemento ya está guardado."
                    candidate.kind == ImportKind.DESCONOCIDO -> "? No pudimos clasificar este elemento."
                    else -> "Elemento pendiente de revisión."
                },
                color = XauxaColor.TextSecondary,
                size = XauxaType.Label,
            )
            when {
                isDuplicate -> Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    XauxaTextAction(label = AppStrings.VerExistente, onClick = { onAction(ImportBatchAction.ViewExisting(candidate.id)) })
                    XauxaTextAction(label = AppStrings.GuardarDeTodosModos, onClick = { onAction(ImportBatchAction.SaveDuplicateAnyway(candidate.id)) })
                }
                else -> Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    XauxaTextAction(label = AppStrings.ReintentarClasificacion, onClick = { onAction(ImportBatchAction.RetryCandidate(candidate.id)) })
                }
            }
            // El descarte siempre está disponible: abandona la intención
            // con este elemento y libera su payload temporal.
            XauxaTextAction(
                label = AppStrings.Descartar,
                onClick = { onAction(ImportBatchAction.DiscardCandidate(candidate.id)) },
            )
        }
    }
}
