package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LocalOperationRepositoryTest {
    @Test
    fun save_update_get_and_delete_work() = runTest {
        val repository = LocalOperationRepository(MemoryOperationsStore())
        val operation = Operation("op-1", OperationType.PAGO, 100, 110, "850", "BOB", "Colegio San José")

        repository.save(operation)
        assertEquals(operation, repository.get("op-1"))

        val updated = operation.copy(note = "actualizado")
        repository.update(updated)
        assertEquals(updated, repository.get("op-1"))

        repository.delete("op-1")
        assertNull(repository.get("op-1"))
    }

    @Test
    fun duplicate_ids_are_rejected() = runTest {
        val repository = LocalOperationRepository(MemoryOperationsStore())
        val operation = Operation("op-1", OperationType.PAGO, 1, 2)

        repository.save(operation)

        assertFailsWith<IllegalArgumentException> {
            repository.save(operation)
        }
    }

    @Test
    fun repository_reloads_from_same_store() = runTest {
        val store = MemoryOperationsStore()
        val operation = Operation("op-1", OperationType.COBRO, 1, 2)

        LocalOperationRepository(store).save(operation)

        assertEquals(operation, LocalOperationRepository(store).get("op-1"))
    }
}

class LocalComprobanteRepositoryTest {
    @Test
    fun receipt_can_be_saved_unassociated_and_associated_later() = runTest {
        val repository = LocalComprobanteRepository(MemoryOperationsStore())
        val receipt = Comprobante("r-1", "local://receipt-1.jpg", 100)

        repository.save(receipt)
        assertNull(repository.get("r-1")?.operationId)

        repository.update(receipt.copy(operationId = "op-1"))
        assertEquals("op-1", repository.get("r-1")?.operationId)
    }

    @Test
    fun duplicate_ids_are_rejected() = runTest {
        val repository = LocalComprobanteRepository(MemoryOperationsStore())
        val receipt = Comprobante("r-1", "local://a", 1)

        repository.save(receipt)

        assertFailsWith<IllegalArgumentException> {
            repository.save(receipt)
        }
    }
}

private class MemoryOperationsStore : OperationsStore {
    private val values = mutableMapOf<String, String>()

    override fun read(key: String): String? = values[key]
    override fun write(key: String, value: String) { values[key] = value }
}
