package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface RemoteOperationRepository {
    suspend fun observe(): List<Operation>
    suspend fun save(operation: Operation)
    suspend fun update(operation: Operation)
    suspend fun delete(id: String)
}

class SupabaseOperationRepository(
    private val client: SupabaseClient = AgendaQrSupabase.client,
) : RemoteOperationRepository {

    override suspend fun observe(): List<Operation> =
        client.from("operations")
            .select()
            .decodeList<OperationRow>()
            .map { it.toDomain() }

    override suspend fun save(operation: Operation) {
        client.from("operations").upsert(operation.toRow())
    }

    override suspend fun update(operation: Operation) {
        client.from("operations").update(operation.toRow()) {
            filter { eq("id", operation.id) }
        }
    }

    override suspend fun delete(id: String) {
        client.from("operations").delete {
            filter { eq("id", id) }
        }
    }

    private fun currentUserId(): String =
        client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for operation synchronization")

    private fun Operation.toRow() = OperationRow(
        id = id,
        userId = currentUserId(),
        type = type.name,
        occurredAt = Instant.fromEpochMilliseconds(occurredAt),
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt),
        amount = amount,
        currency = currency,
        personOrEntity = personOrEntity,
        destinationId = destinationId,
        concept = concept,
        note = note,
    )

    private fun OperationRow.toDomain() = Operation(
        id = id,
        type = OperationType.valueOf(type),
        occurredAt = occurredAt.toEpochMilliseconds(),
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = (updatedAt ?: createdAt).toEpochMilliseconds(),
        amount = amount,
        currency = currency,
        personOrEntity = personOrEntity,
        destinationId = destinationId,
        concept = concept,
        note = note,
    )
}

@Serializable
private data class OperationRow(
    val id: String,
    val userId: String,
    val type: String,
    val occurredAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val amount: String? = null,
    val currency: String? = null,
    val personOrEntity: String? = null,
    val destinationId: String? = null,
    val concept: String? = null,
    val note: String? = null,
)

fun createRemoteOperationRepository(): RemoteOperationRepository =
    SupabaseOperationRepository()
