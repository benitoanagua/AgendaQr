package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.components.AqIcons
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos de accesibilidad y comportamiento del tooltip móvil. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqTooltipTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tooltip_is_hidden_by_default() {
        compose.setContent {
            AqTheme {
                AqTooltip(
                    label = "Actualizar lista",
                    content = {
                        AqButton(
                            label = "Actualizar lista",
                            onClick = {},
                            variant = AqButtonVariant.Text,
                            size = AqButtonSize.Small,
                            icon = AqIcons.Search,
                            iconOnly = true,
                        )
                    },
                )
            }
        }

        // El botón icono conserva nombre accesible, rol y la altura Moon
        // del botón de texto sm (32 dp; el botón entero es el target).
        val node = compose.onNodeWithText("Actualizar lista")
        node.assertExists()
        node.assertHeightIsEqualTo(32.dp)
        node.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))

        // El tooltip no está visible inicialmente.
        compose.onNodeWithTag("aq-tooltip").assertDoesNotExist()
    }

    @Test
    fun tooltip_is_visible_when_forced() {
        compose.setContent {
            AqTheme {
                AqTooltip(
                    label = "Guardar",
                    isVisible = true,
                    content = {
                        androidx.compose.material3.Text(text = "disparador")
                    },
                )
            }
        }
        compose.onNodeWithTag("aq-tooltip").assertExists()
        compose.onNodeWithText("Guardar").assertExists()
    }

    @Test
    fun tooltip_content_keeps_touch_target_and_clicks() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqTooltip(
                    label = "Acción",
                    content = {
                        AqButton(
                            label = "Acción",
                            onClick = { clicks++ },
                            variant = AqButtonVariant.Text,
                            size = AqButtonSize.Small,
                            icon = AqIcons.Search,
                            iconOnly = true,
                        )
                    },
                )
            }
        }
        val node = compose.onNodeWithText("Acción")
        node.assertHeightIsEqualTo(32.dp)
        node.performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun tooltip_button_invokes_click() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqTooltip(
                    label = "Guardar",
                    content = {
                        AqButton(
                            label = "Guardar",
                            onClick = { clicks++ },
                            variant = AqButtonVariant.Text,
                            size = AqButtonSize.Small,
                            icon = AqIcons.Search,
                            iconOnly = true,
                        )
                    },
                )
            }
        }
        compose.onNodeWithText("Guardar").performClick()
        assertEquals(1, clicks)
    }
}
