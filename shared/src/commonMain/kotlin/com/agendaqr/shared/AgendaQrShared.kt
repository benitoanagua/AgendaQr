package com.agendaqr.shared

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.motion.ProvideReducedMotion
import com.agendaqr.destinations.presentation.AgendaQrApp

@Composable
fun AgendaQrSharedApp() = ProvideReducedMotion { AgendaQrApp() }
