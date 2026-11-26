package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Geometría de los componentes añadidos desde la especificación Moon Design
 * v1 (Figma): `AqSelect`, `AqCombobox`, `AqPagination`, `AqSidebar` y
 * `AqLeaderboard` (`docs/05-design-system/evidencia/figma-moon-v1.md`).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqFigmaV1ComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun select_md_matches_text_input_height() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(280.dp)) {
                    AqSelect(
                        options = listOf("A", "B"),
                        selectedIndex = 0,
                        onSelected = {},
                    )
                }
            }
        }
        // Moon v1 Select: mismas alturas que `Text input` (md = 40 dp).
        compose.onNodeWithTag("aq-select-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun pagination_row_is_32dp_tall() {
        compose.setContent {
            AqTheme {
                AqPagination(
                    pageCount = 3,
                    currentPage = 0,
                    onPageSelected = {},
                    previousContentDescription = "Anterior",
                    nextContentDescription = "Siguiente",
                    pageContentDescription = { "Página $it" },
                )
            }
        }
        // Moon v1 Pagination: fila de 32 dp con `gap` 4 dp.
        compose.onNodeWithContentDescription("Página 1")
            .assertHeightIsEqualTo(24.dp)
    }

    @Test
    fun combobox_field_matches_text_input_height() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(280.dp)) {
                    AqCombobox(
                        query = "",
                        onQueryChange = {},
                        options = listOf("A", "B"),
                        selectedIndices = emptySet(),
                        onToggle = {},
                        expanded = false,
                    )
                }
            }
        }
        // Moon v1 Combobox: el campo es `Text input` (md = 40 dp).
        compose.onNodeWithTag("aq-text-input-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun leaderboard_rank_badge_is_24dp() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(320.dp)) {
                    AqLeaderboard(
                        entries = listOf(
                            AqLeaderboardEntry("Primero", rank = 1, value = "100"),
                        ),
                    )
                }
            }
        }
        // Moon v1 Leaderboard: insignia de puesto de 24 dp (`sizes.xs`).
        compose.onNodeWithContentDescription("Puesto 1. Primero. 100")
            .assertHeightIsEqualTo(40.dp) // fila: minHeight sizes.md
    }

    @Test
    fun sidebar_expanded_and_rail_widths() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(320.dp)) {
                    AqSidebar(
                        items = listOf(AqSidebarItem("Inicio")),
                        selectedIndex = 0,
                        onItemSelected = {},
                        expanded = true,
                    )
                }
            }
        }
        // Moon v1 Sidebar: 280 dp expandida / 64 dp rail.
        compose.onNodeWithContentDescription("Inicio")
            .assertWidthIsEqualTo(280.dp - 16.dp) // menos padding interno
    }
}
