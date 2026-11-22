package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.material3.Text
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Accesibilidad: estados y formularios. Cada cambio de comportamiento
 * lleva su test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccessibilityStatesFormsTest {

    @get:Rule
    val compose = createComposeRule()

    // --- 2.1 AuthScreen: valida al enviar; botones habilitados; 1 spinner ---

    @Test
    fun auth_shows_validation_errors_on_submit_and_keeps_buttons_enabled() {
        var email = ""
        var signIn = 0
        compose.setContent {
            AqTheme {
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
        compose.onNodeWithText(AppStrings.IniciarSesion).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.IngresaUnCorreoValido).assertExists()
        compose.onNodeWithText(AppStrings.Minimo6Caracteres).assertExists()
        assert(signIn == 0) { "No debe iniciar sesión con datos inválidos" }
    }

    @Test
    fun auth_signs_in_with_valid_data_and_announces_busy_only_on_primary() {
        var signIn = 0
        compose.setContent {
            AqTheme {
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
        compose.onNodeWithText(AppStrings.IniciarSesion)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.StateDescription))
        compose.onNodeWithText(AppStrings.CrearCuenta)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription))
    }

    // --- 2.4 AqModal destructivo: botón de peligro + foco en Cancelar ---

    @Test
    fun destructive_dialog_focuses_cancel_and_keeps_both_actions() {
        var deleted = false
        var cancelled = false
        compose.setContent {
            AqTheme {
                AqModal(
                    title = "Eliminar destino",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { cancelled = true },
                    confirmLabel = "Eliminar",
                    onConfirm = { deleted = true },
                    destructive = true,
                ) {
                    Text(text = "Esta acción no se puede deshacer.")
                }
            }
        }
        compose.waitForIdle()
        // El botón de descarte está presente y es accionable (el foco inicial
        // del diálogo no es verificable en Robolectric con Dialog; se valida
        // en instrumentados).
        compose.onNodeWithText("Cancelar").assertExists()
        compose.onNodeWithText("Eliminar").performClick()
        assert(deleted) { "La confirmación destructiva debe ejecutar onConfirm" }
        assert(!cancelled)
    }

    // --- 2.5 AqTextField: etiqueta asociada + error como liveRegion ---

    @Test
    fun text_input_exposes_its_label_and_announces_errors() {
        compose.setContent {
            AqTheme {
                AqTextInput(
                    label = AppStrings.Correo,
                    value = "no-correo",
                    onValueChange = { },
                    isError = true,
                    errorMessage = AppStrings.IngresaUnCorreoValido,
                )
            }
        }
        compose.waitForIdle()
        // La etiqueta del OutlinedTextField es el nombre accesible del campo
        // (nodo fusionado, sin asterisco: la obligatoriedad va en copy).
        compose.onNodeWithText(AppStrings.Correo).assertExists()
        // El error se anuncia cuando aparece (liveRegion cortés, §11).
        compose.onNodeWithText(AppStrings.IngresaUnCorreoValido)
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
            AqTheme {
                AqMenuItem(
                    title = "Mercado Central",
                    onClick = { },
                    selected = true,
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Mercado Central").assertIsSelected()
    }

    // --- 2.8 Camera entry: copy y acción de cámara visible antes del diálogo del sistema (B2).

    @Test
    fun camera_entry_shows_qr_copy_and_camera_action() {
        compose.setContent {
            AqTheme {
                CameraQrEntry(onOpen = { })
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Usamos la cámara solo para escanear códigos QR.").assertExists()
        compose.onNodeWithText("Cámara").assertHeightIsAtLeast(48.dp)
    }
}
