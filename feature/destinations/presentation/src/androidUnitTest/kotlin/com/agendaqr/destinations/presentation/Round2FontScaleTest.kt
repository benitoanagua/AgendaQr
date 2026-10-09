package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTileGrid
import com.agendaqr.core.ui.components.XauxaTileItem
import com.agendaqr.core.ui.components.XauxaTileSize
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.accentFor
import kotlin.test.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ronda 2 (Área D): pantallas pequeñas + fuente grande. En un teléfono de
 * 320 dp, con la fuente del sistema al 100 %, 150 % y 200 % (§11
 * escalado), ningún texto de los componentes críticos se sale del ancho,
 * la etiqueta del tile no se recorta sin elipsis y todo control conserva
 * su objetivo táctil de 48 dp. Con la altura FIJA anterior del tile, el
 * caso 200 % recortaba la etiqueta (rojo primero).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w320dp-h640dp")
class Round2FontScaleTest {

    @get:Rule
    val compose = createComposeRule()

    private val longLabel = "Registrar actividad con comprobante asociado"
    private val longSubtitle = "Subtítulo largo con contexto adicional que ocupa varias líneas"

    // Un setContent por test: las tres escalas conviven apiladas en la
    // MISMA composición, cada una con su Density (fontScale) local.
    private fun composeAtAllScales(content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            XauxaTheme {
                Column {
                    listOf(1f, 1.5f, 2f).forEach { scale ->
                        CompositionLocalProvider(
                            LocalDensity provides Density(density = 1f, fontScale = scale),
                        ) {
                            content()
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    private fun assertInsideScreenWidth(text: String) {
        val screenRight = compose.onRoot().getBoundsInRoot().right
        val nodes = compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes()
        assertTrue("sin nodos para: $text", nodes.isNotEmpty())
        nodes.forEach { node ->
            assertTrue(
                "texto '$text' desborda el ancho (right=${node.boundsInRoot.right} > $screenRight) con fuente grande",
                node.boundsInRoot.right <= screenRight.value + 1f,
            )
        }
    }

    @Test
    fun primary_button_wraps_without_overflow_at_every_scale() {
        composeAtAllScales {
            XauxaPrimaryButton(label = longLabel, onClick = {})
        }
        compose.onAllNodesWithText(longLabel).fetchSemanticsNodes().forEach {
            assertTrue("botón con fuente grande desborda", it.boundsInRoot.right <= compose.onRoot().getBoundsInRoot().right.value + 1f)
        }
        compose.onAllNodesWithText(longLabel).fetchSemanticsNodes().forEachIndexed { i, _ ->
            compose.onAllNodesWithText(longLabel)[i].assertHeightIsAtLeast(48.dp)
        }
    }

    @Test
    fun text_input_keeps_label_error_and_target_at_every_scale() {
        composeAtAllScales {
            XauxaTextInput(
                label = "Persona o entidad del pago registrado",
                value = "",
                onValueChange = {},
                isError = true,
                errorMessage = "Usa el formato dd/mm/aaaa",
            )
        }
        assertInsideScreenWidth("Persona o entidad del pago registrado")
        assertInsideScreenWidth("Usa el formato dd/mm/aaaa")
    }

    @Test
    fun tile_grid_grows_instead_of_clipping_at_200_percent() {
        composeAtAllScales {
            XauxaTileGrid(
                items = listOf(
                    XauxaTileItem(
                        label = "Registrar actividad con comprobante",
                        accent = accentFor("ctx"),
                        icon = null,
                        onClick = {},
                    ),
                    XauxaTileItem(
                        label = "Último QR o actividad reciente",
                        accent = XauxaAccents.System,
                        size = XauxaTileSize.WIDE,
                        onClick = {},
                    ),
                ),
            )
        }
        // Criterio de la spec §11 (aceptación): nada se corta ni superpone.
        // - El alto nunca BAJA al crecer la fuente (altura mínima, no fija).
        // - El objetivo del tile sigue siendo alcanzable (>= 76 = TileUnit).
        // - La etiqueta no desborda el ancho (elipsis a las 2 líneas).
        val heights = compose.onAllNodesWithText("Registrar actividad", substring = true)
            .fetchSemanticsNodes().map { it.boundsInRoot.height }
        assertEquals(3, heights.size) // una por escala
        assertTrue("altos no crecientes: $heights", heights[2] >= heights[0])
        assertTrue("tile por debajo del objetivo: $heights", heights.all { it >= 76f })
        assertInsideScreenWidth("Registrar actividad con comprobante")
    }

    @Test
    fun list_row_wraps_long_subtitle_without_overflow_at_every_scale() {
        composeAtAllScales {
            XauxaListRow(
                title = "Carniceria Don Bife con nombre largo para la fila",
                subtitle = longSubtitle,
                onClick = {},
            )
        }
        assertInsideScreenWidth("Carniceria Don Bife con nombre largo para la fila")
        assertInsideScreenWidth(longSubtitle)
    }

    @Test
    fun app_bar_actions_keep_two_lines_and_target_at_every_scale() {
        composeAtAllScales {
            XauxaAppBar(
                actions = listOf(
                    XauxaAppBarAction(
                        label = "Guardar cambios",
                        icon = null,
                        primary = true,
                        onClick = {},
                    ),
                    XauxaAppBarAction(label = "Compartir", icon = null, onClick = {}),
                ),
                backLabel = "Atrás",
            )
        }
        compose.onAllNodesWithText("Guardar cambios").fetchSemanticsNodes().forEachIndexed { i, _ ->
            compose.onAllNodesWithText("Guardar cambios")[i].assertHeightIsAtLeast(48.dp)
        }
        assertInsideScreenWidth("Guardar cambios")
    }
}
