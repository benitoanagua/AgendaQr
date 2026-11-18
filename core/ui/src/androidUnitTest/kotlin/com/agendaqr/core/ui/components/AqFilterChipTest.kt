package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
class AqFilterChipTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun filter_chip_renders_selected_and_unselected_with_count() {
        compose.setContent {
            AqTheme {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    AqFilterChip(label = "Todos", selected = true, onClick = {}, count = 12)
                    AqFilterChip(label = "Favoritos", selected = false, onClick = {}, count = 3)
                    AqFilterChip(label = "Deshabilitado", selected = false, onClick = {}, enabled = false)
                }
            }
        }

        compose.onAllNodesWithText("Todos").assertCountEquals(1)
        compose.onAllNodesWithText("Favoritos").assertCountEquals(1)
        compose.onAllNodesWithText("Deshabilitado").assertCountEquals(1)
        compose.onAllNodesWithText("12").assertCountEquals(1)
        compose.onAllNodesWithText("3").assertCountEquals(1)
    }

    @Test
    fun filter_chip_renders_node_without_throwing() {
        compose.setContent {
            AqTheme {
                AqFilterChip(
                    label = "Chip",
                    selected = false,
                    onClick = {},
                )
            }
        }

        compose.onAllNodesWithText("Chip").assertCountEquals(1)
    }
}
