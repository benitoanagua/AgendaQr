package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaLoading
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
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text("Importación completa", fontSize = XauxaType.Headline, color = XauxaColor.TextPrimary)
        when (state) {
            ImportBatchUiState.Idle -> {
                Text("Trae varios elementos a Agenda QR.", color = XauxaColor.TextSecondary)
                XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
            ImportBatchUiState.Importing -> XauxaLoading()
            ImportBatchUiState.Analyzing -> XauxaLoading()
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction)
            is ImportBatchUiState.Saving -> XauxaLoading()
            is ImportBatchUiState.Saved -> {
                XauxaStatusBanner("Guardado. Los elementos pendientes conservan su revisión.", danger = false)
                XauxaPrimaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
            is ImportBatchUiState.Error -> {
                XauxaStatusBanner(state.message, danger = true)
                state.batch?.let { BatchResultContent(it, onAction) }
                    ?: XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
            }
        }
    }
}

@Composable
private fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
) {
    Text("QR reconocidos: ${batch.qr.size}", color = XauxaColor.TextPrimary)
    Text("Comprobantes reconocidos: ${batch.comprobantes.size}", color = XauxaColor.TextPrimary)
    Text("Duplicados: ${batch.duplicates.size}", color = XauxaColor.TextPrimary)
    Text("Elementos desconocidos para revisar: ${batch.unknown.size}", color = XauxaColor.TextPrimary)
    Text("${batch.uniqueRecognized.size} elementos listos para guardar", color = XauxaColor.TextSecondary)

    if (batch.canSaveRecognized()) {
        XauxaPrimaryButton("Guardar reconocidos", onClick = { onAction(ImportBatchAction.SaveRecognized) })
    }
    if (batch.pendingItems().isNotEmpty()) {
        XauxaSecondaryButton("Revisar pendientes", onClick = { onAction(ImportBatchAction.ReviewPending) })
    }
    XauxaSecondaryButton("Volver", onClick = { onAction(ImportBatchAction.Back) })
}

@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
) {
    Text("Elementos que necesitan revisión", color = XauxaColor.TextPrimary)
    batch.pendingItems().forEach { candidate ->
        Text(
            "${candidate.kind} · ${candidate.fingerprint.take(10)}${candidate.error?.let { " · $it" } ?: ""}",
            color = XauxaColor.TextSecondary,
        )
    }
    if (batch.canSaveRecognized()) {
        XauxaPrimaryButton("Guardar reconocidos", onClick = { onAction(ImportBatchAction.SaveRecognized) })
    }
    XauxaSecondaryButton("Volver a resultado", onClick = { onAction(ImportBatchAction.Back) })
}
