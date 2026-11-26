package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.ImageBitmap

/**
 * wasmJs: no hay sistema de archivos ni miniaturas de comprobantes en el
 * navegador; el contrato (`null` ⇒ estado Failure con fallback semántico)
 * se conserva devolviendo siempre `null`.
 */
actual suspend fun decodeThumbnailBitmap(file: String): ImageBitmap? = null
