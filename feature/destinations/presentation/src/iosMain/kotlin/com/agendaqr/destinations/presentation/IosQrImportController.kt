package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.Foundation.base64EncodedStringWithOptions
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIWindowScene
import platform.UIKit.UISceneActivationStateForegroundActive
import kotlinx.coroutines.launch
import platform.darwin.NSObject

/**
 * U2 — adquisición REAL en iOS (adiós controles muertos).
 *
 * - Galería (única y múltiple) con PHPicker; misma superficie y canal que
 *   Android (`QrImportResult`, lote para S12).
 * - Clasificación deliberadamente conservadora (contrato: QR demostrado;
 *   sin ZXing en común, las imágenes pasan a revisión honesta como
 *   DESCONOCIDO; la cámara detecta QR con el SO).
 * - Runtime: BLOCKED sin Xcode; este código compila contra los bindings
 *   reales de Kotlin/Native y deja el checklist en docs/09-implementacion.
 */
@OptIn(ExperimentalForeignApi::class)
internal object IosQrImportController {

    /** Galería: selección única (misma superficie que "Galería" en Android). */
    fun openGallery(onResult: (QrImportResult) -> Unit) {
        presentPicker(selectionLimit = 1L) { providers -> emitSingle(providers, onResult) }
    }

    /** Galería (varios): alimenta S12 con un lote. */
    fun openMultiple(
        onResult: (QrImportResult) -> Unit,
        onBatch: ((List<ImportCandidate>) -> Unit)? = null,
    ) {
        presentPicker(selectionLimit = 0L) { providers ->
            if (providers.size == 1) {
                emitSingle(providers, onResult)
            } else {
                loadCandidates(providers) { candidates -> onBatch?.invoke(candidates) }
            }
        }
    }

    private fun presentPicker(selectionLimit: Long, handle: (List<NSItemProvider>) -> Unit) {
        val configuration = PHPickerConfiguration()
        configuration.filter = PHPickerFilter.imagesFilter()
        configuration.selectionLimit = selectionLimit
        configuration.selection = PHPickerConfigurationSelectionOrdered
        val picker = PHPickerViewController(configuration = configuration)
        picker.delegate = PickerDelegate(handle)
        rootViewController()?.presentViewController(picker, animated = true, completion = null)
    }

    internal class PickerDelegate(private val handle: (List<NSItemProvider>) -> Unit) :
        NSObject(), PHPickerViewControllerDelegateProtocol {

        override fun picker(
            picker: PHPickerViewController,
            didFinishPicking: List<*>,
        ) {
            picker.dismissViewControllerAnimated(true, completion = null)
            val providers = didFinishPicking.mapNotNull {
                (it as? PHPickerResult)?.itemProvider
            }
            if (providers.isNotEmpty()) handle(providers)
        }
    }

    private fun emitSingle(providers: List<NSItemProvider>, onResult: (QrImportResult) -> Unit) {
        val provider = providers.firstOrNull() ?: return
        loadData(provider) { data, mimeType ->
            if (data != null) {
                val asset = QrAsset(
                    encoded = data.base64EncodedStringWithOptions(0u),
                    mimeType = mimeType,
                )
                onResult(QrImportResult(listOf(asset)))
                // Los no-QR llegan también al diálogo de comprobante
                // recibido (en Android la vía es el share intent).
                IncomingComprobanteFeed.offer(data, mimeType)
            }
        }
    }

    internal fun loadCandidates(
        providers: List<NSItemProvider>,
        completion: (List<ImportCandidate>) -> Unit,
    ) {
        val collected = mutableListOf<ImportCandidate>()
        val expected = providers.size
        providers.forEachIndexed { index, provider ->
            loadData(provider) { data, mimeType ->
                // Los callbacks de PHPicker llegan serializados por la cola
                // principal; sin race de recolección.
                collected += ImportCandidate(
                    id = "import-ios-" + index,
                    kind = ImportKind.DESCONOCIDO,
                    fingerprint = "ios-" + index + "-" + (data?.length ?: 0L),
                    mimeType = mimeType,
                    extension = extensionFor(mimeType),
                    qrAsset = null,
                )
                if (collected.size == expected) completion(collected.toList())
            }
        }
    }

    private fun loadData(
        provider: NSItemProvider,
        completion: (NSData?, String) -> Unit,
    ) {
        val identifier = provider.registeredTypeIdentifiers.firstOrNull() as? String
            ?: "public.png"
        provider.loadDataRepresentationForTypeIdentifier(identifier) { data, _ ->
            completion(data, normalizeMime(identifier))
        }
    }

    private fun normalizeMime(uti: String): String = when {
        uti.contains("jpeg", ignoreCase = true) -> "image/jpeg"
        else -> "image/png"
    }

    private fun extensionFor(mime: String): String = when (mime) {
        "image/jpeg" -> "jpg"
        else -> "png"
    }

    /** NSData → ByteArray (bytes crudos, con pin temporal). */
    internal fun toByteArray(data: NSData): ByteArray {
        val size = data.length.toInt()
        val bytes = ByteArray(size)
        if (size > 0) {
            bytes.usePinned { pinned ->
                platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
            }
        }
        return bytes
    }

    internal fun rootViewController() = currentRootViewController()

    /** Publica un lote (S12) en el canal de ImportBatchControls. */
    fun offerBatch(candidates: List<ImportCandidate>) {
        multiBatchResults.tryEmit(candidates)
    }

    /** ImportBatchControls: abre el picker múltiple que alimenta S12. */
    fun openForBatch() {
        openMultiple(
            onResult = { },
            onBatch = { candidates -> offerBatch(candidates) },
        )
    }

    /** ImportBatchControls: observa los lotes elegidos. */
    fun collectBatches(onBatch: (List<ImportCandidate>) -> Unit) {
        // Scope dedicado del controlador: el canal es del singleton y la
        // colección vive mientras la superficie (usada como dev-tool de
        // composition) la mantenga viva LaunchedEffect.
        controllerScope.launch {
            multiBatchResults.collect { onBatch(it) }
        }
    }
}

/** Canal del lote múltiple para S12 (consumido por ImportBatchControls). */
internal val multiBatchResults =
    kotlinx.coroutines.flow.MutableSharedFlow<List<ImportCandidate>>(extraBufferCapacity = 4)

private val controllerScope =
    kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.Dispatchers.Main +
            kotlinx.coroutines.SupervisorJob(),
    )

/** Escena activa (keyWindow está deprecado desde iOS 13). */
internal fun currentRootViewController(): platform.UIKit.UIViewController? {
    val app = UIApplication.sharedApplication
    val scenes = app.connectedScenes.filterIsInstance<UIWindowScene>()
    val active = scenes.firstOrNull {
        it.activationState == UISceneActivationStateForegroundActive
    } ?: scenes.firstOrNull() ?: return null
    return active.keyWindow?.rootViewController
}
