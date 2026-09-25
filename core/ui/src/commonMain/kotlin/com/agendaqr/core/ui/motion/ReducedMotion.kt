package com.agendaqr.core.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * Xauxa checklist KMP §05 (tarea lt4): ningún sistema operativo objetivo
 * expone `prefers-reduced-motion` como tal, pero los tres tienen un
 * equivalente honesto que sí se puede leer y observar en vivo:
 *
 * - Android: `Settings.Global.ANIMATOR_DURATION_SCALE` (Ajustes >
 *   Accesibilidad > Escala de duración de animación en 0).
 * - iOS: `UIAccessibility.isReduceMotionEnabled`, con
 *   `UIAccessibilityReduceMotionStatusDidChangeNotification` para cambios en
 *   vivo si el usuario toca el ajuste con la app abierta.
 * - Web (wasmJs, laboratorio): `matchMedia('(prefers-reduced-motion: reduce)')`.
 *
 * El ZIP de referencia (`xauxa-design-system/kotlin-compose/ReducedMotion.kt`)
 * solo resolvía el lado Android. Esta versión lo convierte en un expect/actual
 * real de KMP en vez de dejar iOS y web como huecos: es exactamente la
 * diferencia entre "código Kotlin" y "convención Kotlin/Compose multiplataforma"
 * que pidió este pase.
 *
 * Uso: envolver el contenido raíz una sola vez con [ProvideReducedMotion], y
 * leer [LocalReducedMotion] donde haga falta ([XauxaLiveTile] ya lo hace por
 * default).
 */
val LocalReducedMotion = compositionLocalOf { false }

@Composable
expect fun ProvideReducedMotion(content: @Composable () -> Unit)
