package com.agendaqr.destinations.presentation

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OperationDateTest {
    @Test
    fun parseDate_round_trips_through_formatDate() {
        val millis = parseDate("15/03/2026")
        assertNotNull(millis)
        assertEquals("15/03/2026", formatDate(millis))
    }

    @Test
    fun parseDate_rejects_impossible_dates() {
        assertNull(parseDate("31/02/2026"))
        assertNull(parseDate("30/02/2024"))
        assertNull(parseDate("31/04/2026"))
        assertNull(parseDate("32/01/2026"))
        assertNull(parseDate("15/13/2026"))
        assertNull(parseDate("2026-03-15"))
        assertNull(parseDate(""))
        assertNull(parseDate("15/03/1899"))
    }

    @Test
    fun parseDate_accepts_leap_day() {
        val millis = parseDate("29/02/2024")
        assertNotNull(millis)
        assertEquals("29/02/2024", formatDate(millis))
        assertNull(parseDate("29/02/2025"))
    }

    @Test
    fun formatDate_uses_the_local_calendar_day_not_utc() {
        // Regresión: el editor proponía "mañana" en UTC-4 por la tarde porque
        // la fecha civil se calculaba en UTC. 2026-10-04T03:30Z sigue siendo
        // el 3 de octubre en La Paz (UTC-4) y ya es el 4 en UTC.
        val laPaz = TimeZone.of("America/La_Paz")
        val millis = Instant.parse("2026-10-04T03:30:00Z").toEpochMilliseconds()
        assertEquals("03/10/2026", formatDate(millis, laPaz))
        assertEquals("04/10/2026", formatDate(millis, TimeZone.UTC))

        // El valor por defecto del editor es la medianoche local del día
        // visto por el usuario, y renderiza el mismo día en que se escribió.
        val parsed = parseDate("03/10/2026", laPaz)
        assertNotNull(parsed)
        assertEquals("03/10/2026", formatDate(parsed!!, laPaz))
    }
}
