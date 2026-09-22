package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DeletedOperationHistory
import com.agendaqr.destinations.domain.OperationType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalDeletedOperationHistoryRepositoryTest {

    @Test
    fun history_is_persisted_under_its_storage_key() = runTest {
        val store = MemoryHistoryStore()
        val repository = LocalDeletedOperationHistoryRepository(
            store = store,
            storageKey = "agendaqr.deleted_operations.v1.user-a",
        )

        repository.save(history(date = 10, type = OperationType.PAGO))
        repository.save(history(date = 20, type = OperationType.COBRO))

        assertEquals(
            listOf(10L, 20L),
            LocalDeletedOperationHistoryRepository(
                store = store,
                storageKey = "agendaqr.deleted_operations.v1.user-a",
            ).observe().first().map { it.date },
        )
    }

    @Test
    fun user_scoped_keys_keep_histories_isolated() = runTest {
        val store = MemoryHistoryStore()
        val userA = LocalDeletedOperationHistoryRepository(
            store = store,
            storageKey = "agendaqr.deleted_operations.v1.user-a",
        )
        val userB = LocalDeletedOperationHistoryRepository(
            store = store,
            storageKey = "agendaqr.deleted_operations.v1.user-b",
        )

        userA.save(history(date = 100, type = OperationType.PAGO, amount = "850", personOrEntity = "A"))
        userB.save(history(date = 200, type = OperationType.COBRO, amount = "900", personOrEntity = "B"))

        assertEquals(1, userA.observe().first().size)
        assertEquals(listOf(100L), userA.observe().first().map { it.date })
        assertEquals(listOf("A"), userA.observe().first().map { it.personOrEntity })
        assertTrue(userB.observe().first().none { it.personOrEntity == "A" })
        assertEquals(1, userB.observe().first().size)
        assertEquals(listOf(200L), userB.observe().first().map { it.date })
    }

    private fun history(date: Long, type: OperationType, amount: String? = null, personOrEntity: String? = null) =
        DeletedOperationHistory(
            date = date,
            type = type,
            amount = amount,
            personOrEntity = personOrEntity,
        )
}

private class MemoryHistoryStore : OperationsStore {
    private val values = mutableMapOf<String, String>()

    override fun read(key: String): String? = values[key]
    override fun write(key: String, value: String) { values[key] = value }
}