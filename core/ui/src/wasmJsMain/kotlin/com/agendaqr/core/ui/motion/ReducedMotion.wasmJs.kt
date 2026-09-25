package com.agendaqr.core.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@JsFun("() => { try { return window.matchMedia('(prefers-reduced-motion: reduce)').matches; } catch (e) { return false; } }")
private external fun jsPrefersReducedMotion(): Boolean

/**
 * Actual del laboratorio web (wasmJs), no de producto (ver comentario de
 * `wasmJs { browser { ... } }` en `core/ui/build.gradle.kts`: este target es
 * herramienta de desarrollo, no llega a Android/iOS).
 *
 * Lee `prefers-reduced-motion` una sola vez al montar. A diferencia de los
 * actuals de Android/iOS, no observa cambios en vivo del ajuste del sistema
 * operativo (`matchMedia(...).addEventListener('change', ...)`): se deja
 * PENDING a propósito en vez de agregar interop JS sin poder validarlo — este
 * módulo no tiene infraestructura de test de navegador (ver
 * `testTask { enabled = false }` en el mismo build.gradle.kts).
 */
@Composable
actual fun ProvideReducedMotion(content: @Composable () -> Unit) {
    val reduced = remember { runCatching { jsPrefersReducedMotion() }.getOrDefault(false) }
    CompositionLocalProvider(LocalReducedMotion provides reduced) {
        content()
    }
}
