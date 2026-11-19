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

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    val isPdf = mimeType?.contains("pdf", ignoreCase = true) == true
    val document = remember(bytes.contentHashCode(), isPdf) {
        if (!isPdf || bytes.isEmpty()) null
        else runCatching {
            val data = bytes.usePinned { pinned ->
                NSData.create(pinned.addressOf(0), bytes.size.toULong())
            }
            PDFDocument(data)?.takeIf { it.pageCount > 0u }
        }.getOrNull()
    }
    AqMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(
            min = AqMetrics.QrPreviewSize,
            max = AqMetrics.DialogListMaxHeight,
        ),
    ) {
        when {
            document != null -> UIKitView(
                factory = {
                    PDFView().apply {
                        autoScales = true
                        displayMode = kPDFDisplaySinglePageContinuous
                        displayDirection = kPDFDisplayDirectionVertical
                        this.document = document
                    }
                },
                modifier = Modifier.fillMaxSize().semantics {
                    contentDescription = AppStrings.Comprobante
                },
                update = { view -> if (view.document !== document) view.document = document },
            )
            isPdf -> PdfFallback()
            else -> {
                // Image decoding on iOS remains a separate target-specific
                // limitation; the document/PDF path is backed by PDFKit.
                Box(Modifier.fillMaxSize()) {
                    AqText(
                        AppStrings.ComprobanteAdjuntoVistaPreviaPendienteEnIos,
                        size = AqType.Label,
                        color = AqColor.TextSecondary,
                    )
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
