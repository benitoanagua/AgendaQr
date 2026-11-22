package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Semántica de paneles para que el lector de pantalla anuncie overlays. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqDialogAccessibilityTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun dialog_exposes_its_title_as_pane_title() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = true,
                    title = "Eliminar destino",
                    dismissLabel = "Cancelar",
                    onDismissRequest = {},
                    confirmLabel = "Eliminar",
                    onConfirm = {},
                ) {
                    Text(text = "Esta acción no se puede deshacer.")
                }
            }
        }

        compose.onNodeWithText("Eliminar destino").assertExists()
    }

    @Test
    fun long_dialog_content_scrolls_without_removing_the_action_footer() {
        compose.setContent {
            AqTheme {
                AqModal(
                    show = true,
                    title = "Opciones",
                    dismissLabel = "Cancelar",
                    onDismissRequest = {},
                    confirmLabel = "Aceptar",
                    onConfirm = {},
                ) {
                    repeat(24) { index ->
                        androidx.compose.material3.Text("Opción ${index + 1}")
                    }
                }
            }
        }

        compose.onNodeWithText("Opción 24").performScrollTo()
        compose.onNodeWithText("Aceptar").assertExists()
        compose.onNodeWithText("Cancelar").assertExists()
    }
}
