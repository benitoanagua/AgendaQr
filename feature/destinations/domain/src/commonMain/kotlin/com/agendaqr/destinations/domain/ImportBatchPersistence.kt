package com.agendaqr.destinations.domain

/**
 * Transient storage for import payloads.
 *
 * Raw bytes are intentionally kept outside UI state and are deleted after a
 * successful persistence or explicit discard.
 */
interface ImportPayloadStore {
    suspend fun put(reference: String, bytes: ByteArray)
    suspend fun read(reference: String): ByteArray?
    suspend fun delete(reference: String)
}

data class ImportBatchSaveResult(
    val savedQr: Int,
    val savedComprobantes: Int,
    val skipped: Int,
)

class SaveImportBatchUseCase(
    private val destinations: DestinationRepository,
    private val comprobantes: ComprobanteRepository,
    private val comprobanteFileStore: ComprobanteFileStore,
    private val payloadStore: ImportPayloadStore,
) {
    suspend operator fun invoke(batch: ImportBatch): ImportBatchSaveResult {
        var savedQr = 0
        var savedComprobantes = 0
        var skipped = 0

        batch.uniqueRecognized.forEach { candidate ->
            when (candidate.kind) {
                ImportKind.QR -> {
                    val asset = candidate.qrAsset
                    if (asset == null) {
                        skipped++
                    } else {
                        val alreadyExists = destinations.observe().first().any { it.qr == asset }
                        if (alreadyExists || destinations.get(candidate.id) != null) {
                            skipped++
                        } else {
                            val now = nowMillis()
                            destinations.save(
                                Destination(
                                    id = candidate.id,
                                    name = "",
                                    qr = asset,
                                    createdAt = now,
                                    updatedAt = now,
                                )
                            )
                            savedQr++
                        }
                    }
                }

                ImportKind.COMPROBANTE -> {
                    val reference = candidate.payloadRef
                    val bytes = reference?.let(payloadStore::read)
                    if (bytes == null || bytes.isEmpty()) {
                        skipped++
                    } else {
                        val duplicate = FindDuplicateComprobantesUseCase(
                            comprobantes,
                            comprobanteFileStore,
                        )(bytes).isNotEmpty()
                        if (duplicate || comprobantes.get(candidate.id) != null) {
                            payloadStore.delete(reference)
                            skipped++
                        } else {
                            SaveComprobanteUseCase(
                                comprobantes,
                                comprobanteFileStore,
                            )(
                                Comprobante(
                                    id = candidate.id,
                                    file = "",
                                    createdAt = nowMillis(),
                                    updatedAt = nowMillis(),
                                    provenance = ReceiptProvenance.RECIBIDO,
                                ),
                                bytes,
                                candidate.extension ?: "bin",
                                candidate.mimeType ?: "application/octet-stream",
                            )
                            payloadStore.delete(reference)
                            savedComprobantes++
                        }
                    }
                }

                ImportKind.DESCONOCIDO -> skipped++
            }
        }

        return ImportBatchSaveResult(savedQr, savedComprobantes, skipped)
    }
}
