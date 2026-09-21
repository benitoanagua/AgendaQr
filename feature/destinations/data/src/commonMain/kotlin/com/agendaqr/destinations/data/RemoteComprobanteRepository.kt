package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ReceiptProvenance
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class RemoteComprobanteRecord(
    val comprobante: Comprobante,
    val remoteFilePath: String,
)

interface RemoteComprobanteRepository {
    suspend fun observe(): List<RemoteComprobanteRecord>
    suspend fun save(comprobante: Comprobante, bytes: ByteArray)
    suspend fun update(comprobante: Comprobante)
    suspend fun delete(record: RemoteComprobanteRecord)
}

class SupabaseComprobanteRepository(
    private val client: SupabaseClient = AgendaQrSupabase.client,
) : RemoteComprobanteRepository {

    private val bucket get() = client.storage.from("comprobantes")

    override suspend fun observe(): List<RemoteComprobanteRecord> =
        client.from("comprobantes")
            .select()
            .decodeList<ComprobanteRow>()
            .map { it.toRecord() }

    override suspend fun save(comprobante: Comprobante, bytes: ByteArray) {
        val remotePath = remotePath(comprobante.id, extensionFromFile(comprobante.file))
        bucket.upload(remotePath, bytes) { upsert = true }
        runCatching {
            client.from("comprobantes").upsert(comprobante.toRow(remotePath))
        }.onFailure {
            runCatching { bucket.delete(remotePath) }
            throw it
        }
    }

    override suspend fun update(comprobante: Comprobante) {
        val current = findRemote(comprobante.id)
        val remotePath = current?.remoteFilePath
            ?: remotePath(comprobante.id, extensionFromFile(comprobante.file))
        client.from("comprobantes").update(comprobante.toRow(remotePath)) {
            filter { eq("id", comprobante.id) }
        }
    }

    override suspend fun delete(record: RemoteComprobanteRecord) {
        bucket.delete(record.remoteFilePath)
        client.from("comprobantes").delete {
            filter { eq("id", record.comprobante.id) }
        }
    }

    private suspend fun findRemote(id: String): RemoteComprobanteRecord? =
        observe().firstOrNull { it.comprobante.id == id }

    private fun currentUserId(): String =
        client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for receipt synchronization")

    private fun remotePath(id: String, extension: String): String =
        currentUserId() + "/" + id + "." + extension

    private fun extensionFromFile(file: String): String =
        file.substringAfterLast('.', "bin").ifBlank { "bin" }

    private fun Comprobante.toRow(remotePath: String) = ComprobanteRow(
        id = id,
        userId = currentUserId(),
        filePath = remotePath,
        mimeType = "application/octet-stream",
        extension = extensionFromFile(file),
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        provenance = provenance?.name,
        operationId = operationId,
    )

    private fun ComprobanteRow.toRecord() = RemoteComprobanteRecord(
        comprobante = Comprobante(
            id = id,
            file = filePath,
            createdAt = createdAt.toEpochMilliseconds(),
            provenance = provenance?.let { ReceiptProvenance.valueOf(it) },
            operationId = operationId,
        ),
        remoteFilePath = filePath,
    )
}

@Serializable
private data class ComprobanteRow(
    val id: String,
    val userId: String,
    val filePath: String,
    val mimeType: String? = null,
    val extension: String? = null,
    val createdAt: Instant,
    val provenance: String? = null,
    val operationId: String? = null,
)

fun createRemoteComprobanteRepository(): RemoteComprobanteRepository =
    SupabaseComprobanteRepository()
