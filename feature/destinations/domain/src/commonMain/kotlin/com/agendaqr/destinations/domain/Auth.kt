package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.Flow

data class AuthUser(
    val id: String,
    val email: String?,
)

sealed interface AuthState {
    data object Loading : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val user: AuthUser) : AuthState
}

sealed interface SignUpResult {
    data class SignedIn(val user: AuthUser) : SignUpResult
    data object ConfirmationRequired : SignUpResult
}

interface AuthRepository {
    val state: Flow<AuthState>

    suspend fun signIn(email: String, password: String): AuthUser
    suspend fun signUp(email: String, password: String): SignUpResult
    suspend fun signOut()
}

class ObserveAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthState> = repository.state
}

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthUser =
        repository.signIn(email.trim(), password)
}

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): SignUpResult =
        repository.signUp(email.trim(), password)
}

class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}
