package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

@Composable
actual fun rememberAmClipboard(): (String) -> Unit {
    return { text -> UIPasteboard.generalPasteboard.string = text }
}
