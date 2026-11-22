package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqMetaRowTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun description_row_exposes_term_and_value() {
        compose.setContent {
            AqTheme {
                AqMenuItem(
                    title = "Moneda",
                    subtitle = "BOB",
                )
            }
        }

        compose.onNodeWithText("Moneda").assertExists()
        compose.onNodeWithText("BOB").assertExists()
    }
}
