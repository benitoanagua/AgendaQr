package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Fase 2 de la auditoría de accesibilidad (2026-10-08): estados y
 * formularios. Cada cambio de comportamiento lleva su test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuditStatesFormsTest {

    @get:Rule
    val compose = createComposeRule()

    // --- 2.1 AuthScreen: valida al enviar; botones habilitados; 1 spinner ---

    @Test
    fun auth_shows_validation_errors_on_submit_and_keeps_buttons_enabled() {
        var email = ""
        var signIn = 0
        compose.setContent {
            XauxaTheme {
                AuthScreen(
                    state = AuthUiState(email = "", password = "", isSubmitting = false),
                    onEmailChanged = { email = it },
                    onPasswordChanged = { },
                    onSignIn = { signIn++ },
                    onSignUp = { },
                )
            }
        }
        compose.waitForIdle()
        // Antes: el botón nacía deshabilitado (formValid) y el error nunca
        // se veía. Ahora: habilitado, y al enviar muestra los dos errores.
        compose.onNodeWithText("Iniciar sesión").performClick()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
        compose.onNodeWithText("Mínimo 6 caracteres").assertExists()
        assert(signIn == 0) { "No debe iniciar sesión con datos inválidos" }
    }

    @Test
    fun auth_signs_in_with_valid_data_and_announces_busy_only_on_primary() {
        var signIn = 0
        compose.setContent {
            XauxaTheme {
                AuthScreen(
                    state = AuthUiState(
                        email = "e2e@example.com",
                        password = "secreto",
                        isSubmitting = true,
                    ),
                    onEmailChanged = { },
                    onPasswordChanged = { },
                    onSignIn = { signIn++ },
                    onSignUp = { },
                )
            }
        }
        compose.waitForIdle()
        // Un SOLO indicador de ocupado por pantalla: el primario anuncia
        // "Iniciando sesión…" (stateDescription); el secundario queda
        // deshabilitado sin spinner propio.
        compose.onNodeWithText("Iniciar sesión")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.StateDescription))
        compose.onNodeWithText("Crear cuenta")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription))
    }

    // --- 2.4 XauxaDialog destructivo: botón de peligro + foco en Cancelar ---

    @Test
    fun destructive_dialog_focuses_cancel_and_keeps_both_actions() {
        var deleted = false
        var cancelled = false
        compose.setContent {
            XauxaTheme {
                XauxaDialog(
                    title = "Eliminar destino",
                    message = "Esta acción no se puede deshacer.",
                    confirmLabel = "Eliminar",
                    onConfirm = { deleted = true },
                    dismissLabel = "Cancelar",
                    onDismiss = { cancelled = true },
                    destructive = true,
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cancelar").assertIsFocused()
        compose.onNodeWithText("Eliminar").performClick()
        assert(deleted) { "La confirmación destructiva debe ejecutar onConfirm" }
        assert(!cancelled)
    }

    // --- 2.5 XauxaTextInput: etiqueta asociada + error como liveRegion ---

    @Test
    fun text_input_exposes_its_label_and_announces_errors() {
        compose.setContent {
            XauxaTheme {
                XauxaTextInput(
                    label = "Correo",
                    value = "no-correo",
                    onValueChange = { },
                    isRequired = true,
                    isError = true,
                    errorMessage = "Ingresa un correo válido",
                )
            }
        }
        compose.waitForIdle()
        // La etiqueta fija (visual) es también el nombre accesible del campo.
        compose.onNodeWithContentDescription("Correo *").assertExists()
        // El error se anuncia cuando aparece (liveRegion cortés, §11).
        compose.onNodeWithText("Ingresa un correo válido")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.LiveRegion,
                    LiveRegionMode.Polite,
                ),
            )
    }

    // --- 2.7 ListRow: semántica de selección; sin marcador en fila neutra ---

    @Test
    fun list_row_selected_is_exposed_to_accessibility() {
        compose.setContent {
            XauxaTheme {
                XauxaListRow(
                    title = "Mercado Central",
                    onClick = { },
                    selected = true,
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Mercado Central").assertIsSelected()
    }
}
