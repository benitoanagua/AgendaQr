package com.agendaqr.core.ui.components

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqTextActionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tertiary_and_plain_text_actions_are_named_buttons_with_touch_targets() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqTextAction(label = "Más información", onClick = { clicks++ })
                AqTextAction(
                    label = "Cancelar",
                    onClick = { clicks++ },
                    variant = AqTextActionVariant.Neutral,
                )
            }
        }

        compose.onNodeWithText("Más información")
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        compose.onNodeWithText("Cancelar").performClick()
        assertEquals(2, clicks)
    }

    @Test
    fun disabled_text_action_does_not_activate() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqTextAction(label = "No disponible", onClick = { clicks++ }, enabled = false)
            }
        }

        compose.onNodeWithText("No disponible").assertIsNotEnabled()
        assertEquals(0, clicks)
    }
}
