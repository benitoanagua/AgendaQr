package com.agendaqr.core.ui.lab.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.agendaqr.core.ui.lab.AgendaQrComponentLab

/**
 * Browser entrypoint of the AgendaQr component lab (WebAssembly).
 *
 * The lab starts directly in the component catalog: search, category
 * filters, inspector, interactive previews and token specimens are all
 * rendered by [AgendaQrComponentLab] with the same code used by the
 * production modules. This host adds no navigation, no services and no
 * production runtime: it is a development tool only.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() = ComposeViewport(viewportContainerId = "ComposeTarget") { AgendaQrComponentLab() }
