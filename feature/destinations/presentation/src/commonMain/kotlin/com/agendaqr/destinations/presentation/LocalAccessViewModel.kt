package com.agendaqr.destinations.presentation

import androidx.lifecycle.ViewModel
import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.LocalProfile
import com.agendaqr.destinations.domain.LocalProtectionRepository
import com.agendaqr.destinations.domain.PinPolicy
import com.agendaqr.destinations.domain.PinRejectionReason
import com.agendaqr.destinations.domain.PinUpdate
import com.agendaqr.destinations.domain.PinVerification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Qué superficie de acceso local debe mostrarse.
 *
 * - [Onboarding]: primer arranque o protección sin configurar → crear PIN.
 * - [Locked]: hay PIN configurado → desbloquear.
 * - [Unlocked]: acceso concedido → app local completa.
 */
enum class LocalAccessMode { Onboarding, Locked, Unlocked }

/** Error de acceso traducido a copy por la pantalla (sin literales aquí). */
enum class LocalAccessError {
    TooShort,
    TooLong,
    NonDigits,
    Mismatch,
    Incorrect,
    LockedOut,
    NotConfigured,
}

data class LocalAccessUiState(
    val mode: LocalAccessMode,
    val pin: String = "",
    val confirmation: String = "",
    val error: LocalAccessError? = null,
    val remainingAttempts: Int = 0,
    /**
     * Disponibilidad biométrica del dispositivo (la fija el arranque tras
     * consultar la puerta de plataforma). El PIN sigue siendo la vía
     * principal: la biometría es solo un atajo opcional.
     */
    val biometricAvailability: BiometricAvailability = BiometricAvailability.Unavailable,
    /** El último intento biométrico falló: se muestra el fallback al PIN. */
    val biometricFallbackVisible: Boolean = false,
    /** Bloqueos acumulados: clave del lanzamiento biométrico automático
     * (U-20, D11) — una vez por bloqueo, no por recomposición. */
    val lockSequence: Int = 0,
)

/**
 * Estado de acceso local. NO depende de Supabase: la decisión de entrada se
 * basa en el perfil local y su protección. Mantiene el PIN solo durante la
 * composición de la pantalla y lo borra al desbloquear o bloquear.
 */
class LocalAccessViewModel(
    val profile: LocalProfile,
    private val protection: LocalProtectionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LocalAccessUiState(
            mode = if (protection.isConfigured()) LocalAccessMode.Locked else LocalAccessMode.Onboarding,
        ),
    )
    val state: StateFlow<LocalAccessUiState> = _state.asStateFlow()

    fun onPinChanged(value: String) {
        _state.update { it.copy(pin = sanitize(value), error = null, biometricFallbackVisible = false) }
    }

    fun onConfirmationChanged(value: String) {
        _state.update { it.copy(confirmation = sanitize(value), error = null) }
    }

    /** Primer arranque: crea el PIN (validación + confirmación). */
    fun submitOnboarding() {
        val current = _state.value
        when (val result = protection.configure(current.pin.toCharArray(), current.confirmation.toCharArray())) {
            PinUpdate.Success -> unlock()
            is PinUpdate.Rejected -> _state.update { it.copy(error = result.reason.toAccessError()) }
        }
    }

    /** Desbloqueo con PIN. */
    fun submitUnlock() {
        when (val result = protection.verify(_state.value.pin.toCharArray())) {
            PinVerification.Success -> unlock()
            is PinVerification.Incorrect -> _state.update {
                it.copy(error = LocalAccessError.Incorrect, remainingAttempts = result.remainingAttempts)
            }
            is PinVerification.LockedOut -> _state.update {
                it.copy(error = LocalAccessError.LockedOut, remainingAttempts = 0)
            }
            PinVerification.NotConfigured -> _state.update {
                LocalAccessUiState(mode = LocalAccessMode.Onboarding)
            }
        }
    }

    /**
     * Bloquea la app (manual o al pasar a segundo plano, según la política).
     * Sin PIN configurado no hay nada que bloquear: se mantiene el onboarding.
     */
    fun lock() {
        val biometric = _state.value.biometricAvailability
        _state.value = LocalAccessUiState(
            mode = if (protection.isConfigured()) LocalAccessMode.Locked else LocalAccessMode.Onboarding,
            biometricAvailability = biometric,
            lockSequence = _state.value.lockSequence + 1,
        )
    }

    /** Disponibilidad consultada a la puerta de plataforma (arranque). */
    fun setBiometricAvailability(availability: BiometricAvailability) {
        _state.update { it.copy(biometricAvailability = availability) }
    }

    /**
     * Resultado del prompt biométrico. Solo [BiometricAuthResult.Success]
     * desbloquea; cualquier otro resultado conserva el bloqueo y muestra
     * el fallback al PIN (fail-closed: nunca se omite la protección).
     */
    fun onBiometricResult(result: BiometricAuthResult) {
        when (result) {
            BiometricAuthResult.Success -> unlock()
            BiometricAuthResult.Cancelled -> Unit
            is BiometricAuthResult.Failed,
            BiometricAuthResult.Unavailable,
            -> _state.update { it.copy(biometricFallbackVisible = true) }
        }
    }

    private fun unlock() {
        // El PIN nunca sobrevive al desbloqueo en memoria.
        _state.value = LocalAccessUiState(mode = LocalAccessMode.Unlocked)
    }

    private fun sanitize(value: String): String =
        value.filter { it.isDigit() }.take(PinPolicy.MAX_LENGTH)

    private fun PinRejectionReason.toAccessError(): LocalAccessError = when (this) {
        PinRejectionReason.TooShort -> LocalAccessError.TooShort
        PinRejectionReason.TooLong -> LocalAccessError.TooLong
        PinRejectionReason.NonDigits -> LocalAccessError.NonDigits
        PinRejectionReason.Mismatch -> LocalAccessError.Mismatch
        PinRejectionReason.IncorrectCurrent -> LocalAccessError.Incorrect
        PinRejectionReason.LockedOut -> LocalAccessError.LockedOut
        PinRejectionReason.NotConfigured -> LocalAccessError.NotConfigured
    }
}
