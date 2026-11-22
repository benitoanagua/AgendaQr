package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqMetrics

/**
 * S05 — Pantalla completa del código QR de un destino. Muestra el nombre del
 * destino, el glifo QR y una acción de regreso.
 */
@Composable
fun QrFullscreenPattern(
    encodedQr: String,
    destinationName: String,
    onBack: () -> Unit,
) {
    AqScreen(
        title = AppStrings.PantallaCompleta,
        onBack = onBack,
        backLabel = AppStrings.Volver,
    ) {
        com.agendaqr.core.ui.components.AqQrPreview(
            encodedQr = encodedQr,
            size = AqMetrics.QrReviewSize,
        )
        com.agendaqr.core.ui.components.AqText(
            text = destinationName.ifBlank { AppStrings.SinNombre },
        )
        com.agendaqr.core.ui.components.AqText(
            text = AppStrings.Qr,
        )
    }
}
