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
    val errorMessage: String? = null,
    val confirmationMessage: String? = null,
)

class AuthViewModel(
    observe: ObserveAuthStateUseCase,
    private val signIn: SignInUseCase,
    private val signUp: SignUpUseCase,
    private val signOut: SignOutUseCase,
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
        _state.value = _state.value.copy(email = value, errorMessage = null, confirmationMessage = null)
    }

    fun setPassword(value: String) {
        _state.value = _state.value.copy(password = value, errorMessage = null, confirmationMessage = null)
    }

    fun submitSignIn() {
        val current = _state.value
        _state.value = current.copy(isSubmitting = true, errorMessage = null, confirmationMessage = null)
        viewModelScope.launch {
            runCatching { signIn(current.email, current.password) }
                .onFailure { error ->
                    _state.value = _state.value.copy(isSubmitting = false, errorMessage = error.message ?: "No se pudo iniciar sesión.")
                }
                .onSuccess {
                    _state.value = _state.value.copy(isSubmitting = false)
                }
        }
    }

    fun submitSignUp() {
        val current = _state.value
        _state.value = current.copy(isSubmitting = true, errorMessage = null, confirmationMessage = null)
        viewModelScope.launch {
            runCatching { signUp(current.email, current.password) }
                .onFailure { error ->
                    _state.value = _state.value.copy(isSubmitting = false, errorMessage = error.message ?: "No se pudo crear la cuenta.")
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
            runCatching { signOut() }
                .onFailure { error ->
                    _state.value = _state.value.copy(errorMessage = error.message ?: "No se pudo cerrar sesión.")
                }
        }
    }
}
