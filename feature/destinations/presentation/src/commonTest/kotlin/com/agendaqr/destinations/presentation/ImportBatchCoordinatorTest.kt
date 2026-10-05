package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.ImportPayloadStore
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * D1 — ImportBatchCoordinator: la lógica del lote S12 fuera de Compose.
 */
class ImportBatchCoordinatorTest {

    private val qr = ImportCandidate(
        id = "qr-1",
        kind = ImportKind.QR,
        fingerprint = "fp-1",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val unknown = ImportCandidate(
        id = "u-1",
        kind = ImportKind.DESCONOCIDO,
        fingerprint = "fp-u",
        payloadRef = "payload-u",
    )
    private val batch = ImportBatch(listOf(qr, unknown))

    private class RecordingPayloads : ImportPayloadStore {
        val store = mutableMapOf<String, ByteArray>()
        override suspend fun put(reference: String, bytes: ByteArray) { store[reference] = bytes.copyOf() }
        override suspend fun read(reference: String): ByteArray? = store[reference]?.copyOf()
        override suspend fun delete(reference: String) { store.remove(reference) }
    }

    private class RecordingDestinations(initialFail: Boolean = false) : DestinationRepository {
        val saved = mutableListOf<Destination>()
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        var failNext = initialFail
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            if (failNext) { failNext = false; throw IllegalStateException("storage unavailable") }
            saved += destination
            state.value = state.value + destination
        }
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private fun coordinator(
        destinations: RecordingDestinations = RecordingDestinations(),
        payloads: RecordingPayloads = RecordingPayloads(),
    ): Pair<ImportBatchCoordinator, RecordingDestinations> {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val host = ImportBatchItemHost(destinations, NoopReceipts(), NoopFiles(), payloads)
        val save = com.agendaqr.destinations.domain.SaveImportBatchUseCase(destinations, NoopReceipts(), NoopFiles(), payloads)
        return ImportBatchCoordinator(scope, save, payloads, host) to destinations
    }

    @Test
    fun save_recognized_persists_and_reports_saved(): Unit = runBlocking {
        val (c, destinations) = coordinator()
        var state: ImportBatchUiState = ImportBatchUiState.Result(batch)
        c.saveRecognized(batch, { state = it }, { state }, ImportBatchReducer())
        waitUntil { state is ImportBatchUiState.Saved }
        assertEquals(1, destinations.saved.size)
        assertEquals(1, (state as ImportBatchUiState.Saved).batch.uniqueRecognized.size)
    }

    @Test
    fun candidate_failure_is_isolated_and_batch_still_saves(): Unit = runBlocking {
        // El use case aísla fallos por candidato (contrato): un QR que no
        // puede persistirse NO bloquea el resultado del lote — el estado
        // termina en Saved y el resto de la superficie no se rompe.
        val destinations = RecordingDestinations(initialFail = true)
        val (c, _) = coordinator(destinations)
        var state: ImportBatchUiState = ImportBatchUiState.Result(batch)
        c.saveRecognized(batch, { state = it }, { state }, ImportBatchReducer())
        waitUntil { state is ImportBatchUiState.Saved }
        assertEquals(0, destinations.saved.size)
        // El lote sobrevive para revisión del pendiente.
        val saved = state as ImportBatchUiState.Saved
        assertEquals(1, saved.batch.pendingItems().size)
    }

    @Test
    fun discard_deletes_the_transient_payload(): Unit = runBlocking {
        val payloads = RecordingPayloads()
        payloads.store["payload-u"] = byteArrayOf(1)
        val (c, _) = coordinator(payloads = payloads)
        var state: ImportBatchUiState = ImportBatchUiState.Review(batch)
        c.discardCandidate("u-1", { (state as? ImportBatchUiState.Review)?.batch }, { state = it }, { state }, ImportBatchReducer())
        waitUntil { state.currentBatch()?.candidates?.none { it.id == "u-1" } == true }
        // El payload temporal se elimina al descartar (contrato).
        assertNull(payloads.store["payload-u"])
    }

    @Test
    fun double_save_of_a_duplicate_is_protected(): Unit = runBlocking {
        val destinations = RecordingDestinations()
        val (c, _) = coordinator(destinations)
        val duplicate = ImportCandidate(
            id = "dup-1",
            kind = ImportKind.QR,
            fingerprint = "fp-1",
            qrAsset = QrAsset(encoded = "eA=="),
        )
        val withDup = ImportBatch(listOf(duplicate))
        var state: ImportBatchUiState = ImportBatchUiState.Review(withDup)
        val ctx: () -> ImportBatch? = { (state as? ImportBatchUiState.Review)?.batch }
        c.saveDuplicateAnyway("dup-1", ctx, { state = it }, { state }, ImportBatchReducer())
        waitUntil { (state as? ImportBatchUiState.Result) != null || (state as? ImportBatchUiState.Error) != null }
        // Segunda llamada con el mismo id: el elemento ya no está en el lote;
        // el coordinador es idempotente y no guarda dos copias.
        val savedCount = destinations.saved.size
        c.saveDuplicateAnyway("dup-1", ctx, { state = it }, { state }, ImportBatchReducer())
        delay(100)
        assertEquals(savedCount, destinations.saved.size)
    }

    private fun ImportBatchUiState.currentBatch(): ImportBatch? = when (this) {
        is ImportBatchUiState.Result -> batch
        is ImportBatchUiState.Review -> batch
        is ImportBatchUiState.Saving -> batch
        is ImportBatchUiState.Error -> batch
        else -> null
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) { if (condition()) return; delay(20) }
    }

    private class NoopReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(comprobante: Comprobante) {}
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }

    private class NoopFiles : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) {}
    }
}
