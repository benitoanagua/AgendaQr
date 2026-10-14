/**
 * Formato compartido del flujo de operaciones: fechas civiles en la zona
 * del dispositivo (regresión 4b04587) y etiquetas semánticas de tipo y
 * proveniencia (nunca el nombre técnico del enum).
 */
package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
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
 */
internal fun formatDate(millis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
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

