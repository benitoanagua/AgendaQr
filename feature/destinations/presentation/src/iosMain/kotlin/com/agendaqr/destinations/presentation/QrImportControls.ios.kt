package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
actual fun QrImportControls(
    onResult: (QrImportResult) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        QrGalleryControls(onResult = onResult)
        QrCameraEntryControl(onResult = onResult)
    }
}

/**
 * "Elegir de la galería" (§4 S02 V1.2): UNA acción con selección múltiple
 * (PHPicker, selectionLimit coherente con el lote). El enrutado por
 * cantidad vive en el controlador: 1 elemento → flujo de selección única
 * (S09); 2 o más → lote (S12).
 */
@Composable
actual fun QrGalleryControls(
    onResult: (QrImportResult) -> Unit,
) {
    XauxaPrimaryButton(
        label = AppStrings.ElegirDeLaGaleria,
        icon = com.agendaqr.core.ui.components.XauxaIcons.Gallery,
        onClick = { IosQrImportController.openMultiple(onResult = onResult) },
    )
}

@Composable
actual fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
) {
    // Misma política de permiso que Android — contexto
    // antes de pedir, y "Abrir ajustes" cuando está denegado (la URL de
    // ajustes de la app; runtime pendiente de Xcode — checklist).
    var permissionError by rememberSaveable { mutableStateOf<UserFacingError?>(null) }
    // feedback de lectura exitosa por EVENTO.
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
        // §4 S02: el texto de privacidad de la cámara es visible junto a
        // la acción de escanear.
        XauxaText(
            AppStrings.PermisoCamaraContexto,
            size = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
        XauxaSecondaryButton(
            label = AppStrings.Camara,
            icon = com.agendaqr.core.ui.components.XauxaIcons.Camera,
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

/**
 * iOS sin Share Extension: `observeSharedQrImports` nunca emite, así que el
 * bloque "Desde otra app" de S02 no se muestra hasta que la extensión
 * exista (pendiente registrado en el estado de implementación).
 */
actual fun sharedImportsSupported(): Boolean = false

/**
 * iOS: el selector de PDF (UIDocumentPickerViewController) queda pendiente
 * de la pasada nativa con Xcode (riesgo sin verificación en dispositivo);
 * la acción no se muestra hasta que exista.
 */
@Composable
actual fun QrPdfPickerControl() {
}
