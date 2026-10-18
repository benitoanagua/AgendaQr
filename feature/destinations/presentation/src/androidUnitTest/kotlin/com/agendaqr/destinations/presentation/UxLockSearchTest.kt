package com.agendaqr.destinations.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.BiometricAvailability
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * U-20/U-21: la biometría se solicita sola una vez por bloqueo (con
 * botón de respaldo) y el foco de Buscar solo llega desde el disparador
 * de Inicio.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UxLockSearchTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun biometric_auto_request_runs_once_per_lock() {
        var requests = 0
        var lockSequence by mutableStateOf(7)
        var pin by mutableStateOf("")
        compose.setContent {
            AmTheme {
                LocalLockScreen(
                    state = LocalAccessUiState(
                        mode = LocalAccessMode.Locked,
                        biometricAvailability = BiometricAvailability.Available,
                        lockSequence = lockSequence,
                    ),
                    onPinChanged = { pin = it },
                    onSubmit = {},
                    onBiometric = { requests++ },
                )
            }
        }
        compose.waitForIdle()
        assertEquals(1, requests)
        // Recomposición sin bloqueo nuevo (escribir el PIN): no relanza.
        pin = "1"
        compose.waitForIdle()
        assertEquals(1, requests)
        // Bloqueo nuevo: una solicitud más.
        lockSequence = 8
        compose.waitForIdle()
        assertEquals(2, requests)
    }

    @Test
    fun biometric_button_stays_as_fallback() {
        compose.setContent {
            AmTheme {
                LocalLockScreen(
                    state = LocalAccessUiState(
                        mode = LocalAccessMode.Locked,
                        biometricAvailability = BiometricAvailability.Available,
                    ),
                    onPinChanged = {},
                    onSubmit = {},
                    onBiometric = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.UsarBiometria).assertExists()
    }

    @Test
    fun search_focus_only_with_autofocus() {
        compose.setContent {
            AmTheme {
                GlobalSearchScreen(
                    state = GlobalSearchUiState(),
                    onAction = {},
                    onSelect = {},
                    autoFocus = true,
                )
            }
        }
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction() and hasText(AppStrings.Buscar))[0]
            .assertIsFocused()
    }

    @Test
    fun search_tab_entry_has_no_focus() {
        compose.setContent {
            AmTheme {
                GlobalSearchScreen(
                    state = GlobalSearchUiState(),
                    onAction = {},
                    onSelect = {},
                    autoFocus = false,
                )
            }
        }
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction() and hasText(AppStrings.Buscar))[0]
            .assertIsNotFocused()
    }
}
