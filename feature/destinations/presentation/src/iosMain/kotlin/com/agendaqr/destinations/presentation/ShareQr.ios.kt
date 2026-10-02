package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.QrAsset
import kotlin.io.encoding.Base64
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
actual fun shareQr(asset: QrAsset) {
    val bytes = runCatching { Base64.decode(asset.encoded) }.getOrNull() ?: return
    if (bytes.isEmpty()) return
    val data = bytes.usePinned { pinned ->
        NSData.create(pinned.addressOf(0), bytes.size.toULong())
    } ?: return
    val path = "${NSTemporaryDirectory()}agendaqr-share-${NSUUID().UUIDString}.png"
    if (!NSFileManager.defaultManager.createFileAtPath(path, data, null)) return
    val temp = NSURL.fileURLWithPath(path)
    val controller = UIActivityViewController(activityItems = listOf(temp), applicationActivities = null)
    if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        // UIPopoverPresentationController is not exposed to Kotlin/Native,
        // so no popover anchor can be configured. Presenting as a sheet
        // avoids the missing-anchor crash on iPad.
        controller.modalPresentationStyle = UIModalPresentationFormSheet
    }
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
    root.presentViewController(controller, animated = true, completion = null)
}
