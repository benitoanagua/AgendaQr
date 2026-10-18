package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.SaveImportBatchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * D1 — estado del lote S12 + coordinador, aislado de la composición: la
 * UI emite [ImportBatchAction] y este holder resuelve los efectos por
 * elemento y el guardado del lote.
 */
internal class ImportBatchStateHolder(private val graph: AuthenticatedSessionGraph) {
    private val _state = MutableStateFlow<ImportBatchUiState>(ImportBatchUiState.Idle)
    val state: StateFlow<ImportBatchUiState> = _state.asStateFlow()
    private val _existingPreview = MutableStateFlow<ExistingImportPreview?>(null)
    val existingPreview: StateFlow<ExistingImportPreview?> = _existingPreview.asStateFlow()

    private val reducer = ImportBatchReducer()

    val coordinator = ImportBatchCoordinator(
        scope = graph.sessionScope,
        saveImportBatch = graph.saveImportBatch,
        payloads = graph.importPayloadStore,
        host = ImportBatchItemHost(
            destinations = graph.destinationRepository,
            comprobantes = graph.comprobanteRepository,
            comprobanteFiles = graph.comprobanteFiles,
            payloads = graph.importPayloadStore,
        ),
    )

    private val currentState: ImportBatchUiState get() = _state.value

    fun currentBatch(): ImportBatch? = when (val current = currentState) {
        is ImportBatchUiState.Result -> current.batch
        is ImportBatchUiState.Review -> current.batch
        is ImportBatchUiState.Saving -> current.batch
        is ImportBatchUiState.Error -> current.batch
        is ImportBatchUiState.Saved -> current.batch
        else -> null
    }

    /** La UI emite una acción; este holder aplica estado + efectos. */
    fun onAction(action: ImportBatchAction) {
        when (action) {
            ImportBatchAction.Back -> onBack()
            ImportBatchAction.SaveRecognized -> onSaveRecognized()
            is ImportBatchAction.RetryCandidate -> onRetryCandidate(action.candidateId)
            is ImportBatchAction.DiscardCandidate -> onDiscardCandidate(action.candidateId)
            is ImportBatchAction.SaveDuplicateAnyway -> onSaveDuplicateAnyway(action.candidateId)
            is ImportBatchAction.ViewExisting -> onViewExisting(action.candidateId)
            ImportBatchAction.CloseExisting -> _existingPreview.value = null
            else -> _state.value = reducer.reduce(currentState, action)
        }
    }

    fun onAnalyzed(batch: ImportBatch) {
        _state.value = reducer.reduce(currentState, ImportBatchAction.Analyzed(batch))
    }

    private fun onBack() {
        if (currentState is ImportBatchUiState.Review) {
            _state.value = reducer.reduce(currentState, ImportBatchAction.Back)
        } else {
            coordinator.discardAllPayloads(currentBatch())
            _state.value = reducer.reduce(currentState, ImportBatchAction.Back)
        }
    }

    private fun onSaveRecognized() {
        val batch = currentBatch() ?: run {
            _state.value = reducer.reduce(currentState, ImportBatchAction.Failed(userFacingError(IllegalStateException(), ErrorFlow.ImportBatchSave)))
            return
        }
        coordinator.saveRecognized(
            batch = batch,
            onStateChanged = { _state.value = it },
            currentState = { currentState },
            reducer = reducer,
        )
    }

    private fun onRetryCandidate(candidateId: String) {
        coordinator.retryCandidate(
            candidateId = candidateId,
            currentBatch = { currentBatch() },
            onStateChanged = { _state.value = it },
            currentState = { currentState },
            reducer = reducer,
        )
    }

    private fun onDiscardCandidate(candidateId: String) {
        coordinator.discardCandidate(
            candidateId = candidateId,
            currentBatch = { currentBatch() },
            onStateChanged = { _state.value = it },
            currentState = { currentState },
            reducer = reducer,
        )
    }

    private fun onSaveDuplicateAnyway(candidateId: String) {
        coordinator.saveDuplicateAnyway(
            candidateId = candidateId,
            currentBatch = { currentBatch() },
            onStateChanged = { _state.value = it },
            currentState = { currentState },
            reducer = reducer,
        )
    }

    private fun onViewExisting(candidateId: String) {
        coordinator.viewExisting(
            candidateId = candidateId,
            currentBatch = { currentBatch() },
            onExisting = { _existingPreview.value = it },
        )
    }
}
