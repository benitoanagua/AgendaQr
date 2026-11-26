package com.agendaqr.core.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Regresiones visuales de los defectos de la ronda 2026-11-25. Cada prueba
 * fija un comportamiento (no una captura): el banner abraza su contenido, el
 * chip nunca colapsa su etiqueta y el botón nunca la parte en dos líneas.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class AqVisualRegressionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun alert_in_bounded_parent_wraps_content_height() {
        compose.setContent {
            AqTheme {
                Box(Modifier.fillMaxSize()) {
                    AqAlert(tone = AqTone.Danger, message = "Correo o contraseña incorrectos.")
                }
            }
        }
        val root = compose.onRoot().getBoundsInRoot()
        val alert = compose.onNodeWithText("Correo o contraseña incorrectos.").getBoundsInRoot()
        assertTrue("el banner no debe llenar la pantalla", (alert.bottom - alert.top) < (root.bottom - root.top) / 2)
    }

    @Test
    fun alert_text_has_internal_horizontal_padding() {
        compose.setContent {
            AqTheme {
                Box(Modifier.fillMaxSize()) {
                    AqAlert(tone = AqTone.Danger, message = "Sincronización pendiente: 1")
                }
            }
        }
        val text = compose.onNodeWithText("Sincronización pendiente: 1").getBoundsInRoot()
        assertTrue("el texto debe separarse ≥ 16 dp del borde", text.left >= 16.dp)
    }

    @Test
    fun chip_label_is_not_truncated_inside_horizontal_scroll() {
        compose.setContent {
            AqTheme {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    AqChip(text = "Actividades", selected = true, icon = AqIcons.Check)
                    AqChip(text = "Contextos")
                }
            }
        }
        // Con el padding Moon v1 (12 dp) el chip «Actividades» mide ~55 dp;
        // el umbral sigue detectando el colapso a icono+ellipsis (~32 dp).
        val label = compose.onNodeWithText("Actividades").getBoundsInRoot()
        assertTrue("etiqueta colapsada: ${label.right - label.left}", (label.right - label.left) > 48.dp)
    }

    @Test
    fun button_label_stays_on_one_line() {
        compose.setContent {
            AqTheme {
                Row(Modifier.height(48.dp)) {
                    AqButton(label = "Guardar de todos modos", onClick = {}, variant = AqButtonVariant.Text)
                    AqButton(label = "Descartar", onClick = {}, variant = AqButtonVariant.Danger)
                }
            }
        }
        val bounds = compose.onNodeWithText("Descartar").getBoundsInRoot()
        assertTrue("alto de una sola línea: ${bounds.bottom - bounds.top}", (bounds.bottom - bounds.top) <= 48.dp)
    }
}
