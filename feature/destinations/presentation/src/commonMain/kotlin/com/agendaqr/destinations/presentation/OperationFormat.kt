/**
 * Formato compartido del flujo de operaciones: fechas civiles en la zona
 * del dispositivo (regresión 4b04587) y etiquetas semánticas de tipo y
 * proveniencia (nunca el nombre técnico del enum).
 */
package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
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

