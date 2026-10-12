package com.agendaqr.core.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

/**
 * Clase de ancho de ventana (§12 V1.2): la adaptación compacto/medium/
 * expanded de la producción. El cálculo es multiplataforma: la métrica
 * del contenedor de la ventana (iOS/wasm) o la configuración de pantalla
 * (Android), resuelta como [WindowSizeClass] canónico.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
expect fun xauxaWindowWidthSizeClass(): WindowWidthSizeClass
