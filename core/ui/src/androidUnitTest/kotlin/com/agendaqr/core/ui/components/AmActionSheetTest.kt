package com.agendaqr.core.ui.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmActionSheetTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun top_bar_overflow_opens_sheet_and_dispatches_action() {
        val selected = AtomicBoolean(false)
        compose.setContent {
            AmTheme {
                AmScreenScaffold(
                    topBar = {
                        AmTopBar(
                            title = "Detalle",
                            overflowActions = listOf(
                                AmOverflowAction("Editar", onClick = { selected.set(true) }),
                            ),
                            overflowLabel = "Más",
                        )
                    },
                ) {}
            }
        }

        compose.onNodeWithContentDescription("Más").performClick()
        compose.onNodeWithText("Editar").assertIsDisplayed().performClick()
        compose.waitForIdle()

        assertTrue(selected.get())
        compose.onAllNodesWithText("Editar").assertCountEquals(0)
    }
}
