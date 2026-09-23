package com.agendaqr.destinations.domain

class AssignDestinationToContextUseCase(
    private val contexts: ContextRepository,
    private val destinations: DestinationRepository,
) {
    suspend operator fun invoke(destinationId: String, contextId: String?): Destination {
        if (contextId != null) requireNotNull(contexts.get(contextId)) {
            "Context not found: $contextId"
        }
        val destination = requireNotNull(destinations.get(destinationId)) {
            "Destination not found: $destinationId"
        }
        return destination.copy(
            contextId = contextId,
            updatedAt = nowMillis(),
        ).also { destinations.update(it) }
    }
}

class AssignOperationToContextUseCase(
    private val contexts: ContextRepository,
    private val operations: OperationRepository,
) {
    suspend operator fun invoke(operationId: String, contextId: String?): Operation {
        if (contextId != null) requireNotNull(contexts.get(contextId)) {
            "Context not found: $contextId"
        }
        val operation = requireNotNull(operations.get(operationId)) {
            "Operation not found: $operationId"
        }
        return operation.copy(
            contextId = contextId,
            updatedAt = nowMillis(),
        ).also { operations.update(it) }
    }
}

class AssignComprobanteToContextUseCase(
    private val contexts: ContextRepository,
    private val comprobantes: ComprobanteRepository,
) {
    suspend operator fun invoke(comprobanteId: String, contextId: String?): Comprobante {
        if (contextId != null) requireNotNull(contexts.get(contextId)) {
            "Context not found: $contextId"
        }
        val comprobante = requireNotNull(comprobantes.get(comprobanteId)) {
            "Comprobante not found: $comprobanteId"
        }
        return comprobante.copy(
            contextId = contextId,
            updatedAt = nowMillis(),
        ).also { comprobantes.update(it) }
    }
}
