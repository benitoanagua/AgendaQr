package com.agendaqr.destinations.presentation

import android.Manifest
import com.agendaqr.destinations.presentation.AppStrings
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

/**
 * T8 — S03 Cámara con detección automática.
 *
 * Contrato (S03 congelado): "Intención: capturar un QR físico. La
 * detección debe ser automática cuando sea posible. No se muestran
 * métricas técnicas del scanner." Reemplaza `TakePicturePreview`
 * (cámara del sistema) por captura propia con CameraX y análisis ZXing
 * continuo. Al detectar un QR se emite una única vez y el flujo continúa
 * a S09 exactamente como Galería (misma canal de resultados).
 *
 * El lenguaje visual es el del scanner Xauxa (marco con borde de marca,
 * hint textual, sin métricas), sobre la vista previa real de la cámara.
 */
@Composable
internal fun CameraQrCaptureOverlay(
    onQr: (QrAsset) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanSession = remember { QrScanSession() }
    var detected by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { analysisExecutor.shutdown() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XauxaColor.Background),
        ) {
            if (!detected) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreviewWithAnalysis(
                        lifecycleOwner = lifecycleOwner,
                        analysisExecutor = analysisExecutor,
                        onFrame = { proxy ->
                            val luminance = planeY(proxy)
                            val content = QrFrameAnalyzer.decodeLuminance(
                                y = luminance,
                                width = proxy.width,
                                height = proxy.height,
                                rotationDegrees = proxy.imageInfo.rotationDegrees,
                            )
                            val emitted = scanSession.onFrame(content)
                            if (emitted != null) {
                                val asset = assetFromFrame(proxy, emitted)
                                detected = true
                                onQr(asset)
                            }
                            // El wrapper siempre cierra el frame.
                            proxy
                        },
                    )
                    // Lenguaje visual del scanner Xauxa sobre la vista
                    // previa: marco con borde de marca y hint textual; sin
                    // métricas técnicas del scanner (S03).
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(XauxaSpacing.Xxl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(XauxaMetrics.QrPreviewSize)
                                .border(
                                    androidx.compose.foundation.BorderStroke(
                                        XauxaMetrics.BorderStrong,
                                        XauxaColor.Brand,
                                    ),
                                ),
                        )
                        XauxaText(
                            AppStrings.EncuadreElCodigoQr,
                            size = XauxaType.Label,
                            color = XauxaColor.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = XauxaSpacing.Lg),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(XauxaSpacing.Lg),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    XauxaSecondaryButton(label = AppStrings.Volver, onClick = onDismiss)
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewWithAnalysis(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    analysisExecutor: java.util.concurrent.Executor,
    onFrame: (ImageProxy) -> ImageProxy?,
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }

    LaunchedEffect(onFrame) {
        val provider = suspendCancellableCoroutine<ProcessCameraProvider> { continuation ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener(
                {
                    runCatching { future.get() }
                        .onSuccess { continuation.resumeWith(Result.success(it)) }
                        .onFailure { continuation.resumeWith(Result.failure(it)) }
                },
                ContextCompat.getMainExecutor(context),
            )
        }
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(analysisExecutor) { proxy ->
            val result = onFrame(proxy)
            if (result != null) result.close()
        }
        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis,
            )
        } catch (_: Exception) {
            // Sin cámara disponible: el usuario vuelve y usa Galería
            // (la entrada principal del contrato).
        }
    }

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
    )
}

/**
 * Convierte el frame detectado en el asset que espera el flujo de
 * importación (S09): imagen PNG del QR capturado, igual que Galería.
 */
internal fun assetFromFrame(proxy: ImageProxy, content: String): QrAsset {
    // Preview en grises desde el Y ya sin stride: legible para S09 y sin
    // el ensamblado NV21 manual (que sufría el mismo stride en U/V).
    val bitmap = runCatching { proxy.toGrayscaleBitmap() }.getOrNull()
    return if (bitmap != null) {
        val rotated = rotateBitmap(bitmap, proxy.imageInfo.rotationDegrees)
        val bytes = ByteArrayOutputStream().also {
            rotated.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
        QrAsset(
            encoded = Base64.encodeToString(bytes, Base64.NO_WRAP),
            mimeType = "image/png",
            content = content,
        )
    } else {
        QrAsset(encoded = "", mimeType = "image/png", content = content)
    }
}

/** Bitmap en grises del plano Y (stride ya corregido por [planeY]). */
private fun ImageProxy.toGrayscaleBitmap(): Bitmap {
    val y = planeY(this)
    val argb = IntArray(width * height) { i ->
        val v = y[i].toInt() and 0xFF
        (0xFF shl 24) or (v shl 16) or (v shl 8) or v
    }
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(argb, 0, width, 0, 0, width, height)
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    if (degrees % 360 == 0) return bitmap
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * Entrada S03 con manejo de permiso contextual: concedido abre la
 * captura; denegado muestra el error recuperable de T5 con REINTENTAR
 * (volver a pedirlo) o volver a Galería (la entrada principal).
 */
@Composable
internal fun CameraQrEntry(onOpen: () -> Unit) {
    val context = LocalContext.current
    var permissionError by remember { mutableStateOf<UserFacingError?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            permissionError = null
            onOpen()
        } else {
            permissionError = userFacingError(
                SecurityException("camera permission denied"),
                ErrorFlow.CameraPermission,
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        permissionError?.let { error ->
            XauxaStatusBanner(
                error.display(),
                tone = com.agendaqr.core.ui.components.XauxaTone.Danger,
                actionLabel = error.action.label,
                onAction = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onDismiss = { permissionError = null },
            )
        }
        XauxaSecondaryButton(
            label = AppStrings.Camara,
            onClick = {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    onOpen()
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
        )
    }
}

/** Copia el plano de luminancia del frame (sin extensiones de CameraX). */
private fun planeY(proxy: ImageProxy): ByteArray {
    val buffer = proxy.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return bytes
}
