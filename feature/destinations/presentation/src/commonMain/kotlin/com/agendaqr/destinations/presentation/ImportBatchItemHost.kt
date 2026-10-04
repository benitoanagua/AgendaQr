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
import com.agendaqr.destinations.domain.ReceiptProvenance
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.newEntityId
import com.agendaqr.destinations.domain.nowMillis

/**
 * T7 — S12: efectos por elemento que la UI pide y el reducer no puede
 * hacer (persistencia, payload temporal, vista previa del existente).
 *
 * Sin doble guardado: cada acción es idempotente por elemento — la copia
 * deliberada usa un id NUEVO y el elemento sale del lote al guardarse o
 * descartarse, así "Guardar reconocidos" nunca vuelve a verlo.
 */
internal class ImportBatchItemHost(
    private val destinations: DestinationRepository,
    private val comprobantes: ComprobanteRepository,
    private val comprobanteFiles: ComprobanteFileStore,
    private val payloads: ImportPayloadStore,
) {
    /** Resultado de la copia deliberada de un duplicado. */
    sealed interface SaveOutcome {
        data class Saved(val kind: ImportKind) : SaveOutcome
        data object MissingPayload : SaveOutcome
        data object NotApplicable : SaveOutcome
    }

    /**
     * "Guardar de todos modos": copia deliberada (RF-14) con id nuevo.
     * El payload temporal se elimina tras persistir.
     */
    suspend fun saveDuplicateAnyway(candidate: ImportCandidate): SaveOutcome {
        when (candidate.kind) {
            ImportKind.QR -> {
                val asset = candidate.qrAsset ?: return SaveOutcome.MissingPayload
                val now = nowMillis()
                destinations.save(
                    Destination(
                        id = newEntityId("destination-copy"),
                        name = "",
                        qr = asset,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
                return SaveOutcome.Saved(ImportKind.QR)
            }
            ImportKind.COMPROBANTE -> {
                val reference = candidate.payloadRef ?: return SaveOutcome.MissingPayload
                val bytes = payloads.read(reference) ?: return SaveOutcome.MissingPayload
                SaveComprobanteUseCase(comprobantes, comprobanteFiles)(
                    Comprobante(
                        id = newEntityId("comprobante"),
                        file = "",
                        createdAt = nowMillis(),
                        updatedAt = nowMillis(),
                        provenance = ReceiptProvenance.RECIBIDO,
                    ),
                    bytes,
                    candidate.extension ?: "bin",
                    candidate.mimeType ?: "application/octet-stream",
                )
                payloads.delete(reference)
                return SaveOutcome.Saved(ImportKind.COMPROBANTE)
            }
            ImportKind.DESCONOCIDO -> return SaveOutcome.NotApplicable
        }
    }

    /** Elimina el payload temporal de un elemento descartado. */
    suspend fun discardPayload(candidate: ImportCandidate) {
        candidate.payloadRef?.let { payloads.delete(it) }
    }

    /**
     * "Ver existente": la primera ocurrencia válida con la misma huella.
     * Para QR basta el asset (previsualización pura); para comprobantes se
     * cargan los bytes retenidos.
     */
    suspend fun resolveExisting(
        candidate: ImportCandidate,
        batch: ImportBatch,
    ): ExistingImportPreview {
        val existing = batch.candidates.firstOrNull {
            it.id != candidate.id &&
                it.fingerprint == candidate.fingerprint &&
                it.kind != ImportKind.DESCONOCIDO
        } ?: return ExistingImportPreview()
        return when (existing.kind) {
            ImportKind.QR -> ExistingImportPreview(qrAsset = existing.qrAsset)
            else -> {
                val bytes = existing.payloadRef?.let { payloads.read(it) }
                if (bytes != null) {
                    ExistingImportPreview(
                        comprobanteBytes = bytes,
                        comprobanteMimeType = existing.mimeType,
                    )
                } else {
                    ExistingImportPreview()
                }
            }
        }
    }
}
