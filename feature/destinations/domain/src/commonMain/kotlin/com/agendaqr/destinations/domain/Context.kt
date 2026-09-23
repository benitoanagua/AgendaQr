package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * Human-facing continuity boundary for Agenda QR.
 *
 * A context groups the things a person is trying to manage together:
 * QRs, activities and receipts. It does not require any of them to exist.
 */
@Serializable
data class Context(
    val id: String,
    val name: String,
    val note: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

interface ContextRepository {
    fun observe(): Flow<List<Context>>
    suspend fun get(id: String): Context?
    suspend fun save(context: Context)
    suspend fun update(context: Context)
    suspend fun delete(id: String)
}

class ObserveContextsUseCase(private val repository: ContextRepository) {
    operator fun invoke(): Flow<List<Context>> = repository.observe()
}

class GetContextUseCase(private val repository: ContextRepository) {
    suspend operator fun invoke(id: String): Context? = repository.get(id)
}

class SaveContextUseCase(private val repository: ContextRepository) {
    suspend operator fun invoke(context: Context) = repository.save(context)
}

class UpdateContextUseCase(private val repository: ContextRepository) {
    suspend operator fun invoke(context: Context) =
        repository.update(context.copy(updatedAt = nowMillis()))
}

class DeleteContextUseCase(private val repository: ContextRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}
