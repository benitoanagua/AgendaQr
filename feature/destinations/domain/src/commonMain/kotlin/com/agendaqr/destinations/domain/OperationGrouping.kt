package com.agendaqr.destinations.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

/**
 * Grupo de un día civil dentro de un mes.
 */
data class DailyGroup(
    /** Etiqueta civil del día ("16", "17"...). */
    val dayLabel: String,
    /** Medianoche local del día (clave de agrupación). */
    val dayKey: Long,
    /** Operaciones de ese día, ordenadas descendente por occurredAt. */
    val operations: List<Operation>,
)

/**
 * Grupo de un mes civil con su balance neto por moneda y sus días.
 */
data class MonthlyGroup(
    /** Etiqueta del mes ("Mayo 2026"). */
    val monthLabel: String,
    /** Primer día del mes (clave de agrupación). */
    val monthKey: Long,
    /** Balance neto por moneda (texto con signo direccional y 2 decimales). */
    val balanceByCurrency: Map<String, String>,
    /** Días del mes, ordenados descendente. */
    val days: List<DailyGroup>,
)

/** Nombres de mes en español (dominio, sin depender de la UI). */
private val monthNames = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
)

/** Signo direccional por tipo de operación. */
private val signOf: (OperationType) -> Int = { if (it == OperationType.COBRO) +1 else -1 }

/**
 * Saldo neto por moneda: los cobros suman y los pagos restan (regla de
 * signo ya usada en la lista). Las actividades sin importe no suman.
 * El importe es una cadena decimal de unidades mayores (p. ej. "100,00" o
 * "100.50"). Se convierte a unidades menores (centavos) como `Long` para
 * evitar `Double` y las APIs `java.math.*` (no disponibles en
 * Kotlin/Native). La suma por moneda es entera y determinista; el
 * resultado usa 2 decimales y separador decimal punto (canonico).
 */
fun balanceByCurrency(operations: List<Operation>): Map<String, String> {
    val totals = mutableMapOf<String, Long>()
    operations.forEach { op ->
        val amount = op.amount?.takeIf { it.isNotBlank() } ?: return@forEach
        val currency = op.currency ?: return@forEach
        val minorUnits = parseMinorUnits(amount) ?: return@forEach
        val signed = minorUnits * signOf(op.type).toLong()
        totals[currency] = (totals[currency] ?: 0L) + signed
    }
    return totals.mapValues { formatMinorUnits(it.value) }
}

/** Convierte una cadena decimal de unidades mayores ("100,00", "100.5",
 *  "-30", "0,5") a unidades menores (centavos) como `Long`. Redondea
 *  fracciones con más de 2 decimales (HALF_UP). Devuelve null si la
 *  cadena no es un importe válido. */
private fun parseMinorUnits(text: String): Long? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    var s = trimmed.replace(',', '.')
    var sign = 1L
    if (s.startsWith("-")) { sign = -1L; s = s.removePrefix("-") }
    else if (s.startsWith("+")) { s = s.removePrefix("+") }
    // Solo dígitos y a lo más un punto decimal.
    if (s.count { it == '.' } > 1 || s.any { !it.isDigit() && it != '.' }) return null
    val parts = s.split(".", limit = 2)
    val intPart = parts[0].takeIf { it.isNotEmpty() } ?: "0"
    val intValue = intPart.toLongOrNull() ?: return null
    var minor = intValue * 100
    if (parts.size == 2) {
        val frac = parts[1]
        val frac2 = if (frac.length >= 2) frac.substring(0, 2) else frac.padEnd(2, '0')
        var fracValue = frac2.toLongOrNull() ?: return null
        if (frac.length > 2 && frac[2].isDigit() && frac[2].digitToInt() >= 5) {
            fracValue += 1
            if (fracValue > 99) {
                fracValue = 0
                minor += 100 // carry a la parte entera
            }
        }
        minor += fracValue
    }
    return sign * minor
}

/** Formatea unidades menores (centavos) a cadena decimal con 2 decimales y
 *  separador punto ("100.00", "-30.00", "0.00"). */
private fun formatMinorUnits(minorUnits: Long): String {
    val abs = if (minorUnits == Long.MIN_VALUE) Long.MAX_VALUE else abs(minorUnits)
    val major = abs / 100
    val minor = abs % 100
    return "${if (minorUnits < 0) "-" else ""}$major.${minor.toString().padStart(2, '0')}"
}

/**
 * Formatea un balance con signo direccional: "+100,00" o "−50,00"
 * (separador decimal coma para presentación). Devuelve null si no hay
 * balance (mapa vacío).
 */
fun formatBalance(value: String): String {
    val normalized = value.replace('.', ',')
    return if (normalized.startsWith("-")) "−" + normalized.removePrefix("-") else "+" + normalized
}

/**
 * Agrupa operaciones por mes y día civil (zona del dispositivo), de la
 * más reciente a la más antigua. Cada mes trae su balance neto por
 * moneda y cada día sus operaciones.
 */
fun groupOperationsByMonthAndDay(
    operations: List<Operation>,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): List<MonthlyGroup> {
    val byMonth = operations.groupBy { op ->
        Instant.fromEpochMilliseconds(op.occurredAt).toLocalDateTime(timeZone).date
    }
    val monthEntries = byMonth.entries.sortedByDescending { (date, _) -> date }

    return monthEntries.map { (monthDate, monthOps) ->
        val monthLabel = monthNames[monthDate.month.ordinal] + " " + monthDate.year
        val monthKey = monthDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val balance = balanceByCurrency(monthOps)

        val byDay = monthOps.groupBy { op ->
            Instant.fromEpochMilliseconds(op.occurredAt).toLocalDateTime(timeZone).date
        }
        val dayEntries = byDay.entries.sortedByDescending { (date, _) -> date }

        val days = dayEntries.map { (dayDate, dayOps) ->
            DailyGroup(
                dayLabel = dayDate.dayOfMonth.toString(),
                dayKey = dayDate.atStartOfDayIn(timeZone).toEpochMilliseconds(),
                operations = dayOps.sortedByDescending { it.occurredAt },
            )
        }

        MonthlyGroup(
            monthLabel = monthLabel,
            monthKey = monthKey,
            balanceByCurrency = balance,
            days = days,
        )
    }
}
