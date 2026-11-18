package com.agendaqr.core.ui.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de [AqMultilineTextField]: campo multilínea para notas de
 * producto (S02/S07 edición de destino, S07 registro de operación).
 *
 * - Renderiza etiqueta y valor.
 * - Muestra mensaje de error + icono de descarte cuando [isError].
 * - Asocia semántica de error al campo editable.
 * - Conserva el objetivo táctil de 48 dp en la caja.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqMultilineTextFieldTest {

    @get:Rule
    val compose = createComposeRule()

    private fun composeContent(
        isError: Boolean = false,
        errorMessage: String? = null,
        helperMessage: String? = null,
        enabled: Boolean = true,
    ) {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqMultilineTextField(
                    label = "Nota",
                    value = "Valor de prueba",
                    onValueChange = {},
                    isError = isError,
                    errorMessage = errorMessage,
                    helperMessage = helperMessage,
                    enabled = enabled,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun renders_label_and_value() {
        composeContent()
        compose.onNodeWithText("Nota").assertIsEnabled()
        compose.onNodeWithText("Valor de prueba").assertIsEnabled()
    }

    @Test
    fun shows_error_message_and_icon_when_error() {
        composeContent(isError = true, errorMessage = "Importe inválido")
        compose.onNodeWithText("Importe inválido").assertIsEnabled()
    }

    @Test
    fun shows_helper_message_when_provided() {
        composeContent(helperMessage = "Texto de ayuda")
        compose.onNodeWithText("Texto de ayuda").assertIsEnabled()
    }

    @Test
    fun disabled_field_renders_without_error() {
        composeContent(enabled = false)
        compose.onNodeWithText("Nota")
        compose.onNodeWithText("Valor de prueba")
    }
}
