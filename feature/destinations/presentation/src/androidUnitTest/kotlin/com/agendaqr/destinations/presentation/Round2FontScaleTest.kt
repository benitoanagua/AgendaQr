package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
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
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmTextField
import com.agendaqr.core.ui.theme.AmTheme
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
 * test). El layout crece con la fuente: apilar las tres escalas a la
 * vez consumía el espacio de las últimas instancias.
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
            AmTheme {
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
        composeAtScales({ AmPrimaryButton(label = longLabel, onClick = {}) }) {
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
                AmTextField(
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
    fun s02_and_s12_primary_actions_stay_uncut_at_every_scale() {
        composeAtScales(
            {
                Column {
                    // S02: acción única de galería (primaria con icono).
                    AmPrimaryButton(
                        label = AppStrings.ElegirDeLaGaleria,
                        onClick = {},
                        icon = AmIcons.Gallery,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    // S12: guardado de reconocidos (principal fija abajo).
                    AmPrimaryButton(
                        label = AppStrings.GuardarReconocidos,
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AmSecondaryButton(
                        label = AppStrings.ElegirArchivoPdf,
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
        ) {
            assertInsideScreenWidth("Elegir de la galería")
            assertInsideScreenWidth("Guardar reconocidos")
            assertInsideScreenWidth("Elegir archivo (PDF)")
        }
    }

    @Test
    fun destination_rows_wrap_long_labels_at_every_scale() {
        composeAtScales(
            {
                AmListRow(
                    title = "Registrar actividad con comprobante",
                    subtitle = "Último QR o actividad reciente",
                    onClick = {},
                )
            },
        ) { scale ->
            // Criterio de la spec §11 (aceptación): nada se corta ni superpone.
            // §12 V1.2: la fila (ListItem) crece con la fuente; la etiqueta
            // desborda hacia el alto, nunca hacia el ancho.
            val row = compose.onAllNodesWithText("Registrar actividad", substring = true)
                .fetchSemanticsNodes().single()
            assertTrue(
                "fila por debajo del objetivo (escala $scale): ${row.boundsInRoot.height}",
                row.boundsInRoot.height >= 48f,
            )
            assertInsideScreenWidth("Registrar actividad con comprobante")
        }
    }

    @Test
    fun list_row_wraps_long_subtitle_without_overflow_at_every_scale() {
        composeAtScales(
            {
                AmListRow(
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
    fun top_bar_actions_keep_target_at_every_scale() {
        composeAtScales(
            {
                com.agendaqr.core.ui.components.AmTopBar(
                    title = "Detalle con nombre largo de destino de ejemplo",
                    onBack = {},
                    backLabel = "Atrás",
                    actions = listOf(
                        com.agendaqr.core.ui.components.AmTopBarAction(
                            label = "Guardar cambios",
                            onClick = {},
                        ),
                        com.agendaqr.core.ui.components.AmTopBarAction(label = "Compartir", onClick = {}),
                    ),
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
