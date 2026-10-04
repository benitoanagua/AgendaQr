package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportCandidate

/** Plataformas sin adquisición de importación: sin cambios. */
actual suspend fun reclassifyImportCandidate(
    candidate: ImportCandidate,
    bytes: ByteArray,
): ImportCandidate = candidate
