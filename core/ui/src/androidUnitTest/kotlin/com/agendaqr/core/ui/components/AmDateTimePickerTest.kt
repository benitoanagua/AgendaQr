package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Los selectores de fecha y hora funcionan bajo Robolectric (a diferencia
 * de `ModalBottomSheet`, cuya ventana no despacha acciones semánticas):
 * son `Dialog`/`AlertDialog` de Compose y el contrato de pruebas UI los
 * cubre.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmDateTimePickerTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun date_dialog_confirms_the_selected_day() {
        // Medianoche UTC exacta de un día civil: el picker la conserva.
        val utcMidnight = 1_791_158_400_000L
        var confirmed: Long? = null
        var dismissed = false
        compose.setContent {
            AmTheme {
                AmDatePickerDialog(
                    selectedDateUtc = utcMidnight,
                    onConfirm = { confirmed = it },
                    onDismiss = { dismissed = true },
                    confirmLabel = "Elegir",
                    dismissLabel = "Cancelar",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performClick()
        assertEquals(utcMidnight, confirmed)
        assertTrue(!dismissed)
    }

    @Test
    fun date_dialog_dismisses_without_confirming() {
        var confirmed: Long? = null
        var dismissed = false
        compose.setContent {
            AmTheme {
                AmDatePickerDialog(
                    selectedDateUtc = null,
                    onConfirm = { confirmed = it },
                    onDismiss = { dismissed = true },
                    confirmLabel = "Elegir",
                    dismissLabel = "Cancelar",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cancelar").performClick()
        assertTrue(confirmed == null)
        assertTrue(dismissed)
    }

    @Test
    fun time_dialog_confirms_the_entered_time() {
        var confirmed: Pair<Int, Int>? = null
        compose.setContent {
            AmTheme {
                AmTimePickerDialog(
                    initialHour = 14,
                    initialMinute = 30,
                    onConfirm = { hour, minute -> confirmed = hour to minute },
                    onDismiss = {},
                    confirmLabel = "Elegir",
                    dismissLabel = "Cancelar",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performClick()
        assertEquals(14 to 30, confirmed)
    }
}
