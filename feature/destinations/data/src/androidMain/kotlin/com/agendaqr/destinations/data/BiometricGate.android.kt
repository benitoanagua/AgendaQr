package com.agendaqr.destinations.data

import android.hardware.biometrics.BiometricManager
import android.os.Build
import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.BiometricGate

/**
 * Puerta biométrica Android (framework, sin dependencias extra).
 *
 * - [availability]: real, vía `BiometricManager.canAuthenticate(BIOMETRIC_STRONG)`.
 * - [authenticate]: PENDIENTE de cablear el `BiometricPrompt` ligado a la
 *   Activity en primer plano + verificación en dispositivo físico. Devuelve
 *   [BiometricAuthResult.Unavailable] de forma fail-closed: la UI conserva
 *   el PIN como única vía de desbloqueo hasta entonces.
 */
private object AndroidBiometricGate : BiometricGate {
    override suspend fun availability(): BiometricAvailability {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return BiometricAvailability.Unavailable
        return try {
            // El contexto de aplicación lo fija el host (MainActivity) en
            // BiometricContextHolder; sin él el estado no es determinable
            // (fail-closed: se conserva solo el PIN).
            val appContext = BiometricContextHolder.context ?: return BiometricAvailability.Unavailable
            val manager = appContext.getSystemService(BiometricManager::class.java) as? BiometricManager
                ?: return BiometricAvailability.Unavailable
            when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
                else -> BiometricAvailability.Unavailable
            }
        } catch (_: Exception) {
            BiometricAvailability.Unavailable
        }
    }

    override suspend fun authenticate(): BiometricAuthResult =
        // Fail-closed hasta cablear BiometricPrompt con la Activity.
        BiometricAuthResult.Unavailable
}

/** Contexto de aplicación para el chequeo de disponibilidad (lo fija el host). */
object BiometricContextHolder {
    var context: android.content.Context? = null
}

actual fun platformBiometricGate(): BiometricGate = AndroidBiometricGate
