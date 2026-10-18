package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos de accesibilidad de los controles reutilizables del sistema. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmAccessibilityContractTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun icon_button_has_accessible_name_button_role_and_48dp_target() {
        var clicks = 0
        compose.setContent {
            AmTheme {
                AmIconButton(
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
    fun quick_action_is_one_named_button_with_a_touch_target() {
        var clicks = 0
        compose.setContent {
            AmTheme {
                AmQuickAction(
                    label = "Traer un QR",
                    icon = AmIcons.QrCode,
                    onClick = { clicks++ },
                )
            }
        }

        compose.onNodeWithText("Traer un QR")
            .assertExists()
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        assert(clicks == 1) { "La acción rápida debe ejecutarse una vez" }
    }

    @Test
    fun filter_chip_exposes_selected_state_and_keeps_touch_target() {
        compose.setContent {
            AmTheme {
                AmFilterChip(label = "Activos", selected = true, onClick = {})
            }
        }

        compose.onNodeWithText("Activos").assertIsSelected().assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun keypad_keys_are_named_buttons_with_48dp_targets() {
        val digits = StringBuilder()
        compose.setContent {
            AmTheme {
                AmKeypad(
                    onDigit = { digits.append(it) },
                    onBackspace = {
                        if (digits.isNotEmpty()) digits.deleteCharAt(digits.length - 1)
                    },
                    backspaceDescription = "Borrar dígito",
                )
            }
        }

        compose.onNodeWithText("1")
            .assertExists()
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        compose.onNodeWithText("0").performClick()
        assert(digits.toString() == "10") { "Las teclas deben registrar sus dígitos" }
        compose.onNodeWithContentDescription("Borrar dígito")
            .assertExists()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assert(digits.toString() == "1") { "El borrado quita el último dígito" }
    }

    @Test
    fun search_field_has_a_named_48dp_edit_target() {
        compose.setContent {
            AmTheme {
                AmSearchField(
                    value = "",
                    onValueChange = {},
                    label = "Buscar destinos",
                    placeholder = "Nombre o categoría",
                )
            }
        }

        compose.onNodeWithContentDescription("Buscar destinos")
            .assertExists()
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun section_header_has_the_group_height() {
        compose.setContent {
            AmTheme {
                AmSectionHeader(
                    text = "Destinos",
                    modifier = Modifier.testTag("section-header"),
                )
            }
        }

        compose.onNodeWithTag("section-header").assertHeightIsAtLeast(45.dp)
    }

    @Test
    fun selected_list_row_exposes_selected_semantics() {
        // El punto de acento del AmListRow solo aparece con la identidad
        // REAL de un contexto; la selección se expresa con la semántica
        // `selected`, no con un color.
        compose.setContent {
            AmTheme {
                androidx.compose.foundation.layout.Column {
                    AmListRow(title = "Mercado", selected = true, onClick = {})
                    AmListRow(title = "Casa", onClick = {})
                }
            }
        }
        compose.onNodeWithText("Mercado")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        compose.onNodeWithText("Casa")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false).not())
    }

    @Test
    fun text_input_exposes_label_and_error_message() {
        compose.setContent {
            AmTheme {
                AmTextField(
                    label = "Correo",
                    value = "incorrecto",
                    onValueChange = {},
                    isError = true,
                    errorMessage = "Ingresa un correo válido",
                )
            }
        }

        // El label del campo se fusiona con el nodo editable (nombre
        // accesible) y el error se asocia a esa misma semántica.
        compose.onNodeWithContentDescription("Correo")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Ingresa un correo válido"))
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }

    @Test
    fun disabled_text_input_is_not_enabled_but_remains_named() {
        compose.setContent {
            AmTheme {
                AmTextField(
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
            AmTheme {
                AmTextField(
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
            AmTheme {
                AmTextField(
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
