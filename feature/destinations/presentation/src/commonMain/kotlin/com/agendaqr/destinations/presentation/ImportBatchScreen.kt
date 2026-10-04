package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.ImportBatch

@Composable
fun ImportBatchScreen(
    state: ImportBatchUiState,
    onAction: (ImportBatchAction) -> Unit,
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
        Text("Resultado de importación", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Headline, color = XauxaColor.TextPrimary)
        when (state) {
            ImportBatchUiState.Idle -> {
                Text("Trae varios elementos a Agenda QR.", color = XauxaColor.TextSecondary)
                XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
            ImportBatchUiState.Importing -> XauxaLoading(message = "Importando…")
            ImportBatchUiState.Analyzing -> XauxaLoading(message = "Analizando…")
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Saving -> {
                XauxaLoading(message = "Guardando elementos reconocidos…")
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                XauxaStatusBanner("Guardado. Los elementos pendientes conservan su revisión.", tone = XauxaTone.Success)
                XauxaPrimaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
            is ImportBatchUiState.Error -> {
                XauxaStatusBanner(state.message, tone = XauxaTone.Danger)
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
                    ?: XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
        }
    }
}

@Composable
private fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    Text("✓ Reconocidos: ${batch.uniqueRecognized.size}", color = XauxaColor.TextPrimary)
    Text("! Posibles duplicados: ${batch.duplicates.size}", color = XauxaColor.TextSecondary)
    Text("? Pendientes de revisión: ${batch.unknown.size}", color = XauxaColor.TextSecondary)

    if (batch.canSaveRecognized()) {
        XauxaPrimaryButton(
            "Guardar reconocidos",
            onClick = { onAction(ImportBatchAction.SaveRecognized) },
            enabled = !isSaving,
            isLoading = isSaving,
        )
    }
    if (batch.pendingItems().isNotEmpty()) {
        XauxaSecondaryButton("Revisar pendientes", onClick = { onAction(ImportBatchAction.ReviewPending) }, enabled = !isSaving)
    }
    XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) }, enabled = !isSaving)
}

@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    Text("Elementos que necesitan revisión", color = XauxaColor.TextPrimary)
    batch.pendingItems().forEach { candidate ->
        Text(
            when (candidate.kind.name) {
                "DUPLICATE" -> "! Parece que este elemento ya está guardado."
                "UNKNOWN" -> "? No pudimos clasificar este elemento."
                "QR" -> "QR pendiente de revisión."
                "COMPROBANTE" -> "Comprobante pendiente de revisión."
                else -> "Elemento pendiente de revisión."
            },
            color = XauxaColor.TextSecondary,
        )
    }
    if (batch.canSaveRecognized()) {
        XauxaPrimaryButton(
            "Guardar reconocidos",
            onClick = { onAction(ImportBatchAction.SaveRecognized) },
            enabled = !isSaving,
            isLoading = isSaving,
        )
    }
    XauxaSecondaryButton("Volver a resultado", onClick = { onAction(ImportBatchAction.Back) }, enabled = !isSaving)
}
