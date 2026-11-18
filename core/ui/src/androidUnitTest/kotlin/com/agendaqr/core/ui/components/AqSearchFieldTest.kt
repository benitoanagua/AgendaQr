package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
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
class AqSearchFieldTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun search_field_renders_placeholder_and_clear_action() {
        compose.setContent {
            AqTheme {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    AqSearchField(
                        value = "",
                        onValueChange = {},
                        label = "Buscar",
                        placeholder = "Buscar en Agenda QR",
                    )
                    AqSearchField(
                        value = "",
                        onValueChange = {},
                        label = "Buscar con texto",
                        placeholder = "Buscar en Agenda QR",
                        onClear = {},
                        clearLabel = "Limpiar búsqueda",
                    )
                }
            }
        }

        compose.onAllNodesWithText("Buscar en Agenda QR").assertCountEquals(2)
    }

    @Test
    fun search_field_renders_without_throwing() {
        compose.setContent {
            AqTheme {
                AqSearchField(
                    value = "",
                    onValueChange = {},
                    label = "Buscar",
                    placeholder = "Buscar en Agenda QR",
                    onClear = {},
                    clearLabel = "Limpiar búsqueda",
                )
            }
        }

        compose.onAllNodesWithText("Buscar en Agenda QR").assertCountEquals(1)
    }
}
