package com.agendaqr.destinations.data

data class SupabaseConfig(
    val url: String,
    val publishableKey: String,
)

expect fun supabaseConfig(): SupabaseConfig
