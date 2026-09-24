package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ImportBatchPersistenceTest {

    @Test
    fun saves_qr_and_receipt_without_blocking_each_other() = runTest {
        val destinations = MemoryDestinationRepository()
        val receipts = MemoryComprobanteRepository()
        val files = MemoryFileStore()
        val payloads = MemoryPayloadStore()
        payloads.put("receipt-ref", byteArrayOf(1, 2, 3))

        val result = SaveImportBatchUseCase(
            destinations,
            receipts,
            files,
            payloads,
        )(
            ImportBatch(
                listOf(
                    ImportCandidate(
                        id = "qr-1",
                        kind = ImportKind.QR,
                        fingerprint = "qr-fp",
                        qrAsset = QrAsset("qr-data"),
                    ),
                    ImportCandidate(
                        id = "receipt-1",
                        kind = ImportKind.COMPROBANTE,
                        fingerprint = "receipt-fp",
                        mimeType = "image/png",
                        extension = "png",
                        payloadRef = "receipt-ref",
                    ),
                    ImportCandidate(
                        id = "unknown-1",
                        kind = ImportKind.DESCONOCIDO,
                        fingerprint = "unknown-fp",
                    ),
                ),
            ),
        )

        assertEquals(1, result.savedQr)
        assertEquals(1, result.savedComprobantes)
        assertEquals(1, result.skipped)
        assertNotNull(destinations.get("qr-1"))
        assertNotNull(receipts.get("receipt-1"))
        assertNull(payloads.read("receipt-ref"))
    }

    @Test
    fun second_save_is_idempotent_and_does_not_duplicate_receipt() = runTest {
        val destinations = MemoryDestinationRepository()
        val receipts = MemoryComprobanteRepository()
        val files = MemoryFileStore()
        val payloads = MemoryPayloadStore()
        payloads.put("receipt-ref", byteArrayOf(4, 5, 6))

        val batch = ImportBatch(
            listOf(
                ImportCandidate(
                    id = "qr-1",
                    kind = ImportKind.QR,
                    fingerprint = "qr-fp",
                    qrAsset = QrAsset("qr-data"),
                ),
                ImportCandidate(
                    id = "receipt-1",
                    kind = ImportKind.COMPROBANTE,
                    fingerprint = "receipt-fp",
                    mimeType = "image/png",
                    extension = "png",
                    payloadRef = "receipt-ref",
                ),
            ),
        )

        val useCase = SaveImportBatchUseCase(destinations, receipts, files, payloads)
        val first = useCase(batch)
        val second = useCase(batch)

        assertEquals(1, first.savedQr)
        assertEquals(1, first.savedComprobantes)
        assertEquals(0, second.savedQr)
        assertEquals(0, second.savedComprobantes)
        assertEquals(2, second.skipped)
        assertEquals(1, destinations.observe().first().size)
        assertEquals(1, receipts.observe().first().size)
    }

    @Test
    fun duplicate_occurrences_in_one_batch_are_not_persisted_twice() = runTest {
        val destinations = MemoryDestinationRepository()
        val receipts = MemoryComprobanteRepository()
        val files = MemoryFileStore()
        val payloads = MemoryPayloadStore()
        payloads.put("receipt-ref-1", byteArrayOf(1, 2, 3))
        payloads.put("receipt-ref-2", byteArrayOf(1, 2, 3))

        val result = SaveImportBatchUseCase(
            destinations,
            receipts,
            files,
            payloads,
        )(
            ImportBatch(
                listOf(
                    ImportCandidate(
                        id = "receipt-1",
                        kind = ImportKind.COMPROBANTE,
                        fingerprint = "receipt-fp",
                        mimeType = "image/png",
                        extension = "png",
                        payloadRef = "receipt-ref-1",
                    ),
                    ImportCandidate(
                        id = "receipt-2",
                        kind = ImportKind.COMPROBANTE,
                        fingerprint = "receipt-fp",
                        mimeType = "image/png",
                        extension = "png",
                        payloadRef = "receipt-ref-2",
                    ),
                ),
            ),
        )

        assertEquals(0, result.savedQr)
        assertEquals(1, result.savedComprobantes)
        assertEquals(1, result.skipped)
        assertEquals(1, receipts.observe().first().size)
        // The persisted occurrence cleans its temporary payload; the duplicate
        // occurrence keeps its payload available for review and retry.
        assertNull(payloads.read("receipt-ref-1"))
        assertNotNull(payloads.read("receipt-ref-2"))
    }

    private class MemoryDestinationRepository : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) = save(destination)
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class MemoryComprobanteRepository : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value.filterNot { it.id == comprobante.id } + comprobante
        }
        override suspend fun update(comprobante: Comprobante) = save(comprobante)
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class MemoryFileStore : ComprobanteFileStore {
        private val files = mutableMapOf<String, ByteArray>()
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
            val path = "comprobantes/$id.$extension"
            files[path] = bytes.copyOf()
            return path
        }
        override suspend fun read(file: String): ByteArray? = files[file]?.copyOf()
        override suspend fun delete(file: String) {
            files.remove(file)
        }
    }

    private class MemoryPayloadStore : ImportPayloadStore {
        private val values = mutableMapOf<String, ByteArray>()
        override suspend fun put(reference: String, bytes: ByteArray) {
            values[reference] = bytes.copyOf()
        }
        override suspend fun read(reference: String): ByteArray? = values[reference]?.copyOf()
        override suspend fun delete(reference: String) {
            values.remove(reference)
        }
    }
}
