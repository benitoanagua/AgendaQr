package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

/**
 * T-f — en los diálogos de lista la acción principal es elegir (§1):
 * el confirmar puede pintarse como acción de texto en vez de primario.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class XauxaDialogProminenceTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun confirm_as_text_still_confirms_on_click() {
        var confirmed = false
        compose.setContent {
            XauxaTheme {
                XauxaDialog(
                    title = "Seleccionar contexto",
                    confirmLabel = "Cancelar",
                    confirmAsText = true,
                    onConfirm = { confirmed = true },
                    dismissLabel = "Quitar contexto",
                    onDismiss = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cancelar").performClick()
        assertTrue(confirmed)
    }
}
