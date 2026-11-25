package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.platform.testTag
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
class AqSelectTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun select_opens_dropdown_choices_and_returns_the_selected_option() {
        var selectedIndex: Int? = null
        compose.setContent {
            AqTheme {
                Row {
                    AqDropdown(
                        open = true,
                        options = listOf("USD", "BOB"),
                        selected = 1,
                        onOptionClick = { selectedIndex = it },
                        anchor = { },
                        content = {
                            // El componente ya renderiza `options` como filas clickeables;
                            // el slot `content` es adicional, no debe duplicar opciones.
                            Row {
                                androidx.compose.material3.Text(
                                    text = "USD",
                                    modifier = androidx.compose.ui.Modifier.testTag("usd"),
                                )
                            }
                        },
                    )
                }
            }
        }

        compose.onNodeWithText("BOB")
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performSemanticsAction(SemanticsActions.OnClick)
        assertEquals(1, selectedIndex)
    }
}
