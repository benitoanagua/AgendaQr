package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.destinations.domain.ImportBatch

/**
 * Botón que abre el selector de imágenes del lote (S12): PHPicker con
 * selectionLimit coherente con el lote.
 */
@Composable
actual fun ImportBatchPickerButton(
    label: String,
) {
    AmPrimaryButton(
        label = label,
        onClick = { IosQrImportController.openForBatch() },
    )
}

/**
 * U2 — adquisición por lote REAL en iOS: PHPicker múltiple → lote S12 con
 * clasificación conservadora (los elementos pasan a revisión honesta).
 */
@Composable
actual fun ImportBatchControls(onBatch: (ImportBatch) -> Unit) {
    // El lote elegido en PHPicker llega por el canal compartido del
    // controlador (misma superficie que Android: S12 con pendientes).
    LaunchedEffect(Unit) {
        IosQrImportController.collectBatches { candidates ->
            onBatch(ImportBatch(candidates))
        }
    }
}
