package com.agendaqr.core.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

/**
 * Clase de ancho de ventana: la adaptación compacto/medium/expanded de
 * AgendaMobile (NavigationBar vs NavigationRail, contenido centrado con
 * ContentMaxWidth). El cálculo es multiplataforma: la métrica del
 * contenedor de la ventana (iOS) o la configuración de pantalla (Android),
 * resuelta como WindowSizeClass canónico.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
expect fun amWindowWidthSizeClass(): WindowWidthSizeClass
