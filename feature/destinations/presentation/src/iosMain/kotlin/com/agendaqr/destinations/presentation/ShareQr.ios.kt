package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.QrAsset
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareQr(asset: QrAsset) {
    val data = NSData.create(base64EncodedString = asset.encoded, options = 0u) ?: return
    val temp = NSURL.fileURLWithPath(
        "${NSTemporaryDirectory()}agendaqr-share-${platform.Foundation.NSUUID().UUIDString}.png"
    )
    data.writeToURL(temp, atomically = true)
    val controller = UIActivityViewController(activityItems = listOf(temp), applicationActivities = null)
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
    controller.popoverPresentationController?.sourceView = root.view
    root.presentViewController(controller, animated = true, completion = null)
}
