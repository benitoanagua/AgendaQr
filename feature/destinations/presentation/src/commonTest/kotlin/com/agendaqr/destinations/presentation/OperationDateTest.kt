package com.agendaqr.destinations.presentation

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
}
