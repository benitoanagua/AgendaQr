package com.agendaqr.destinations.presentation

/**
 * Comparte el archivo de un comprobante ya cargado en memoria
 * mediante el mecanismo nativo de cada plataforma. No-op si
 * [bytes] está vacío.
 */
expect fun shareComprobante(bytes: ByteArray, extension: String, mimeType: String?)
