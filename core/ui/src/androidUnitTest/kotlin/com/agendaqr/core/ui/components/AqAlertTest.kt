package com.agendaqr.core.ui.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Pruebas de [AqAlert]: etiqueta/contenido separados y geometría Moon (ADR-0044). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqAlertTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun title_and_message_are_separate_text_nodes() {
        compose.setContent {
            AqTheme {
                AqAlert(
                    tone = AqTone.Info,
                    title = "Modo demostración",
                    message = "Datos ficticios: nada se sincroniza.",
                )
            }
        }
        compose.onNodeWithText("Modo demostración").assertExists()
        compose.onNodeWithText("Datos ficticios: nada se sincroniza.").assertExists()
    }

    @Test
    fun alert_without_title_renders_message_only() {
        compose.setContent {
            AqTheme {
                AqAlert(
                    tone = AqTone.Info,
                    message = "Sin conexión",
                )
            }
        }
        compose.onNodeWithText("Sin conexión").assertExists()
    }

    @Test
    fun alert_with_dismiss_action_keeps_moon_button_height() {
        compose.setContent {
            AqTheme {
                AqAlert(
                    tone = AqTone.Info,
                    message = "Mensaje",
                    dismissLabel = "Cerrar",
                    onDismiss = { },
                )
            }
        }
        // Moon: la acción de descarte es un botón sm (32 dp); el botón
        // entero es el objetivo táctil, sin expansión extra.
        compose.onNodeWithText("Cerrar").assertHeightIsEqualTo(32.dp)
    }
}
