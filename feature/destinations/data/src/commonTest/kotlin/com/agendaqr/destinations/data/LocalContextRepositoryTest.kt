package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalContextRepositoryTest {
    @Test
    fun saves_and_observes_context() = runTest {
        val store = MemoryDestinationStore()
        val repository = LocalContextRepository(store, "contexts")

        repository.save(Context("context-1", "Colegio de Mateo", createdAt = 1L, updatedAt = 1L))

        assertEquals("Colegio de Mateo", repository.get("context-1")?.name)
        assertEquals(1, repository.observe().first().size)
    }

    @Test
    fun deletes_context() = runTest {
        val store = MemoryDestinationStore()
        val repository = LocalContextRepository(store, "contexts")
        repository.save(Context("context-1", "Colegio de Mateo", createdAt = 1L, updatedAt = 1L))

        repository.delete("context-1")

        assertNull(repository.get("context-1"))
    }
}

private class MemoryDestinationStore : DestinationStore {
    private val values = mutableMapOf<String, String>()
    override fun read(key: String): String? = values[key]
    override fun write(key: String, value: String) {
        values[key] = value
    }
}
