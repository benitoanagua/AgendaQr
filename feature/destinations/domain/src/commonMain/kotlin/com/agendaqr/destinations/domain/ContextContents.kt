package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class ContextContents(
    val context: Context,
    val destinations: List<Destination>,
    val operations: List<Operation>,
    val comprobantes: List<Comprobante>,
)

class ObserveContextContentsUseCase(
    private val contexts: ContextRepository,
    private val destinations: DestinationRepository,
    private val operations: OperationRepository,
    private val comprobantes: ComprobanteRepository,
) {
    operator fun invoke(contextId: String): Flow<ContextContents?> =
        combine(
            contexts.observe(),
            destinations.observe(),
            operations.observe(),
            comprobantes.observe(),
        ) { contextList, destinationList, operationList, receiptList ->
            val context = contextList.firstOrNull { it.id == contextId } ?: return@combine null
            ContextContents(
                context = context,
                destinations = destinationList.filter { it.contextId == contextId },
                operations = operationList
                    .filter { it.contextId == contextId }
                    .sortedByDescending { it.occurredAt },
                comprobantes = receiptList
                    .filter { it.contextId == contextId }
                    .sortedByDescending { it.createdAt },
            )
        }
}
