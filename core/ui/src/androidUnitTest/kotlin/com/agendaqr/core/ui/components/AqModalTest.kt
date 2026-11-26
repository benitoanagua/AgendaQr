package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Pruebas de [AqModal]: semántica de diálogo y cierre por scrim. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqModalTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun modal_renders_title_and_content() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = true,
                    title = "Confirmar eliminación",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { },
                ) {
                    androidx.compose.material3.Text(text = "¿Eliminar este comprobante?")
                }
            }
        }
        compose.onNodeWithTag("aq-modal-pane", useUnmergedTree = true).assertExists()
        compose.onNodeWithTag("aq-modal-scrim", useUnmergedTree = true).assertExists()
    }

    @Test
    fun modal_pane_title_es_el_titulo() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = true,
                    title = "Confirmar eliminación",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { },
                ) { }
            }
        }
        compose.onNodeWithText("Confirmar eliminación").assertExists()
    }

    @Test
    fun modal_scrim_click_invokes_on_dismiss_request() {
        var dismissed = false
        compose.setContent {
            AqTheme {
                var show by remember { mutableStateOf(true) }
                if (show) {
                    AqModal(
                        show = true,
                        title = "Título",
                        dismissLabel = "Cancelar",
                        onDismissRequest = { dismissed = true; show = false },
                    ) { }
                }
            }
        }
        compose.onNodeWithTag("aq-modal-scrim", useUnmergedTree = true).performClick()
        assertTrue(dismissed)
    }

    @Test
    fun modal_is_not_shown_when_show_is_false() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = false,
                    title = "Título",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { },
                ) { }
            }
        }
        compose.onNodeWithTag("aq-modal-pane", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun modal_with_tall_content_keeps_actions_visible() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = true,
                    title = "Selector",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { },
                ) {
                    Column {
                        repeat(80) { index ->
                            androidx.compose.material3.Text("Fila $index")
                        }
                    }
                }
            }
        }
        compose.onNodeWithText("Fila 0").assertExists()
        // Moon: el botón de texto sm mide 32 dp y el contenido hace scroll
        // dejando las acciones siempre visibles (ADR-0044).
        compose.onNodeWithText("Cancelar").assertHeightIsEqualTo(32.dp)
    }
}
