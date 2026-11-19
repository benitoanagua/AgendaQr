package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

/** Comprueba los flujos de confirmación y descarte de fecha y hora. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqDateTimePickerTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun date_dialog_confirms_the_selected_day() {
        // Medianoche UTC exacta de un día civil: el picker la conserva.
        val utcMidnight = 1_791_158_400_000L
        val confirmed = AtomicReference<Long?>()
        val dismissed = AtomicBoolean(false)
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqDatePickerDialog(
                        selectedDateUtc = utcMidnight,
                        title = "Fecha",
                        monthNames = months,
                        weekdays = weekdays,
                        previousMonthLabel = "Mes anterior",
                        nextMonthLabel = "Mes siguiente",
                        onConfirm = { confirmed.set(it) },
                        onDismiss = { dismissed.set(true) },
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(utcMidnight, confirmed.get())
        assertTrue(!dismissed.get())
    }

    @Test
    fun date_dialog_dismisses_without_confirming() {
        val confirmed = AtomicReference<Long?>()
        val dismissed = AtomicBoolean(false)
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqDatePickerDialog(
                        selectedDateUtc = null,
                        title = "Fecha",
                        monthNames = months,
                        weekdays = weekdays,
                        previousMonthLabel = "Mes anterior",
                        nextMonthLabel = "Mes siguiente",
                        onConfirm = { confirmed.set(it) },
                        onDismiss = { dismissed.set(true) },
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        compose.waitForIdle()
        assertTrue(confirmed.get() == null)
        assertTrue(dismissed.get())
    }

    @Test
    fun date_calendar_selects_day_and_keeps_utc_midnight_contract() {
        val initial = LocalDate(2026, 10, 5).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val confirmed = AtomicReference<Long?>()
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqDatePickerDialog(
                        selectedDateUtc = initial,
                        title = "Fecha",
                        onConfirm = { confirmed.set(it) },
                        onDismiss = {},
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                        monthNames = months,
                        weekdays = weekdays,
                        previousMonthLabel = "Mes anterior",
                        nextMonthLabel = "Mes siguiente",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithContentDescription("6 octubre 2026").performClick()
        compose.onNodeWithContentDescription("6 octubre 2026").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
        )
        compose.onNodeWithText("Elegir").performScrollTo().performClick()
        assertEquals(LocalDate(2026, 10, 6).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), confirmed.get())
    }

    @Test
    fun calendar_day_targets_remain_48dp_at_200_percent_font_scale() {
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                    AqScreenScaffold {
                        AqDatePickerDialog(
                            selectedDateUtc = LocalDate(2026, 10, 5).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
                            title = "Fecha",
                            onConfirm = {},
                            onDismiss = {},
                            confirmLabel = "Elegir",
                            dismissLabel = "Cancelar",
                            monthNames = months,
                            weekdays = weekdays,
                            previousMonthLabel = "Mes anterior",
                            nextMonthLabel = "Mes siguiente",
                        )
                    }
                }
            }
        }
        val bounds = compose.onNodeWithContentDescription("6 octubre 2026").getBoundsInRoot()
        val width = bounds.right - bounds.left
        assertTrue(width >= 48.dp, "Día debe conservar target de 48 dp a fuente 200 %; recibió $width")
    }

    @Test
    fun time_dialog_confirms_the_entered_time() {
        val confirmed = AtomicReference<Pair<Int, Int>?>()
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqTimePickerDialog(
                        initialHour = 14,
                        initialMinute = 30,
                        onConfirm = { hour, minute -> confirmed.set(hour to minute) },
                        onDismiss = {},
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                        title = "Hora",
                        hourLabel = "Hora",
                        minuteLabel = "Minutos",
                        invalidTimeMessage = "Hora inválida",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Elegir").performClick()
        compose.waitForIdle()
        assertEquals(14 to 30, confirmed.get())
    }

    @Test
    fun time_dialog_rejects_out_of_range_hour_and_announces_validation() {
        val confirmed = AtomicReference<Pair<Int, Int>?>()
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqTimePickerDialog(
                        initialHour = 14,
                        initialMinute = 30,
                        onConfirm = { hour, minute -> confirmed.set(hour to minute) },
                        onDismiss = {},
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                        title = "Hora",
                        hourLabel = "Hora",
                        minuteLabel = "Minutos",
                        invalidTimeMessage = "Hora inválida",
                    )
                }
            }
        }
        compose.onNodeWithContentDescription("Hora").performTextClearance()
        compose.onNodeWithContentDescription("Hora").performTextInput("25")
        compose.onNodeWithText("Elegir").performClick()
        compose.onNodeWithText("Hora inválida").assertExists()
        assertTrue(confirmed.get() == null)
    }

    @Test
    fun twelve_hour_time_input_converts_meridiem_to_24_hour_value() {
        val confirmed = AtomicReference<Pair<Int, Int>?>()
        compose.setContent {
            AqTheme {
                AqScreenScaffold {
                    AqTimePickerDialog(
                        initialHour = 14,
                        initialMinute = 30,
                        onConfirm = { hour, minute -> confirmed.set(hour to minute) },
                        onDismiss = {},
                        confirmLabel = "Elegir",
                        dismissLabel = "Cancelar",
                        title = "Hora",
                        hourLabel = "Hora",
                        minuteLabel = "Minutos",
                        invalidTimeMessage = "Hora inválida",
                        is24Hour = false,
                        meridiemLabels = listOf("a. m.", "p. m."),
                    )
                }
            }
        }
        compose.onNodeWithText("a. m.").performClick()
        compose.onNodeWithText("Elegir").performClick()
        assertEquals(2 to 30, confirmed.get())
    }

    private companion object {
        val months = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
        val weekdays = listOf("domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado")
    }
}
