package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ReceiptMatchingTest {
    @Test
    fun existing_association_is_preserved() = runBlocking {
        val receipt = receipt("r1", operationId = "op1")
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(emptyList()),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.SINGLE, result.kind)
        assertEquals(listOf("op1"), result.operationIds)
    }

    @Test
    fun one_operation_in_same_context_is_proposed() = runBlocking {
        val receipt = receipt("r1", contextId = "ctx")
        val operation = operation("op1", contextId = "ctx")
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(listOf(operation)),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.SINGLE, result.kind)
        assertEquals("op1", result.proposedOperationId)
    }

    @Test
    fun multiple_operations_in_same_context_require_ambiguity() = runBlocking {
        val receipt = receipt("r1", contextId = "ctx")
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(
                listOf(operation("op1", "ctx"), operation("op2", "ctx"))
            ),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.MULTIPLE, result.kind)
        assertEquals(listOf("op1", "op2"), result.operationIds)
    }

    @Test
    fun multiple_context_operations_are_resolved_by_unique_same_day_signal() = runBlocking {
        val receipt = receipt("r1", contextId = "ctx", createdAt = 86_400_000L)
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(
                listOf(
                    operation("op1", "ctx", occurredAt = 86_400_000L),
                    operation("op2", "ctx", occurredAt = 10L),
                ),
            ),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.SINGLE, result.kind)
        assertEquals("op1", result.proposedOperationId)
    }

    @Test
    fun filename_tokens_can_resolve_an_unassociated_receipt() = runBlocking {
        val receipt = receipt(
            id = "r1",
            file = "local://pago-colegio-450-bob.png",
        )
        val operation = operation(
            id = "op1",
            contextId = null,
            occurredAt = 100_000L,
            amount = "450",
            currency = "BOB",
            personOrEntity = "Colegio",
        )
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(listOf(operation)),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.SINGLE, result.kind)
        assertEquals("op1", result.proposedOperationId)
    }

    @Test
    fun weak_single_candidate_is_not_auto_associated() = runBlocking {
        val receipt = receipt("r1", createdAt = 1_000_000L)
        val operation = operation("op1", null, occurredAt = 10_000_000L)
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(listOf(operation)),
        )

        val result = useCase("r1")

        assertEquals(ReceiptMatchKind.NONE, result.kind)
        assertEquals(emptyList(), result.operationIds)
    }

    @Test
    fun no_context_and_no_high_confidence_match_can_be_saved_unassociated() = runBlocking {
        val receipt = receipt("receipt", contextId = null)
        val useCase = SuggestReceiptAssociationUseCase(
            comprobantes = FakeComprobanteRepository(receipt),
            operations = FakeOperationRepository(listOf(operation("op1", "ctx"))),
        )

        val result = useCase("receipt")

        assertEquals(ReceiptMatchKind.NONE, result.kind)
        assertEquals(emptyList(), result.operationIds)
    }

    private fun receipt(
        id: String,
        contextId: String? = null,
        operationId: String? = null,
        file: String = "local://$id.png",
        createdAt: Long = 1,
    ) = Comprobante(
        id = id,
        file = file,
        createdAt = createdAt,
        updatedAt = createdAt,
        contextId = contextId,
        operationId = operationId,
    )

    private fun operation(
        id: String,
        contextId: String?,
        occurredAt: Long = 1,
        amount: String? = "100",
        currency: String? = "BOB",
        personOrEntity: String? = "Colegio",
    ) = Operation(
        id = id,
        type = OperationType.PAGO,
        occurredAt = occurredAt,
        createdAt = occurredAt,
        updatedAt = occurredAt,
        amount = amount,
        currency = currency,
        personOrEntity = personOrEntity,
        contextId = contextId,
    )
}

private class FakeComprobanteRepository(
    initial: Comprobante,
) : ComprobanteRepository {
    private val state = MutableStateFlow(listOf(initial))
    override fun observe() = state
    override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
    override suspend fun save(comprobante: Comprobante) {
        state.value = state.value + comprobante
    }
    override suspend fun update(comprobante: Comprobante) {
        state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
    }
    override suspend fun delete(id: String) {
        state.value = state.value.filterNot { it.id == id }
    }
}

private class FakeOperationRepository(
    initial: List<Operation>,
) : OperationRepository {
    private val state = MutableStateFlow(initial)
    override fun observe() = state
    override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
    override suspend fun save(operation: Operation) {
        state.value = state.value + operation
    }
    override suspend fun update(operation: Operation) {
        state.value = state.value.map { if (it.id == operation.id) operation else it }
    }
    override suspend fun delete(id: String) {
        state.value = state.value.filterNot { it.id == id }
    }
}
