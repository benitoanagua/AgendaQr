package com.agendaqr.destinations.presentation

import android.content.Intent
import com.agendaqr.destinations.domain.Operation

actual fun shareOperation(operation: Operation) {
    // Flujo externo (U-02): la hoja de compartir saca la app.
    ExternalFlowGates.tracker.markLaunch()
    val activity = AgendaQrAndroidShareLauncher.requireActivity()
    val text = buildString {
        append(operation.type.name)
        append(" | ")
        append(operation.amount.orEmpty().ifBlank { AppStrings.SinMonto })
        operation.currency?.let { append(" " + it) }
        operation.personOrEntity?.let { append(" | " + it) }
        operation.concept?.let { append(" | " + it) }
    }
    activity.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }, null))
}
