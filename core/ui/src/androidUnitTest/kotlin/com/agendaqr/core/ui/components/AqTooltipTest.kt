package com.agendaqr.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
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
                        AqIconButton(
                            contentDescription = "Actualizar lista",
                            onClick = {},
                        ) {
                            Icon(
                                imageVector = AqIcons.Search,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
        }

        // El botón icono conserva nombre accesible, rol y target de 48 dp.
        val node = compose.onNodeWithContentDescription("Actualizar lista")
        node.assertExists()
        node.assertHeightIsAtLeast(48.dp)
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
                        AqIconButton(
                            contentDescription = "Acción",
                            onClick = { clicks++ },
                        ) {
                            Icon(
                                imageVector = AqIcons.Search,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
        }
        val node = compose.onNodeWithContentDescription("Acción")
        node.assertHeightIsAtLeast(48.dp)
        node.performClick()
        assertEquals(1, clicks)
    }
}
