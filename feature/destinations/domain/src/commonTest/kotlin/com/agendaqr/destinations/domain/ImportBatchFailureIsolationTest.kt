package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ImportBatchFailureIsolationTest {

    @Test
    fun receipt_failure_does_not_block_qr_and_keeps_payload_for_retry() = runTest {
        val destinations = MemoryDestinationRepository()
        val receipts = MemoryComprobanteRepository()
        val files = FailingFileStore()
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
                ),
            ),
        )

        assertEquals(1, result.savedQr)
        assertEquals(0, result.savedComprobantes)
        assertEquals(1, result.skipped)
        assertNotNull(destinations.get("qr-1"))
        assertNull(receipts.get("receipt-1"))
        assertNotNull(payloads.read("receipt-ref"))
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

    private class FailingFileStore : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String =
            error("simulated file-store failure")

        override suspend fun read(file: String): ByteArray? = null

        override suspend fun delete(file: String) = Unit
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
