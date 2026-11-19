package com.agendaqr.core.ui.components

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.launch

@Composable
actual fun rememberAqClipboard(): (String) -> Unit {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    return { text ->
        scope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Agenda QR", text)))
        }
    }
}
