package com.agendaqr.destinations.presentation

import kotlinx.coroutines.flow.Flow

actual fun observeSharedQrImports(): Flow<QrImportResult> =
    AgendaQrAndroidImportLauncher.sharedQrResults

actual fun observeImportErrors(): Flow<String> =
    AgendaQrAndroidImportLauncher.importErrors
