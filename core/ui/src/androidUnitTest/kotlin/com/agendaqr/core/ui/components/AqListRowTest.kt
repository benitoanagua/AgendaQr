package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
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
class AqListRowTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun list_row_renders_normal_selected_and_without_action() {
        compose.setContent {
            AqTheme {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    AqMenuItem(
                        title = "Destino ejemplo",
                        subtitle = "Comprobante recibido",
                        onClick = {},
                    )
                    AqMenuItem(
                        title = "Fila seleccionada",
                        subtitle = "Estado activo",
                        selected = true,
                        onClick = {},
                    )
                    AqMenuItem(
                        title = "Fila sin acción",
                        subtitle = "Sin interacción",
                        onClick = null,
                    )
                }
            }
        }

        compose.onAllNodesWithText("Destino ejemplo").assertCountEquals(1)
        compose.onAllNodesWithText("Comprobante recibido").assertCountEquals(1)
        compose.onAllNodesWithText("Fila seleccionada").assertCountEquals(1)
        compose.onAllNodesWithText("Estado activo").assertCountEquals(1)
        compose.onAllNodesWithText("Fila sin acción").assertCountEquals(1)
        compose.onAllNodesWithText("Sin interacción").assertCountEquals(1)
    }

    @Test
    fun list_row_keeps_long_title_in_semantics() {
        val longTitle =
            "Este es un título de fila extremadamente largo que debe recortarse con elipsis " +
                "cuando no cabe en el ancho disponible de la pantalla compacta"
        compose.setContent {
            AqTheme {
                AqMenuItem(
                    title = longTitle,
                    subtitle = "Subtítulo",
                    onClick = {},
                )
            }
        }

        compose.onAllNodesWithText(longTitle).assertCountEquals(1)
        compose.onAllNodesWithText("Subtítulo").assertCountEquals(1)
    }

    @Test
    fun long_click_fires_only_long_click_and_tap_fires_only_click() {
        var taps = 0
        var longs = 0
        compose.setContent {
            AqTheme {
                AqMenuItem(
                    title = "Fila con pulsación larga",
                    onClick = { taps++ },
                    onLongClick = { longs++ },
                    onLongClickLabel = "Seleccionar",
                )
            }
        }
        // La pulsación larga se verifica vía la acción semántica
        // OnLongClick: la inyección táctil real no es fiable bajo Robolectric.
        compose.onNodeWithText("Fila con pulsación larga")
            .performSemanticsAction(SemanticsActions.OnLongClick)
        compose.waitForIdle()
        kotlin.test.assertEquals(1 to 0, longs to taps)
        compose.onNodeWithText("Fila con pulsación larga")
            .performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
        kotlin.test.assertEquals(1 to 1, longs to taps)
    }
}
