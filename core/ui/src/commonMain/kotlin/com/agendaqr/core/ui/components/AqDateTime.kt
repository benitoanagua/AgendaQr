package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Selectores de fecha y hora. Las fechas viajan como milisegundos UTC de
 * la medianoche del día civil; la zona del dispositivo se aplica al
 * combinar fecha y hora en la capa de presentación.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AqDatePickerDialog(
    selectedDateUtc: Long?,
    title: String,
    onConfirm: (utcDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateUtc)
    AqDialog(
        title = title,
        confirmLabel = confirmLabel,
        onConfirm = { state.selectedDateMillis?.let(onConfirm) },
        dismissLabel = dismissLabel,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss,
        content = { DatePicker(state = state) },
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AqTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
    title: String,
    is24Hour: Boolean = true,
) {
    require(initialHour in 0..23) { "AqTimePickerDialog: hora fuera de rango" }
    require(initialMinute in 0..59) { "AqTimePickerDialog: minuto fuera de rango" }
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
    AqDialog(
        title = title,
        confirmLabel = confirmLabel,
        onConfirm = { onConfirm(state.hour, state.minute) },
        dismissLabel = dismissLabel,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss,
        content = {
            TimeInput(
                state = state,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}
