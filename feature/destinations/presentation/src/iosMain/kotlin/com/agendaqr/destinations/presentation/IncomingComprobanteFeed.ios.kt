package com.agendaqr.destinations.presentation

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData

/**
 * Canal iOS de comprobantes entrantes (U2). Alimentado por la adquisición
 * propia cuando el elemento no es un QR clasificable.
 */
@OptIn(ExperimentalForeignApi::class)
internal object IncomingComprobanteFeed {
    private val pending = kotlinx.coroutines.flow.MutableSharedFlow<IncomingComprobante>(extraBufferCapacity = 8)
    val flow: kotlinx.coroutines.flow.Flow<IncomingComprobante> = pending

    fun offer(data: NSData, mimeType: String) {
        pending.tryEmit(
            IncomingComprobante(
                bytes = IosQrImportController.toByteArray(data),
                mimeType = mimeType,
                extension = when (mimeType) {
                    "image/jpeg" -> "jpg"
                    else -> "png"
                },
            ),
        )
    }
}
