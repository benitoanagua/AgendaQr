package com.agendaqr.destinations.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object AgendaQrSupabase {
    val client by lazy {
        val config = supabaseConfig()
        require(config.url.isNotBlank()) { "SUPABASE_URL is not configured" }
        require(config.publishableKey.isNotBlank()) { "SUPABASE_PUBLISHABLE_KEY is not configured" }

        createSupabaseClient(
            supabaseUrl = config.url,
            supabaseKey = config.publishableKey,
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
