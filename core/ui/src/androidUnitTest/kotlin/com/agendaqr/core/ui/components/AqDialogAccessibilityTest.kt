package com.agendaqr.core.ui.components

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.semantics.SemanticsProperties
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
                AqDialog(
                    title = "Eliminar destino",
                    message = "Esta acción no se puede deshacer.",
                    confirmLabel = "Eliminar",
                    onConfirm = {},
                    dismissLabel = "Cancelar",
                    onDismiss = {},
                )
            }
        }

        compose.onNodeWithTag("aq-dialog-pane", useUnmergedTree = true)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Eliminar destino"))
    }
}
