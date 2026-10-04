package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface RemoteOperationRepository {
    suspend fun observe(): List<Operation>
    suspend fun save(operation: Operation)
    suspend fun update(operation: Operation)
    suspend fun delete(id: String)
    /** Lectura puntual; por defecto filtra el snapshot. */
    suspend fun get(id: String): Operation? = observe().firstOrNull { it.id == id }
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
        contextId = contextId,
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
        contextId = contextId,
    )
}

@Serializable
internal data class OperationRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    val type: String,
    @SerialName("occurred_at") val occurredAt: Instant,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("updated_at") val updatedAt: Instant? = null,
    /** `numeric(20,6)` in SQL: PostgREST returns a JSON number on reads. */
    @Serializable(with = NumericAsStringSerializer::class) val amount: String? = null,
    val currency: String? = null,
    @SerialName("person_or_entity") val personOrEntity: String? = null,
    @SerialName("destination_id") val destinationId: String? = null,
    val concept: String? = null,
    val note: String? = null,
    @SerialName("context_id") val contextId: String? = null,
)

/**
 * The SQL schema stores `amount` as `numeric(20,6)`, while the domain carries
 * the user's original text. PostgREST serializes numeric as a JSON NUMBER
 * (e.g. `180.000000`), which plain String decoding rejects — every drain that
 * read an operation with an amount failed and the mutation stayed in retry
 * forever. This serializer accepts both shapes: on decode it canonicalizes
 * pure numeric text (trailing zeros trimmed: "180.000000" -> "180"), and on
 * encode it always emits a string, which PostgREST accepts for numeric.
 */
object NumericAsStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NumericAsString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: throw IllegalStateException("NumericAsStringSerializer requires JSON input")
        return canonicalize(jsonDecoder.decodeJsonElement())
    }

    private fun canonicalize(element: kotlinx.serialization.json.JsonElement): String {
        require(element is kotlinx.serialization.json.JsonPrimitive) {
            "Expected a JSON primitive for amount, got $element"
        }
        val text = element.content
        if (!element.isString && text.contains('.') && text.none { it == 'e' || it == 'E' }) {
            val trimmed = text.trimEnd('0').trimEnd('.')
            return trimmed.ifEmpty { "0" }
        }
        return text
    }
}

fun createRemoteOperationRepository(): RemoteOperationRepository =
    SupabaseOperationRepository()
