package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

enum class AgendaSearchResultType {
    CONTEXT,
    QR,
    ACTIVITY,
    COMPROBANTE,
}

data class AgendaSearchResult(
    val type: AgendaSearchResultType,
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val contextId: String? = null,
    val destinationId: String? = null,
    val operationId: String? = null,
)

data class AgendaSearchQuery(
    val text: String,
)

class SearchAgendaQrUseCase(
    private val contexts: ContextRepository,
    private val destinations: DestinationRepository,
    private val operations: OperationRepository,
    private val comprobantes: ComprobanteRepository,
) {
    operator fun invoke(query: AgendaSearchQuery): Flow<List<AgendaSearchResult>> =
        combine(
            contexts.observe(),
            destinations.observe(),
            operations.observe(),
            comprobantes.observe(),
        ) { contextList, destinationList, operationList, receiptList ->
            val text = query.text.trim().lowercase()
            if (text.isEmpty()) {
                emptyList()
            } else {
                buildList {
                    contextList.forEach { context ->
                        if (context.matches(text)) {
                            add(
                                AgendaSearchResult(
                                    type = AgendaSearchResultType.CONTEXT,
                                    id = context.id,
                                    title = context.name,
                                    subtitle = context.note,
                                    contextId = context.id,
                                )
                            )
                        }
                    }

                    destinationList.forEach { destination ->
                        if (destination.matches(text)) {
                            add(
                                AgendaSearchResult(
                                    type = AgendaSearchResultType.QR,
                                    id = destination.id,
                                    title = destination.name,
                                    subtitle = destination.category ?: destination.note,
                                    contextId = destination.contextId,
                                    destinationId = destination.id,
                                )
                            )
                        }
                    }

                    operationList.forEach { operation ->
                        if (operation.matches(text)) {
                            add(
                                AgendaSearchResult(
                                    type = AgendaSearchResultType.ACTIVITY,
                                    id = operation.id,
                                    title = operation.personOrEntity ?: operation.concept ?: operation.type.name,
                                    subtitle = listOfNotNull(
                                        operation.amount,
                                        operation.currency,
                                        operation.concept,
                                    ).joinToString(" · ").ifBlank { null },
                                    contextId = operation.contextId,
                                    destinationId = operation.destinationId,
                                    operationId = operation.id,
                                )
                            )
                        }
                    }

                    receiptList.forEach { receipt ->
                        if (receipt.matches(text)) {
                            add(
                                AgendaSearchResult(
                                    type = AgendaSearchResultType.COMPROBANTE,
                                    id = receipt.id,
                                    title = receipt.file.substringAfterLast('/'),
                                    subtitle = receipt.provenance?.name ?: receipt.mimeType,
                                    contextId = receipt.contextId,
                                    operationId = receipt.operationId,
                                )
                            )
                        }
                    }
                }
            }
        }

    private fun Context.matches(text: String): Boolean =
        listOfNotNull(name, note).any { it.contains(text, ignoreCase = true) }

    private fun Destination.matches(text: String): Boolean =
        listOfNotNull(name, category, note).any { it.contains(text, ignoreCase = true) }

    private fun Operation.matches(text: String): Boolean =
        listOfNotNull(
            type.name,
            amount,
            currency,
            personOrEntity,
            concept,
            note,
        ).any { it.contains(text, ignoreCase = true) }

    private fun Comprobante.matches(text: String): Boolean =
        listOfNotNull(
            id,
            file,
            mimeType,
            extension,
            provenance?.name,
        ).any { it.contains(text, ignoreCase = true) }
}
