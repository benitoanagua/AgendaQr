package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class QrImportResult(val assets: List<QrAsset>)

/**
 * Entradas de adquisición de QR (§4 S02 V1.4).
 */
@Composable
expect fun QrImportControls(
    onResult: (QrImportResult) -> Unit,
)

/**
 * "Elegir de la galería": UNA acción con selección múltiple (1 elemento →
 * revisión única S09; 2 o más → lote S12). No existe el par
 * "Galería / Galería (varios)".
 */
@Composable
expect fun QrGalleryControls(
    onResult: (QrImportResult) -> Unit,
)

/** Solo Cámara: captura propia con detección (S03). El divisor lo decide la
 * agrupación de la pantalla (S02 la agrupa con Galería y Archivo). */
@Composable
expect fun QrCameraEntryControl(
    onResult: (QrImportResult) -> Unit,
    showDivider: Boolean,
)

/**
 * "Elegir archivo (PDF)" (opcional, §4 S02 V1.4): solo donde la plataforma
 * lo soporta con bajo riesgo. Android: OpenDocument(application/pdf) por el
 * canal de comprobantes entrantes. iOS: pendiente (documentado en el
 * estado de implementación).
 */
@Composable
expect fun QrPdfPickerControl(showDivider: Boolean)

/**
 * ¿Soporta esta plataforma recibir compartidos desde otras apps?
 * Android: sí (SEND/SEND_MULTIPLE). iOS: pendiente de Share Extension;
 * el bloque "Desde otra app" no se muestra hasta que exista.
 */
expect fun sharedImportsSupported(): Boolean

/**
 * QR imports delivered from OUTSIDE the app (share intents). Unlike in-app
 * acquisition, a share can arrive before any collector exists — cold start,
 * or even before sign-in — so platforms that support it must retain the last
 * value until the signed-in app consumes it.
 */
expect fun observeSharedQrImports(): Flow<QrImportResult>

/**
 * Recoverable import failures (unreadable/revoked shared URIs). Retained the
 * same way as shared imports; consumed by the signed-in app.
 */
expect fun observeImportErrors(): Flow<String>

/** Platforms without share acquisition simply never emit. */
val noSharedQrImports: Flow<QrImportResult> = flowOf()
val noImportErrors: Flow<String> = flowOf()
