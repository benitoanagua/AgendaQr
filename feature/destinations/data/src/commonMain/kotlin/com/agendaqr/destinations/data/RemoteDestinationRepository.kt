package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.QrAsset
import io.github.jan.supabase.SupabaseClient
import kotlinx.serialization.Serializable
import kotlin.time.Instant

interface RemoteDestinationRepository {
    suspend fun observe(): List<Destination>
    suspend fun save(destination: Destination)
    suspend fun update(destination: Destination)
    suspend fun delete(id: String)
}

class SupabaseDestinationRepository(
    private val client: SupabaseClient = AgendaQrSupabase.client,
) : RemoteDestinationRepository {

    override suspend fun observe(): List<Destination> =
        client.from("destinations")
            .select()
            .decodeList<DestinationRow>()
            .map { it.toDomain() }

    override suspend fun save(destination: Destination) {
        client.from("destinations").upsert(destination.toRow())
    }

    override suspend fun update(destination: Destination) {
        client.from("destinations").update(destination.toRow()) {
            filter { eq("id", destination.id) }
        }
    }

    override suspend fun delete(id: String) {
        client.from("destinations").delete {
            filter { eq("id", id) }
        }
    }

    private fun currentUserId(): String =
        client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for destination synchronization")

    private fun Destination.toRow() = DestinationRow(
        id = id,
        userId = currentUserId(),
        name = name,
        qrRawContent = qr.encoded,
        qrKind = "UNKNOWN",
        category = category,
        note = note,
        favorite = favorite,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    )

    private fun DestinationRow.toDomain() = Destination(
        id = id,
        name = name,
        qr = QrAsset(encoded = qrRawContent),
        category = category,
        note = note,
        favorite = favorite,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds(),
    )
}

@Serializable
private data class DestinationRow(
    val id: String,
    val userId: String,
    val name: String,
    val qrRawContent: String,
    val qrKind: String,
    val category: String? = null,
    val note: String? = null,
    val favorite: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun createRemoteDestinationRepository(): RemoteDestinationRepository =
    SupabaseDestinationRepository()
