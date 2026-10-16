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
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles

/**
 * Selectores de fecha y hora (envoltorios finos de los pickers de M3 con
 * tokens del sistema). Todo el copy llega por parámetros (ADR-0008).
 *
 * Las fechas viajan como milisegundos UTC de la medianoche del día civil:
 * el picker muestra días civiles, y la zona del dispositivo solo se aplica
 * al combinar fecha + hora en la llamada ([combineDateAndTime] vive en la
 * capa de presentación, no aquí).
 */

/**
 * Diálogo de fecha: calendario con confirmar/descarte. [selectedDateUtc]
 * son los milisegundos UTC de la medianoche del día seleccionado (null =
 * sin selección inicial); [onConfirm] devuelve lo mismo para el día
 * elegido.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AmDatePickerDialog(
    selectedDateUtc: Long?,
    onConfirm: (utcDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    /** copy del llamador (sin defaults hardcodeados). */
    confirmLabel: String,
    dismissLabel: String,
    title: String? = null,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateUtc)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                onClick = { state.selectedDateMillis?.let(onConfirm) },
                shape = AmShapes.Small,
            ) {
                Text(confirmLabel, style = AmTextStyles.ButtonLabel)
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                onClick = onDismiss,
                shape = AmShapes.Small,
            ) {
                Text(dismissLabel, style = AmTextStyles.ButtonLabel)
            }
        },
        shape = AmShapes.Large,
    ) {
        Column(modifier = Modifier.padding(horizontal = AmSpacing.Md)) {
            title?.let {
                Text(
                    it,
                    modifier = Modifier.padding(vertical = AmSpacing.Sm),
                    style = AmTextStyles.Title,
                    color = AmColor.TextPrimary,
                )
            }
            DatePicker(state = state)
        }
    }
}

/**
 * Diálogo de hora: entrada de hora con confirmar/descarte.
 * [onConfirm] devuelve la hora y el minuto elegidos (0–23, 0–59).
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AmTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    /** copy del llamador (sin defaults hardcodeados). */
    confirmLabel: String,
    dismissLabel: String,
    /** Reloj de 24 h (formato del dispositivo). */
    is24Hour: Boolean = true,
    title: String? = null,
) {
    require(initialHour in 0..23) { "AmTimePickerDialog: hora fuera de rango" }
    require(initialMinute in 0..59) { "AmTimePickerDialog: minuto fuera de rango" }
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AmShapes.Large,
        containerColor = AmColor.Surface,
        titleContentColor = AmColor.TextPrimary,
        textContentColor = AmColor.TextPrimary,
        title = title?.let { { Text(it, style = AmTextStyles.Title) } },
        text = {
            TimeInput(
                state = state,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                onClick = { onConfirm(state.hour, state.minute) },
                shape = AmShapes.Small,
            ) {
                Text(confirmLabel, style = AmTextStyles.ButtonLabel, color = AmColor.AccentText)
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                onClick = onDismiss,
                shape = AmShapes.Small,
            ) {
                Text(dismissLabel, style = AmTextStyles.ButtonLabel, color = AmColor.TextPrimary)
            }
        },
    )
}
