package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore
import io.github.jan.supabase.auth.auth

private object IosComprobanteFileStore : ComprobanteFileStore {
    // In-memory fallback for iOS until Foundation file APIs are re-validated with Xcode.
    // Preserves user-scoped logic and prevents compile blocking Android release gate.
    private val memory = mutableMapOf<String, ByteArray>()
    private fun key(id: String, extension: String): String {
        val userId = AgendaQrSupabase.client.auth.currentUserOrNull()?.id
            ?: error("Authentication required for receipt storage")
        return "comprobantes/$userId/$id.$extension"
    }
    override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
        require(bytes.isNotEmpty()) { "Receipt file cannot be empty." }
        val k = key(id, extension)
        memory[k] = bytes
        return "memory://$k"
    }
    override suspend fun read(file: String): ByteArray? {
        val k = file.removePrefix("memory://")
        return memory[k]
    }
    override suspend fun delete(file: String) {
        val k = file.removePrefix("memory://")
        memory.remove(k)
    }
}

actual fun platformComprobanteFileStore(): ComprobanteFileStore =
    IosComprobanteFileStore
