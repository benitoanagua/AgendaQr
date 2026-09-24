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

        val allOperations = operations.observe().first()
        val contextCandidates = allOperations
            .asSequence()
            .filter { operation ->
                receipt.contextId != null && operation.contextId == receipt.contextId
            }
            .toList()

        if (contextCandidates.size == 1) {
            return ReceiptAssociationSuggestion(
                kind = ReceiptMatchKind.SINGLE,
                receiptId = comprobanteId,
                operationIds = listOf(contextCandidates.single().id),
            )
        }

        val filenameTokens = receipt.file
            .substringAfterLast('/')
            .substringBeforeLast('.')
            .normalizeTokens()

        val scored = allOperations
            .asSequence()
            .map { operation ->
                operation to score(operation, receipt, filenameTokens)
            }
            .filter { (_, score) -> score > 0 }
            .sortedWith(
                compareByDescending<Pair<Operation, Int>> { it.second }
                    .thenByDescending { it.first.occurredAt }
                    .thenBy { it.first.id },
            )
            .toList()

        if (scored.isEmpty()) {
            return ReceiptAssociationSuggestion(
                kind = ReceiptMatchKind.NONE,
                receiptId = comprobanteId,
                operationIds = emptyList(),
            )
        }

        val top = scored.first()
        val second = scored.getOrNull(1)

        // A context-only match is useful when it is the sole activity, but
        // when several activities exist we only auto-propose a unique match
        // if an additional deterministic signal separates it from the rest.
        val hasStrongSignal = top.second >= STRONG_MATCH_SCORE
        val clearlyAhead = second == null || top.second - second.second >= MIN_SCORE_MARGIN

        if (hasStrongSignal && clearlyAhead) {
            return ReceiptAssociationSuggestion(
                kind = ReceiptMatchKind.SINGLE,
                receiptId = comprobanteId,
                operationIds = listOf(top.first.id),
            )
        }

        if (second == null) {
            return ReceiptAssociationSuggestion(
                kind = ReceiptMatchKind.NONE,
                receiptId = comprobanteId,
                operationIds = emptyList(),
            )
        }

        val ambiguityFloor = (top.second - MIN_SCORE_MARGIN + 1).coerceAtLeast(1)
        val ambiguousIds = scored
            .takeWhile { (_, score) -> score >= ambiguityFloor }
            .take(MAX_AMBIGUOUS_CANDIDATES)
            .map { it.first.id }

        return ReceiptAssociationSuggestion(
            kind = ReceiptMatchKind.MULTIPLE,
            receiptId = comprobanteId,
            operationIds = ambiguousIds,
        )
    }

    private fun score(
        operation: Operation,
        receipt: Comprobante,
        filenameTokens: Set<String>,
    ): Int {
        var score = 0

        if (receipt.contextId != null && operation.contextId == receipt.contextId) {
            score += CONTEXT_SCORE
        }

        val dayDistance = kotlin.math.abs(operation.occurredAt - receipt.createdAt)
        when {
            dayDistance <= DAY_MILLIS -> score += SAME_DAY_SCORE
            dayDistance <= THREE_DAYS_MILLIS -> score += NEAR_DATE_SCORE
        }

        val searchable = listOfNotNull(
            operation.amount,
            operation.currency,
            operation.personOrEntity,
            operation.concept,
            operation.note,
        ).joinToString(" ").normalizeTokens()

        score += filenameTokens.count { token ->
            token.length >= MIN_TOKEN_LENGTH && token in searchable
        } * FILENAME_TOKEN_SCORE

        return score
    }

    private fun String.normalizeTokens(): Set<String> =
        lowercase()
            .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
            .split(' ')
            .filter { it.isNotBlank() }
            .toSet()

    private companion object {
        const val DAY_MILLIS = 86_400_000L
        const val THREE_DAYS_MILLIS = DAY_MILLIS * 3
        const val CONTEXT_SCORE = 10
        const val SAME_DAY_SCORE = 5
        const val NEAR_DATE_SCORE = 2
        const val FILENAME_TOKEN_SCORE = 3
        const val MIN_TOKEN_LENGTH = 3
        const val STRONG_MATCH_SCORE = FILENAME_TOKEN_SCORE * 3 + SAME_DAY_SCORE
        const val MIN_SCORE_MARGIN = 3
        const val MAX_AMBIGUOUS_CANDIDATES = 8
    }
}
