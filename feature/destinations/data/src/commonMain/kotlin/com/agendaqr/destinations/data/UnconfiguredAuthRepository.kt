package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.AuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.AuthUser
import com.agendaqr.destinations.domain.SignUpResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Auth repository for builds produced without Supabase configuration
 * (for example a local debug build without SUPABASE_URL /
 * SUPABASE_PUBLISHABLE_KEY available).
 *
 * A missing remote configuration must not crash the app while Compose is
 * composing the first frame: the session is reported as signed out so the
 * app starts normally, and authentication attempts fail with an explicit
 * configuration error that the auth screen already knows how to display.
 */
class UnconfiguredAuthRepository : AuthRepository {

    override val state: Flow<AuthState> = flowOf(AuthState.SignedOut)

    override suspend fun signIn(email: String, password: String): AuthUser =
        error("Supabase no está configurado en esta compilación; no se puede iniciar sesión.")

    override suspend fun signUp(email: String, password: String): SignUpResult =
        error("Supabase no está configurado en esta compilación; no se puede crear la cuenta.")

    override suspend fun signOut() = Unit
}
