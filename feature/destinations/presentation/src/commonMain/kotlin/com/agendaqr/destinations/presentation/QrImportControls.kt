package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class QrImportResult(val assets: List<QrAsset>)

@Composable
expect fun QrImportControls(onResult: (QrImportResult) -> Unit)

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
