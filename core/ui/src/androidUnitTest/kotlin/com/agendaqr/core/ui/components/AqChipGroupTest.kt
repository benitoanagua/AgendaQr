package com.agendaqr.core.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqChipRowTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun chip_group_renders_options_with_selection_and_count() {
        compose.setContent {
            AqTheme {
                AqChipRow(
                    options = listOf(
                        AqChipOption("Todos", count = 12),
                        AqChipOption("Favoritos", count = 3),
                        AqChipOption("Deshabilitado", enabled = false),
                    ),
                    selectedIndex = 0,
                    onSelect = {},
                )
            }
        }

        compose.onAllNodesWithText("Todos").assertCountEquals(1)
        compose.onAllNodesWithText("Favoritos").assertCountEquals(1)
        compose.onAllNodesWithText("Deshabilitado").assertCountEquals(1)
        compose.onAllNodesWithText("12").assertCountEquals(1)
        compose.onAllNodesWithText("3").assertCountEquals(1)
        compose.onNodeWithText("Todos")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun chip_group_emits_index_on_click() {
        var selected by mutableStateOf(-1)
        compose.setContent {
            AqTheme {
                AqChipRow(
                    options = listOf(AqChipOption("A"), AqChipOption("B")),
                    selectedIndex = selected,
                    onSelect = { selected = it },
                )
            }
        }

        compose.onNodeWithText("B").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("B").assertIsSelected()
        compose.onNodeWithText("A")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
    }

    @Test
    fun chip_count_is_announced_as_separate_text_from_label() {
        compose.setContent {
            AqTheme {
                AqChipRow(
                    options = listOf(AqChipOption("Todos", count = 2)),
                    selectedIndex = -1,
                    onSelect = {},
                )
            }
        }

        // El count viaja como texto semántico propio del chip fusionado:
        // "Todos" y "2" se leen por separado, nunca como "Todos2".
        val node = compose.onNodeWithText("Todos").fetchSemanticsNode()
        val texts = node.config[SemanticsProperties.Text]
            as? List<androidx.compose.ui.text.AnnotatedString>
        val mergedText = texts.orEmpty().joinToString(" ") { it.text }
        org.junit.Assert.assertTrue(
            "chip semantics debe contener label y count separados: '$mergedText'",
            mergedText.contains("Todos") && mergedText.contains("2"),
        )
    }

    @Test
    fun chip_group_renders_node_without_throwing() {
        compose.setContent {
            AqTheme {
                AqChipRow(
                    options = listOf(AqChipOption("Chip")),
                    selectedIndex = -1,
                    onSelect = {},
                )
            }
        }

        compose.onAllNodesWithText("Chip").assertCountEquals(1)
    }
}
