package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind

/**
 * S12: reintentar la clasificación de un pendiente desde su payload
 * retenido. La decisión de clasificación es de plataforma (la misma que
 * usa la importación); el resultado reemplaza al candidato en el lote.
 *
 * - Android: reintenta la decodificación ZXing; si reconoce un QR, el
 *   candidato pasa a QR y sale de revisión (estado derivado del lote).
 *   Si no, permanece DESCONOCIDO para decisión del usuario.
 * - Otras plataformas: sin adquisición de importación (stubs), el
 *   candidato se devuelve sin cambios.
 */
expect suspend fun reclassifyImportCandidate(
    candidate: ImportCandidate,
    bytes: ByteArray,
): ImportCandidate

/** Nombre visible del archivo pendiente (miniatura textual del elemento). */
internal fun pendingItemName(candidate: ImportCandidate): String {
    val extension = candidate.extension?.let { AppStrings.ArchivoPrefijo + it } ?: AppStrings.ElementoImportado
    return when (candidate.kind) {
        ImportKind.QR -> extension + " · QR"
        ImportKind.COMPROBANTE -> extension + " · comprobante"
        ImportKind.DESCONOCIDO -> extension
    }
}
