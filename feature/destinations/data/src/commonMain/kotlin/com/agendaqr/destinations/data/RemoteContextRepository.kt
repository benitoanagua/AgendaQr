package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface RemoteContextRepository {
    suspend fun observe(): List<Context>
    suspend fun save(context: Context)
    suspend fun update(context: Context)
    suspend fun delete(id: String)
}

class SupabaseContextRepository(
    private val client: SupabaseClient = AgendaQrSupabase.client,
) : RemoteContextRepository {
    override suspend fun observe(): List<Context> =
        client.from("contexts").select().decodeList<ContextRow>().map { it.toDomain() }

    override suspend fun save(context: Context) {
        client.from("contexts").upsert(context.toRow())
    }

    override suspend fun update(context: Context) {
        client.from("contexts").update(context.toRow()) {
            filter { eq("id", context.id) }
        }
    }

    override suspend fun delete(id: String) {
        client.from("contexts").delete { filter { eq("id", id) } }
    }

    private fun currentUserId(): String =
        client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for context synchronization")

    private fun Context.toRow() = ContextRow(
        id = id,
        userId = currentUserId(),
        name = name,
        note = note,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    )

    private fun ContextRow.toDomain() = Context(
        id = id,
        name = name,
        note = note,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds(),
    )
}

@Serializable
private data class ContextRow(
    val id: String,
    val userId: String,
    val name: String,
    val note: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun createRemoteContextRepository(): RemoteContextRepository = SupabaseContextRepository()
