package com.agendaqr.core.ui.components

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqSelectTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun select_opens_modal_choices_and_returns_the_selected_option() {
        var selectedId: String? = null
        compose.setContent {
            AqTheme {
                AqSelect(
                    options = listOf(AqChoiceOption("usd", "USD"), AqChoiceOption("bob", "BOB")),
                    selectedId = "bob",
                    onSelected = { selectedId = it },
                    label = "Moneda",
                    dialogTitle = "Selecciona moneda",
                    confirmLabel = "Aceptar",
                    dismissLabel = "Cancelar",
                )
            }
        }

        compose.onNodeWithText("BOB")
            .assertHeightIsAtLeast(48.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
            .performClick()
        compose.onNodeWithText("USD").performClick()
        assertEquals("usd", selectedId)
        compose.onNodeWithText("Aceptar").performClick()
        compose.onNodeWithText("Selecciona moneda").assertDoesNotExist()
    }
}
