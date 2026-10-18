package com.agendaqr.destinations.presentation

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * U-03: el editor guarda fecha Y hora (sin texto libre dd/mm/aaaa). La
 * fecha por defecto incluye la hora; "Ayer" resta un día civil local;
 * los datos antiguos a medianoche se muestran sin hora y no se
 * reescriben.
 */
class UxDateReproductionTest {

    private val laPaz = TimeZone.of("America/La_Paz")

    @Test
    fun combineDateAndTime_round_trips_through_formatDateTime() {
        // 16/10/2026 14:30 La Paz: día civil (medianoche UTC) + hora.
        val dayUtc = utcDayMillis(Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds(), laPaz)
        val combined = combineDateAndTime(dayUtc, 14, 30, laPaz)
        assertEquals("16/10/2026 14:30", formatDateTime(combined, laPaz))
        assertFalse(isStartOfDay(combined, laPaz))
    }

    @Test
    fun legacy_midnight_shows_without_hour_and_is_detected() {
        val midnight = combineDateAndTime(
            utcDayMillis(Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds(), laPaz),
            0,
            0,
            laPaz,
        )
        assertTrue(isStartOfDay(midnight, laPaz))
        assertEquals("16/10/2026", formatDateTime(midnight, laPaz))
    }

    @Test
    fun yesterday_keeps_the_time_of_day() {
        val now = Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds()
        val yesterday = minusCivilDays(now, 1, laPaz)
        assertEquals("15/10/2026 14:30", formatDateTime(yesterday, laPaz))
    }

    @Test
    fun same_day_activities_keep_distinct_times_for_ordering() {
        val dayUtc = utcDayMillis(Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds(), laPaz)
        val morning = combineDateAndTime(dayUtc, 9, 15, laPaz)
        val evening = combineDateAndTime(dayUtc, 20, 45, laPaz)
        assertTrue(morning < evening)
        assertEquals("16/10/2026 09:15", formatDateTime(morning, laPaz))
        assertEquals("16/10/2026 20:45", formatDateTime(evening, laPaz))
    }

    @Test
    fun relative_day_labels_today_and_yesterday_only() {
        val now = Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds()
        assertEquals(AppStrings.Hoy, relativeDayLabel(now, now, laPaz))
        assertEquals(AppStrings.Ayer, relativeDayLabel(minusCivilDays(now, 1, laPaz), now, laPaz))
        assertNull(relativeDayLabel(minusCivilDays(now, 2, laPaz), now, laPaz))
    }
}
