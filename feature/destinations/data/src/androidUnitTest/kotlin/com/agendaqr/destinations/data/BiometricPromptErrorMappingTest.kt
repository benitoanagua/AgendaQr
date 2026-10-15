package com.agendaqr.destinations.data

import androidx.biometric.BiometricPrompt
import com.agendaqr.destinations.domain.BiometricAuthResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Contrato fail-closed de la puerta biométrica Android.
 *
 * El prompt real requiere hardware (NO VERIFICADO en JVM); aquí se fija la
 * parte verificable sin dispositivo:
 * - el mapeo de códigos `BiometricPrompt` al contrato compartido;
 * - que NINGÚN código de error produce `Success` (un resultado desconocido
 *   jamás desbloquea);
 * - que sin Activity host el intento es `Unavailable` (fail-closed).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BiometricPromptErrorMappingTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun user_cancellations_map_to_cancelled() {
        assertEquals(BiometricAuthResult.Cancelled, biometricPromptErrorToResult(BiometricPrompt.ERROR_NEGATIVE_BUTTON))
        assertEquals(BiometricAuthResult.Cancelled, biometricPromptErrorToResult(BiometricPrompt.ERROR_USER_CANCELED))
        assertEquals(BiometricAuthResult.Cancelled, biometricPromptErrorToResult(BiometricPrompt.ERROR_CANCELED))
    }

    @Test
    fun unusable_biometry_maps_to_unavailable_with_pin_fallback() {
        val unavailable = setOf(
            BiometricPrompt.ERROR_NO_BIOMETRICS,
            BiometricPrompt.ERROR_HW_NOT_PRESENT,
            BiometricPrompt.ERROR_HW_UNAVAILABLE,
            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED,
        )
        unavailable.forEach { code ->
            assertEquals(BiometricAuthResult.Unavailable, biometricPromptErrorToResult(code), "code=$code")
        }
    }

    @Test
    fun sensor_lockouts_map_to_non_retryable_failure() {
        val lockout = biometricPromptErrorToResult(BiometricPrompt.ERROR_LOCKOUT)
        val permanent = biometricPromptErrorToResult(BiometricPrompt.ERROR_LOCKOUT_PERMANENT)
        assertIs<BiometricAuthResult.Failed>(lockout)
        assertIs<BiometricAuthResult.Failed>(permanent)
        assertEquals(false, lockout.retryAllowed)
        assertEquals(false, permanent.retryAllowed)
    }

    @Test
    fun transient_and_unknown_errors_map_to_retryable_failure() {
        val transient = setOf(
            BiometricPrompt.ERROR_TIMEOUT,
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS,
            BiometricPrompt.ERROR_VENDOR,
            BiometricPrompt.ERROR_NO_SPACE,
            999, // código desconocido del fabricante
        )
        transient.forEach { code ->
            val result = biometricPromptErrorToResult(code)
            assertIs<BiometricAuthResult.Failed>(result, "code=$code")
            assertTrue(result.retryAllowed, "code=$code debe permitir reintento")
        }
    }

    @Test
    fun no_error_code_ever_unlocks() {
        // Fail-closed: ningún código de error (ni conocidos ni desconocidos)
        // puede producir Success. Solo onAuthenticationSucceeded desbloquea.
        for (code in -2..40) {
            assertTrue(
                biometricPromptErrorToResult(code) != BiometricAuthResult.Success,
                "code=$code no debe desbloquear",
            )
        }
    }

    @Test
    fun without_activity_host_authentication_is_unavailable() = runTest {
        // Sin Activity en primer plano no hay dueño de ciclo de vida válido
        // para el prompt: el intento es fail-closed, nunca un éxito.
        val saved = BiometricPromptHost.activity
        BiometricPromptHost.activity = null
        try {
            val result = platformBiometricGate().authenticate()
            assertEquals(BiometricAuthResult.Unavailable, result)
        } finally {
            @Suppress("UNCHECKED_CAST")
            BiometricPromptHost.activity = saved as? androidx.fragment.app.FragmentActivity
        }
    }
}
