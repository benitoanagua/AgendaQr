package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqTextStyles
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Selector de fecha con calendario AgendaQr/Polaris. Los días viajan como
 * milisegundos UTC de medianoche; la feature combina después la hora local.
 * [monthNames] contiene los doce nombres localizados; [weekdays] contiene
 * nombres completos en orden domingo a sábado (se abrevian solo visualmente).
 * Copy y acciones accesibles pertenecen al llamador.
 */
@Composable
fun AqDatePickerDialog(
    selectedDateUtc: Long?,
    title: String,
    onConfirm: (utcDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
    monthNames: List<String>,
    weekdays: List<String>,
    previousMonthLabel: String,
    nextMonthLabel: String,
) {
    require(monthNames.size == 12) { "AqDatePickerDialog requiere 12 nombres de mes" }
    require(weekdays.size == 7) { "AqDatePickerDialog requiere 7 nombres de día" }
    val initial = selectedDateUtc?.let(::dateFromUtc)
        ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var chosenDate by remember(selectedDateUtc) { mutableStateOf(selectedDateUtc?.let(::dateFromUtc)) }
    var month by remember(selectedDateUtc) { mutableStateOf(initial.firstOfMonth()) }
    AqDialog(
        title = title,
        confirmLabel = confirmLabel,
        onConfirm = { chosenDate?.let { onConfirm(it.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()) } },
        dismissLabel = dismissLabel,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss,
        fullWidth = true,
        contentHorizontalPadding = AqSpacing.None,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AqIconButton(previousMonthLabel, onClick = { month = month.minus(DatePeriod(months = 1)) }) {
                        AqIcon(AqIcons.Back, contentDescription = null)
                    }
                    Text(
                        "${monthNames[month.month.ordinal]} ${month.year}",
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        style = AqTextStyles.Title,
                        color = AqColor.TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    AqIconButton(nextMonthLabel, onClick = { month = month.plus(DatePeriod(months = 1)) }) {
                        AqIcon(AqIcons.ChevronRight, contentDescription = null)
                    }
                }
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    weekdays.forEach { day ->
                        Box(
                            Modifier.size(AqMetrics.ControlMinSize),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                day.take(2),
                                modifier = Modifier.semantics { contentDescription = day },
                                style = AqTextStyles.Support,
                                color = AqColor.TextSecondary,
                            )
                        }
                    }
                }
                val first = month.firstOfMonth()
                val offset = first.dayOfWeek.isoDayNumber % 7 // domingo = primera columna
                val nextMonth = first.plus(DatePeriod(months = 1))
                val daysInMonth = nextMonth.toEpochDays() - first.toEpochDays()
                Column(
                    modifier = Modifier.horizontalScroll(rememberScrollState()).selectableGroup(),
                ) {
                    repeat(6) { week ->
                        Row {
                            repeat(7) { weekday ->
                                val dayOfMonth = week * 7 + weekday - offset + 1
                                if (dayOfMonth in 1..daysInMonth) {
                                    val date = LocalDate(first.year, first.month.ordinal + 1, dayOfMonth)
                                    val isSelected = date == chosenDate
                                    val description = "$dayOfMonth ${monthNames[month.month.ordinal]} ${month.year}"
                                    Box(
                                        modifier = Modifier
                                            .size(AqMetrics.ControlMinSize)
                                            .background(
                                                if (isSelected) AqColor.EmphasisFill else AqColor.Transparent,
                                                AqShapes.Pill,
                                            )
                                            .clickable(role = Role.RadioButton, onClickLabel = description) { chosenDate = date }
                                            .semantics {
                                                contentDescription = description
                                                selected = isSelected
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            dayOfMonth.toString(),
                                            style = AqTextStyles.Label,
                                            color = if (isSelected) AqColor.OnEmphasisFill else AqColor.TextPrimary,
                                        )
                                    }
                                } else {
                                    Box(Modifier.size(AqMetrics.ControlMinSize))
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

/** Selector horario con dos campos numéricos accesibles y validación local. */
@Composable
fun AqTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
    title: String,
    hourLabel: String,
    minuteLabel: String,
    invalidTimeMessage: String,
    is24Hour: Boolean = true,
    meridiemLabels: List<String> = emptyList(),
) {
    require(initialHour in 0..23) { "AqTimePickerDialog: hora fuera de rango" }
    require(initialMinute in 0..59) { "AqTimePickerDialog: minuto fuera de rango" }
    require(is24Hour || meridiemLabels.size == 2) { "AqTimePickerDialog requiere etiquetas AM y PM en modo 12 horas" }
    var hour by remember(initialHour, is24Hour) {
        val initialDisplayHour = if (is24Hour) initialHour else ((initialHour + 11) % 12) + 1
        mutableStateOf(initialDisplayHour.toString().padStart(2, '0'))
    }
    var minute by remember(initialMinute) { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var meridiem by remember(initialHour) { mutableStateOf(if (initialHour >= 12) 1 else 0) }
    var validate by remember { mutableStateOf(false) }
    val parsedHour = hour.toIntOrNull()?.takeIf { it in if (is24Hour) 0..23 else 1..12 }
    val parsedMinute = minute.toIntOrNull()?.takeIf { it in 0..59 }
    AqDialog(
        title = title,
        confirmLabel = confirmLabel,
        onConfirm = {
            if (parsedHour != null && parsedMinute != null) {
                val hour24 = if (is24Hour) parsedHour else (parsedHour % 12) + meridiem * 12
                onConfirm(hour24, parsedMinute)
            }
            else validate = true
        },
        dismissLabel = dismissLabel,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                verticalAlignment = Alignment.Bottom,
            ) {
                AqTextField(
                    label = hourLabel,
                    value = hour,
                    onValueChange = { value -> if (value.length <= 2 && value.all(Char::isDigit)) hour = value },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    isError = validate && parsedHour == null,
                    errorMessage = if (validate && parsedHour == null) invalidTimeMessage else null,
                )
                Text(":", style = AqTextStyles.Title, color = AqColor.TextPrimary, modifier = Modifier.padding(bottom = AqSpacing.Md))
                AqTextField(
                    label = minuteLabel,
                    value = minute,
                    onValueChange = { value -> if (value.length <= 2 && value.all(Char::isDigit)) minute = value },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    isError = validate && parsedMinute == null,
                    errorMessage = if (validate && parsedMinute == null) invalidTimeMessage else null,
                )
            }
            if (!is24Hour) {
                AqSegmentedControl(
                    options = meridiemLabels.map { AqSegmentedOption(label = it) },
                    selectedIndex = meridiem,
                    onSelect = { meridiem = it },
                )
            }
        },
    )
}

private fun dateFromUtc(millis: Long): LocalDate =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date

private fun LocalDate.firstOfMonth(): LocalDate = LocalDate(year, month.ordinal + 1, 1)
