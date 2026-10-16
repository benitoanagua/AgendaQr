package com.agendaqr.destinations.presentation

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Reproducción U-03: el editor guarda el día a medianoche
 * (`parseDate`/`formatDate`), por lo que actividades del mismo día no se
 * ordenan. Este test falla con el comportamiento actual y pasa cuando la
 * fecha por defecto conserva la hora.
 */
class UxDateReproductionTest {

    @Test
    fun default_occurredAt_preserves_time_of_day() {
        val laPaz = TimeZone.of("America/La_Paz")
        // 14:30 hora civil en La Paz (no medianoche).
        val now = Instant.parse("2026-10-16T18:30:00Z").toEpochMilliseconds()
        assertEquals(
            now,
            parseDate(formatDate(now, laPaz), laPaz),
            "U-03: la fecha del editor no debe truncar la hora del día",
        )
    }
}
