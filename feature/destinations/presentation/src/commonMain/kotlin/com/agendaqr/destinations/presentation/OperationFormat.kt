/**
 * Formato compartido del flujo de operaciones: fechas civiles en la zona
 * del dispositivo (regresión 4b04587) y etiquetas semánticas de tipo y
 * proveniencia (nunca el nombre técnico del enum).
 */
package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal fun receiptProvenanceLabel(provenance: ReceiptProvenance?): String = when (provenance) {
    ReceiptProvenance.ENVIADO -> "Enviado"
    ReceiptProvenance.RECIBIDO -> "Recibido"
    ReceiptProvenance.DESCONOCIDO, null -> "Origen desconocido"
}

/**
 * Fecha civil dd/mm/aaaa de [millis] en [timeZone] (por defecto, la del
 * dispositivo).
 *
 * Se calcula en la zona local (no UTC): con la zona del público objetivo
 * (p. ej. UTC-4) la medianoche UTC ya pertenece al día siguiente por la
 * tarde, y el editor proponía "mañana" como fecha por defecto.
 *
 * Valores ≤ 0 (epoch o pre-epoch) indican fecha desconocida: se muestra
 * un marcador explícito en lugar de una fecha inválida (ej. 31/12/1969).
 */
internal fun formatDate(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    if (millis <= 0) return AppStrings.FechaDesconocida
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
    fun two(value: Int) = if (value < 10) "0" + value else value.toString()
    return two(date.dayOfMonth) + "/" + two(date.monthNumber) + "/" + date.year
}

/** Inversa estricta de [formatDate]: solo acepta dd/mm/aaaa reales. */
internal fun parseDate(text: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long? {
    val parts = text.trim().split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    if (year < 1900 || year > 2100 || month !in 1..12 || day !in 1..31) return null
    // LocalDate rechaza 31/02, 29/02 en año no bisiesto, etc.
    val date = runCatching { LocalDate(year, month, day) }.getOrNull() ?: return null
    // Medianoche local de esa fecha civil: mismo valor que vería el usuario.
    return date.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

/** Hora civil HH:mm de [millis] en la zona del dispositivo. */
internal fun formatTime(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    if (millis <= 0) return AppStrings.FechaDesconocida
    val time = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).time
    fun two(value: Int) = if (value < 10) "0" + value else value.toString()
    return two(time.hour) + ":" + two(time.minute)
}

/**
 * Fecha y hora civiles ("16/10/2026 14:30"). Los datos antiguos
 * guardados a medianoche exacta se muestran sin hora (o con la hora en
 * blanco): no se reescriben salvo que el usuario la elija (U-03).
 */
internal fun formatDateTime(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    if (millis <= 0) return AppStrings.FechaDesconocida
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
    return if (dateTime.hour == 0 && dateTime.minute == 0) {
        formatDate(millis, timeZone)
    } else {
        formatDate(millis, timeZone) + " " + formatTime(millis, timeZone)
    }
}

/**
 * Día civil de [millis] como medianoche UTC: lo que el selector de fecha
 * consume y devuelve (el picker muestra días civiles, sin zona).
 */
internal fun utcDayMillis(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
    return date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

/**
 * Combina el día civil del picker (medianoche UTC) con la hora elegida
 * en la zona del dispositivo. El tipo de dato del dominio no cambia
 * (`occurredAt` ya son milisegundos).
 */
internal fun combineDateAndTime(
    utcDateMillis: Long,
    hour: Int,
    minute: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Long {
    require(hour in 0..23) { "hora fuera de rango" }
    require(minute in 0..59) { "minuto fuera de rango" }
    val date = Instant.fromEpochMilliseconds(utcDateMillis).toLocalDateTime(TimeZone.UTC).date
    return LocalDateTime(date, LocalTime(hour, minute)).toInstant(timeZone).toEpochMilliseconds()
}

/** ¿Medianoche local exacta (dato antiguo sin hora)? */
internal fun isStartOfDay(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    if (millis <= 0) return false
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
    return dateTime.hour == 0 && dateTime.minute == 0 &&
        millis == dateTime.date.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

/** ¿Mismo día civil en la zona del dispositivo? */
internal fun isSameCivilDay(
    first: Long,
    second: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Boolean = Instant.fromEpochMilliseconds(first).toLocalDateTime(timeZone).date ==
    Instant.fromEpochMilliseconds(second).toLocalDateTime(timeZone).date

/**
 * Resta días civiles conservando la hora (chip "Ayer"): ayer a la misma
 * hora, en la zona del dispositivo.
 */
internal fun minusCivilDays(
    millis: Long,
    days: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Long {
    require(days >= 0) { "días fuera de rango" }
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
    return LocalDateTime(dateTime.date.minus(days, DateTimeUnit.DAY), dateTime.time)
        .toInstant(timeZone).toEpochMilliseconds()
}

/**
 * Prefijo relativo del día ("Hoy"/"Ayer") en la zona del dispositivo;
 * null para cualquier otro día.
 */
internal fun relativeDayLabel(
    millis: Long,
    now: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String? = when {
    isSameCivilDay(millis, now, timeZone) -> AppStrings.Hoy
    isSameCivilDay(millis, minusCivilDays(now, 1, timeZone), timeZone) -> AppStrings.Ayer
    else -> null
}

/**
 * Validación real del importe (U-07, D6): dígitos y UN separador decimal
 * (`,` o `.`), máx. 2 decimales, sin espacios. Vacío = válido (el
 * importe sigue siendo opcional).
 */
internal fun isValidAmount(text: String): Boolean {
    if (text.isEmpty()) return true
    if (text.any { it.isWhitespace() }) return false
    val normalized = text.replace(',', '.')
    if (normalized.count { it == '.' } > 1) return false
    val integer = normalized.substringBefore('.')
    val fraction = if ('.' in normalized) normalized.substringAfter('.') else ""
    if (integer.isEmpty() && fraction.isEmpty()) return false
    if (integer.any { !it.isDigit() }) return false
    if (fraction.any { !it.isDigit() }) return false
    return fraction.length <= 2
}

/** Canoniza el importe con punto al guardar (el dominio lo guarda como
 * texto). */
internal fun canonizeAmount(text: String): String = text.replace(',', '.')

/**
 * Moneda por defecto del editor (U-05, D6): la de la actividad más
 * reciente (`operations` ya viene ordenada descendente); sin historial,
 * vacío. Derivada del estado, sin persistencia nueva.
 */
internal fun mostRecentCurrency(operations: List<Operation>): String =
    operations.firstOrNull()?.currency?.takeIf { it.isNotBlank() }.orEmpty()

/**
 * Sugerencias de persona o entidad (U-05): últimas contrapartes distintas
 * (máx. 5, las más recientes primero). Derivadas del estado, sin
 * persistencia nueva.
 */
internal fun recentCounterparts(operations: List<Operation>, max: Int = 5): List<String> =
    operations.mapNotNull { it.personOrEntity?.takeIf { name -> name.isNotBlank() } }
        .distinct()
        .take(max.coerceAtLeast(1))

private val monthNames = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
)

/** Etiqueta de mes civil ("Mayo 2024") para la barra del historial. */
internal fun monthYearLabel(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
    val month = monthNames[date.monthNumber - 1]
    return month.replaceFirstChar { it.uppercase() } + " " + date.year
}


internal fun operationTypeLabel(type: OperationType): String = when (type) {
    OperationType.PAGO -> "Pago"
    OperationType.COBRO -> "Cobro"
}

/**
 * Título de la fila de actividad (referencia Stitch): la contraparte es
 * el contenido principal; el tipo queda como texto del subtítulo (la
 * forma de la flecha del [com.agendaqr.core.ui.components.AmIconBadge]
 * acompaña, nunca sustituye). Sin contraparte ni concepto, el propio tipo.
 */
internal fun operationTitle(operation: Operation): String =
    operation.personOrEntity?.takeIf { it.isNotBlank() }
        ?: operation.concept?.takeIf { it.isNotBlank() }
        ?: operationTypeLabel(operation.type)

/**
 * Monto con SIGNO DIRECCIONAL (referencia Stitch): cobro "+", pago "−",
 * con la moneda cuando existe. NEUTRO (textPrimary): la referencia no
 * colorea la cifra — el tipo lo comunica la píldora
 * ([operationTypeTone]); el signo acompaña. Es solo presentación: no
 * cambia el valor registrado. Devuelve null sin monto.
 */
internal fun operationAmountText(operation: Operation): String? {
    val amount = operation.amount?.takeIf { it.isNotBlank() } ?: return null
    val sign = if (operation.type == OperationType.COBRO) AppStrings.SignoCobro else AppStrings.SignoPago
    return sign + listOfNotNull(amount, operation.currency?.takeIf { it.isNotBlank() }).joinToString(" ")
}

/** Icono direccional de la actividad (decorativo; el texto comunica). */
internal fun operationTypeIcon(type: OperationType): androidx.compose.ui.graphics.vector.ImageVector =
    if (type == OperationType.PAGO) {
        com.agendaqr.core.ui.components.AmIcons.Expense
    } else {
        com.agendaqr.core.ui.components.AmIcons.Income
    }

/** Tono semántico del tipo (pago = salida, danger; cobro = entrada, success). */
internal fun operationTypeTone(type: OperationType): com.agendaqr.core.ui.components.AmTone =
    if (type == OperationType.PAGO) {
        com.agendaqr.core.ui.components.AmTone.Danger
    } else {
        com.agendaqr.core.ui.components.AmTone.Success
    }

/** Tono de la etiqueta tonal de procedencia del comprobante. */
internal fun provenanceTone(provenance: ReceiptProvenance?): com.agendaqr.core.ui.components.AmTone =
    when (provenance) {
        ReceiptProvenance.ENVIADO -> com.agendaqr.core.ui.components.AmTone.Info
        ReceiptProvenance.RECIBIDO -> com.agendaqr.core.ui.components.AmTone.Success
        ReceiptProvenance.DESCONOCIDO, null -> com.agendaqr.core.ui.components.AmTone.Neutral
    }

