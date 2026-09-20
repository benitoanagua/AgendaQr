package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
enum class OperationType {
    PAGO,
    COBRO,
}

@Serializable
data class Operation(
    val id: String,
    val type: OperationType,
    val occurredAt: Long,
    val createdAt: Long,
    val amount: String? = null,
    val currency: String? = null,
    val personOrEntity: String? = null,
    val destinationId: String? = null,
    val concept: String? = null,
    val note: String? = null,
)

@Serializable
enum class ReceiptProvenance {
    ENVIADO,
    RECIBIDO,
    DESCONOCIDO,
}

@Serializable
data class Comprobante(
    val id: String,
    val file: String,
    val createdAt: Long,
    val provenance: ReceiptProvenance? = null,
    val operationId: String? = null,
)

@Serializable
data class DeletedOperationHistory(
    val date: Long,
    val type: OperationType,
    val amount: String? = null,
    val personOrEntity: String? = null,
)

interface OperationRepository {
    fun observe(): Flow<List<Operation>>
    suspend fun get(id: String): Operation?
    suspend fun save(operation: Operation)
    suspend fun update(operation: Operation)
    suspend fun delete(id: String)
}

interface ComprobanteRepository {
    fun observe(): Flow<List<Comprobante>>
    suspend fun get(id: String): Comprobante?
    suspend fun save(comprobante: Comprobante)
    suspend fun update(comprobante: Comprobante)
    suspend fun delete(id: String)
}

interface ComprobanteFileStore {
    suspend fun save(id: String, bytes: ByteArray, extension: String): String
    suspend fun read(file: String): ByteArray?
    suspend fun delete(file: String)
}

class ObserveOperationsUseCase(private val repository: OperationRepository) {
    operator fun invoke(): Flow<List<Operation>> = repository.observe()
}

class GetOperationUseCase(private val repository: OperationRepository) {
    suspend operator fun invoke(id: String): Operation? = repository.get(id)
}

class SaveOperationUseCase(private val repository: OperationRepository) {
    suspend operator fun invoke(operation: Operation) = repository.save(operation)
}

class UpdateOperationUseCase(private val repository: OperationRepository) {
    suspend operator fun invoke(operation: Operation) = repository.update(operation)
}

class DeleteOperationUseCase(private val repository: OperationRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}

class ObserveComprobantesUseCase(private val repository: ComprobanteRepository) {
    operator fun invoke(): Flow<List<Comprobante>> = repository.observe()
}

class GetComprobanteUseCase(private val repository: ComprobanteRepository) {
    suspend operator fun invoke(id: String): Comprobante? = repository.get(id)
}

class SaveComprobanteUseCase(
    private val repository: ComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
) {
    suspend operator fun invoke(
        comprobante: Comprobante,
        bytes: ByteArray,
        extension: String,
    ): Comprobante {
        val file = fileStore.save(comprobante.id, bytes, extension)
        return try {
            comprobante.copy(file = file).also { repository.save(it) }
        } catch (error: Throwable) {
            fileStore.delete(file)
            throw error
        }
    }
}

class UpdateComprobanteUseCase(private val repository: ComprobanteRepository) {
    suspend operator fun invoke(comprobante: Comprobante) = repository.update(comprobante)
}

class DeleteComprobanteUseCase(
    private val repository: ComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
) {
    suspend operator fun invoke(comprobante: Comprobante) {
        repository.delete(comprobante.id)
        fileStore.delete(comprobante.file)
    }
}

expect fun nowMillis(): Long

