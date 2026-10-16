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
internal fun pendingItemName(candidate: ImportCandidate, index: Int = 0): String {
    // Con nombre original disponible, el título es el nombre del archivo.
    candidate.displayName?.takeIf { it.isNotBlank() }?.let { return it }
    // Respaldo numerado estable por tipo cuando no hay nombre.
    val typeLabel = when (candidate.kind) {
        ImportKind.QR -> "QR"
        ImportKind.COMPROBANTE -> "comprobante"
        ImportKind.DESCONOCIDO -> "sin clasificar"
    }
    val extension = candidate.extension?.let { AppStrings.ArchivoPrefijo + it }
    return if (extension != null) {
        extension + " · " + typeLabel
    } else {
        "Imagen " + (index + 1) + " · " + typeLabel
    }
}
