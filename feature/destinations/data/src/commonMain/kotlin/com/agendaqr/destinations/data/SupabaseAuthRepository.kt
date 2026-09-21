package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.AuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.AuthUser
import com.agendaqr.destinations.domain.SignUpResult
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SupabaseAuthRepository(
    private val client: io.github.jan.supabase.SupabaseClient = AgendaQrSupabase.client,
) : AuthRepository {

    override val state: Flow<AuthState> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    AuthState.SignedIn(
                        AuthUser(
                            id = user?.id.orEmpty(),
                            email = user?.email,
                        ),
                    )
                }
                is SessionStatus.NotAuthenticated,
                is SessionStatus.RefreshFailure -> AuthState.SignedOut
            }
        }

    override suspend fun signIn(email: String, password: String): AuthUser {
        require(email.isNotBlank()) { "Email is required" }
        require(password.isNotBlank()) { "Password is required" }

        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        return currentUser()
    }

    override suspend fun signUp(email: String, password: String): SignUpResult {
        require(email.isNotBlank()) { "Email is required" }
        require(password.isNotBlank()) { "Password is required" }

        val response = client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val user = response
        return if (user != null) {
            SignUpResult.ConfirmationRequired
        } else {
            SignUpResult.SignedIn(currentUser())
        }
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }

    private fun currentUser(): AuthUser {
        val user = client.auth.currentUserOrNull()
            ?: error("Authentication completed without an authenticated user")
        return AuthUser(
            id = user.id,
            email = user.email,
        )
    }
}
