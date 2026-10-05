package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.destinations.domain.ImportBatch

/**
 * U2 — adquisición por lote REAL en iOS: PHPicker múltiple → lote S12 con
 * clasificación conservadora (los elementos pasan a revisión honesta).
 */
@Composable
actual fun ImportBatchControls(onBatch: (ImportBatch) -> Unit) {
    // El lote elegido en PHPicker llega por el canal compartido del
    // controlador (misma superficie que Android: S12 con pendientes).
    androidx.compose.runtime.LaunchedEffect(Unit) {
        IosQrImportController.collectBatches { candidates ->
            onBatch(com.agendaqr.destinations.domain.ImportBatch(candidates))
        }
    }
    XauxaSecondaryButton(
        label = AppStrings.GaleriaVarios,
        onClick = { IosQrImportController.openForBatch() },
    )
}
