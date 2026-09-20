package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaSpacing

@Composable
actual fun QrImportControls(onResult: (QrImportResult) -> Unit) {
    // iOS acquisition remains behind this boundary so Vision/Photos/UIKit can be
    // introduced without leaking platform APIs into the feature/domain layers.
    Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        XauxaSecondaryButton("Camera", onClick = { IosQrImportController.openCamera(onResult) })
        XauxaSecondaryButton("Gallery", onClick = { IosQrImportController.openGallery(onResult) })
        XauxaSecondaryButton("Multiple", onClick = { IosQrImportController.openMultiple(onResult) })
    }
}
