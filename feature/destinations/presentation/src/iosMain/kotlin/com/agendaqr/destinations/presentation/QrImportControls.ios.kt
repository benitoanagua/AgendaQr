package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.agendaqr.core.ui.components.AmFeedbackEvent
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmBannerAction
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
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
    Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
        // En el editor la acción primaria ES Guardar (botón fijo abajo):
        // las fuentes de imagen son alternativas y no compiten por el
        // peso visual (jerarquía de botones, §7).
        AmSecondaryButton(
            label = AppStrings.ElegirDeLaGaleria,
            icon = com.agendaqr.core.ui.components.AmIcons.Gallery,
            onClick = { IosQrImportController.openMultiple(onResult = onResult) },
        )
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
    AmPrimaryButton(
        label = AppStrings.ElegirDeLaGaleria,
        icon = com.agendaqr.core.ui.components.AmIcons.Gallery,
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
    AmFeedbackEvent(event = detectionToken, message = AppStrings.CodigoQrDetectado)
    Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
        permissionError?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = {
                    NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                        ?.let { url -> UIApplication.sharedApplication.openURL(url) }
                },
                onClear = { permissionError = null },
            )
            // La acción del error (CameraPermission → Retry) se reemplaza por
            // "Abrir ajustes": no es reintentar, es abrir el selector del SO.
            val action = AmBannerAction(AppStrings.AbrirAjustes, bannerAction?.onClick ?: { permissionError = null })
            AmStatusBanner(
                title = error.what,
                message = error.dataStatus,
                tone = AmTone.Danger,
                action = action,
                dismiss = bannerDismiss,
            )
        }
        // §4 S02: el texto de privacidad de la cámara es visible junto a
        // la acción de escanear.
        AmText(
            AppStrings.PermisoCamaraContexto,
            size = AmType.Caption,
            color = AmColor.TextSecondary,
        )
        AmSecondaryButton(
            label = AppStrings.Camara,
            icon = com.agendaqr.core.ui.components.AmIcons.Camera,
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
 * iOS: selector de PDF REAL con `UIDocumentPickerViewController` (misma
 * acción y canal que Android; el archivo llega al diálogo "Comprobante
 * recibido"). Runtime del picker pendiente de Xcode/simulador —
 * docs/STATE.md.
 */
@Composable
actual fun QrPdfPickerControl() {
    AmSecondaryButton(
        label = AppStrings.ElegirArchivoPdf,
        onClick = { IosPdfPicker.present() },
    )
}
