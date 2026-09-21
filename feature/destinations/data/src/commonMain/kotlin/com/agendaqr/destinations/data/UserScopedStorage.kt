package com.agendaqr.destinations.data

import io.github.jan.supabase.auth.auth

fun userScopedKey(prefix: String): String =
    prefix + "." + (AgendaQrSupabase.client.auth.currentUserOrNull()?.id
        ?: error("Authentication required"))
