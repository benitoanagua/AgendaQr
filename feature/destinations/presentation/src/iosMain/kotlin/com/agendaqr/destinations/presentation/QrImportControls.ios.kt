package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * U2 — iOS: Galería/Cámara alimentan el mismo canal que Android. La cámara
 * presenta la superficie de captura del SO con detección nativa de QR
 * (AVFoundation); runtime BLOCKED sin Xcode, pero el control ya no es un
 * botón muerto y compila contra los bindings reales.
 */
@Composable
actual fun QrImportControls(
    onResult: (QrImportResult) -> Unit,
    galleryAsPrimary: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        if (galleryAsPrimary) {
            XauxaPrimaryButton(
                label = AppStrings.Galeria,
                onClick = { IosQrImportController.openGallery(onResult) },
            )
        } else {
            XauxaSecondaryButton(
                label = AppStrings.Galeria,
                onClick = { IosQrImportController.openGallery(onResult) },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaSecondaryButton(
                label = AppStrings.GaleriaVarios,
                onClick = { IosQrImportController.openMultiple(onResult) },
            )
            XauxaSecondaryButton(
                label = AppStrings.Camara,
                onClick = { IosQrCameraPresenter(onQr = onResult, onDismiss = {}).present() },
            )
        }
    }
}
