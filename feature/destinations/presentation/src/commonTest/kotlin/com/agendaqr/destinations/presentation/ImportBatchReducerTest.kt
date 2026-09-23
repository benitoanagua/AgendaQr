package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ImportBatchReducerTest {
    private val batch = ImportBatch(
        listOf(
            ImportCandidate("qr", ImportKind.QR, "qr"),
            ImportCandidate("unknown", ImportKind.DESCONOCIDO, "unknown"),
            ImportCandidate("duplicate", ImportKind.QR, "qr"),
        )
    )

    @Test
    fun follows_import_analyze_result_review_save_flow() {
        val reducer = ImportBatchReducer()
        var state: ImportBatchUiState = ImportBatchUiState.Idle

        state = reducer.reduce(state, ImportBatchAction.Begin)
        assertIs<ImportBatchUiState.Importing>(state)

        state = reducer.reduce(state, ImportBatchAction.Analyzed(batch))
        assertIs<ImportBatchUiState.Result>(state)

        state = reducer.reduce(state, ImportBatchAction.ReviewPending)
        assertIs<ImportBatchUiState.Review>(state)

        state = reducer.reduce(state, ImportBatchAction.SaveRecognized)
        assertIs<ImportBatchUiState.Saving>(state)

        state = reducer.reduce(state, ImportBatchAction.Saved)
        assertIs<ImportBatchUiState.Saved>(state)
    }

    @Test
    fun back_from_review_returns_to_result_without_losing_batch() {
        val reducer = ImportBatchReducer()

        val review = reducer.reduce(
            ImportBatchUiState.Result(batch),
            ImportBatchAction.ReviewPending,
        )
        val result = reducer.reduce(review, ImportBatchAction.Back)

        assertEquals(batch, assertIs<ImportBatchUiState.Result>(result).batch)
    }

    @Test
    fun failed_save_is_recoverable() {
        val reducer = ImportBatchReducer()

        val saving = reducer.reduce(
            ImportBatchUiState.Result(batch),
            ImportBatchAction.SaveRecognized,
        )
        val error = reducer.reduce(
            saving,
            ImportBatchAction.Failed("storage unavailable"),
        )

        val state = assertIs<ImportBatchUiState.Error>(error)
        assertEquals("storage unavailable", state.message)
        assertEquals(batch, state.batch)
        assertTrue(batch.canSaveRecognized())
    }
}
