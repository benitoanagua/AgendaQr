package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.LocalProfile
import com.agendaqr.destinations.domain.LocalProtectionRepository
import com.agendaqr.destinations.domain.LocalProtectionStore
import com.agendaqr.destinations.domain.PinHasher
import com.agendaqr.destinations.domain.PinPolicy
import com.agendaqr.destinations.domain.PinUpdate
import com.agendaqr.destinations.domain.PinVerification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Hasher determinista (NO criptográfico): aisla la lógica del gate. */
private object FakeHasher : PinHasher {
    override fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        val out = ByteArray(keyLengthBytes)
        for (index in out.indices) {
            out[index] = (pin[index % pin.size].code + salt[index % salt.size]).toByte()
        }
        return out
    }
}

private class FakeProtectionStore : LocalProtectionStore {
    var credential: String? = null
    override fun readCredential(): String? = credential
    override fun writeCredential(value: String) { credential = value }
    override fun clearCredential() { credential = null }
}

/**
 * Gate de acceso local. Demuestra que la app arranca SIN cuenta: no hay
 * pantalla de login remota en el camino principal, y el PIN protege el
 * acceso con límite de intentos.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalAccessGateTest {

    @get:Rule
    val compose = createComposeRule()

    private val profile = LocalProfile(id = "profile-test", createdAtEpochMillis = 1L)
    private lateinit var store: FakeProtectionStore
    private lateinit var protection: LocalProtectionRepository
    private lateinit var viewModel: LocalAccessViewModel

    @Before
    fun setUp() {
        store = FakeProtectionStore()
        protection = LocalProtectionRepository(
            store = store,
            hasher = FakeHasher,
            randomBytes = { size -> ByteArray(size) { (it + 1).toByte() } },
            now = { 1_000L },
        )
        viewModel = LocalAccessViewModel(profile, protection)
    }

    // ------------------------------------------------------------------
    // ViewModel
    // ------------------------------------------------------------------

    @Test
    fun first_run_requires_onboarding_and_no_remote_login() {
        assertEquals(LocalAccessMode.Onboarding, viewModel.state.value.mode)
    }

    @Test
    fun configured_pin_starts_locked_and_unlocks() {
        viewModel.onPinChanged("1234")
        viewModel.onConfirmationChanged("1234")
        viewModel.submitOnboarding()
        assertEquals(LocalAccessMode.Unlocked, viewModel.state.value.mode)

        viewModel.lock()
        assertEquals(LocalAccessMode.Locked, viewModel.state.value.mode)
        // el PIN nunca sobrevive al bloqueo en memoria
        assertEquals("", viewModel.state.value.pin)

        viewModel.onPinChanged("1234")
        viewModel.submitUnlock()
        assertEquals(LocalAccessMode.Unlocked, viewModel.state.value.mode)
    }

    @Test
    fun pin_input_is_sanitized_to_digits_within_the_policy_length() {
        viewModel.onPinChanged("1a2b3c4d5e6f7g8h9")
        assertEquals("123456789", viewModel.state.value.pin)
        assertTrue(viewModel.state.value.pin.all { it.isDigit() })
        // El recorte aplica el máximo de la política, no el del teclado.
        viewModel.onPinChanged("1234567890123456")
        assertEquals(PinPolicy.MAX_LENGTH, viewModel.state.value.pin.length)
        assertTrue(viewModel.state.value.pin.all { it.isDigit() })
    }

    @Test
    fun mismatched_confirmation_is_rejected_and_no_pin_is_stored() {
        viewModel.onPinChanged("1234")
        viewModel.onConfirmationChanged("4321")
        viewModel.submitOnboarding()
        assertEquals(LocalAccessError.Mismatch, viewModel.state.value.error)
        assertNull(store.credential)
        assertEquals(LocalAccessMode.Onboarding, viewModel.state.value.mode)
    }

    @Test
    fun wrong_pin_reports_incorrect_and_decrements_attempts() {
        protection.configure("1234".toCharArray(), "1234".toCharArray())
        val locked = LocalAccessViewModel(profile, protection)
        locked.onPinChanged("0000")
        locked.submitUnlock()
        assertEquals(LocalAccessError.Incorrect, locked.state.value.error)
        assertEquals(PinPolicy.MAX_ATTEMPTS - 1, locked.state.value.remainingAttempts)
    }

    @Test
    fun lockout_after_max_attempts_blocks_verification() {
        protection.configure("1234".toCharArray(), "1234".toCharArray())
        repeat(PinPolicy.MAX_ATTEMPTS) { protection.verify("0000".toCharArray()) }
        assertTrue(protection.verify("1234".toCharArray()) is PinVerification.LockedOut)
    }

    // ------------------------------------------------------------------
    // Pantallas
    // ------------------------------------------------------------------

    @Test
    fun onboarding_screen_shows_pin_setup_without_any_remote_credentials() {
        compose.setContent {
            AmTheme {
                LocalOnboardingScreen(
                    state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
                    onPinChanged = {},
                    onConfirmationChanged = {},
                    onSubmit = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.ConfiguraTuPin, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.Pin, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.ConfirmaElPin, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.GuardarPin, useUnmergedTree = true).assertExists()
        // No hay ningún campo ni copy de correo/contraseña: no es un login remoto.
        compose.onNodeWithText(AppStrings.Correo, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.Contrasena, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun lock_screen_shows_unlock_action_and_own_keypad() {
        compose.setContent {
            AmTheme {
                LocalLockScreen(
                    state = LocalAccessUiState(mode = LocalAccessMode.Locked),
                    onPinChanged = {},
                    onSubmit = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.DesbloqueaAgendaQr, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.Desbloquear, useUnmergedTree = true).assertExists()
        // Entrada propia (S00 de Stitch): teclas de dígito con rol de botón
        // y borrado con nombre accesible.
        compose.onNodeWithText("1", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription(AppStrings.BorrarDigito).assertExists()
    }

    @Test
    fun lock_screen_surfaces_lockout_error() {
        compose.setContent {
            AmTheme {
                LocalLockScreen(
                    state = LocalAccessUiState(
                        mode = LocalAccessMode.Locked,
                        error = LocalAccessError.LockedOut,
                    ),
                    onPinChanged = {},
                    onSubmit = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.PinErrorBloqueado, useUnmergedTree = true).assertExists()
    }

    @Test
    fun protection_flow_uses_pin_verification_not_remote_auth() {
        // La única guarda es el PIN local: sin él la app no se abre.
        assertEquals(PinUpdate.Success, protection.configure("9999".toCharArray(), "9999".toCharArray()))
        assertTrue(protection.isConfigured())
        assertEquals(PinVerification.Success, protection.verify("9999".toCharArray()))
    }

    @Test
    fun manual_lock_is_never_skipped_by_the_external_flow_grace_and_drops_the_pin() {
        // (e)/(f): el bloqueo manual no consulta la gracia de flujos externos
        // (esa gracia solo afecta a la política ON_STOP/ON_START) y NUNCA
        // retiene el PIN en memoria.
        val previous = ExternalFlowGates.tracker
        val marked = ExternalFlowTracker(clock = { 1_000L })
        marked.markLaunch()
        ExternalFlowGates.tracker = marked
        try {
            protection.configure("1234".toCharArray(), "1234".toCharArray())
            val vm = LocalAccessViewModel(profile, protection)
            vm.onPinChanged("1234")
            vm.submitUnlock()
            assertEquals(LocalAccessMode.Unlocked, vm.state.value.mode)

            vm.onPinChanged("9")
            vm.lock()
            assertEquals(LocalAccessMode.Locked, vm.state.value.mode)
            assertEquals("", vm.state.value.pin)
        } finally {
            ExternalFlowGates.tracker = previous
        }
    }
}
