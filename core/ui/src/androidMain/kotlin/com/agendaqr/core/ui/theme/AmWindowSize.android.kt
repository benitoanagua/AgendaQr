package com.agendaqr.core.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Android: la clase de ancho se calcula desde la configuración de
 * pantalla (dp reales de la ventana), sin depender de un Activity.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun amWindowWidthSizeClass(): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    val size = DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
    return WindowSizeClass.calculateFromSize(size).widthSizeClass
}
