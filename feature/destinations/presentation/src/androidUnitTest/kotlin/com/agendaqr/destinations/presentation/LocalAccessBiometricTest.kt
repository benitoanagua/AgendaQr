package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.BiometricAuthResult
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.LocalProfile
import com.agendaqr.destinations.domain.LocalProtectionRepository
import com.agendaqr.destinations.domain.LocalProtectionStore
import com.agendaqr.destinations.domain.PinHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private object BiometricTestHasher : PinHasher {
    override fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        val out = ByteArray(keyLengthBytes)
        for (index in out.indices) {
            out[index] = (pin[index % pin.size].code + salt[index % salt.size]).toByte()
        }
        return out
    }
}

private class BiometricTestStore : LocalProtectionStore {
    var credential: String? = null
    override fun readCredential(): String? = credential
    override fun writeCredential(value: String) { credential = value }
    override fun clearCredential() { credential = null }
}

/**
 * Biometría como atajo opcional: el PIN sigue siendo la vía principal.
 * Solo el éxito biométrico desbloquea; cualquier otro resultado conserva
 * el bloqueo (fail-closed) y muestra el fallback al PIN.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalAccessBiometricTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var protection: LocalProtectionRepository
    private lateinit var viewModel: LocalAccessViewModel

    @Before
    fun setUp() {
        protection = LocalProtectionRepository(
            store = BiometricTestStore(),
            hasher = BiometricTestHasher,
            randomBytes = { size -> ByteArray(size) { 1 } },
            now = { 1_000L },
        )
        viewModel = LocalAccessViewModel(
            LocalProfile(id = "profile-test", createdAtEpochMillis = 1L),
            protection,
        )
    }

    @Test
    fun biometric_success_unlocks_without_pin() {
        viewModel.setBiometricAvailability(BiometricAvailability.Available)
        viewModel.onBiometricResult(BiometricAuthResult.Success)
        assertEquals(LocalAccessMode.Unlocked, viewModel.state.value.mode)
    }

    @Test
    fun biometric_failure_keeps_lock_and_shows_pin_fallback() {
        // Con PIN configurado el arranque es Locked.
        viewModel.onPinChanged("1234")
        viewModel.onConfirmationChanged("1234")
        viewModel.submitOnboarding()
        viewModel.lock()
        assertEquals(LocalAccessMode.Locked, viewModel.state.value.mode)

        viewModel.onBiometricResult(BiometricAuthResult.Failed())
        assertEquals(LocalAccessMode.Locked, viewModel.state.value.mode)
        assertTrue(viewModel.state.value.biometricFallbackVisible)

        // El PIN sigue desbloqueando después del fallo biométrico.
        viewModel.onPinChanged("1234")
        assertFalse(viewModel.state.value.biometricFallbackVisible)
        viewModel.submitUnlock()
        assertEquals(LocalAccessMode.Unlocked, viewModel.state.value.mode)
    }

    @Test
    fun biometric_cancelled_keeps_state_silently() {
        viewModel.onBiometricResult(BiometricAuthResult.Cancelled)
        assertEquals(LocalAccessMode.Onboarding, viewModel.state.value.mode)
        assertFalse(viewModel.state.value.biometricFallbackVisible)
    }

    @Test
    fun unavailable_gate_never_unlocks() {
        viewModel.setBiometricAvailability(BiometricAvailability.NoHardware)
        viewModel.onBiometricResult(BiometricAuthResult.Unavailable)
        assertEquals(LocalAccessMode.Onboarding, viewModel.state.value.mode)
    }

    @Test
    fun lock_preserves_biometric_availability() {
        viewModel.setBiometricAvailability(BiometricAvailability.Available)
        viewModel.lock()
        assertEquals(BiometricAvailability.Available, viewModel.state.value.biometricAvailability)
    }

    @Test
    fun lock_screen_offers_biometric_only_when_available() {
        val lockedAvailable = LocalAccessUiState(
            mode = LocalAccessMode.Locked,
            biometricAvailability = BiometricAvailability.Available,
        )
        compose.setContent {
            AmTheme {
                LocalLockScreen(state = lockedAvailable, onPinChanged = {}, onSubmit = {}, onBiometric = {})
            }
        }
        compose.onNodeWithText(AppStrings.UsarBiometria).assertIsDisplayed()
        compose.onNodeWithText(AppStrings.Desbloquear).assertIsDisplayed()
    }

    @Test
    fun lock_screen_hides_biometric_when_unavailable_and_offers_demo() {
        var demoTapped = false
        compose.setContent {
            AmTheme {
                LocalLockScreen(
                    state = LocalAccessUiState(mode = LocalAccessMode.Locked),
                    onPinChanged = {},
                    onSubmit = {},
                    onExploreDemo = { demoTapped = true },
                )
            }
        }
        compose.onNodeWithText(AppStrings.UsarBiometria).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.ExplorarDemostracion).assertIsDisplayed()
        compose.onNodeWithText(AppStrings.ExplorarDemostracion).performClick()
        assertTrue(demoTapped)
    }

    @Test
    fun onboarding_offers_demo_without_pin() {
        compose.setContent {
            AmTheme {
                LocalOnboardingScreen(
                    state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
                    onPinChanged = {},
                    onConfirmationChanged = {},
                    onSubmit = {},
                    onExploreDemo = {},
                )
            }
        }
        compose.onNodeWithText(AppStrings.ConfiguraTuPin).assertIsDisplayed()
        // La entrada a la demostración sigue accesible sin PIN; con los campos
        // ya agrupados en tarjeta, en el viewport de prueba queda bajo el
        // pliegue, así que se desplaza hasta ella antes de exigir visibilidad.
        compose.onNodeWithText(AppStrings.ExplorarDemostracion)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
