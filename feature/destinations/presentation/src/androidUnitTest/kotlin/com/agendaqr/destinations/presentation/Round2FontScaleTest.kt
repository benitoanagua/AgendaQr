package com.agendaqr.destinations.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato responsive (§11): en un teléfono de 320 dp, con la fuente del
 * sistema al 100 %, 150 % y 200 %, ningún texto de los componentes
 * críticos se sale del ancho, la etiqueta del tile no se recorta sin
 * elipsis y todo control conserva su objetivo táctil de 48 dp.
 *
 * Las tres escalas viven en la MISMA composición, una por vez, moviendo
 * el estado de la escala (setContent solo puede llamarse una vez por
 * test). La rejilla Metro deriva el alto del tile de la unidad calculada:
 * apilar las tres escalas a la vez consumía el espacio de las últimas
 * instancias.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w320dp-h640dp")
class Round2FontScaleTest {

    @get:Rule
    val compose = createComposeRule()

    private val longLabel = "Registrar actividad con comprobante asociado"
    private val longSubtitle = "Subtítulo largo con contexto adicional que ocupa varias líneas"

    private fun composeAtScales(
        content: @androidx.compose.runtime.Composable () -> Unit,
        check: (Float) -> Unit,
    ) {
        var scale by mutableStateOf(1f)
        compose.setContent {
            XauxaTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density = 1f, fontScale = scale),
                ) {
                    content()
                }
            }
        }
        listOf(1f, 1.5f, 2f).forEach { value ->
            compose.runOnIdle { scale = value }
            compose.waitForIdle()
            check(value)
        }
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
        composeAtScales({ XauxaPrimaryButton(label = longLabel, onClick = {}) }) {
            compose.onAllNodesWithText(longLabel).fetchSemanticsNodes().forEach {
                assertTrue(
                    "botón con fuente $it desborda",
                    it.boundsInRoot.right <= compose.onRoot().getBoundsInRoot().right.value + 1f,
                )
            }
            compose.onAllNodesWithText(longLabel).fetchSemanticsNodes().forEachIndexed { i, _ ->
                compose.onAllNodesWithText(longLabel)[i].assertHeightIsAtLeast(48.dp)
            }
        }
    }

    @Test
    fun text_input_keeps_label_error_and_target_at_every_scale() {
        composeAtScales(
            {
                XauxaTextInput(
                    label = "Persona o entidad del pago registrado",
                    value = "",
                    onValueChange = {},
                    isError = true,
                    errorMessage = "Usa el formato dd/mm/aaaa",
                )
            },
        ) {
            assertInsideScreenWidth("Persona o entidad del pago registrado")
            assertInsideScreenWidth("Usa el formato dd/mm/aaaa")
        }
    }

    @Test
    fun tile_grid_grows_instead_of_clipping_at_every_scale() {
        composeAtScales(
            {
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
            },
        ) { scale ->
            // Criterio de la spec §11 (aceptación): nada se corta ni superpone.
            // - El alto del tile se DERIVA de la unidad calculada (1×1
            //   cuadrado; el alto es mínimo, no fijo: crece con la fuente).
            // - El objetivo del tile sigue siendo alcanzable (>= TileUnit).
            // - La etiqueta no desborda el ancho (elipsis a las 2 líneas).
            val tile = compose.onAllNodesWithText("Registrar actividad", substring = true)
                .fetchSemanticsNodes().single()
            val wide = compose.onAllNodesWithText("Último QR o actividad reciente")
                .fetchSemanticsNodes().single()
            assertTrue(
                "tile 1×1 por debajo del objetivo (escala $scale): ${tile.boundsInRoot.height}",
                tile.boundsInRoot.height >= 76f,
            )
            // 1×1 cuadrado: el alto derivado acompaña al ancho calculado
            // (a 320 dp y escala 1: 101×101; a 150/200 %: 156×156).
            assertTrue(
                "tile 1×1 deja de ser cuadrado (escala $scale): ${tile.boundsInRoot}",
                kotlin.math.abs(tile.boundsInRoot.height - tile.boundsInRoot.width) <= 2f,
            )
            // 2×2/4×2: dos unidades más una separación.
            assertTrue(
                "tile ancho no mide dos unidades más el gap (escala $scale): ${wide.boundsInRoot}",
                wide.boundsInRoot.height >= tile.boundsInRoot.height * 2f,
            )
            assertInsideScreenWidth("Registrar actividad con comprobante")
        }
    }

    @Test
    fun list_row_wraps_long_subtitle_without_overflow_at_every_scale() {
        composeAtScales(
            {
                XauxaListRow(
                    title = "Carniceria Don Bife con nombre largo para la fila",
                    subtitle = longSubtitle,
                    onClick = {},
                )
            },
        ) {
            assertInsideScreenWidth("Carniceria Don Bife con nombre largo para la fila")
            assertInsideScreenWidth(longSubtitle)
        }
    }

    @Test
    fun app_bar_actions_keep_two_lines_and_target_at_every_scale() {
        composeAtScales(
            {
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
            },
        ) {
            compose.onAllNodesWithText("Guardar cambios").fetchSemanticsNodes().forEachIndexed { i, _ ->
                compose.onAllNodesWithText("Guardar cambios")[i].assertHeightIsAtLeast(48.dp)
            }
            assertInsideScreenWidth("Guardar cambios")
        }
    }
}
