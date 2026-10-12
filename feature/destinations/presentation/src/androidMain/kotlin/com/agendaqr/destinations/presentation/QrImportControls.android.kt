package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.destinations.domain.QrAsset

/**
 * S03: la Cámara ya no es la cámara del sistema
 * (`TakePicturePreview`): es captura propia con CameraX + análisis
 * continuo ([CameraQrCaptureOverlay]) con detección automática. Galería
 * sigue siendo la entrada principal (contrato S02).
 */
@Composable
actual fun QrImportControls(
    onResult: (QrImportResult) -> Unit,
) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    Column(verticalArrangement = Arrangement.spacedBy(com.agendaqr.core.ui.theme.AmSpacing.Sm)) {
        AmPrimaryButton(
            label = AppStrings.ElegirDeLaGaleria,
            onClick = AgendaQrAndroidImportLauncher::gallery,
            icon = com.agendaqr.core.ui.components.AmIcons.Gallery,
        )
        CameraCaptureHost(onQr = { asset -> onResult(QrImportResult(listOf(asset))) })
    }
}

/**
 * "Elegir de la galería" (§4 S02 V1.2): UNA acción de selección múltiple.
 * El enrutado por cantidad vive en el canal: 1 elemento → flujo de
 * selección única (S09); 2 o más → lote (S12).
 */
@Composable
actual fun QrGalleryControls(
    onResult: (QrImportResult) -> Unit,
) {
    LaunchedEffect(Unit) {
        AgendaQrAndroidImportLauncher.results.collect(onResult)
    }
    AmPrimaryButton(
        label = AppStrings.ElegirDeLaGaleria,
        onClick = AgendaQrAndroidImportLauncher::gallery,
        icon = AmIcons.Gallery,
    )
}

@Composable
actual fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
) {
    CameraCaptureHost(onQr = { asset -> onResult(QrImportResult(listOf(asset))) })
}

/** Android: PDF vía OpenDocument por el canal de comprobantes entrantes. */
@Composable
actual fun QrPdfPickerControl() {
    AmSecondaryButton(
        label = AppStrings.ElegirArchivoPdf,
        onClick = AgendaQrAndroidImportLauncher::pdf,
    )
}

/** Android recibe SEND/SEND_MULTIPLE de otras apps: el bloque S02 se muestra. */
actual fun sharedImportsSupported(): Boolean = true

/**
 * Entrada de cámara con su overlay (estado propio): compartida entre el
 * editor (todo junto) y S02 (cámara separada de la galería por el orden
 * congelado).
 */
@Composable
internal fun CameraCaptureHost(onQr: (QrAsset) -> Unit) {
    var cameraOpen by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }
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
