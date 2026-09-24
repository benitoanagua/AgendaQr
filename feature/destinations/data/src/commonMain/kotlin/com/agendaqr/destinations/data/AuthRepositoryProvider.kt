package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.AuthRepository

fun createAuthRepository(): AuthRepository =
    if (supabaseConfig().url.isBlank()) {
        UnconfiguredAuthRepository()
    } else {
        SupabaseAuthRepository()
    }
