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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Comprueba los flujos de confirmación y descarte de fecha y hora. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmDateTimePickerTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun date_dialog_confirms_the_selected_day() {
        // Medianoche UTC exacta de un día civil: el picker la conserva.
        val utcMidnight = 1_791_158_400_000L
        val confirmed = AtomicReference<Long?>()
        val dismissed = AtomicBoolean(false)
        compose.setContent {
            AmTheme {
                AmScreenScaffold {
                    AmDatePickerDialog(
                        selectedDateUtc = utcMidnight,
                        onConfirm = { confirmed.set(it) },
                        onDismiss = { dismissed.set(true) },
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performClick()
        compose.waitForIdle()
        assertEquals(utcMidnight, confirmed.get())
        assertTrue(!dismissed.get())
    }

    @Test
    fun date_dialog_dismisses_without_confirming() {
        val confirmed = AtomicReference<Long?>()
        val dismissed = AtomicBoolean(false)
        compose.setContent {
            AmTheme {
                AmScreenScaffold {
                    AmDatePickerDialog(
                        selectedDateUtc = null,
                        onConfirm = { confirmed.set(it) },
                        onDismiss = { dismissed.set(true) },
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cancelar").performClick()
        compose.waitForIdle()
        assertTrue(confirmed.get() == null)
        assertTrue(dismissed.get())
    }

    @Test
    fun time_dialog_confirms_the_entered_time() {
        val confirmed = AtomicReference<Pair<Int, Int>?>()
        compose.setContent {
            AmTheme {
                AmScreenScaffold {
                    AmTimePickerDialog(
                        initialHour = 14,
                        initialMinute = 30,
                        onConfirm = { hour, minute -> confirmed.set(hour to minute) },
                        onDismiss = {},
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performClick()
        compose.waitForIdle()
        assertEquals(14 to 30, confirmed.get())
    }
}
