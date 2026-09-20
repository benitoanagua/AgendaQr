package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    val id: String,
    val name: String,
    val qr: QrAsset,
    val category: String? = null,
    val note: String? = null,
    val favorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class QrAsset(
    /** Base64 encoded image bytes. The domain intentionally does not know how the QR is decoded. */
    val encoded: String,
    val mimeType: String = "image/png",
)

interface DestinationRepository {
    fun observe(): Flow<List<Destination>>
    suspend fun get(id: String): Destination?
    suspend fun save(destination: Destination)
    suspend fun update(destination: Destination)
    suspend fun delete(id: String)
}

class ObserveDestinationsUseCase(private val repository: DestinationRepository) {
    operator fun invoke(): Flow<List<Destination>> = repository.observe()
}

class GetDestinationUseCase(private val repository: DestinationRepository) {
    suspend operator fun invoke(id: String): Destination? = repository.get(id)
}

class SaveDestinationUseCase(private val repository: DestinationRepository) {
    suspend operator fun invoke(destination: Destination) = repository.save(destination)
}

class UpdateDestinationUseCase(private val repository: DestinationRepository) {
    suspend operator fun invoke(destination: Destination) = repository.update(destination)
}

class DeleteDestinationUseCase(private val repository: DestinationRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}

class ToggleFavoriteUseCase(private val repository: DestinationRepository) {
    suspend operator fun invoke(destination: Destination) {
        repository.update(
            destination.copy(
                favorite = !destination.favorite,
                updatedAt = nowMillis(),
            )
        )
    }
}

expect fun nowMillis(): Long
