package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportBatchSaveResult
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportPayloadStore
import com.agendaqr.destinations.domain.SaveImportBatchUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * D1 — la lógica de negocio del lote S12 fuera de las lambdas de UI: sin
 * Compose, testeable. La UI emite intenciones ([ImportBatchAction.Ui]);
 * el coordinador resuelve efectos (guardar, reclasificar, descartar,
 * "guardar de todos modos", visor del existente) y devuelve eventos puros
 * que el reducer ya entiende.
 */
internal class ImportBatchCoordinator(
    private val scope: CoroutineScope,
    private val saveImportBatch: SaveImportBatchUseCase,
    private val payloads: ImportPayloadStore,
    private val host: ImportBatchItemHost,
) {
    /** Un lote por elemento: guarda idempotente. */
    private val savedCandidates = mutableSetOf<String>()

    fun saveRecognized(
        batch: ImportBatch,
        onStateChanged: (ImportBatchUiState) -> Unit,
        currentState: () -> ImportBatchUiState,
        reducer: ImportBatchReducer,
    ) {
        onStateChanged(reducer.reduce(currentState(), ImportBatchAction.SaveRecognized))
        scope.launch {
            runCatching { saveImportBatch(batch) }
                .onSuccess { onStateChanged(reducer.reduce(currentState(), ImportBatchAction.Saved)) }
                .onFailure { error ->
                    onStateChanged(
                        reducer.reduce(
                            currentState(),
                            ImportBatchAction.Failed(
                                userFacingError(error, ErrorFlow.ImportBatchSave),
                            ),
                        ),
                    )
                }
        }
    }

    fun retryCandidate(
        candidateId: String,
        currentBatch: () -> ImportBatch?,
        onStateChanged: (ImportBatchUiState) -> Unit,
        currentState: () -> ImportBatchUiState,
        reducer: ImportBatchReducer,
    ) {
        val candidate = currentBatch()?.candidates?.firstOrNull { it.id == candidateId } ?: return
        val payloadRef = candidate.payloadRef ?: return
        scope.launch {
            val bytes = runCatching { payloads.read(payloadRef) }.getOrNull()
            val reclassified = if (bytes != null) reclassifyImportCandidate(candidate, bytes) else candidate
            onStateChanged(
                reducer.reduce(currentState(), ImportBatchAction.CandidateReclassified(reclassified)),
            )
        }
    }

    fun discardCandidate(
        candidateId: String,
        currentBatch: () -> ImportBatch?,
        onStateChanged: (ImportBatchUiState) -> Unit,
        currentState: () -> ImportBatchUiState,
        reducer: ImportBatchReducer,
    ) {
        val candidate = currentBatch()?.candidates?.firstOrNull { it.id == candidateId } ?: return
        // El payload temporal se elimina al descartar (contrato S12).
        scope.launch { host.discardPayload(candidate) }
        // Sin doble guardado: el elemento sale del lote inmediatamente.
        onStateChanged(reducer.reduce(currentState(), ImportBatchAction.CandidateSaved(candidateId)))
    }

    fun saveDuplicateAnyway(
        candidateId: String,
        currentBatch: () -> ImportBatch?,
        onStateChanged: (ImportBatchUiState) -> Unit,
        currentState: () -> ImportBatchUiState,
        reducer: ImportBatchReducer,
    ) {
        val candidate = currentBatch()?.candidates?.firstOrNull { it.id == candidateId } ?: return
        if (candidateId in savedCandidates) return // idempotencia por elemento
        savedCandidates += candidateId
        scope.launch {
            runCatching { host.saveDuplicateAnyway(candidate) }
                .onSuccess {
                    onStateChanged(
                        reducer.reduce(currentState(), ImportBatchAction.CandidateSaved(candidateId)),
                    )
                }
                .onFailure { error ->
                    savedCandidates -= candidateId
                    onStateChanged(
                        reducer.reduce(
                            currentState(),
                            ImportBatchAction.Failed(
                                userFacingError(error, ErrorFlow.ImportBatchSave),
                            ),
                        ),
                    )
                }
        }
    }

    fun viewExisting(
        candidateId: String,
        currentBatch: () -> ImportBatch?,
        onExisting: (ExistingImportPreview) -> Unit,
    ) {
        val candidate = currentBatch()?.candidates?.firstOrNull { it.id == candidateId } ?: return
        val batch = currentBatch() ?: return
        scope.launch {
            onExisting(host.resolveExisting(candidate, batch))
        }
    }

    fun discardAllPayloads(batch: ImportBatch?) {
        batch ?: return
        scope.launch {
            batch.candidates.mapNotNull { it.payloadRef }
                .distinct()
                .forEach { payloads.delete(it) }
        }
    }
}
