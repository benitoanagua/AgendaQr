package com.agendaqr.destinations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignUpResult
import com.agendaqr.destinations.domain.SignUpUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: UserFacingError? = null,
    /** Flujo que produjo el error, para cablear REINTENTAR al mismo paso. */
    val errorFlow: ErrorFlow? = null,
    val confirmationMessage: String? = null,
)

class AuthViewModel(
    observe: ObserveAuthStateUseCase,
    private val signIn: SignInUseCase,
    private val signUp: SignUpUseCase,
    // Named distinctly: a `signOut` property is shadowed by the member
    // function below, making `runCatching { signOut() }` recurse into
    // itself until the main thread ANRs (observed on device).
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observe().collect { authState ->
                _state.value = _state.value.copy(authState = authState)
            }
        }
    }

    fun setEmail(value: String) {
        _state.value = _state.value.copy(email = value, errorMessage = null, errorFlow = null, confirmationMessage = null)
    }

    fun setPassword(value: String) {
        _state.value = _state.value.copy(password = value, errorMessage = null, errorFlow = null, confirmationMessage = null)
    }

    fun submitSignIn() {
        val current = _state.value
        _state.value = current.copy(isSubmitting = true, errorMessage = null, errorFlow = null, confirmationMessage = null)
        viewModelScope.launch {
            runCatching { signIn(current.email, current.password) }
                .onFailure { error ->
                    // T5: el error de auth muestra qué/data/acción (spec §10);
                    // nunca el `error.message` de Supabase (inglés/técnico).
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = userFacingError(error, ErrorFlow.SignIn),
                        errorFlow = ErrorFlow.SignIn,
                    )
                }
                .onSuccess {
                    _state.value = _state.value.copy(isSubmitting = false)
                }
        }
    }

    fun submitSignUp() {
        val current = _state.value
        _state.value = current.copy(isSubmitting = true, errorMessage = null, errorFlow = null, confirmationMessage = null)
        viewModelScope.launch {
            runCatching { signUp(current.email, current.password) }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = userFacingError(error, ErrorFlow.SignUp),
                        errorFlow = ErrorFlow.SignUp,
                    )
                }
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        confirmationMessage = if (result is SignUpResult.ConfirmationRequired) {
                            "Revisa tu correo para confirmar tu cuenta."
                        } else {
                            null
                        },
                    )
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching { signOutUseCase() }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        errorMessage = userFacingError(error, ErrorFlow.SignOut),
                        errorFlow = ErrorFlow.SignOut,
                    )
                }
        }
    }

    /** Descarta el mensaje de error (acción del banner / nuevo intento). */
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null, errorFlow = null)
    }
}
