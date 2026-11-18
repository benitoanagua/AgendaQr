package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles

/**
 * Selectores de fecha y hora. Las fechas viajan como milisegundos UTC de
 * la medianoche del día civil; la zona del dispositivo se aplica al
 * combinar fecha y hora en la capa de presentación.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AqDatePickerDialog(
    selectedDateUtc: Long?,
    onConfirm: (utcDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String,
    dismissLabel: String,
    title: String? = null,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateUtc)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                onClick = { state.selectedDateMillis?.let(onConfirm) },
                shape = AqShapes.Small,
            ) {
                Text(confirmLabel, style = AqTextStyles.ButtonLabel)
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                onClick = onDismiss,
                shape = AqShapes.Small,
            ) {
                Text(dismissLabel, style = AqTextStyles.ButtonLabel)
            }
        },
        shape = AqShapes.Large,
    ) {
        Column(modifier = Modifier.padding(horizontal = AqSpacing.Md)) {
            title?.let {
                Text(
                    it,
                    modifier = Modifier.padding(vertical = AqSpacing.Sm),
                    style = AqTextStyles.Title,
                    color = AqColor.TextPrimary,
                )
            }
            DatePicker(state = state)
        }
    }
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
    is24Hour: Boolean = true,
    title: String? = null,
) {
    require(initialHour in 0..23) { "AqTimePickerDialog: hora fuera de rango" }
    require(initialMinute in 0..59) { "AqTimePickerDialog: minuto fuera de rango" }
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AqShapes.Large,
        containerColor = AqColor.Surface,
        titleContentColor = AqColor.TextPrimary,
        textContentColor = AqColor.TextPrimary,
        title = title?.let { { Text(it, style = AqTextStyles.Title) } },
        text = {
            TimeInput(
                state = state,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                onClick = { onConfirm(state.hour, state.minute) },
                shape = AqShapes.Small,
            ) {
                Text(confirmLabel, style = AqTextStyles.ButtonLabel, color = AqColor.AccentText)
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                onClick = onDismiss,
                shape = AqShapes.Small,
            ) {
                Text(dismissLabel, style = AqTextStyles.ButtonLabel, color = AqColor.TextPrimary)
            }
        },
    )
}
