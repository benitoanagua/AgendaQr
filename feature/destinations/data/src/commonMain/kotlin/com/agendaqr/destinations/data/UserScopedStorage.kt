package com.agendaqr.destinations.data

fun userScopedKey(prefix: String): String =
    prefix + "." + (AgendaQrSupabase.client.auth.currentUserOrNull()?.id
        ?: error("Authentication required"))
