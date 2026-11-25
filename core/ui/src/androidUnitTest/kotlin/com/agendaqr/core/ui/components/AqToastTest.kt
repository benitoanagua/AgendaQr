package com.agendaqr.core.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Pruebas de [AqToast], incluido el defecto de duración (antes 200 000 ms). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqToastTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun toast_renders_message_and_action() {
        compose.setContent {
            AqTheme {
                AqToast(
                    show = true,
                    message = "Operación guardada",
                    actionLabel = "Deshacer",
                    onAction = {},
                    durationMs = 5000,
                )
            }
        }
        compose.onNodeWithText("Operación guardada").assertExists()
        compose.onNodeWithText("Deshacer").assertExists()
    }

    @Test
    fun toast_duration_default_no_es_el_valor_defectuoso_de_200_segundos() {
        // Antes de la corrección, durationMs = DurationMediumMs * 1000 =
        // 200 000 ms (≈ 3,3 min). Tras la corrección es ToastDurationMs =
        // 3 000 ms (Moon `toast_theme` displayDuration por defecto).
        assertEquals(3000L, AqMotion.ToastDurationMs)
        assertTrue(AqMotion.ToastDurationMs < AqMotion.DurationMediumMs * 1000)
    }

    @Test
    fun toast_con_duration_cero_no_se_descarta_por_tiempo() {
        var dismissed = false
        compose.setContent {
            AqTheme {
                var show by remember { mutableStateOf(true) }
                AqToast(
                    show = show,
                    message = "Persistente",
                    onDismissRequest = {
                        dismissed = true
                        show = false
                    },
                    durationMs = 0,
                )
            }
        }
        compose.onNodeWithText("Persistente").assertExists()
        // Sin avance de tiempo real en este test; solo se verifica que se
        // monta y no se descarta automáticamente al poco (duration 0).
        assertFalse(dismissed)
    }
}
