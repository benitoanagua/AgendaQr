package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DeletedOperationHistory
import com.agendaqr.destinations.domain.DeletedOperationHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val STORAGE_KEY_PREFIX = "agendaqr.deleted_operations.v1"

private val historyJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

class LocalDeletedOperationHistoryRepository(
    private val store: OperationsStore = operationsStore(),
    private val storageKey: String = userScopedKey(STORAGE_KEY_PREFIX),
) : DeletedOperationHistoryRepository {
    private val mutex = Mutex()
    private val state = MutableStateFlow(load())

    override fun observe(): Flow<List<DeletedOperationHistory>> = state.asStateFlow()

    override suspend fun save(history: DeletedOperationHistory) {
        mutex.withLock {
            persist(state.value + history)
        }
    }

    private fun load(): List<DeletedOperationHistory> =
        store.read(storageKey)?.let {
            runCatching {
                historyJson.decodeFromString<DeletedOperationHistoryList>(it).items
            }.getOrDefault(emptyList())
        } ?: emptyList()

    private fun persist(value: List<DeletedOperationHistory>) {
        state.value = value
        store.write(storageKey, historyJson.encodeToString(DeletedOperationHistoryList(value)))
    }
}

@Serializable
private data class DeletedOperationHistoryList(
    val items: List<DeletedOperationHistory>,
)

expect fun createDeletedOperationHistoryRepository(): DeletedOperationHistoryRepository
