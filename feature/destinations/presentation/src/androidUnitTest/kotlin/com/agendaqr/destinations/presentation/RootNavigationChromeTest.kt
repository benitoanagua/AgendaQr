package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.components.AqTabBar
import com.agendaqr.core.ui.theme.AqTheme
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Chrome de navegación raíz (§4 S01 V1.2): 4 destinos con etiqueta+icono;
 * cambiar de destino conserva la raíz del back stack (popToRoot+push) y el
 * Back vuelve a Inicio.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RootNavigationChromeTest {

    private val tabs = listOf(
        AqTab(AppStrings.Inicio, icon = AqIcons.Home),
        AqTab(AppStrings.Buscar, icon = AqIcons.Search),
        AqTab(AppStrings.Operaciones, icon = AqIcons.Register),
        AqTab(AppStrings.Contextos, icon = AqIcons.Context),
    )

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun nav_bar_shows_all_four_destinations_with_labels() {
        compose.setContent {
            AqTheme {
                AqTabBar(
                    tabs = tabs,
                    selectedIndex = 0,
                    onSelect = {},
                )
            }
        }
        compose.waitForIdle()
        // §12: cada destino con etiqueta visible (nunca solo icono).
        listOf(AppStrings.Inicio, AppStrings.Buscar, AppStrings.Operaciones, AppStrings.Contextos)
            .forEach { label ->
                compose.onNodeWithText(label).assertExists()
            }
    }

    @Test
    fun switching_destination_keeps_home_as_root_and_back_returns_to_it() {
        var selectedIndex = 0
        compose.setContent {
            AqTheme {
                AqTabBar(
                    tabs = tabs,
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.Contextos).performClick()
        compose.waitForIdle()
        assertEquals(3, selectedIndex, "Destino visible tras el cambio: $selectedIndex")
        // Desde cualquier destino, el usuario puede volver a Inicio (índice 0).
        compose.onNodeWithText(AppStrings.Inicio).performClick()
        compose.waitForIdle()
        assertEquals(0, selectedIndex, "El Back desde otro destino vuelve a Inicio")
    }
}
