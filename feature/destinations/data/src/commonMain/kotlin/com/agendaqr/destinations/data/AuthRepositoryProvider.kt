package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.AuthRepository

fun createAuthRepository(): AuthRepository = SupabaseAuthRepository()
