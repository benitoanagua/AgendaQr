package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.destinations.domain.ImportBatch
import kotlinx.coroutines.flow.collect

/**
 * Botón que abre el selector de imágenes del lote (S12 Idle): selector
 * múltiple del sistema (Photo Picker en Android, ver ImportQr.android.kt).
 */
@Composable
actual fun ImportBatchPickerButton(
    label: String,
) {
    XauxaPrimaryButton(
        label = label,
        onClick = { AgendaQrAndroidImportLauncher.gallery() },
    )
}

@Composable
actual fun ImportBatchControls(onBatch: (ImportBatch) -> Unit) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.batchResults.collect(onBatch)
    }
}
