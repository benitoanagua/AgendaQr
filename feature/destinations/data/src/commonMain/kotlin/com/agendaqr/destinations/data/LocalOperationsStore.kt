package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val OPERATIONS_STORAGE_KEY = "agendaqr.operations.v1"
private const val RECEIPTS_STORAGE_KEY = "agendaqr.comprobantes.v1"

private val operationsJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

interface OperationsStore {
    fun read(key: String): String?
    fun write(key: String, value: String)
}

fun operationsStore(): OperationsStore = object : OperationsStore {
    private val delegate = platformDestinationStore()

    override fun read(key: String): String? = delegate.read(key)
    override fun write(key: String, value: String) = delegate.write(key, value)
}

class LocalOperationRepository(
    private val store: OperationsStore = operationsStore(),
) : OperationRepository {
    private val mutex = Mutex()
    private val state = MutableStateFlow(load())

    override fun observe(): Flow<List<Operation>> = state.asStateFlow()
    override suspend fun get(id: String): Operation? = state.value.firstOrNull { it.id == id }

    override suspend fun save(operation: Operation) {
        mutex.withLock {
            require(state.value.none { it.id == operation.id })
            persist(state.value + operation)
        }
    }

    override suspend fun update(operation: Operation) {
        mutex.withLock {
            val next = state.value.map { if (it.id == operation.id) operation else it }
            require(next.any { it.id == operation.id })
            persist(next)
        }
    }

    override suspend fun delete(id: String) {
        mutex.withLock { persist(state.value.filterNot { it.id == id }) }
    }

    private fun load(): List<Operation> =
        store.read(OPERATIONS_STORAGE_KEY)?.let {
            runCatching {
                operationsJson.decodeFromString<OperationList>(it).items
            }.getOrDefault(emptyList())
        } ?: emptyList()

    private fun persist(value: List<Operation>) {
        state.value = value
        store.write(OPERATIONS_STORAGE_KEY, operationsJson.encodeToString(OperationList(value)))
    }
}

class LocalComprobanteRepository(
    private val store: OperationsStore = operationsStore(),
) : ComprobanteRepository {
    private val mutex = Mutex()
    private val state = MutableStateFlow(load())

    override fun observe(): Flow<List<Comprobante>> = state.asStateFlow()
    override suspend fun get(id: String): Comprobante? = state.value.firstOrNull { it.id == id }

    override suspend fun save(comprobante: Comprobante) {
        mutex.withLock {
            require(state.value.none { it.id == comprobante.id })
            persist(state.value + comprobante)
        }
    }

    override suspend fun update(comprobante: Comprobante) {
        mutex.withLock {
            val next = state.value.map { if (it.id == comprobante.id) comprobante else it }
            require(next.any { it.id == comprobante.id })
            persist(next)
        }
    }

    override suspend fun delete(id: String) {
        mutex.withLock { persist(state.value.filterNot { it.id == id }) }
    }

    private fun load(): List<Comprobante> =
        store.read(RECEIPTS_STORAGE_KEY)?.let {
            runCatching {
                operationsJson.decodeFromString<ComprobanteList>(it).items
            }.getOrDefault(emptyList())
        } ?: emptyList()

    private fun persist(value: List<Comprobante>) {
        state.value = value
        store.write(RECEIPTS_STORAGE_KEY, operationsJson.encodeToString(ComprobanteList(value)))
    }
}

@Serializable
private data class OperationList(val items: List<Operation>)

@Serializable
private data class ComprobanteList(val items: List<Comprobante>)

expect fun createOperationRepository(): OperationRepository
expect fun createComprobanteRepository(): ComprobanteRepository
