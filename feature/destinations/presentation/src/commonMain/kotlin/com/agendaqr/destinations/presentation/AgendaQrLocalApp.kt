package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.agendaqr.core.ui.components.AqToastHostState

/**
 * S01 — Contenedor de la app autenticada local (grafo de sesión +
 * navegación + pantallas).
 *
 * Construye y recuerda ([remember]) el [AuthenticatedSessionGraph] del
 * [profileId], aloja [HomeSurface] y la navegación con back stack
 * restaurable ([AppRoute]/[AppBackStack]), y cablea [onLock].
 *
 * Respeta el modo de demostración: cuando [profileId] es
 * [DEMO_PROFILE_ID] el grafo no sincroniza (`remoteConfigured = false`)
 * y los datos son ficticios e isolados, ya que
 * [rememberAuthenticatedSessionGraph] fuerza `remoteConfigured = false` para
 * perfiles de demostración.
 */
@Composable
fun AgendaQrLocalApp(
    profileId: String,
    onLock: () -> Unit,
) {
    val graph = rememberAuthenticatedSessionGraph(profileId)
    // ADR-0006: el stack se restaura tras rotación/recreación; sin Saver la
    // superficie abierta volaría a Inicio.
    val nav = rememberSaveable(saver = AppBackStackSaver) { AppBackStack() }
    val toastHost = remember { AqToastHostState() }
    val appHosts = AppHosts(toastHost = toastHost)
    AuthenticatedAppRoot(
        graph = graph,
        appHosts = appHosts,
        onLock = onLock,
        nav = nav,
    )
}
