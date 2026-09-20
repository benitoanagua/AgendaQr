package com.agendaqr.destinations.presentation

import android.content.Intent
import com.agendaqr.destinations.domain.Operation

actual fun shareOperation(operation: Operation) {
    val activity = AgendaQrAndroidShareLauncher.requireActivity()
    val text = buildString {
        append(operation.type.name)
        append(" | ")
        append(operation.amount.orEmpty().ifBlank { "sin monto" })
        operation.currency?.let { append(" " + it) }
        operation.personOrEntity?.let { append(" | " + it) }
        operation.concept?.let { append(" | " + it) }
    }
    activity.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }, null))
}
