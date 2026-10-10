package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTileGrid
import com.agendaqr.core.ui.components.XauxaTileItem
import com.agendaqr.core.ui.components.XauxaTileSize
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.core.ui.theme.accentFor
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato responsive en un viewport de 360 dp, complementario al contrato
 * de 320 dp: 100/150/200 % de escala tipográfica, desbordamiento
 * horizontal y geometría de tiles derivada de la unidad calculada (1×1
 * cuadrado; nunca por debajo de la unidad mínima).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class XauxaResponsiveLayoutContractTest {
    @get:Rule
    val compose = createComposeRule()

    private val tileLabel = "Registrar actividad con comprobante asociado"
    private val wideLabel = "Último QR o actividad reciente"
    private val inputLabel = "Persona o entidad del pago registrado"
    private val inputError = "Usa el formato dd/mm/aaaa"

    private fun composeAtScales(
        content: @Composable () -> Unit,
        check: (Float) -> Unit,
    ) {
        var scale by mutableStateOf(1f)
        compose.setContent {
            XauxaTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    val currentDensity = LocalDensity.current
                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            density = currentDensity.density,
                            fontScale = scale,
                        ),
                    ) {
                        content()
                    }
                }
            }
        }
        listOf(1f, 1.5f, 2f).forEach { value ->
            compose.runOnIdle { scale = value }
            compose.waitForIdle()
            check(value)
        }
    }

    private fun assertTextStaysInsideViewport(text: String) {
        val screenRight = compose.onRoot().getBoundsInRoot().right.value
        val nodes = compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes()
        assertTrue("No se encontró texto visible: '$text'", nodes.isNotEmpty())
        nodes.forEach { node ->
            assertTrue(
                "El texto '$text' desborda el viewport de 360 dp: right=${node.boundsInRoot.right}, screen=$screenRight",
                node.boundsInRoot.left >= -1f && node.boundsInRoot.right <= screenRight + 1f,
            )
        }
    }

    @Test
    fun controls_keep_labels_and_error_inside_360dp_at_100_150_and_200_percent() {
        composeAtScales(
            {
                XauxaPrimaryButton(label = "Guardar cambios de la actividad", onClick = {})
                XauxaTextInput(
                    label = inputLabel,
                    value = "",
                    onValueChange = {},
                    isError = true,
                    errorMessage = inputError,
                )
            },
        ) {
            assertTextStaysInsideViewport("Guardar cambios de la actividad")
            assertTextStaysInsideViewport(inputLabel)
            assertTextStaysInsideViewport(inputError)
            compose.onAllNodesWithText("Guardar cambios de la actividad").fetchSemanticsNodes().forEachIndexed { i, _ ->
                compose.onAllNodesWithText("Guardar cambios de la actividad")[i].assertHeightIsAtLeast(48.dp)
            }
        }
    }

    @Test
    fun metro_tiles_grow_at_large_font_scale_without_horizontal_overflow() {
        composeAtScales(
            {
                XauxaTileGrid(
                    items = listOf(
                        XauxaTileItem(
                            label = tileLabel,
                            accent = accentFor("responsive-test"),
                            onClick = {},
                        ),
                        XauxaTileItem(
                            label = "Favoritos",
                            accent = XauxaAccents.System,
                            onClick = {},
                        ),
                        XauxaTileItem(
                            label = wideLabel,
                            accent = XauxaAccents.System,
                            size = XauxaTileSize.WIDE,
                            onClick = {},
                        ),
                    )
                )
            },
        ) { scale ->
            assertTextStaysInsideViewport(tileLabel)
            assertTextStaysInsideViewport("Favoritos")
            assertTextStaysInsideViewport(wideLabel)
            // El alto del 1×1 se deriva de la unidad calculada: nunca baja
            // del objetivo mínimo (76 dp) y acompaña al ancho (cuadrado).
            val tile = compose.onAllNodesWithText(tileLabel, substring = true)
                .fetchSemanticsNodes().single()
            assertTrue(
                "El tile queda por debajo del objetivo mínimo con escala $scale: ${tile.boundsInRoot}",
                tile.boundsInRoot.height >= 76f,
            )
            assertTrue(
                "El tile 1×1 deja de ser cuadrado con escala $scale: ${tile.boundsInRoot}",
                kotlin.math.abs(tile.boundsInRoot.height - tile.boundsInRoot.width) <= 2f,
            )
        }
    }
}
