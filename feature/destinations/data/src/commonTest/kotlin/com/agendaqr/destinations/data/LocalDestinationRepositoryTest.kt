package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LocalDestinationRepositoryTest {
    private class MemoryStore : DestinationStore {
        private val values = mutableMapOf<String, String>()
        override fun read(key: String): String? = values[key]
        override fun write(key: String, value: String) { values[key] = value }
    }

    @Test
    fun save_update_delete_are_persistent_through_store() = kotlinx.coroutines.test.runTest {
        val repository = LocalDestinationRepository(MemoryStore())
        val destination = Destination("1", "Store", QrAsset("abc"), createdAt = 1, updatedAt = 1)
        repository.save(destination)
        assertEquals(destination, repository.get("1"))

        val updated = destination.copy(name = "Updated")
        repository.update(updated)
        assertEquals("Updated", repository.get("1")?.name)

        repository.delete("1")
        assertEquals(null, repository.get("1"))
    }

    @Test
    fun duplicate_ids_are_rejected() = kotlinx.coroutines.test.runTest {
        val repository = LocalDestinationRepository(MemoryStore())
        val destination = Destination("1", "Store", QrAsset("abc"), createdAt = 1, updatedAt = 1)
        repository.save(destination)
        assertFails { repository.save(destination) }
        assertEquals(1, repository.observe().first().size)
    }
}
