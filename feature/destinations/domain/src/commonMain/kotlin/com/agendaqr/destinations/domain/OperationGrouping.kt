package com.agendaqr.destinations.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

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
 * El resultado usa 2 decimales y separador decimal punto (canonico).
 */
fun balanceByCurrency(operations: List<Operation>): Map<String, String> {
    val totals = mutableMapOf<String, Long>()
    operations.forEach { op ->
        val amount = op.amount?.takeIf { it.isNotBlank() } ?: return@forEach
        val currency = op.currency ?: return@forEach
        val cents = amount.toCentsHalfUp() ?: return@forEach
        val signedCents = if (op.type == OperationType.COBRO) cents else -cents
        totals[currency] = (totals[currency] ?: 0L) + signedCents
    }
    return totals.mapValues { (_, cents) -> cents.toMoneyString() }
}

/** Parse a decimal amount into minor units using HALF_UP rounding. */
private fun String.toCentsHalfUp(): Long? {
    val normalized = trim().replace(',', '.')
    if (normalized.isEmpty()) return null
    val negative = normalized.startsWith('-')
    val unsigned = normalized.removePrefix("-").removePrefix("+")
    val parts = unsigned.split('.', limit = 2)
    if (parts.size > 2 || parts[0].isEmpty() || parts[0].any { !it.isDigit() }) return null
    val fraction = parts.getOrElse(1) { "" }
    if (fraction.any { !it.isDigit() }) return null
    val whole = parts[0].toLongOrNull() ?: return null
    val firstTwo = (fraction + "00").take(2).toLongOrNull() ?: return null
    val roundUp = fraction.getOrNull(2)?.let { it >= '5' } ?: false
    val magnitude = whole * 100L + firstTwo + if (roundUp) 1L else 0L
    return if (negative) -magnitude else magnitude
}

private fun Long.toMoneyString(): String {
    val negative = this < 0
    val magnitude = if (negative) -this else this
    val whole = magnitude / 100L
    val fraction = (magnitude % 100L).toString().padStart(2, '0')
    return (if (negative) "-" else "") + whole.toString() + "." + fraction
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
