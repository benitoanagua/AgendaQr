package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.agendaqr.destinations.domain.ImportBatch
import kotlinx.coroutines.flow.collect

@Composable
actual fun ImportBatchControls(onBatch: (ImportBatch) -> Unit) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.batchResults.collect(onBatch)
    }
}
