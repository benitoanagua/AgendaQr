package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AqNavChromeTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun root_navigation_keeps_destinations_selectable_and_semantic() {
        var selected by mutableStateOf("home")
        val destinations = listOf(
            AqNavDestination("home", "Inicio", AqIcons.Home),
            AqNavDestination("search", "Buscar", AqIcons.Search),
            AqNavDestination("operations", "Operaciones", AqIcons.Activity),
        )

        compose.setContent {
            AqTheme {
                AqBottomNavigation(
                    destinations = destinations,
                    selectedId = selected,
                    onSelect = { selected = it },
                    modifier = Modifier.testTag("root-navigation"),
                )
            }
        }

        compose.onNodeWithTag("root-navigation").assertExists()
        compose.onNodeWithText("Inicio").assertExists()
        compose.onNodeWithText("Buscar").performClick()
        compose.runOnIdle { assertEquals("search", selected) }
    }

    @Test
    fun root_navigation_requires_unique_destination_ids() {
        compose.setContent {
            AqTheme {
                AqBottomNavigation(
                    destinations = listOf(
                        AqNavDestination("same", "Inicio", AqIcons.Home),
                        AqNavDestination("same", "Buscar", AqIcons.Search),
                    ),
                    selectedId = "same",
                    onSelect = {},
                )
            }
        }
    }
}
