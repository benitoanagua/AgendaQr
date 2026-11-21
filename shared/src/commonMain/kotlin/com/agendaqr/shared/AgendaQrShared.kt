package com.agendaqr.shared

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.motion.ProvideReducedMotion
import com.agendaqr.destinations.presentation.AgendaQrApp

@Composable
fun AgendaQrSharedApp() = ProvideReducedMotion { AgendaQrApp() }

/**
 * Capacidad de plataforma: ¿soporta compartir desde otra app (Share
 * Extension / intent recibido)?
 *
 * iOS: false hasta que exista el target Xcode y la firma (ADR-0037).
 * Android: se anulará cuando se implemente la recepción de intents de
 * compartir. Hoy se deja conservador (false) para no prometer lo que no
 * existe.
 */
val supportsShareExtension: Boolean = false
