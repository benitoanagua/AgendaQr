package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTheme
import kotlin.math.abs
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Contrato de propiedad del espacio (ADR-0027): cada nivel atómico aplica
 * SOLO el espaciado que le corresponde.
 * - El banner (organismo) no añade margen externo; el host lo pone.
 * - La tarjeta tocable (organismo) aporta 16 dp internos.
 * - La fila de lista (molécula dentro del organismo) aporta 12 dp verticales.
 * - El dock inferior (organismo) aporta el margen de pantalla y los insets.
 *
 * Las tolerancias son de 1 dp porque Robolectric redondea a píxeles.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AqAtomicSpacingContractTest {

    @get:Rule
    val compose = createComposeRule()

    private fun assertNear(expected: Float, actual: Float, label: String) {
        assertTrue(
            abs(expected - actual) <= 1f,
            "$label: esperado $expected dp, real $actual dp",
        )
    }

    @Test
    fun status_banner_does_not_apply_its_own_margin() {
        compose.setContent {
            AqTheme {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AqStatusBanner(
                        message = "Mensaje",
                        tone = AqTone.Danger,
                    )
                }
            }
        }
        compose.waitForIdle()
        val root = compose.onRoot().getBoundsInRoot()
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        assertNear(root.left.value, banner.left.value, "banner sin margen propio (izquierda)")
        assertNear(root.right.value, banner.right.value, "banner sin margen propio (derecha)")
    }

    @Test
    fun host_owns_the_banner_screen_margin() {
        compose.setContent {
            AqTheme {
                Column(modifier = Modifier.padding(horizontal = AqSpacing.ScreenMargin)) {
                    AqStatusBanner(
                        message = "Mensaje",
                        tone = AqTone.Danger,
                    )
                }
            }
        }
        compose.waitForIdle()
        val root = compose.onRoot().getBoundsInRoot()
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        assertNear(AqSpacing.ScreenMargin.value, banner.left.value, "host aplica margen izquierdo")
        assertNear(
            AqSpacing.ScreenMargin.value,
            (root.right - banner.right).value,
            "host aplica margen derecho",
        )
    }

    @Test
    fun tappable_card_uses_16dp_internal_padding() {
        compose.setContent {
            AqTheme {
                AqTappableCard(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    AqText("Contenido")
                }
            }
        }
        compose.waitForIdle()
        val card = compose.onNodeWithTag("aq-tappable-card").getBoundsInRoot()
        val content = compose.onNodeWithText("Contenido", useUnmergedTree = true).getBoundsInRoot()
        assertNear(AqSpacing.Lg.value, (content.left - card.left).value, "tarjeta: inset izquierdo")
        assertNear(AqSpacing.Lg.value, (content.top - card.top).value, "tarjeta: inset superior")
    }

    @Test
    fun list_row_uses_12dp_vertical_padding() {
        compose.setContent {
            AqTheme {
                AqListBody(modifier = Modifier.fillMaxWidth()) {
                    AqListRow(title = "Fila")
                }
            }
        }
        compose.waitForIdle()
        val body = compose.onNodeWithTag("aq-list-body").getBoundsInRoot()
        val title = compose.onNodeWithText("Fila", useUnmergedTree = true).getBoundsInRoot()
        assertNear(AqSpacing.Lg.value, (title.left - body.left).value, "fila: inset horizontal")
        assertNear(AqSpacing.MdPlus.value, (title.top - body.top).value, "fila: inset vertical")
    }

    @Test
    fun bottom_action_dock_owns_screen_margin_and_insets() {
        compose.setContent {
            AqTheme {
                AqBottomActionDock(modifier = Modifier.fillMaxWidth()) {
                    AqPrimaryButton(
                        label = "Guardar",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        compose.waitForIdle()
        val dock = compose.onNodeWithTag("aq-bottom-action-dock").getBoundsInRoot()
        val button = compose.onNodeWithText("Guardar").getBoundsInRoot()
        assertNear(
            AqSpacing.ScreenMargin.value,
            (button.left - dock.left).value,
            "dock: margen de pantalla izquierdo",
        )
    }

    @Test
    fun amount_field_owns_its_error_message() {
        compose.setContent {
            AqTheme {
                AqAmountField(
                    label = "Importe",
                    value = "abc",
                    onValueChange = {},
                    state = AqFieldState(isError = true, errorMessage = "Importe inválido"),
                )
            }
        }
        compose.waitForIdle()
        val label = compose.onNodeWithText("Importe").getBoundsInRoot()
        val error = compose.onNodeWithText("Importe inválido").getBoundsInRoot()
        assertTrue(
            error.top.value >= label.bottom.value,
            "el error debe vivir bajo la etiqueta (label=$label error=$error)",
        )
        assertTrue(
            error.left.value >= label.left.value - 1f,
            "el error no debe salir del campo por la izquierda (label=$label error=$error)",
        )
    }

    @Test
    fun spacing_tokens_expose_the_polaris_steps() {
        // D-01: 1rem = 16 dp y la escala publicada conserva 12/20 dp.
        assertNear(12f, AqSpacing.MdPlus.value, "AqSpacing.MdPlus")
        assertNear(20f, AqSpacing.LgPlus.value, "AqSpacing.LgPlus")
        assertNear(16f, AqSpacing.ScreenMargin.value, "AqSpacing.ScreenMargin")
    }
}
