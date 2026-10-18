package com.agendaqr.destinations.presentation

import kotlinx.coroutines.flow.Flow

actual fun observeIncomingComprobantes(): Flow<IncomingComprobante> =
    AgendaQrAndroidImportLauncher.receiptResults
