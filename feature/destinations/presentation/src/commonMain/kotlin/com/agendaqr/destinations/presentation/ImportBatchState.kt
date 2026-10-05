package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate

sealed interface ImportBatchUiState {
    data object Idle : ImportBatchUiState
    data object Importing : ImportBatchUiState
    data object Analyzing : ImportBatchUiState
    data class Result(val batch: ImportBatch) : ImportBatchUiState
    data class Review(val batch: ImportBatch) : ImportBatchUiState
    data class Saving(val batch: ImportBatch) : ImportBatchUiState
    data class Saved(val batch: ImportBatch) : ImportBatchUiState
    data class Error(val message: String, val batch: ImportBatch? = null) : ImportBatchUiState
}

sealed interface ImportBatchAction {
    data object Begin : ImportBatchAction
    data class Analyzed(val batch: ImportBatch) : ImportBatchAction
    data object ReviewPending : ImportBatchAction
    data object SaveRecognized : ImportBatchAction
    data object Saved : ImportBatchAction
    data class Failed(val message: String) : ImportBatchAction
    data object Back : ImportBatchAction

    // ------------------------------------------------------------------
    // T7 — S12: resolución de pendientes por elemento (intención de UI).
    // El host de la app las traduce en los eventos del reducer de abajo.
    // ------------------------------------------------------------------

    /** Reintentar la clasificación de un elemento desde su payload retenido. */
    data class RetryCandidate(val candidateId: String) : ImportBatchAction

    /** Descartar el elemento (el host elimina su payload temporal). */
    data class DiscardCandidate(val candidateId: String) : ImportBatchAction

    /** Guardar un duplicado de forma intencional (copia deliberada). */
    data class SaveDuplicateAnyway(val candidateId: String) : ImportBatchAction

    /** Ver el elemento existente con el que coincide este duplicado. */
    data class ViewExisting(val candidateId: String) : ImportBatchAction

    /** Cerrar el visor del existente. */
    data object CloseExisting : ImportBatchAction

    // Eventos del reducer (efectos ya resueltos por el host).
    data class CandidateReclassified(val candidate: ImportCandidate) : ImportBatchAction
    data class CandidateSaved(val candidateId: String) : ImportBatchAction
}

class ImportBatchReducer {
    fun reduce(state: ImportBatchUiState, action: ImportBatchAction): ImportBatchUiState =
        when (action) {
            ImportBatchAction.Begin -> ImportBatchUiState.Importing
            is ImportBatchAction.Analyzed -> ImportBatchUiState.Result(action.batch)
            ImportBatchAction.ReviewPending -> when (state) {
                is ImportBatchUiState.Result -> ImportBatchUiState.Review(state.batch)
                else -> state
            }
            ImportBatchAction.SaveRecognized -> when (state) {
                is ImportBatchUiState.Result -> ImportBatchUiState.Saving(state.batch)
                is ImportBatchUiState.Review -> ImportBatchUiState.Saving(state.batch)
                else -> state
            }
            ImportBatchAction.Saved -> when (state) {
                is ImportBatchUiState.Saving -> ImportBatchUiState.Saved(state.batch)
                else -> state
            }
            is ImportBatchAction.Failed -> ImportBatchUiState.Error(
                message = action.message,
                batch = when (state) {
                    is ImportBatchUiState.Result -> state.batch
                    is ImportBatchUiState.Review -> state.batch
                    is ImportBatchUiState.Saving -> state.batch
                    is ImportBatchUiState.Error -> state.batch
                    is ImportBatchUiState.Saved -> state.batch
                    else -> null
                },
            )
            ImportBatchAction.Back -> when (state) {
                is ImportBatchUiState.Result -> ImportBatchUiState.Idle
                is ImportBatchUiState.Review -> ImportBatchUiState.Result(state.batch)
                is ImportBatchUiState.Error -> state.batch?.let { ImportBatchUiState.Result(it) } ?: ImportBatchUiState.Idle
                else -> ImportBatchUiState.Idle
            }
            // Intenciones de UI: el host de la app las resuelve y emite el
            // evento del reducer correspondiente; sin ellas el reducer no
            // cambia el estado.
            is ImportBatchAction.RetryCandidate,
            is ImportBatchAction.DiscardCandidate,
            is ImportBatchAction.SaveDuplicateAnyway,
            is ImportBatchAction.ViewExisting,
            ImportBatchAction.CloseExisting,
            -> state
            // El elemento reclasificado reemplaza al pendiente; su nuevo
            // tipo decide si sigue en revisión (estado derivado del lote).
            is ImportBatchAction.CandidateReclassified -> when (state) {
                is ImportBatchUiState.Result -> state.copy(
                    batch = state.batch.withCandidate(action.candidate),
                )
                is ImportBatchUiState.Review -> state.copy(
                    batch = state.batch.withCandidate(action.candidate),
                )
                is ImportBatchUiState.Saving -> state.copy(
                    batch = state.batch.withCandidate(action.candidate),
                )
                is ImportBatchUiState.Error -> state.batch?.let {
                    state.copy(batch = it.withCandidate(action.candidate))
                } ?: state
                else -> state
            }
            // El elemento guardado o descartado sale del lote: no puede
            // volver a guardarse (sin doble guardado).
            is ImportBatchAction.CandidateSaved -> when (state) {
                is ImportBatchUiState.Result -> state.copy(
                    batch = state.batch.withoutCandidate(action.candidateId),
                )
                is ImportBatchUiState.Review -> state.copy(
                    batch = state.batch.withoutCandidate(action.candidateId),
                )
                is ImportBatchUiState.Saving -> state.copy(
                    batch = state.batch.withoutCandidate(action.candidateId),
                )
                is ImportBatchUiState.Error -> state.batch?.let {
                    state.copy(batch = it.withoutCandidate(action.candidateId))
                } ?: state
                else -> state
            }
        }
}

/** Reemplaza un candidato por id (reclasificación). */
internal fun ImportBatch.withCandidate(candidate: ImportCandidate): ImportBatch =
    copy(candidates = candidates.map { if (it.id == candidate.id) candidate else it })

/** Elimina un candidato por id (descarte o guardado por elemento). */
internal fun ImportBatch.withoutCandidate(candidateId: String): ImportBatch =
    copy(candidates = candidates.filterNot { it.id == candidateId })

fun ImportBatch.canSaveRecognized(): Boolean = uniqueRecognized.isNotEmpty()

fun ImportBatch.pendingItems(): List<ImportCandidate> = pendingReview

/** Etiqueta del botón de revisión: "Revisar N pendiente(s)" (contrato S12). */
fun reviewPendingLabel(batch: ImportBatch): String =
    reviewPendingLabel(batch.pendingItems().size)
