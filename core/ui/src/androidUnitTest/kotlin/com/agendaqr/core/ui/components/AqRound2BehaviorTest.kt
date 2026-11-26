package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de comportamiento de la ronda 2 de defectos visuales:
 * texto alineado al inicio bajo la etiqueta flotante, placeholder de
 * búsqueda que desaparece al escribir y etiqueta de botón centrada en
 * `fullWidth`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class AqRound2BehaviorTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun floating_label_value_text_stays_left_aligned() {
        compose.setContent {
            AqTheme {
                Box(Modifier.fillMaxSize()) {
                    AqTextInput(
                        label = "Correo",
                        value = "usuario@agendaqr.dev",
                        onValueChange = {},
                    )
                }
            }
        }
        compose.waitForIdle()
        val box = compose.onNodeWithTag("aq-text-input-visual").getBoundsInRoot()
        val editable = compose.onNode(hasSetTextAction()).getBoundsInRoot()
        // Alineado al inicio: el texto arranca junto al padding interno,
        // no en el centro de la caja (BottomCenter lo centraba).
        assertTrue(
            "texto a la izquierda: editable.left=${editable.left}, box.left=${box.left}",
            editable.left - box.left < 64.dp,
        )
    }

    @Test
    fun search_input_with_value_shows_no_hint_nor_label() {
        compose.setContent {
            AqTheme {
                Box(Modifier.fillMaxSize()) {
                    AqSearchInput(
                        label = "Buscar",
                        value = "Carnicería",
                        onValueChange = {},
                        placeholder = "Buscar en Agenda QR",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Buscar en Agenda QR").assertDoesNotExist()
        // «Buscar» solo puede vivir como nombre accesible del campo editable;
        // ni el hint ni una etiqueta flotante deben renderizarse.
        compose.onAllNodes(hasText("Buscar")).fetchSemanticsNodes().forEach { node ->
            assertTrue(
                "«Buscar» no debe ser texto visible",
                node.config.contains(SemanticsActions.SetText),
            )
        }
    }

    @Test
    fun full_width_button_label_is_horizontally_centered() {
        compose.setContent {
            AqTheme {
                Box(Modifier.fillMaxSize()) {
                    AqButton(
                        label = "Iniciar sesión",
                        onClick = {},
                        variant = AqButtonVariant.Primary,
                        fullWidth = true,
                    )
                }
            }
        }
        compose.waitForIdle()
        val visual = compose.onNodeWithTag("aq-button-visual", useUnmergedTree = true)
            .getBoundsInRoot()
        // El BasicText de la etiqueta limpia su semántica pero conserva sus
        // bounds: es el único nodo con `text` vacío dentro del botón.
        val emptyText = SemanticsMatcher("texto de etiqueta limpiado") { node ->
            node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
                .any { it.text.isEmpty() }
        }
        val label = compose
            .onAllNodes(emptyText, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .map { node ->
                val r = node.boundsInRoot
                with(compose.density) {
                    androidx.compose.ui.unit.DpRect(
                        r.left.toDp(), r.top.toDp(), r.right.toDp(), r.bottom.toDp(),
                    )
                }
            }
            .filter {
                it.left >= visual.left && it.right <= visual.right &&
                    it.right - it.left > 0.dp
            }
            .maxByOrNull { it.right - it.left }
            ?: error("no se encontró el nodo de la etiqueta")
        val labelCenter = (label.left + label.right) / 2
        val visualCenter = (visual.left + visual.right) / 2
        assertTrue(
            "etiqueta centrada: labelCenter=$labelCenter visualCenter=$visualCenter",
            kotlin.math.abs((labelCenter - visualCenter).value) <= 2f,
        )
    }
}
