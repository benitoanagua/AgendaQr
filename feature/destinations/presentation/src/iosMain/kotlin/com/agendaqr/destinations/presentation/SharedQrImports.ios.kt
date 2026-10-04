package com.agendaqr.destinations.presentation

import kotlinx.coroutines.flow.Flow

actual fun observeSharedQrImports(): Flow<QrImportResult> = noSharedQrImports

actual fun observeImportErrors(): Flow<String> = noImportErrors
