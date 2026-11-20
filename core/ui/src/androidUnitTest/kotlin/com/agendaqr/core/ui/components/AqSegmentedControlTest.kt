package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.AqSpacing
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqSegmentedControlTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun segmented_control_renders_options_with_counts_and_selection() {
        compose.setContent {
            AqTheme {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    AqSegmentedControl(
                        options = listOf(
                            AqSegmentedOption(label = "Todos", count = 12),
                            AqSegmentedOption(
                                label = "Favoritos",
                                icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
                                count = 3,
                            ),
                        ),
                        selectedIndex = 0,
                        onSelect = {},
                    )
                }
            }
        }

        compose.onAllNodesWithText("Todos").assertCountEquals(1)
        compose.onAllNodesWithText("Favoritos").assertCountEquals(1)
        compose.onAllNodesWithText("12").assertCountEquals(1)
        compose.onAllNodesWithText("3").assertCountEquals(1)
    }

    @Test
    fun segmented_control_renders_two_options() {
        compose.setContent {
            AqTheme {
                AqSegmentedControl(
                    options = listOf(
                        AqSegmentedOption(label = "Uno"),
                        AqSegmentedOption(label = "Dos"),
                    ),
                    selectedIndex = 0,
                    onSelect = {},
                )
            }
        }

        compose.onAllNodesWithText("Uno").assertCountEquals(1)
        compose.onAllNodesWithText("Dos").assertCountEquals(1)
    }

    @Test
    fun segmented_control_accepts_minus_one_as_no_selection() {
        var selected by mutableStateOf(-1)
        compose.setContent {
            AqTheme {
                AqSegmentedControl(
                    options = listOf(
                        AqSegmentedOption(label = "Hoy"),
                        AqSegmentedOption(label = "Ayer"),
                    ),
                    selectedIndex = selected,
                    onSelect = { selected = it },
                )
            }
        }

        // -1: ningún segmento activo; el click sigue emitiendo el índice.
        compose.onAllNodesWithText("Hoy").assertCountEquals(1)
        compose.onNodeWithText("Ayer")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
            .performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Ayer").assertIsSelected()
    }
}
