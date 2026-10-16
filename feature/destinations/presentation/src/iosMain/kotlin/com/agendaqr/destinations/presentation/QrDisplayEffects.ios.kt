package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen

/**
 * Brillo al máximo + pantalla encendida en iOS (U-05, D4): guarda y
 * restaura `UIScreen.mainScreen.brightness` e `idleTimerDisabled`
 * mientras el QR está en composición. Solo compilación en este entorno
 * (runtime iOS requiere Xcode/dispositivo).
 */
@Composable
actual fun QrDisplayScreenEffects() {
    DisposableEffect(Unit) {
        val screen = UIScreen.mainScreen
        val previousBrightness = screen.brightness
        screen.brightness = 1.0
        val application = UIApplication.sharedApplication
        val previousIdleDisabled = application.idleTimerDisabled
        application.idleTimerDisabled = true
        onDispose {
            UIScreen.mainScreen.brightness = previousBrightness
            UIApplication.sharedApplication.idleTimerDisabled = previousIdleDisabled
        }
    }
}
