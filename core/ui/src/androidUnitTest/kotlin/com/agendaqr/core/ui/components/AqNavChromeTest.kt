package com.agendaqr.core.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqNavChromeTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun root_navigation_keeps_destinations_selectable_and_semantic() {
        var selected by mutableStateOf("home")
        val destinations = listOf(
            AqNavDestination("home", "Inicio", AqIcons.Home),
            AqNavDestination("search", "Buscar", AqIcons.Search),
            AqNavDestination("operations", "Operaciones", AqIcons.Register),
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
        compose.onNodeWithContentDescription("Buscar")
            .performSemanticsAction(SemanticsActions.OnClick)
        compose.runOnIdle { assertEquals("search", selected) }
    }
}
