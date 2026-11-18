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
                    AqListRow(
                        title = "Destino ejemplo",
                        subtitle = "Comprobante recibido",
                        showChevron = true,
                        onClick = {},
                    )
                    AqListRow(
                        title = "Fila seleccionada",
                        subtitle = "Estado activo",
                        selected = true,
                        showChevron = true,
                        onClick = {},
                    )
                    AqListRow(
                        title = "Fila sin acción",
                        subtitle = "Sin interacción",
                        showChevron = true,
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
                AqListRow(
                    title = longTitle,
                    subtitle = "Subtítulo",
                    onClick = {},
                )
            }
        }

        compose.onAllNodesWithText(longTitle).assertCountEquals(1)
        compose.onAllNodesWithText("Subtítulo").assertCountEquals(1)
    }
}
