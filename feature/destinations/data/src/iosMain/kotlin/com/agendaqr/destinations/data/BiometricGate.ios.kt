package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.BiometricGate

/**
 * Puerta biométrica iOS: PENDIENTE de implementación nativa con Xcode
 * (`LocalAuthentication`: `LAContext.canEvaluatePolicy` +
 * `evaluatePolicy`). Hasta entonces devuelve [BiometricAvailability.Unavailable]
 * de forma fail-closed: el PIN es la única vía de desbloqueo.
 */
private object IosBiometricGate : BiometricGate {
    override suspend fun availability(): BiometricAvailability = BiometricAvailability.Unavailable
    override suspend fun authenticate(): BiometricAuthResult = BiometricAuthResult.Unavailable
}

actual fun platformBiometricGate(): BiometricGate = IosBiometricGate
