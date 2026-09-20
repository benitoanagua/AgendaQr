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
