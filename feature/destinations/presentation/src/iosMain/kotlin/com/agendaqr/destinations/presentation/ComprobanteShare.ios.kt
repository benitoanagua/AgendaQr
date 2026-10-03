package com.agendaqr.destinations.presentation

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIModalPresentationFormSheet
import platform.UIKit.UIUserInterfaceIdiomPad

@OptIn(ExperimentalForeignApi::class)
actual fun shareComprobante(bytes: ByteArray, extension: String, mimeType: String?) {
    if (bytes.isEmpty()) return
    val data = bytes.usePinned { pinned ->
        NSData.create(pinned.addressOf(0), bytes.size.toULong())
    } ?: return
    val safeExtension = extension.trim().trimStart('.').takeIf { it.isNotBlank() } ?: "bin"
    val path = "${NSTemporaryDirectory()}agendaqr-comprobante-${NSUUID().UUIDString}.$safeExtension"
    if (!NSFileManager.defaultManager.createFileAtPath(path, data, null)) return
    val temp = NSURL.fileURLWithPath(path)
    val controller = UIActivityViewController(activityItems = listOf(temp), applicationActivities = null)
    if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        controller.modalPresentationStyle = UIModalPresentationFormSheet
    }
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
    root.presentViewController(controller, animated = true, completion = null)
}
