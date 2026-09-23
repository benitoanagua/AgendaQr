package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val CONTEXTS_STORAGE_KEY_PREFIX = "agendaqr.contexts.v1."

private val contextJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

class LocalContextRepository(
    private val store: DestinationStore = platformDestinationStore(),
    private val storageKey: String = CONTEXTS_STORAGE_KEY_PREFIX + "legacy",
) : ContextRepository {
    private val mutex = Mutex()
    private val state = MutableStateFlow(load())

    override fun observe(): Flow<List<Context>> = state.asStateFlow()
    override suspend fun get(id: String): Context? = state.value.firstOrNull { it.id == id }

    override suspend fun save(context: Context) {
        mutex.withLock {
            require(state.value.none { it.id == context.id }) { "Context already exists: " + context.id }
            persist(state.value + context)
        }
    }

    override suspend fun update(context: Context) {
        mutex.withLock {
            val next = state.value.map { if (it.id == context.id) context else it }
            require(next.any { it.id == context.id }) { "Context not found: " + context.id }
            persist(next)
        }
    }

    override suspend fun delete(id: String) {
        mutex.withLock { persist(state.value.filterNot { it.id == id }) }
    }

    private fun load(): List<Context> =
        store.read(storageKey)?.let {
            runCatching { contextJson.decodeFromString<ContextList>(it).items }
                .getOrDefault(emptyList())
        } ?: emptyList()

    private fun persist(value: List<Context>) {
        state.value = value
        store.write(storageKey, contextJson.encodeToString(ContextList(value)))
    }
}

@Serializable
private data class ContextList(val items: List<Context>)
