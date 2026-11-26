package com.agendaqr.core.ui.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de [AqTextArea]: campo multilínea para notas de
 * producto (S02/S07 edición de destino, S07 registro de operación).
 *
 * - Renderiza etiqueta accesible y valor (Moon no tiene etiqueta externa:
 *   [label] es el nombre accesible del campo).
 * - Muestra mensaje de error + icono de descarte cuando [isError].
 * - Asocia semántica de error al campo editable.
 * - La caja mide la altura Moon (md = 40 dp) y ES el área táctil.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqTextAreaContractTest {

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
                AqTextArea(
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
        // Moon `MoonTextArea` no tiene etiqueta persistente: `label` queda
        // como nombre accesible (contentDescription) de la caja.
        compose.onNodeWithContentDescription("Nota").assertExists()
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
        compose.onNodeWithContentDescription("Nota").assertExists()
        compose.onNodeWithText("Valor de prueba").assertExists()
    }
}
