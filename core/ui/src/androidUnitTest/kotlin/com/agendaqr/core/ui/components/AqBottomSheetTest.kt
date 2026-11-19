package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.semantics.SemanticsProperties
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos del bottom sheet con gestos y estados expanded/collapsed. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqBottomSheetTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun bottom_sheet_renders_title_and_content_when_expanded() {
        compose.setContent {
            AqTheme {
                AqBottomSheet(
                    title = "Título de la hoja",
                    onDismissRequest = {},
                    dismissDescription = "Cerrar",
                    expanded = true,
                ) {
                    androidx.compose.material3.Text(text = "Contenido de la hoja")
                }
            }
        }
        compose.onNodeWithText("Título de la hoja").assertExists()
        compose.onNodeWithText("Contenido de la hoja").assertExists()
        compose.onNodeWithText("Cerrar").assertExists()
        compose.onNodeWithTag("aq-bottom-sheet-pane", useUnmergedTree = true)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Título de la hoja"))
    }

    @Test
    fun bottom_sheet_collapses_content_when_not_expanded() {
        compose.setContent {
            AqTheme {
                AqBottomSheet(
                    title = "Título colapsado",
                    onDismissRequest = {},
                    dismissDescription = "Cerrar",
                    expanded = false,
                ) {
                    androidx.compose.material3.Text(text = "Contenido oculto")
                }
            }
        }
        compose.onNodeWithText("Título colapsado").assertExists()
        compose.onNodeWithText("Cerrar").assertExists()
        compose.onNodeWithText("Contenido oculto").assertDoesNotExist()
    }

    @Test
    fun downward_drag_on_handle_dismisses_after_threshold() {
        var dismissed = false
        compose.setContent {
            AqTheme {
                AqBottomSheet(
                    title = "Hoja arrastrable",
                    onDismissRequest = { dismissed = true },
                    dismissDescription = "Cerrar",
                ) { }
            }
        }

        compose.onNodeWithTag("aq-bottom-sheet-handle", useUnmergedTree = true).performTouchInput {
            swipeDown(startY = centerY, endY = centerY + 240f, durationMillis = 500)
        }
        assertTrue(dismissed)
    }

    @Test
    fun downward_drag_can_be_disabled() {
        var dismissed = false
        compose.setContent {
            AqTheme {
                AqBottomSheet(
                    title = "Hoja fija",
                    onDismissRequest = { dismissed = true },
                    dismissDescription = "Cerrar",
                    dismissOnSwipeDown = false,
                ) { }
            }
        }

        compose.onNodeWithTag("aq-bottom-sheet-handle", useUnmergedTree = true).performTouchInput {
            swipeDown(startY = centerY, endY = centerY + 240f, durationMillis = 500)
        }
        assertFalse(dismissed)
    }

    @Test
    fun dismissing_bottom_sheet_restores_focus_to_the_invoking_field() {
        val focusRequester = FocusRequester()
        compose.setContent {
            AqTheme {
                var sheetVisible by remember { mutableStateOf(false) }
                Column {
                    BasicTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.focusRequester(focusRequester).testTag("invoking-field"),
                    )
                    AqTextAction(label = "Abrir hoja", onClick = { sheetVisible = true })
                }
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
                if (sheetVisible) {
                    AqBottomSheet(
                        title = "Opciones",
                        onDismissRequest = { sheetVisible = false },
                        dismissDescription = "Cerrar",
                    ) { }
                }
            }
        }

        compose.onNodeWithTag("invoking-field").assertIsFocused()
        compose.onNodeWithText("Abrir hoja").performClick()
        compose.onNodeWithText("Cerrar").performClick()
        compose.onNodeWithTag("invoking-field").assertIsFocused()
    }
}
