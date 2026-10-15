package com.agendaqr.destinations.data

import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.BiometricGate
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Puerta biométrica Android — implementación REAL con `androidx.biometric`.
 *
 * - [availability]: `BiometricManager.canAuthenticate(BIOMETRIC_WEAK)` con el
 *   contexto de aplicación fijado por el host. Se acepta clase 2+ (WEAK
 *   incluye STRONG): la biometría es un atajo de conveniencia y el PIN
 *   permanece como puerta fuerte; exigir solo clase 3 dejaría sin atajo a
 *   dispositivos con biometría de clase 2.
 * - [authenticate]: `BiometricPrompt` ligado a la [FragmentActivity] en
 *   primer plano (dueño válido del ciclo de vida), resuelto mediante un
 *   puente suspend. Sin Activity host → [BiometricAuthResult.Unavailable]
 *   (fail-closed). La cancelación de la corrutina cancela el prompt.
 *
 * Verificación: la lógica de mapeo de errores se prueba en JVM
 * (`BiometricPromptErrorMappingTest`); el prompt real requiere hardware
 * biométrico y se valida en dispositivo (docs/STATE.md, NO VERIFICADO).
 */
private object AndroidBiometricGate : BiometricGate {

    /** Copy del prompt (diálogo del sistema, capa data sin AppStrings). */
    private const val PROMPT_TITLE = "Desbloquear Agenda QR"
    private const val PROMPT_SUBTITLE = "Confirma que eres tú para continuar"
    private const val PROMPT_NEGATIVE = "Usar PIN"

    /** Un solo prompt en vuelo: los taps repetidos no apilan diálogos. */
    private val inFlight = AtomicBoolean(false)

    override suspend fun availability(): BiometricAvailability {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return BiometricAvailability.Unavailable
        return try {
            // El contexto de aplicación lo fija el host (MainActivity) en
            // BiometricContextHolder; sin él el estado no es determinable
            // (fail-closed: se conserva solo el PIN).
            val appContext = BiometricContextHolder.context ?: return BiometricAvailability.Unavailable
            val manager = appContext.getSystemService(BiometricManager::class.java) as? BiometricManager
                ?: return BiometricAvailability.Unavailable
            when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
                else -> BiometricAvailability.Unavailable
            }
        } catch (_: Exception) {
            BiometricAvailability.Unavailable
        }
    }

    override suspend fun authenticate(): BiometricAuthResult {
        // Ya hay un prompt activo: no se apila otro (el usuario puede usar
        // el PIN mientras tanto).
        if (!inFlight.compareAndSet(false, true)) return BiometricAuthResult.Cancelled
        try {
            return withContext(Dispatchers.Main) {
                val activity = BiometricPromptHost.activity
                    ?: return@withContext BiometricAuthResult.Unavailable
                // Re-chequeo defensivo en el instante del intento: si la
                // biometría dejó de estar disponible/inscrita, fail-closed.
                if (availability() != BiometricAvailability.Available) {
                    return@withContext BiometricAuthResult.Unavailable
                }
                suspendCancellableCoroutine<BiometricAuthResult> { continuation ->
                    val executor = ContextCompat.getMainExecutor(activity)
                    var prompt: BiometricPrompt? = null
                    prompt = BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                continuation.resumeWith(Result.success(BiometricAuthResult.Success))
                            }

                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                continuation.resumeWith(
                                    Result.success(biometricPromptErrorToResult(errorCode)),
                                )
                            }
                            // onAuthenticationFailed = intento individual fallido:
                            // BiometricPrompt sigue escuchando; NO se resuelve aquí.
                        },
                    )
                    val info = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(PROMPT_TITLE)
                        .setSubtitle(PROMPT_SUBTITLE)
                        .setNegativeButtonText(PROMPT_NEGATIVE)
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        // Confirmación explícita: necesario para rostro de
                        // clase 2 y consistente con "desbloqueo consciente".
                        .setConfirmationRequired(true)
                        .build()
                    continuation.invokeOnCancellation {
                        runCatching { prompt?.cancelAuthentication() }
                    }
                    prompt.authenticate(info)
                }
            }
        } finally {
            inFlight.set(false)
        }
    }
}

/**
 * Mapeo del código de error de `BiometricPrompt` al contrato compartido.
 * Función pura (constantes de compilación) → verificable en JVM sin hardware.
 */
internal fun biometricPromptErrorToResult(errorCode: Int): BiometricAuthResult = when (errorCode) {
    // El usuario eligió el botón negativo ("Usar PIN") o canceló el
    // diálogo: vuelve al PIN sin tratarlo como fallo biométrico.
    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
    BiometricPrompt.ERROR_USER_CANCELED,
    BiometricPrompt.ERROR_CANCELED,
    -> BiometricAuthResult.Cancelled
    // Sin biometría utilizable AHORA: el PIN es la vía (fallback visible).
    BiometricPrompt.ERROR_NO_BIOMETRICS,
    BiometricPrompt.ERROR_HW_NOT_PRESENT,
    BiometricPrompt.ERROR_HW_UNAVAILABLE,
    BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED,
    -> BiometricAuthResult.Unavailable
    // Bloqueo temporal del sensor: reintentar ya no sirve en esta tanda.
    BiometricPrompt.ERROR_LOCKOUT,
    BiometricPrompt.ERROR_LOCKOUT_PERMANENT,
    -> BiometricAuthResult.Failed(retryAllowed = false)
    // TIMEOUT/VENDOR/UNABLE_TO_PROCESS/NO_SPACE/desconocido: recuperable,
    // pero NUNCA desbloquea (el resultado solo permite fallback al PIN).
    else -> BiometricAuthResult.Failed(retryAllowed = true)
}

/** Contexto de aplicación para el chequeo de disponibilidad (lo fija el host). */
object BiometricContextHolder {
    var context: android.content.Context? = null
}

/**
 * Activity host del prompt (dueño del ciclo de vida exigido por
 * `BiometricPrompt`). La fija [com.agendaqr.android.MainActivity] en
 * `onCreate` y se limpia en `onDestroy` SOLO si sigue siendo esta instancia
 * (la recreación configura la nueva antes de destruir la vieja).
 */
object BiometricPromptHost {
    @Volatile
    var activity: FragmentActivity? = null

    fun release(owner: FragmentActivity) {
        if (activity === owner) activity = null
    }
}

actual fun platformBiometricGate(): BiometricGate = AndroidBiometricGate
