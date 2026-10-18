package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.AuthUser
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import com.agendaqr.destinations.domain.SignUpResult
import com.agendaqr.destinations.domain.SignUpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regresión del ANR de "Cerrar sesión": dentro de AuthViewModel, la propiedad
 * `signOut` (SignOutUseCase) quedaba eclipsada por la función miembro
 * `signOut()`, así que `runCatching { signOut() }` se llamaba a sí misma
 * infinitamente en el hilo principal hasta disparar un ANR en el emulador.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelSignOutTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signOut_invokes_the_use_case_once_instead_of_recurring() = runTest(dispatcher.scheduler) {
        val repository = RecordingAuthRepository()
        val viewModel = AuthViewModel(
            observe = ObserveAuthStateUseCase(repository),
            signIn = SignInUseCase(repository),
            signUp = SignUpUseCase(repository),
            signOutUseCase = SignOutUseCase(repository),
        )

        viewModel.signOut()
        advanceUntilIdle()

        assertEquals(1, repository.signOutCalls)
        assertEquals(AuthState.SignedOut, repository.stateFlow.value)
    }

    private class RecordingAuthRepository : AuthRepository {
        val stateFlow = MutableStateFlow<AuthState>(AuthState.SignedIn(AuthUser("user-1", "a@b.test")))
        var signOutCalls = 0
        override val state: Flow<AuthState> = stateFlow
        override suspend fun signIn(email: String, password: String): AuthUser =
            AuthUser("user-1", email)
        override suspend fun signUp(email: String, password: String): SignUpResult =
            SignUpResult.SignedIn(AuthUser("user-1", email))
        override suspend fun signOut() {
            signOutCalls++
            stateFlow.value = AuthState.SignedOut
        }
    }
}
