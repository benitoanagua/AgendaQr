package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.ReceiptProvenance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * T7 — S12: resolver pendientes por elemento.
 *
 * Contrato S12 (congelado): "✓ reconocidos / ! duplicados / ? pendientes /
 * [ GUARDAR RECONOCIDOS ] / Revisar N pendientes. Los válidos no dependen
 * de los pendientes."
 */
class ImportBatchPendingItemsTest {
    private val reducer = ImportBatchReducer()

    private val unknown = ImportCandidate(
        id = "unknown-1",
        kind = ImportKind.DESCONOCIDO,
        fingerprint = "fp-unknown",
        mimeType = "image/png",
        extension = "png",
        payloadRef = "payload-unknown",
    )
    private val qrFirst = ImportCandidate(
        id = "qr-first",
        kind = ImportKind.QR,
        fingerprint = "fp-qr",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val qrDuplicate = ImportCandidate(
        id = "qr-dup",
        kind = ImportKind.QR,
        fingerprint = "fp-qr",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val batch = ImportBatch(listOf(qrFirst, unknown, qrDuplicate))

    // ------------------------------------------------------------------
    // Botón: "Revisar N pendientes" con el conteo real.
    // ------------------------------------------------------------------

    @Test
    fun review_button_label_counts_pending_items() {
        assertEquals(2, batch.pendingItems().size)
        assertEquals("Revisar 2 pendientes", reviewPendingLabel(batch))
        assertEquals("Revisar 0 pendientes", reviewPendingLabel(ImportBatch(emptyList())))
    }

    // ------------------------------------------------------------------
    // Descartar: el elemento sale del lote y no vuelve a guardarse.
    // ------------------------------------------------------------------

    @Test
    fun discard_removes_the_candidate_from_the_batch() {
        val review = reducer.reduce(ImportBatchUiState.Result(batch), ImportBatchAction.ReviewPending)
        val after = reducer.reduce(review, ImportBatchAction.CandidateSaved(unknown.id))
        val state = assertIs<ImportBatchUiState.Review>(after)

        assertNull(state.batch.candidates.firstOrNull { it.id == unknown.id })
        assertEquals(1, state.batch.pendingItems().size)
        // Los válidos no dependen de los pendientes: el QR original sigue.
        assertTrue(state.batch.canSaveRecognized())
        assertTrue(state.batch.uniqueRecognized.any { it.id == qrFirst.id })
    }

    // ------------------------------------------------------------------
    // Reintentar clasificación: reclasificado como QR sale de revisión;
    // sigue desconocido si no se reconoce.
    // ------------------------------------------------------------------

    @Test
    fun reclassified_as_qr_leaves_pending_review() {
        val review = reducer.reduce(ImportBatchUiState.Result(batch), ImportBatchAction.ReviewPending)
        val reclassified = unknown.copy(kind = ImportKind.QR, qrAsset = QrAsset(encoded = "eA=="))
        val after = reducer.reduce(review, ImportBatchAction.CandidateReclassified(reclassified))
        val state = assertIs<ImportBatchUiState.Review>(after)

        assertEquals(ImportKind.QR, state.batch.candidates.first { it.id == unknown.id }.kind)
        // Ya no es pendiente: la revisión queda solo con el duplicado.
        assertEquals(listOf(qrDuplicate.id), state.batch.pendingItems().map { it.id })
        // Y el recién clasificado entra en los reconocidos guardables.
        assertTrue(state.batch.uniqueRecognized.any { it.id == unknown.id })
    }

    @Test
    fun still_unknown_after_retry_stays_pending() {
        val review = reducer.reduce(ImportBatchUiState.Result(batch), ImportBatchAction.ReviewPending)
        val after = reducer.reduce(review, ImportBatchAction.CandidateReclassified(unknown))
        val state = assertIs<ImportBatchUiState.Review>(after)

        assertEquals(2, state.batch.pendingItems().size)
    }

    // ------------------------------------------------------------------
    // Intenciones de UI: el reducer solo cambia con los eventos resueltos.
    // ------------------------------------------------------------------

    @Test
    fun ui_intents_do_not_change_reducer_state_by_themselves() {
        val review = reducer.reduce(ImportBatchUiState.Result(batch), ImportBatchAction.ReviewPending)
        assertEquals(review, reducer.reduce(review, ImportBatchAction.RetryCandidate("unknown-1")))
        assertEquals(review, reducer.reduce(review, ImportBatchAction.DiscardCandidate("unknown-1")))
        assertEquals(review, reducer.reduce(review, ImportBatchAction.SaveDuplicateAnyway("qr-dup")))
        assertEquals(review, reducer.reduce(review, ImportBatchAction.ViewExisting("qr-dup")))
        assertEquals(review, reducer.reduce(review, ImportBatchAction.CloseExisting))
    }

    // ------------------------------------------------------------------
    // Host: copia deliberada, payload temporal y existente.
    // ------------------------------------------------------------------

    @Test
    fun save_duplicate_qr_persists_a_new_copy() = runBlocking {
        val destinations = RecordingDestinations()
        val host = ImportBatchItemHost(destinations, NoopReceipts(), NoopFiles(), RecordingPayloads())
        val outcome = host.saveDuplicateAnyway(qrDuplicate)

        assertEquals(ImportBatchItemHost.SaveOutcome.Saved(ImportKind.QR), outcome)
        val saved = destinations.saved.single()
        // Copia deliberada con id NUEVO: el original y la copia conviven.
        assertTrue(saved.id != qrDuplicate.id, "La copia no debe pisar al original")
        assertEquals(qrDuplicate.qrAsset, saved.qr)
    }

    @Test
    fun save_duplicate_comprobante_persists_and_deletes_payload() = runBlocking {
        val receipts = NoopReceipts()
        val payloads = RecordingPayloads()
        payloads.store["payload-dup"] = byteArrayOf(1, 2, 3)
        val duplicateReceipt = ImportCandidate(
            id = "rec-dup",
            kind = ImportKind.COMPROBANTE,
            fingerprint = "fp-rec",
            mimeType = "image/png",
            extension = "png",
            payloadRef = "payload-dup",
        )
        val host = ImportBatchItemHost(NoopDestinations(), receipts, NoopFiles(), payloads)

        val outcome = host.saveDuplicateAnyway(duplicateReceipt)

        assertEquals(ImportBatchItemHost.SaveOutcome.Saved(ImportKind.COMPROBANTE), outcome)
        assertEquals(1, receipts.saved.size)
        assertNotNull(receipts.saved.single())
        // El payload temporal se elimina al guardar.
        assertNull(payloads.store["payload-dup"])
    }

    @Test
    fun save_without_payload_reports_missing_payload() = runBlocking {
        val host = ImportBatchItemHost(NoopDestinations(), NoopReceipts(), NoopFiles(), RecordingPayloads())
        val outcome = host.saveDuplicateAnyway(
            ImportCandidate("rec-x", ImportKind.COMPROBANTE, "fp-x", payloadRef = "missing"),
        )
        assertEquals(ImportBatchItemHost.SaveOutcome.MissingPayload, outcome)
        assertEquals(
            ImportBatchItemHost.SaveOutcome.NotApplicable,
            host.saveDuplicateAnyway(unknown),
        )
    }

    @Test
    fun discard_deletes_the_transient_payload() = runBlocking {
        val payloads = RecordingPayloads()
        payloads.store["payload-unknown"] = byteArrayOf(9)
        val host = ImportBatchItemHost(NoopDestinations(), NoopReceipts(), NoopFiles(), payloads)

        host.discardPayload(unknown)
        assertNull(payloads.store["payload-unknown"])
    }

    @Test
    fun view_existing_resolves_the_first_valid_occurrence() = runBlocking {
        val payloads = RecordingPayloads()
        val host = ImportBatchItemHost(NoopDestinations(), NoopReceipts(), NoopFiles(), payloads)

        val qrExisting = host.resolveExisting(qrDuplicate, batch)
        assertEquals(qrFirst.qrAsset, qrExisting.qrAsset)

        // Sin coincidencia: preview vacía (la UI muestra un mensaje honesto).
        val alone = ImportBatch(listOf(unknown))
        assertEquals(ExistingImportPreview(), host.resolveExisting(unknown, alone))
    }

    // ------------------------------------------------------------------
    // Sin doble guardado: tras guardar/descartar, Guardar reconocidos no
    // vuelve a ver el elemento.
    // ------------------------------------------------------------------

    @Test
    fun saved_candidate_is_not_persisted_twice_by_save_recognized() = runBlocking {
        val review = reducer.reduce(ImportBatchUiState.Result(batch), ImportBatchAction.ReviewPending)
        val after = reducer.reduce(review, ImportBatchAction.CandidateSaved(qrDuplicate.id))
        val state = assertIs<ImportBatchUiState.Review>(after)

        // El duplicado ya no está en el lote: "Guardar reconocidos" y el
        // use case de persistencia no pueden volver a tocarlo.
        assertNull(state.batch.candidates.firstOrNull { it.id == qrDuplicate.id })
        assertTrue(state.batch.uniqueRecognized.none { it.id == qrDuplicate.id })
    }

    private class RecordingDestinations : DestinationRepository {
        val saved = mutableListOf<Destination>()
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            saved += destination
            state.value = state.value + destination
        }
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopReceipts : ComprobanteRepository {
        val saved = mutableListOf<Comprobante>()
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            saved += comprobante
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopFiles : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) {}
    }

    private class RecordingPayloads : com.agendaqr.destinations.domain.ImportPayloadStore {
        val store = mutableMapOf<String, ByteArray>()
        override suspend fun put(reference: String, bytes: ByteArray) {
            store[reference] = bytes.copyOf()
        }
        override suspend fun read(reference: String): ByteArray? = store[reference]?.copyOf()
        override suspend fun delete(reference: String) {
            store.remove(reference)
        }
    }
}
