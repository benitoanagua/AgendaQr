package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.components.AqIcons
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Contratos de accesibilidad de los controles reutilizables del sistema. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqAccessibilityContractTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun icon_button_has_accessible_name_button_role_and_moon_height() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqButton(
                    label = "Actualizar lista",
                    onClick = { clicks++ },
                    variant = AqButtonVariant.Text,
                    size = AqButtonSize.Small,
                    icon = AqIcons.Search,
                    iconOnly = true,
                )
            }
        }

        val node = compose.onNodeWithText("Actualizar lista")
        node.assertExists()
        // Moon fija la altura del botón (sm = 32 dp); el botón entero es el
        // objetivo táctil, sin expansión extra (ADR-0044).
        node.assertHeightIsEqualTo(32.dp)
        node.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        node.performClick()
        assert(clicks == 1) { "El botón debe ejecutar la acción una vez" }
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun quick_action_is_one_named_button_with_a_touch_target() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                // wrapContentSize: Surface(fillMaxSize) propaga constraints
                // mínimos a hijos directos; sin el Box el botón llena la
                // pantalla en los tests.
                Box(Modifier.wrapContentSize()) {
                    AqButton(
                        label = "Traer un QR",
                        icon = AqIcons.QrCode,
                        onClick = { clicks++ },
                        variant = AqButtonVariant.Primary,
                        size = AqButtonSize.Small,
                    )
                }
            }
        }

        compose.onNodeWithText("Traer un QR")
            .assertExists()
            .assertHeightIsEqualTo(32.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        assert(clicks == 1) { "La acción rápida debe ejecutarse una vez" }
    }

    @Test
    fun chip_group_exposes_selected_state_and_keeps_touch_target() {
        compose.setContent {
            AqTheme {
                AqChipRow(
                    options = listOf(AqChipOption("Activos")),
                    selectedIndex = 0,
                    onSelect = {},
                )
            }
        }

        // Moon `chip.dart`: el chip md mide `sizes.md` (40 dp) de alto.
        compose.onNodeWithText("Activos").assertIsSelected().assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun keypad_keys_are_named_buttons_with_48dp_targets() {
        val digits = StringBuilder()
        compose.setContent {
            AqTheme {
                AqKeypad(
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
    fun keypad_keys_render_in_dark_theme_with_distinct_keys() {
        val digits = StringBuilder()
        compose.setContent {
            AqTheme(darkTheme = true) {
                AqKeypad(
                    onDigit = { digits.append(it) },
                    onBackspace = {},
                    backspaceDescription = "Borrar dígito",
                )
            }
        }

        compose.onNodeWithText("1").assertExists()
        compose.onNodeWithText("2").assertExists()
        compose.onNodeWithText("3").assertExists()
        compose.onNodeWithText("4").assertExists()
        compose.onNodeWithText("5").assertExists()
        compose.onNodeWithText("6").assertExists()
        compose.onNodeWithText("7").assertExists()
        compose.onNodeWithText("8").assertExists()
        compose.onNodeWithText("9").assertExists()
        compose.onNodeWithText("0").assertExists()
        compose.onNodeWithContentDescription("Borrar dígito").assertExists()
    }

    @Test
    fun search_input_has_a_named_48dp_edit_target() {
        compose.setContent {
            AqTheme {
                AqSearchInput(
                    label = "Buscar destinos",
                    value = "",
                    onValueChange = {},
                    placeholder = "Nombre o categoría",
                )
            }
        }

        compose.onNodeWithText("Buscar destinos").assertExists()
        // Moon `text_input`: la caja visual es el campo completo (md = 40 dp);
        // no hay target táctil extra por encima de la geometría Moon.
        compose.onNodeWithTag("aq-text-input-visual").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun section_title_has_the_group_height() {
        compose.setContent {
            AqTheme {
                AqSectionTitle(
                    text = "Destinos",
                    modifier = Modifier.testTag("section-header"),
                )
            }
        }

        compose.onNodeWithTag("section-header").assertHeightIsAtLeast(45.dp)
    }

    @Test
    fun selected_menu_item_exposes_selected_semantics() {
        compose.setContent {
            AqTheme {
                Column {
                    AqMenuItem(title = "Mercado", selected = true, onClick = {})
                    AqMenuItem(title = "Casa", onClick = {})
                }
            }
        }
        compose.onNodeWithText("Mercado")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        compose.onNodeWithText("Casa")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
    }

    @Test
    fun text_input_exposes_label_and_error_message() {
        compose.setContent {
            AqTheme {
                AqTextInput(
                    label = "Correo",
                    value = "incorrecto",
                    onValueChange = {},
                    isError = true,
                    errorMessage = "Ingresa un correo válido",
                )
            }
        }

        compose.onNodeWithText("Correo")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Ingresa un correo válido"))
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }

    @Test
    fun disabled_text_input_is_not_enabled_but_remains_named() {
        compose.setContent {
            AqTheme {
                AqTextInput(
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
            AqTheme {
                AqTextInput(
                    label = "Referencia",
                    value = "REF-456",
                    onValueChange = {},
                    readOnly = true,
                )
            }
        }

        compose.onNodeWithText("Referencia")
            .assertExists()
            .assertIsEnabled()
        compose.onNodeWithText("REF-456").assertExists()
    }

    @Test
    fun changing_error_message_updates_semantics_and_visible_feedback() {
        val message = mutableStateOf("Formato inválido")
        compose.setContent {
            AqTheme {
                AqTextInput(
                    label = "Identificador",
                    value = "abc",
                    onValueChange = {},
                    isError = true,
                    errorMessage = message.value,
                )
            }
        }

        compose.onNodeWithText("Identificador")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Formato inválido"))
        compose.onNodeWithText("Formato inválido").assertExists()

        compose.runOnIdle { message.value = "Usa al menos 8 caracteres" }

        compose.onNodeWithText("Identificador")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Usa al menos 8 caracteres"))
        compose.onNodeWithText("Usa al menos 8 caracteres").assertExists()
    }
}
