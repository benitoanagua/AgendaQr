package com.agendaqr.destinations.presentation

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UniformTypeIdentifiers.UTTypePDF
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

/**
 * Selector de PDF de iOS (pendiente registrado en docs/STATE.md, ahora
 * implementado): `UIDocumentPickerViewController` presentado sobre la
 * escena activa, con copia del archivo al sandbox (`asCopy = true`, sin
 * security-scoped bookmarks) y entrega por el MISMO canal de comprobantes
 * entrantes que Android (`IncomingComprobanteFeed` → diálogo "Comprobante
 * recibido" de S07).
 *
 * Verificación: compila contra los bindings reales; el runtime del picker
 * requiere simulador/dispositivo con Xcode (docs/OPERATIONS.md, checklist
 * iOS — NO VERIFICADO aquí).
 */
@OptIn(ExperimentalForeignApi::class)
internal object IosPdfPicker {

    /**
     * El delegate de UIKit es weak: se retiene fuerte hasta el cierre para
     * que el colector no lo retire con el diálogo abierto.
     */
    private var retainedDelegate: Delegate? = null

    fun present() {
        val root = currentRootViewController() ?: return
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypePDF),
            asCopy = true,
        )
        val delegate = Delegate()
        retainedDelegate = delegate
        picker.delegate = delegate
        root.presentViewController(picker, animated = true, completion = null)
    }

    private fun finish(controller: UIDocumentPickerViewController) {
        retainedDelegate = null
        controller.dismissViewControllerAnimated(true, completion = null)
    }

    internal class Delegate : NSObject(), UIDocumentPickerDelegateProtocol {

        override fun documentPicker(
            controller: UIDocumentPickerViewController,
            didPickDocumentsAtURLs: List<*>,
        ) {
            finish(controller)
            // Un solo PDF por selección (contrato de la acción "Elegir
            // archivo (PDF)": el lote múltiple va por la galería).
            val url = didPickDocumentsAtURLs.filterIsInstance<NSURL>().firstOrNull() ?: return
            val data = NSData.dataWithContentsOfURL(url) ?: return
            if (data.length.toInt() == 0) return
            IncomingComprobanteFeed.offer(data, "application/pdf", "pdf")
        }

        override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            // Cancelar no pierde nada: el flujo padre queda intacto.
            finish(controller)
        }
    }
}
