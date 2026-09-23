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
    ) = Comprobante(
        id = id,
        file = "local://$id.png",
        createdAt = 1,
        updatedAt = 1,
        contextId = contextId,
        operationId = operationId,
    )

    private fun operation(id: String, contextId: String?) = Operation(
        id = id,
        type = OperationType.PAGO,
        occurredAt = 1,
        createdAt = 1,
        updatedAt = 1,
        amount = "100",
        currency = "BOB",
        personOrEntity = "Colegio",
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
