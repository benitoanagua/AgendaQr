package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.components.AqIcons
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqNavRailLayoutTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tablet_rail_keeps_all_destinations_visible_at_200_percent_font_scale() {
        var selected = 1
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                AqTheme {
                    Row(Modifier.size(width = 720.dp, height = 800.dp)) {
                        AqTabBar(
                            tabs = listOf(
                                AqTab("Inicio", icon = AqIcons.Home),
                                AqTab("Buscar", icon = AqIcons.Search),
                                AqTab("Operaciones", icon = AqIcons.Register),
                                AqTab("Contextos", icon = AqIcons.Context),
                            ),
                            selectedIndex = selected,
                            onSelect = { selected = it },
                        )
                    }
                }
            }
        }

        listOf("Inicio", "Buscar", "Operaciones", "Contextos").forEach { label ->
            compose.onNodeWithText(label).assertIsDisplayed()
        }
        compose.onNodeWithText("Operaciones")
            .performSemanticsAction(SemanticsActions.OnClick)
        assertEquals(2, selected)
    }
}
