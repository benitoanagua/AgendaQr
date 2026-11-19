package com.agendaqr.destinations.presentation

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.agendaqr.core.ui.components.AqMediaFrame
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

private class PdfDocumentHandle(
    private val file: File,
    private val descriptor: ParcelFileDescriptor,
    private val renderer: PdfRenderer,
) {
    val pageCount: Int get() = renderer.pageCount
    val mutex = Mutex()

    suspend fun renderPage(index: Int, targetWidth: Int): Bitmap? = mutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                renderer.openPage(index).use { page ->
                    val width = targetWidth.coerceAtLeast(1)
                    val height = (width.toDouble() * page.height / page.width).toInt().coerceAtLeast(1)
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                null
            }
        }
    }

    fun close() {
        runCatching { renderer.close() }
        runCatching { descriptor.close() }
        runCatching { file.delete() }
    }
}

private suspend fun openPdf(bytes: ByteArray, context: android.content.Context): PdfDocumentHandle? {
    if (bytes.size < 5 || bytes.copyOfRange(0, 5).decodeToString() != "%PDF-") return null
    return try {
        withContext(Dispatchers.IO) {
        val file = File.createTempFile("agendaqr-receipt-", ".pdf", context.cacheDir)
        var descriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            file.writeBytes(bytes)
            val openedDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            descriptor = openedDescriptor
            val openedRenderer = PdfRenderer(openedDescriptor)
            renderer = openedRenderer
            require(openedRenderer.pageCount > 0) { "El PDF no contiene páginas" }
            PdfDocumentHandle(file, openedDescriptor, openedRenderer)
        } catch (error: Throwable) {
            runCatching { renderer?.close() }
            runCatching { descriptor?.close() }
            file.delete()
            throw error
        }
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        null
    }
}

@Composable
actual fun ComprobantePreview(bytes: ByteArray, mimeType: String?, modifier: Modifier) {
    if (mimeType?.contains("pdf", ignoreCase = true) == true) {
        PdfComprobantePreview(bytes, modifier)
        return
    }
    val bitmap = remember(bytes) {
        runCatching { android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap() }.getOrNull()
    }
    AqMediaFrame(modifier = modifier.fillMaxWidth().heightIn(min = AqMetrics.QrPreviewSize)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = AppStrings.Comprobante,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        } else {
            Box(Modifier.fillMaxSize()) {
                AqText(AppStrings.VistaPreviaNoDisponible, size = AqType.Label, color = AqColor.TextSecondary)
            }
        }
    }
}

@Composable
private fun PdfComprobantePreview(bytes: ByteArray, modifier: Modifier) {
    val context = LocalContext.current
    var document by remember { mutableStateOf<PdfDocumentHandle?>(null) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(bytes.contentHashCode()) {
        document = null
        failed = false
        document = openPdf(bytes, context)
        failed = document == null
    }
    DisposableEffect(document) {
        onDispose { document?.close() }
    }
    val handle = document
    AqMediaFrame(
        modifier = modifier.fillMaxWidth().heightIn(
            min = AqMetrics.QrPreviewSize,
            max = AqMetrics.DialogListMaxHeight,
        ),
    ) {
        when {
            handle != null -> {
                val density = LocalDensity.current
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val targetWidth = with(density) { maxWidth.roundToPx() }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items((0 until handle.pageCount).toList()) { index ->
                            PdfPage(handle, index, targetWidth)
                        }
                    }
                }
            }
            failed -> AqText(AppStrings.PdfAdjuntoSinVistaPrevia, size = AqType.Label, color = AqColor.TextSecondary)
            else -> AqText(AppStrings.AbriendoComprobante, size = AqType.Label, color = AqColor.TextSecondary)
        }
    }
}

@Composable
private fun PdfPage(document: PdfDocumentHandle, page: Int, targetWidthPx: Int) {
    var bitmap by remember(document, page) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(document, page, targetWidthPx) { bitmap = document.renderPage(page, targetWidthPx) }
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "${AppStrings.Pagina} ${page + 1}",
            modifier = Modifier.fillMaxWidth().aspectRatio(it.width.toFloat() / it.height),
            contentScale = ContentScale.Fit,
        )
    }
}
