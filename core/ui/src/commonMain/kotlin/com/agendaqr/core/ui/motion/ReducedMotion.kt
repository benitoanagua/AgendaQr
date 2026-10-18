package com.agendaqr.core.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * Movimiento reducido del sistema (accesibilidad): ningún sistema
 * operativo objetivo expone `prefers-reduced-motion` como tal, pero ambos
 * tienen un equivalente honesto que sí se puede leer y observar en
 * vivo:
 *
 * - Android: `Settings.Global.ANIMATOR_DURATION_SCALE` (Ajustes >
 *   Accesibilidad > Escala de duración de animación en 0).
 * - iOS: `UIAccessibility.isReduceMotionEnabled`, con
 *   `UIAccessibilityReduceMotionStatusDidChangeNotification` para cambios
 *   en vivo si el usuario toca el ajuste con la app abierta.
 *
 * Uso: envolver el contenido raíz una sola vez con
 * [ProvideReducedMotion], y leer [LocalReducedMotion] donde haga falta
 * (la transición estándar de superficies lo hace por default).
 */
val LocalReducedMotion = compositionLocalOf { false }

@Composable
expect fun ProvideReducedMotion(content: @Composable () -> Unit)
