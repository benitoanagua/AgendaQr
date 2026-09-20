package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaSpacing

@Composable
actual fun QrImportControls(onResult: (QrImportResult) -> Unit) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        XauxaSecondaryButton("Camera", AgendaQrAndroidImportLauncher::camera)
        XauxaSecondaryButton("Gallery", AgendaQrAndroidImportLauncher::gallery)
        XauxaSecondaryButton("Multiple", AgendaQrAndroidImportLauncher::multiple)
    }
}
