package com.agendaqr.destinations.presentation

import kotlinx.coroutines.flow.Flow

/**
 * U2 — iOS emite comprobantes entrantes reales: la galería propia con
 * clasificación conservadora entrega los elementos no-QR al diálogo
 * "Comprobante recibido" (en Android la vía es el share intent).
 */
actual fun observeIncomingComprobantes(): Flow<IncomingComprobante> =
    IncomingComprobanteFeed.flow
