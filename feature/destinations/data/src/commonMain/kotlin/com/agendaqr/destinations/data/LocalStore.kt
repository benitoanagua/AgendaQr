package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val STORAGE_KEY_PREFIX = "agendaqr.destinations.v1."

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

interface DestinationStore {
    fun read(key: String): String?
    fun write(key: String, value: String)
}

expect fun platformDestinationStore(): DestinationStore

class LocalDestinationRepository(
    private val store: DestinationStore = platformDestinationStore(),
    private val storageKey: String = STORAGE_KEY_PREFIX + "legacy",
) : DestinationRepository {
    private val mutex = Mutex()
    private val state = MutableStateFlow(load())

    override fun observe(): Flow<List<Destination>> = state.asStateFlow()

    override suspend fun get(id: String): Destination? = state.value.firstOrNull { it.id == id }

    override suspend fun save(destination: Destination) {
        mutex.withLock {
            require(state.value.none { it.id == destination.id }) { "Destination already exists: ${destination.id}" }
            persist(state.value + destination)
        }
    }

    override suspend fun update(destination: Destination) {
        mutex.withLock {
            val next = state.value.map { if (it.id == destination.id) destination else it }
            require(next.any { it.id == destination.id }) { "Destination not found: ${destination.id}" }
            persist(next)
        }
    }

    override suspend fun delete(id: String) {
        mutex.withLock { persist(state.value.filterNot { it.id == id }) }
    }

    private fun load(): List<Destination> = store.read(storageKey)
        ?.let { runCatching { json.decodeFromString<DestinationList>(it).items }.getOrDefault(emptyList()) }
        ?: emptyList()

    private fun persist(value: List<Destination>) {
        state.value = value
        store.write(storageKey, json.encodeToString(DestinationList(value)))
    }
}

@Serializable
private data class DestinationList(val items: List<Destination>)

expect fun createDestinationRepository(): DestinationRepository
