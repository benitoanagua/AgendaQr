package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/**
 * U2 — iOS: Galería/Cámara alimentan el mismo canal que Android. La cámara
 * presenta la superficie de captura del SO con detección nativa de QR
 * (AVFoundation); runtime BLOCKED sin Xcode, pero el control ya no es un
 * botón muerto y compila contra los bindings reales.
 */
@Composable
actual fun QrGalleryControls(
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
        XauxaSecondaryButton(
            label = AppStrings.GaleriaVarios,
            onClick = { IosQrImportController.openMultiple(onResult) },
        )
    }
}

@Composable
actual fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
) {
    // Ronda 2 (Área B/K): misma política de permiso que Android — contexto
    // antes de pedir, y "Abrir ajustes" cuando está denegado (la URL de
    // ajustes de la app; runtime pendiente de Xcode — checklist).
    var permissionError by rememberSaveable { mutableStateOf<UserFacingError?>(null) }
    // Ronda 2 (Área B): feedback de lectura exitosa por EVENTO.
    var detectionToken by remember { mutableStateOf<Any?>(null) }
    XauxaFeedbackEvent(event = detectionToken, message = AppStrings.CodigoQrDetectado)
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        permissionError?.let { error ->
            XauxaStatusBanner(
                error.display(),
                tone = XauxaTone.Danger,
                actionLabel = AppStrings.AbrirAjustes,
                // UIApplicationOpenSettingsURLString: la constante C es
                // la que exponen los bindings (openSettingsURLString de
                // Swift no está en el cinterop precompilado).
                onAction = {
                    NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                        ?.let { url -> UIApplication.sharedApplication.openURL(url) }
                },
                onDismiss = { permissionError = null },
                dismissLabel = AppStrings.Descartar,
            )
        }
        XauxaText(
            AppStrings.PermisoCamaraContexto,
            size = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
        XauxaSecondaryButton(
            label = AppStrings.Camara,
            onClick = {
                IosQrCameraPresenter(
                    onQr = { result ->
                        detectionToken = Any()
                        onResult(result)
                    },
                    onDismiss = {},
                    onPermissionDenied = {
                        permissionError = userFacingError(
                            // SecurityException no existe fuera de JVM: se
                            // clasifica por flujo explícito.
                            IllegalArgumentException("camera permission denied"),
                            ErrorFlow.CameraPermission,
                        )
                    },
                ).present()
            },
        )
    }
}

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
            QrCameraEntryControl(onResult)
        }
    }
}
