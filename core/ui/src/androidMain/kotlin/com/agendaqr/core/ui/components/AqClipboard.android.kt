package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
actual fun rememberAmClipboard(): (String) -> Unit {
    val manager = LocalClipboardManager.current
    return { text -> manager.setText(AnnotatedString(text)) }
}
