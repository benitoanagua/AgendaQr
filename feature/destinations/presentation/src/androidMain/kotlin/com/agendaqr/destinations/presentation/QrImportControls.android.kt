package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.destinations.domain.QrAsset

/**
 * S03: la Cámara ya no es la cámara del sistema
 * (`TakePicturePreview`): es captura propia con CameraX + análisis
 * continuo ([CameraQrCaptureOverlay]) con detección automática. Galería
 * sigue siendo la entrada principal (contrato S02).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
actual fun QrImportControls(
    onResult: (QrImportResult) -> Unit,
    galleryAsPrimary: Boolean,
) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        if (galleryAsPrimary) {
            com.agendaqr.core.ui.components.XauxaPrimaryButton(AppStrings.Galeria, AgendaQrAndroidImportLauncher::gallery)
        } else {
            XauxaSecondaryButton(AppStrings.Galeria, AgendaQrAndroidImportLauncher::gallery)
        }
        XauxaSecondaryButton(AppStrings.GaleriaVarios, AgendaQrAndroidImportLauncher::multiple)
        CameraCaptureHost(onQr = { asset -> onResult(QrImportResult(listOf(asset))) })
    }
}

@Composable
actual fun QrGalleryControls(
    onResult: (QrImportResult) -> Unit,
    galleryAsPrimary: Boolean,
) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        if (galleryAsPrimary) {
            com.agendaqr.core.ui.components.XauxaPrimaryButton(AppStrings.Galeria, AgendaQrAndroidImportLauncher::gallery)
        } else {
            XauxaSecondaryButton(AppStrings.Galeria, AgendaQrAndroidImportLauncher::gallery)
        }
        XauxaSecondaryButton(AppStrings.GaleriaVarios, AgendaQrAndroidImportLauncher::multiple)
    }
}

@Composable
actual fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
) {
    CameraCaptureHost(onQr = { asset -> onResult(QrImportResult(listOf(asset))) })
}

/**
 * Entrada de cámara con su overlay (estado propio): compartida entre el
 * editor (todo junto) y S02 (cámara separada de la galería por el orden
 * congelado).
 */
@Composable
internal fun CameraCaptureHost(onQr: (QrAsset) -> Unit) {
    var cameraOpen by remember { mutableStateOf(false) }
    CameraQrEntry(onOpen = { cameraOpen = true })
    if (cameraOpen) {
        CameraQrCaptureOverlay(
            onQr = { asset ->
                cameraOpen = false
                onQr(asset)
            },
            onDismiss = { cameraOpen = false },
        )
    }
}
