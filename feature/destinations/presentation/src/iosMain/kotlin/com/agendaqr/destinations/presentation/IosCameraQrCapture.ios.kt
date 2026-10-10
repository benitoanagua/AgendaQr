package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.QrAsset
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIViewController
import platform.darwin.NSObject

/**
 * U2 — S03 en iOS: AVFoundation con detección de QR NATIVA del SO
 * (AVCaptureMetadataOutput), sin métricas técnicas (contrato S03).
 * Runtime BLOCKED (sin Xcode); compila contra los bindings reales.
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosQrCameraPresenter(
    private val onQr: (QrImportResult) -> Unit,
    private val onDismiss: () -> Unit,
    /** Permiso denegado o restringido. */
    private val onPermissionDenied: () -> Unit = {},
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

    private val session = AVCaptureSession()
    private var emitted = false

    fun present() {
        // La API de autorización de AVFoundation
        // (authorizationStatusForMediaType/requestAccessForMediaType) NO
        // está expuesta en los bindings precompilados de KMP usados aquí
        // (verificado por sonda de compilación; ver
        // 07-auditoria-ronda-2.md y el checklist de Xcode). Mientras
        // tanto, el fallo de creación del input (permiso denegado o sin
        // cámara) ya NO hace dismiss silencioso: se notifica para
        // mostrar el banner con "Abrir ajustes". El pedido explícito del
        // permiso y su verificación quedan en el checklist de Xcode.
        val devices = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack,
        ).devices
        val device = devices.firstOrNull() as? AVCaptureDevice ?: run {
            onPermissionDenied()
            return
        }
        val input = AVCaptureDeviceInput.deviceInputWithDevice(device, error = null) ?: run {
            onPermissionDenied()
            return
        }
        session.beginConfiguration()
        if (session.canAddInput(input)) {
            session.addInput(input)
        }
        val output = AVCaptureMetadataOutput()
        if (session.canAddOutput(output)) {
            session.addOutput(output)
            output.setMetadataObjectsDelegate(this, queue = null)
        }
        session.commitConfiguration()
        session.startRunning()

        val controller = IosCameraViewController(session) {
            session.stopRunning()
            onDismiss()
        }
        currentRootViewController()?.presentViewController(
            controller,
            animated = true,
            completion = null,
        )
    }

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection,
    ) {
        if (emitted) return
        val code = didOutputMetadataObjects.firstOrNull()
            as? AVMetadataMachineReadableCodeObject ?: return
        if (code.type != AVMetadataObjectTypeQRCode) return
        emitted = true
        val value = code.stringValue ?: return
        // el feedback de lectura exitosa vive en la
        // entrada componible (XauxaFeedbackEvent) — háptica/anuncio
        // multiplataforma sin depender de bindings de UIKit.
        // El QR detectado se entrega como asset por el mismo canal; el
        // contenido llega a S09 igual que en Android (el flujo revisa lo
        // que la app entendió).
        val asset = QrAsset(encoded = "", mimeType = "image/png", content = value)
        currentRootViewController()?.dismissViewControllerAnimated(true) {
            session.stopRunning()
            onQr(QrImportResult(listOf(asset)))
        }
    }
}

/** ViewController mínimo que hospeda el preview layer y cierra al tocar. */
@OptIn(ExperimentalForeignApi::class)
internal class IosCameraViewController(
    session: AVCaptureSession,
    private val onClose: () -> Unit,
) : UIViewController(nibName = null, bundle = null) {
    private val previewLayer = AVCaptureVideoPreviewLayer(session = session)

    override fun viewDidLoad() {
        super.viewDidLoad()
        view.layer.addSublayer(previewLayer)
        view.backgroundColor = UIColor.blackColor
        val close = platform.UIKit.UIButton.buttonWithType(platform.UIKit.UIButtonTypeRoundedRect)
        close.setTitle(AppStrings.Volver, forState = platform.UIKit.UIControlStateNormal)
        close.addTarget(
            target = this,
            action = platform.objc.sel_registerName("closeTapped"),
            forControlEvents = platform.UIKit.UIControlEventTouchUpInside,
        )
        view.addSubview(close)
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer.frame = view.bounds
    }

    @Suppress("unused")
    fun closeTapped() {
        dismissViewControllerAnimated(true, completion = null)
        onClose()
    }
}
