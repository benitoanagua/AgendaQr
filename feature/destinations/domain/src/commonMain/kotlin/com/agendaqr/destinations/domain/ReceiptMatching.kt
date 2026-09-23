package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.first

enum class ReceiptMatchKind {
    NONE,
    SINGLE,
    MULTIPLE,
}

data class ReceiptAssociationSuggestion(
    val kind: ReceiptMatchKind,
    val receiptId: String,
    val operationIds: List<String>,
) {
    val proposedOperationId: String?
        get() = operationIds.singleOrNull()
}

class SuggestReceiptAssociationUseCase(
    private val comprobantes: ComprobanteRepository,
    private val operations: OperationRepository,
) {
    suspend operator fun invoke(comprobanteId: String): ReceiptAssociationSuggestion {
        val receipt = requireNotNull(comprobantes.get(comprobanteId)) {
            "Comprobante not found: $comprobanteId"
        }

        // An existing association is already a decision; do not reinterpret it.
        receipt.operationId?.let {
            return ReceiptAssociationSuggestion(
                kind = ReceiptMatchKind.SINGLE,
                receiptId = comprobanteId,
                operationIds = listOf(it),
            )
        }

        val contextCandidates = operations.observe().first()
            .asSequence()
            .filter { operation ->
                receipt.contextId != null && operation.contextId == receipt.contextId
            }
            .sortedByDescending { it.occurredAt }
            .map { it.id }
            .distinct()
            .toList()

        if (contextCandidates.isNotEmpty()) {
            return when (contextCandidates.size) {
                1 -> ReceiptAssociationSuggestion(ReceiptMatchKind.SINGLE, comprobanteId, contextCandidates)
                else -> ReceiptAssociationSuggestion(ReceiptMatchKind.MULTIPLE, comprobanteId, contextCandidates)
            }
        }

        val receiptName = receipt.file.substringAfterLast('/').substringBeforeLast('.')
        val filenameCandidates = operations.observe().first()
            .asSequence()
            .filter { operation ->
                val haystack = listOfNotNull(
                    operation.personOrEntity,
                    operation.concept,
                    operation.note,
                    operation.amount,
                    operation.currency,
                ).joinToString(" ")
                receiptName.isNotBlank() && haystack.contains(receiptName, ignoreCase = true)
            }
            .sortedByDescending { it.occurredAt }
            .map { it.id }
            .distinct()
            .toList()

        val candidates = filenameCandidates

        return when (candidates.size) {
            0 -> ReceiptAssociationSuggestion(ReceiptMatchKind.NONE, comprobanteId, emptyList())
            1 -> ReceiptAssociationSuggestion(ReceiptMatchKind.SINGLE, comprobanteId, candidates)
            else -> ReceiptAssociationSuggestion(ReceiptMatchKind.MULTIPLE, comprobanteId, candidates)
        }
    }
}
