package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.BiometricGate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LABiometryTypeNone
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

/**
 * Puerta biométrica iOS — implementación con `LocalAuthentication`.
 *
 * - [availability]: `LAContext.canEvaluatePolicy` (solo biometría, sin
 *  código del dispositivo); `biometryType` distingue sin-hardware de
 *  no-inscrita.
 * - [authenticate]: `evaluatePolicy` presentado por el sistema, resuelto
 *  con un puente suspend. La cancelación de la corrutina invalida el
 *  contexto (el sistema retira el diálogo).
 *
 * Verificación: compila contra los bindings reales de Kotlin/Native; el
 * runtime requiere simulador/dispositivo con biometría inscrita
 * (docs/STATE.md, NO VERIFICADO). El PIN es siempre la alternativa.
 *
 * Códigos `LAError` (LocalAuthentication/LAError.h, estables): se usan los
 * valores numéricos documentados porque el cinterop tipa `NSError.code`
 * como entero; cada constante lleva su nombre oficial en el comentario.
 */
@OptIn(ExperimentalForeignApi::class)
private object IosBiometricGate : BiometricGate {

    /** Copy del prompt (diálogo del sistema, capa data sin AppStrings). */
    private const val PROMPT_REASON = "Desbloquear tu agenda en este dispositivo"
    private const val PROMPT_FALLBACK = "Usar PIN"

    /** Un solo prompt en vuelo: los taps repetidos no apilan diálogos.
     * Guardia NO atómica a propósito: todo acceso ocurre dentro de
     * Dispatchers.Main (dispatcher serializado), sin carrera posible. */
    private var inFlight: Boolean = false

    override suspend fun availability(): BiometricAvailability {
        val context = LAContext()
        val can = runCatching {
            // error = null: sin puntero de error, la llamada solo devuelve
            // false cuando la política no puede evaluarse.
            context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
        }.getOrDefault(false)
        if (can) return BiometricAvailability.Available
        // biometryType es significativo tras canEvaluatePolicy: distingue
        // "sin hardware biométrico" de "hardware sin biometría inscrita".
        return try {
            if (context.biometryType == LABiometryTypeNone) {
                BiometricAvailability.NoHardware
            } else {
                BiometricAvailability.NotEnrolled
            }
        } catch (_: Exception) {
            BiometricAvailability.Unavailable
        }
    }

    override suspend fun authenticate(): BiometricAuthResult = withContext(Dispatchers.Main) {
        // Ya hay un prompt activo: no se apila otro.
        if (inFlight) return@withContext BiometricAuthResult.Cancelled
        inFlight = true
        try {
            val context = LAContext()
            context.localizedFallbackTitle = PROMPT_FALLBACK
            // Re-chequeo defensivo en el instante del intento.
            val can = runCatching {
                context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
            }.getOrDefault(false)
            if (!can) return@withContext BiometricAuthResult.Unavailable
            suspendCancellableCoroutine<BiometricAuthResult> { continuation ->
                try {
                    context.evaluatePolicy(
                        LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                        localizedReason = PROMPT_REASON,
                    ) { success, error ->
                        if (continuation.isActive) {
                            continuation.resumeWith(
                                Result.success(laErrorToResult(success, error)),
                            )
                        }
                    }
                } catch (failure: Throwable) {
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.success(BiometricAuthResult.Unavailable))
                    }
                }
                // La corrutina se canceló (p. ej. la app pasa a segundo
                // plano): retirar el diálogo del sistema.
                continuation.invokeOnCancellation {
                    runCatching { context.invalidate() }
                }
            }
        } finally {
            inFlight = false
        }
    }
}

/**
 * Mapeo del resultado de `evaluatePolicy` al contrato compartido.
 * Códigos LAError (LocalAuthentication/LAError.h):
 * 1=AuthenticationFailed, 2=UserCancel, 3=UserFallback, 4=SystemCancel,
 * 5=PasscodeNotSet, 6=BiometryNotAvailable, 7=BiometryNotEnrolled,
 * 8=BiometryLockout, 9=AppCancel, 10=InvalidContext.
 */
internal fun laErrorToResult(success: Boolean, error: NSError?): BiometricAuthResult {
    if (success) return BiometricAuthResult.Success
    return when (error?.code?.toLong() ?: 0L) {
        2L, 3L, 4L, 9L -> BiometricAuthResult.Cancelled
        // Sin biometría utilizable AHORA (o sin código del dispositivo):
        // el PIN es la vía (fallback visible).
        5L, 6L, 7L -> BiometricAuthResult.Unavailable
        // Bloqueo del sensor: reintentar ya no sirve en esta tanda.
        8L -> BiometricAuthResult.Failed(retryAllowed = false)
        // Intento fallido y cualquier resultado desconocido: NUNCA
        // desbloquea; se conserva el bloqueo con el PIN como salida.
        else -> BiometricAuthResult.Failed(retryAllowed = true)
    }
}

actual fun platformBiometricGate(): BiometricGate = IosBiometricGate
