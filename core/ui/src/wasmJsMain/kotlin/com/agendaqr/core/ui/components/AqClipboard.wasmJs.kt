package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

@OptIn(ExperimentalWasmJsInterop::class)
private external interface AqNavigator : JsAny {
    val clipboard: AqClipboardJs
}

@OptIn(ExperimentalWasmJsInterop::class)
private external interface AqClipboardJs : JsAny {
    /** Devuelve una Promise; el portapapeles del navegador puede rechazarla
     *  si el documento no tiene foco o el permiso fue denegado. */
    fun writeText(text: String): JsAny
}

@OptIn(ExperimentalWasmJsInterop::class)
private external val navigator: AqNavigator

/**
 * wasmJs: `navigator.clipboard.writeText`. Solo escritura, igual que en las
 * demás plataformas; el pegado pertenece a otras apps.
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun rememberAqClipboard(): (String) -> Unit {
    return { text -> navigator.clipboard.writeText(text) }
}
