package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqToast
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.components.AqTone
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
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        // En el editor la acción primaria ES Guardar (botón fijo abajo):
        // las fuentes de imagen son alternativas y no compiten por el
        // peso visual (jerarquía de botones, §7).
        AqButton(
            label = AppStrings.ElegirDeLaGaleria,
            icon = com.agendaqr.core.ui.components.AqIcons.Gallery,
            onClick = { IosQrImportController.openMultiple(onResult = onResult) },
            variant = com.agendaqr.core.ui.components.AqButtonVariant.Outline,
        )
        QrCameraEntryControl(onResult = onResult, showDivider = false)
    }
}

/**
 * "Elegir de la galería": UNA acción con selección múltiple
 * (PHPicker, selectionLimit coherente con el lote). El enrutado por
 * cantidad vive en el controlador: 1 elemento → flujo de selección única
 * (S09); 2 o más → lote (S12).
 */
@Composable
actual fun QrGalleryControls(
    onResult: (QrImportResult) -> Unit,
) {
    AqMenuItem(
        title = AppStrings.ElegirDeLaGaleria,
        titleMaxLines = Int.MAX_VALUE,
        onClick = { IosQrImportController.openMultiple(onResult = onResult) },
        leading = { AqIcon(imageVector = AqIcons.Gallery, contentDescription = null) },
        // AqMenuItem no tiene showChevron: el chevron vive en el slot trailing.
        trailing = { AqIcon(imageVector = AqIcons.ChevronRight, contentDescription = null) },
        showDivider = true,
    )
}

@Composable
actual fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
    showDivider: Boolean,
) {
    // Misma política de permiso que Android — contexto
    // antes de pedir, y "Abrir ajustes" cuando está denegado (la URL de
    // ajustes de la app; runtime pendiente de Xcode — checklist).
    var permissionError by rememberSaveable { mutableStateOf<UserFacingError?>(null) }
    // feedback de lectura exitosa por EVENTO.
    var detectionToken by remember { mutableStateOf<Any?>(null) }
    // El toast se muestra cuando hay un evento de detección pendiente y se
    // descarta solo tras el duration del toast (auto-clear), sin bloquear
    // la composición (AqToast no expone token de evento; show se deriva del
    // token y LaunchedEffect lo limpia).
    LaunchedEffect(detectionToken) {
        if (detectionToken != null) {
            delay(AqMotion.ToastDurationMs.toLong())
            detectionToken = null
        }
    }
    AqToast(show = detectionToken != null, message = AppStrings.CodigoQrDetectado)
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        permissionError?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = {
                    NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                        ?.let { url -> UIApplication.sharedApplication.openURL(url) }
                },
                onClear = { permissionError = null },
            )
            // AqAlert no tiene action/dismiss: usa confirmLabel/onConfirm y
            // dismissLabel/onDismiss (contrato de bannerActions, como en
            // ContextScreen/DestinationsScreen/OperationEditorScreen).
            AqAlert(
                tone = AqTone.Danger,
                message = error.dataStatus,
                title = error.what,
                confirmLabel = bannerAction?.label,
                onConfirm = bannerAction?.onClick,
                dismissLabel = bannerDismiss?.label ?: AppStrings.Cerrar,
                onDismiss = bannerDismiss?.onClick ?: { },
            )
        }
        AqMenuItem(
            title = AppStrings.Camara,
            subtitle = AppStrings.PermisoCamaraContexto,
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
            leading = { AqIcon(imageVector = AqIcons.Camera, contentDescription = null) },
            trailing = { AqIcon(imageVector = AqIcons.ChevronRight, contentDescription = null) },
            showDivider = showDivider,
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
actual fun QrPdfPickerControl(showDivider: Boolean) {
    AqMenuItem(
        title = AppStrings.ElegirArchivoPdf,
        titleMaxLines = Int.MAX_VALUE,
        onClick = { IosPdfPicker.present() },
        leading = { AqIcon(imageVector = AqIcons.Attachment, contentDescription = null) },
        trailing = { AqIcon(imageVector = AqIcons.ChevronRight, contentDescription = null) },
        showDivider = showDivider,
    )
}
