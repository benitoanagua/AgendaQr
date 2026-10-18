package com.agendaqr.destinations.data

/**
 * PNG QR legible en Base64 para el [content] dado.
 *
 * Android lo genera en tiempo de ejecución con el codificador del proyecto
 * (ZXing, el mismo de importación/cámara): cada destino demo tiene su QR
 * propio y escaneable. iOS devuelve una imagen QR válida embebida
 * (generada con `qrencode` para `https://agendaqr.app/demo`); la
 * generación por contenido con CoreImage queda pendiente de verificación
 * con Xcode (ver actual iOS).
 */
expect fun demoQrPngBase64(content: String): String
