package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqPullToRefreshTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun pullToRefresh_rendersContent() {
        compose.setContent {
            AqTheme {
                AqPullToRefresh(
                    isRefreshing = false,
                    onRefresh = {},
                    refreshingLabel = "Actualizando contenido",
                ) {
                    AqText(text = "Contenido de lista")
                }
            }
        }

        compose.onNodeWithText("Contenido de lista").assertExists()
        compose.onNodeWithContentDescription("Actualizando contenido").assertDoesNotExist()
    }

    @Test
    fun pullToRefresh_whenRefreshing_showsAccessibleSpinner() {
        val label = "Cargando nuevos datos"

        compose.setContent {
            AqTheme {
                AqPullToRefresh(
                    isRefreshing = true,
                    onRefresh = {},
                    refreshingLabel = label,
                ) {
                    AqText(text = "Contenido de lista")
                }
            }
        }

        compose.onNodeWithText("Contenido de lista").assertExists()
        compose.onNodeWithContentDescription(label).assertExists()
    }
}
