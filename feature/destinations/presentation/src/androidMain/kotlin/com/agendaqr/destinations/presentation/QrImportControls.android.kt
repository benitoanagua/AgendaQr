package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
actual fun QrImportControls(onResult: (QrImportResult) -> Unit) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        XauxaSecondaryButton("Galería", AgendaQrAndroidImportLauncher::gallery)
        XauxaSecondaryButton("Galería (varios)", AgendaQrAndroidImportLauncher::multiple)
        XauxaSecondaryButton("Cámara", AgendaQrAndroidImportLauncher::camera)
    }
}
