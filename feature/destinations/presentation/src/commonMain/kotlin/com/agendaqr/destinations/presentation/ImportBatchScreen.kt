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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
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
 * (T7 — "Ver existente"). No navega: el usuario decide sin perder la
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
) {
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
        XauxaHeading(text = AppStrings.ResultadoDeImportacion, size = XauxaType.Headline, fontWeight = FontWeight.Normal)
        when (state) {
            ImportBatchUiState.Idle -> {
                XauxaText(AppStrings.TraeVariosElementosAAgenda, color = XauxaColor.TextSecondary)
                XauxaSecondaryButton(AppStrings.Volver, onClick = { onAction(ImportBatchAction.Back) })
            }
            ImportBatchUiState.Importing -> XauxaLoading(message = AppStrings.Importando)
            ImportBatchUiState.Analyzing -> XauxaLoading(message = AppStrings.Analizando)
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Saving -> {
                XauxaLoading(message = AppStrings.GuardandoElementosReconocidos)
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                XauxaStatusBanner(AppStrings.GuardadoLosElementosPendientesConservan, tone = XauxaTone.Success)
                XauxaPrimaryButton(AppStrings.Volver, onClick = { onAction(ImportBatchAction.Back) })
            }
            is ImportBatchUiState.Error -> {
                XauxaStatusBanner(state.message, tone = XauxaTone.Danger)
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
                    ?: XauxaSecondaryButton(AppStrings.Volver, onClick = { onAction(ImportBatchAction.Back) })
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
    XauxaText("✓ " + AppStrings.Reconocidos + ": " + batch.uniqueRecognized.size, color = XauxaColor.TextPrimary)
    XauxaText("! " + AppStrings.PosiblesDuplicados + ": " + batch.duplicates.size, color = XauxaColor.TextSecondary)
    XauxaText("? " + AppStrings.PendientesDeRevision + ": " + batch.unknown.size, color = XauxaColor.TextSecondary)

    if (batch.canSaveRecognized()) {
        XauxaPrimaryButton(
            AppStrings.GuardarReconocidos,
            onClick = { onAction(ImportBatchAction.SaveRecognized) },
            enabled = !isSaving,
            isLoading = isSaving,
        )
    }
    if (batch.pendingItems().isNotEmpty()) {
        // Contrato S12: "Revisar N pendientes".
        XauxaSecondaryButton(reviewPendingLabel(batch), onClick = { onAction(ImportBatchAction.ReviewPending) }, enabled = !isSaving)
    }
    XauxaSecondaryButton(AppStrings.Volver, onClick = { onAction(ImportBatchAction.Back) }, enabled = !isSaving)
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
    if (batch.canSaveRecognized()) {
        // Los válidos nunca dependen de los pendientes (contrato S12).
        XauxaPrimaryButton(
            AppStrings.GuardarReconocidos,
            onClick = { onAction(ImportBatchAction.SaveRecognized) },
            enabled = !isSaving,
            isLoading = isSaving,
        )
    }
    XauxaSecondaryButton(AppStrings.VolverAResultado, onClick = { onAction(ImportBatchAction.Back) }, enabled = !isSaving)
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
