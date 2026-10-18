package com.agendaqr.core.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize

/**
 * iOS: la clase de ancho se calcula desde el tamaño del contenedor de la
 * ventana (ComposeWindowInfo), igual que la referencia skiko de Material.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun amWindowWidthSizeClass(): WindowWidthSizeClass {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val size = with(density) { windowInfo.containerSize.toSize().toDpSize() }
    return WindowSizeClass.calculateFromSize(size).widthSizeClass
}
