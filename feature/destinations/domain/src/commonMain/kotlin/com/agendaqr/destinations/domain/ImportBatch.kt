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

    /**
     * Fingerprints that appear more than once among recognized candidates.
     *
     * An unrecognized item can never turn a valid occurrence into a
     * duplicate: only content that can actually be persisted counts.
     */
    val duplicateFingerprints: Set<String>
        get() = recognized.groupingBy { it.fingerprint }
            .eachCount()
            .filterValues { it > 1 }
            .keys

    /**
     * Recognized occurrences beyond the first one of each fingerprint.
     *
     * The first valid occurrence is always kept; later repetitions are the
     * ones held for review so a duplicate never blocks the original.
     */
    val duplicates: List<ImportCandidate>
        get() {
            val seen = mutableSetOf<String>()
            val result = mutableListOf<ImportCandidate>()
            for (candidate in candidates) {
                if (candidate.kind == ImportKind.DESCONOCIDO) continue
                if (!seen.add(candidate.fingerprint)) result.add(candidate)
            }
            return result
        }

    val pendingReview: List<ImportCandidate>
        get() {
            val duplicateIds = duplicates.mapTo(mutableSetOf()) { it.id }
            return candidates.filter {
                it.kind == ImportKind.DESCONOCIDO || it.id in duplicateIds
            }
        }

    val uniqueRecognized: List<ImportCandidate>
        get() {
            val duplicateIds = duplicates.mapTo(mutableSetOf()) { it.id }
            return recognized.filter { it.id !in duplicateIds }
        }
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
