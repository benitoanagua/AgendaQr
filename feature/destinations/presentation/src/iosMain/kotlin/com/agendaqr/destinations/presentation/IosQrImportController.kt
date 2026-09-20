package com.agendaqr.destinations.presentation

/**
 * iOS acquisition boundary. The shared feature does not depend on UIKit/Photos/Vision.
 * Xcode integration can provide the concrete controller without changing domain or UI contracts.
 */
internal object IosQrImportController {
    fun openCamera(onResult: (QrImportResult) -> Unit) = Unit
    fun openGallery(onResult: (QrImportResult) -> Unit) = Unit
    fun openMultiple(onResult: (QrImportResult) -> Unit) = Unit
}
