package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    val updatedAt: Long = createdAt,
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

interface DeletedOperationHistoryRepository {
    fun observe(): Flow<List<DeletedOperationHistory>>
    suspend fun save(history: DeletedOperationHistory)
}

enum class SensitiveOperationField {
    TYPE,
    OCCURRED_AT,
    AMOUNT,
    CURRENCY,
    PERSON_OR_ENTITY,
    DESTINATION,
}

fun Operation.hasSensitiveChangesComparedTo(other: Operation): Boolean =
    type != other.type ||
        occurredAt != other.occurredAt ||
        amount != other.amount ||
        currency != other.currency ||
        personOrEntity != other.personOrEntity ||
        destinationId != other.destinationId

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

data class OperationSearchQuery(
    val text: String = "",
    val type: OperationType? = null,
    val occurredFrom: Long? = null,
    val occurredTo: Long? = null,
)

class SearchOperationsUseCase(private val repository: OperationRepository) {
    operator fun invoke(query: OperationSearchQuery): Flow<List<Operation>> =
        repository.observe().map { operations ->
            val text = query.text.trim().lowercase()
            operations
                .asSequence()
                .filter { operation -> query.type == null || operation.type == query.type }
                .filter { operation -> query.occurredFrom == null || operation.occurredAt >= query.occurredFrom }
                .filter { operation -> query.occurredTo == null || operation.occurredAt <= query.occurredTo }
                .filter { operation ->
                    text.isEmpty() || listOfNotNull(
                        operation.amount,
                        operation.currency,
                        operation.personOrEntity,
                        operation.concept,
                        operation.note,
                    ).any { it.lowercase().contains(text) }
                }
                .sortedByDescending { it.occurredAt }
                .toList()
        }
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

class ObserveUnassociatedComprobantesUseCase(
    private val repository: ComprobanteRepository,
) {
    operator fun invoke(): Flow<List<Comprobante>> =
        repository.observe().map { receipts ->
            receipts.filter { it.operationId == null }
        }
}

class ObserveOperationComprobantesUseCase(
    private val repository: ComprobanteRepository,
) {
    operator fun invoke(operationId: String): Flow<List<Comprobante>> =
        repository.observe().map { receipts ->
            receipts.filter { it.operationId == operationId }
        }
}

class AssociateComprobanteToOperationUseCase(
    private val operationRepository: OperationRepository,
    private val comprobanteRepository: ComprobanteRepository,
) {
    suspend operator fun invoke(comprobanteId: String, operationId: String): Comprobante {
        requireNotNull(operationRepository.get(operationId)) {
            "Operation not found: $operationId"
        }
        val comprobante = requireNotNull(comprobanteRepository.get(comprobanteId)) {
            "Comprobante not found: $comprobanteId"
        }
        return comprobante.copy(operationId = operationId).also {
            comprobanteRepository.update(it)
        }
    }
}

class UnassociateComprobanteUseCase(
    private val repository: ComprobanteRepository,
) {
    suspend operator fun invoke(comprobanteId: String): Comprobante {
        val comprobante = requireNotNull(repository.get(comprobanteId)) {
            "Comprobante not found: $comprobanteId"
        }
        return comprobante.copy(operationId = null).also {
            repository.update(it)
        }
    }
}

class FindDuplicateComprobantesUseCase(
    private val repository: ComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
) {
    suspend operator fun invoke(bytes: ByteArray): List<Comprobante> {
        if (bytes.isEmpty()) return emptyList()
        return repository.observe().first().filter { receipt ->
            fileStore.read(receipt.file)?.contentEquals(bytes) == true
        }
    }
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

class DeleteOperationWithHistoryUseCase(
    private val operationRepository: OperationRepository,
    private val comprobanteRepository: ComprobanteRepository,
    private val fileStore: ComprobanteFileStore,
    private val historyRepository: DeletedOperationHistoryRepository,
) {
    suspend operator fun invoke(operationId: String): DeletedOperationHistory? {
        val operation = operationRepository.get(operationId) ?: return null
        val receipts = comprobanteRepository.observe().first().filter { it.operationId == operationId }

        receipts.forEach { receipt ->
            comprobanteRepository.delete(receipt.id)
            fileStore.delete(receipt.file)
        }

        operationRepository.delete(operationId)

        return DeletedOperationHistory(
            date = operation.occurredAt,
            type = operation.type,
            amount = operation.amount,
            personOrEntity = operation.personOrEntity,
        ).also { historyRepository.save(it) }
    }
}

expect fun nowMillis(): Long

