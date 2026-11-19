package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.UIKitView
import com.agendaqr.core.ui.components.AqMediaFrame
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.PDFKit.kPDFDisplayDirectionVertical
import platform.PDFKit.kPDFDisplaySinglePageContinuous
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    val isPdf = mimeType?.contains("pdf", ignoreCase = true) == true
    val media = remember(bytes.contentHashCode(), isPdf) {
        if (bytes.isEmpty()) null
        else runCatching {
            val data = bytes.usePinned { pinned ->
                NSData.create(pinned.addressOf(0), bytes.size.toULong())
            }
            if (isPdf) {
                PDFDocument(data)?.takeIf { it.pageCount > 0u }
            } else {
                UIImage(data)
            }
        }.getOrNull()
    }
    AqMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(
            min = AqMetrics.QrPreviewSize,
            max = AqMetrics.DialogListMaxHeight,
        ),
    ) {
        when {
            isPdf && media is PDFDocument -> UIKitView(
                factory = {
                    PDFView().apply {
                        autoScales = true
                        displayMode = kPDFDisplaySinglePageContinuous
                        displayDirection = kPDFDisplayDirectionVertical
                        this.document = media
                    }
                },
                modifier = Modifier.fillMaxSize().semantics {
                    contentDescription = AppStrings.Comprobante
                },
                update = { view -> if (view.document !== media) view.document = media },
            )
            !isPdf && media is UIImage -> UIKitView(
                factory = {
                    UIImageView().apply {
                        image = media
                        contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                    }
                },
                modifier = Modifier.fillMaxSize().semantics {
                    contentDescription = AppStrings.Comprobante
                },
                update = { view -> if (view.image !== media) view.image = media },
            )
            isPdf -> PdfFallback()
            else -> {
                Box(Modifier.fillMaxSize()) {
                    AqText(AppStrings.VistaPreviaNoDisponible, size = AqType.Label, color = AqColor.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun PdfFallback() {
    Box(Modifier.fillMaxSize()) {
        AqText(
            AppStrings.PdfAdjuntoSinVistaPrevia,
            size = AqType.Label,
            color = AqColor.TextSecondary,
        )
    }
}
