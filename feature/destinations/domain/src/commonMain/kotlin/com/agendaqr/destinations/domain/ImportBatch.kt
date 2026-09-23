package com.agendaqr.destinations.domain

import kotlinx.serialization.Serializable

/**
 * Resultado común del análisis de una importación.
 *
 * La clasificación es deliberadamente independiente de la pantalla de origen:
 * galería, compartir y cámara pueden alimentar el mismo lote.
 */
@Serializable
enum class ImportKind {
    QR,
    COMPROBANTE,
    DESCONOCIDO,
}

@Serializable
data class ImportCandidate(
    val id: String,
    val kind: ImportKind,
    val fingerprint: String,
    val mimeType: String? = null,
    val extension: String? = null,
    val qrAsset: QrAsset? = null,
    /** Reference to transient raw bytes; never stores bytes in UI/domain state. */
    val payloadRef: String? = null,
    val error: String? = null,
)

data class ImportBatch(
    val candidates: List<ImportCandidate>,
) {
    val recognized: List<ImportCandidate>
        get() = candidates.filter { it.kind != ImportKind.DESCONOCIDO }

    val qr: List<ImportCandidate>
        get() = candidates.filter { it.kind == ImportKind.QR }

    val comprobantes: List<ImportCandidate>
        get() = candidates.filter { it.kind == ImportKind.COMPROBANTE }

    val unknown: List<ImportCandidate>
        get() = candidates.filter { it.kind == ImportKind.DESCONOCIDO }

    val duplicateFingerprints: Set<String>
        get() = candidates.groupingBy { it.fingerprint }
            .eachCount()
            .filterValues { it > 1 }
            .keys

    val duplicates: List<ImportCandidate>
        get() = candidates.filter { it.fingerprint in duplicateFingerprints }

    val pendingReview: List<ImportCandidate>
        get() = candidates.filter {
            it.kind == ImportKind.DESCONOCIDO || it.fingerprint in duplicateFingerprints
        }

    val uniqueRecognized: List<ImportCandidate>
        get() = recognized.filter { it.fingerprint !in duplicateFingerprints }
}

/**
 * Clasifica y normaliza un lote sin descartar elementos válidos por errores ajenos.
 *
 * Si un elemento no puede analizarse, permanece en el lote como DESCONOCIDO.
 * La deduplicación se basa en una huella estable producida por la capa de entrada.
 */
class AnalyzeImportBatchUseCase {
    operator fun invoke(candidates: List<ImportCandidate>): ImportBatch =
        ImportBatch(candidates = candidates)
}
