package com.agendaqr.core.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification

@Composable
actual fun ProvideReducedMotion(content: @Composable () -> Unit) {
    var reduced by remember { mutableStateOf(UIAccessibilityIsReduceMotionEnabled()) }

    DisposableEffect(Unit) {
        // VoiceOver/Accesibilidad puede cambiar "Reducir movimiento" con la
        // app abierta, igual que el observer de Settings.Global del lado
        // Android: se registra el mismo tipo de observador en vivo, no solo
        // una lectura al montar.
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIAccessibilityReduceMotionStatusDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ ->
            reduced = UIAccessibilityIsReduceMotionEnabled()
        }

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
    }

    CompositionLocalProvider(LocalReducedMotion provides reduced) {
        content()
    }
}
