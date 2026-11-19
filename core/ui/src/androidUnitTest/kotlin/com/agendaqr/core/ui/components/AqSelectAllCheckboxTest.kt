package com.agendaqr.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assert
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
class AqSelectAllCheckboxTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun mixed_selection_is_announced_as_mixed_checkbox_and_can_be_activated() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqSelectAllCheckbox(
                    label = "Seleccionar todos",
                    description = "Hay opciones seleccionadas parcialmente",
                    checkedState = ToggleableState.Indeterminate,
                    onClick = { clicks++ },
                    modifier = Modifier,
                )
            }
        }

        compose.onNodeWithText("Seleccionar todos")
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ToggleableState,
                    ToggleableState.Indeterminate,
                ),
            )
            .performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabled_select_all_does_not_invoke_callback() {
        var clicks = 0
        compose.setContent {
            AqTheme {
                AqSelectAllCheckbox(
                    label = "Seleccionar todos",
                    checkedState = ToggleableState.Off,
                    onClick = { clicks++ },
                    state = AqFieldState(enabled = false),
                )
            }
        }

        compose.onNodeWithText("Seleccionar todos").assertIsNotEnabled()
        assertEquals(0, clicks)
    }
}
