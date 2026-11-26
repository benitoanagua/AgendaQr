package com.agendaqr.core.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

@OptIn(ExperimentalWasmJsInterop::class)
private external interface AqMediaQueryList : JsAny {
    val matches: Boolean
    fun addEventListener(type: String, listener: (AqMediaQueryListEvent) -> Unit)
    fun removeEventListener(type: String, listener: (AqMediaQueryListEvent) -> Unit)
}

@OptIn(ExperimentalWasmJsInterop::class)
private external interface AqMediaQueryListEvent : JsAny {
    val matches: Boolean
}

@OptIn(ExperimentalWasmJsInterop::class)
private external interface AqWindow : JsAny {
    fun matchMedia(query: String): AqMediaQueryList
}

@OptIn(ExperimentalWasmJsInterop::class)
private external val window: AqWindow

private const val ReducedMotionQuery = "(prefers-reduced-motion: reduce)"

/**
 * wasmJs: lee `prefers-reduced-motion` de `matchMedia` y observa el evento
 * `change`, igual que Android observa `ANIMATOR_DURATION_SCALE` e iOS la
 * notificación de accesibilidad.
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun ProvideReducedMotion(content: @Composable () -> Unit) {
    val mediaQuery = remember { window.matchMedia(ReducedMotionQuery) }
    var reduced by remember { mutableStateOf(mediaQuery.matches) }

    DisposableEffect(mediaQuery) {
        val listener: (AqMediaQueryListEvent) -> Unit = { reduced = it.matches }
        mediaQuery.addEventListener("change", listener)
        onDispose { mediaQuery.removeEventListener("change", listener) }
    }

    CompositionLocalProvider(LocalReducedMotion provides reduced) {
        content()
    }
}
