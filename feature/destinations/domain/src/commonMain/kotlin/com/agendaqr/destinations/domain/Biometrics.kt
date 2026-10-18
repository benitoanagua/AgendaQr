package com.agendaqr.destinations.domain

/**
 * Biometría local (huella/rostro del dispositivo).
 *
 * El PIN sigue siendo el mecanismo principal y la alternativa obligatoria:
 * la biometría solo es un atajo de desbloqueo cuando el hardware existe,
 * está inscrito y el usuario la acepta. Nunca sustituye la protección del
 * perfil local ni cifra datos (los datos NO están cifrados con el PIN;
 * ver `LocalAccess.kt`).
 */
enum class BiometricAvailability {
    /** Hardware presente, inscrito y listo para autenticar. */
    Available,

    /** Sin hardware biométrico en el dispositivo. */
    NoHardware,

    /** Hardware presente pero sin biometría inscrita. */
    NotEnrolled,

    /** Plataforma sin implementación o estado no determinable aquí. */
    Unavailable,
}

/** Resultado de un intento biométrico (nunca simula éxito). */
sealed interface BiometricAuthResult {
    data object Success : BiometricAuthResult
    data object Cancelled : BiometricAuthResult
    data class Failed(val retryAllowed: Boolean = true) : BiometricAuthResult
    data object Unavailable : BiometricAuthResult
}

/**
 * Puerta biométrica de plataforma. Las implementaciones reales viven en
 * `androidMain` (`BiometricPrompt`) e `iosMain` (`LocalAuthentication`);
 * en JVM/tests se usa un doble determinista.
 */
interface BiometricGate {
    suspend fun availability(): BiometricAvailability
    suspend fun authenticate(): BiometricAuthResult
}

/** Doble determinista para tests y previews (nunca en producción). */
class StubBiometricGate(
    private val availability: BiometricAvailability = BiometricAvailability.Unavailable,
    private val result: BiometricAuthResult = BiometricAuthResult.Unavailable,
) : BiometricGate {
    override suspend fun availability(): BiometricAvailability = availability
    override suspend fun authenticate(): BiometricAuthResult =
        if (availability == BiometricAvailability.Available) result
        else BiometricAuthResult.Unavailable
}
