package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ronda 2 (Área C): lo que el usuario no debe perder sobrevive a la
 * recreación de la actividad. Con `remember` simple estos tests fallan
 * (rojo primero): la recreación olvidaba que se intentó enviar.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Round2StateRestorationTest {

    @get:Rule
    val compose = androidx.compose.ui.test.junit4.createComposeRule()

    @Test
    fun auth_validation_errors_survive_recreation() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            XauxaTheme {
                AuthScreen(
                    state = AuthUiState(email = "", password = "", isSubmitting = false),
                    onEmailChanged = { },
                    onPasswordChanged = { },
                    onSignIn = { },
                    onSignUp = { },
                )
            }
        }
        compose.waitForIdle()
        // Intento con datos inválidos: los errores de validación aparecen.
        compose.onNodeWithText("Iniciar sesión").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
        // Recreación (rotación/muerte de proceso): el intento se recuerda.
        restoration.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }
}
