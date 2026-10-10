package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos de accesibilidad de los controles reutilizables Xauxa. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class XauxaAccessibilityContractTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun icon_button_has_accessible_name_button_role_and_48dp_target() {
        var clicks = 0
        compose.setContent {
            XauxaTheme {
                XauxaIconButton(
                    contentDescription = "Actualizar lista",
                    onClick = { clicks++ },
                ) { Text("↻") }
            }
        }

        val node = compose.onNodeWithContentDescription("Actualizar lista")
        node.assertExists()
        node.assertHeightIsAtLeast(48.dp)
        node.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        node.performClick()
        assert(clicks == 1) { "El botón debe ejecutar la acción una vez" }
    }

    @Test
    fun filter_chip_exposes_selected_state_and_keeps_touch_target() {
        compose.setContent {
            XauxaTheme {
                XauxaFilterChip(label = "Activos", selected = true, onClick = {})
            }
        }

        compose.onNodeWithText("Activos").assertIsSelected().assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun text_input_exposes_required_label_and_error_message() {
        compose.setContent {
            XauxaTheme {
                XauxaTextInput(
                    label = "Correo",
                    value = "incorrecto",
                    onValueChange = {},
                    isRequired = true,
                    isError = true,
                    errorMessage = "Ingresa un correo válido",
                )
            }
        }

        compose.onNodeWithContentDescription("Correo *")
            .assertExists()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Ingresa un correo válido"))
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }

    @Test
    fun disabled_text_input_is_not_enabled_but_remains_named() {
        compose.setContent {
            XauxaTheme {
                XauxaTextInput(
                    label = "Código de reserva",
                    value = "QR-123",
                    onValueChange = {},
                    enabled = false,
                )
            }
        }

        compose.onNodeWithContentDescription("Código de reserva")
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun read_only_text_input_remains_enabled_and_exposes_its_value() {
        compose.setContent {
            XauxaTheme {
                XauxaTextInput(
                    label = "Referencia",
                    value = "REF-456",
                    onValueChange = {},
                    readOnly = true,
                )
            }
        }

        // readOnly permite foco/selección y copia; no equivale a disabled.
        compose.onNodeWithContentDescription("Referencia")
            .assertExists()
            .assertIsEnabled()
        compose.onNodeWithText("REF-456").assertExists()
    }

    @Test
    fun changing_error_message_updates_semantics_and_visible_feedback() {
        val message = mutableStateOf("Formato inválido")
        compose.setContent {
            XauxaTheme {
                XauxaTextInput(
                    label = "Identificador",
                    value = "abc",
                    onValueChange = {},
                    isError = true,
                    errorMessage = message.value,
                )
            }
        }

        compose.onNodeWithContentDescription("Identificador")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Formato inválido"))
        compose.onNodeWithText("Formato inválido").assertExists()

        compose.runOnIdle { message.value = "Usa al menos 8 caracteres" }

        compose.onNodeWithContentDescription("Identificador")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Usa al menos 8 caracteres"))
        compose.onNodeWithText("Usa al menos 8 caracteres").assertExists()
    }

}
