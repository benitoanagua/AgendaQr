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
        }
}

fun ImportBatch.canSaveRecognized(): Boolean = uniqueRecognized.isNotEmpty()

fun ImportBatch.pendingItems(): List<ImportCandidate> = pendingReview
