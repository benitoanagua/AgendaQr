package com.agendaqr.destinations.presentation

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Brillo al máximo + pantalla encendida en Android (U-05, D4): fija
 * `screenBrightness` a 1f y añade `FLAG_KEEP_SCREEN_ON` mientras el QR
 * está en composición; al salir restaura el brillo previo y retira el
 * flag solo si no estaba antes (rotación: la actividad se recrea, el
 * efecto se re-aplica sobre los valores ya restaurados).
 */
@Composable
actual fun QrDisplayScreenEffects() {
    val context = LocalContext.current
    val window = (context as? Activity)?.window ?: return
    DisposableEffect(window) {
        val previousBrightness = window.attributes.screenBrightness
        val hadKeepScreenOn = window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window.attributes = window.attributes.apply {
                screenBrightness = previousBrightness
            }
            if (!hadKeepScreenOn) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}
