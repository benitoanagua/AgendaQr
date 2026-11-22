package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by graph.destinationsViewModel.state.collectAsStateWithLifecycle()
    val contextState by graph.contextsViewModel.state.collectAsStateWithLifecycle()
    val nav by remember { mutableStateOf(AppBackStack()) }
    val toastHost = remember { AqToastHostState() }
    val appHosts = AppHosts(toastHost = toastHost)
    HomeSurface(
        state = state,
        contextState = contextState,
        graph = graph,
        onLock = onLock,
        nav = nav,
        navChrome = null,
        appHosts = appHosts,
        syncLookup = ElementSyncLookup.Empty,
        onRetrySync = { },
        onSearchOpened = { nav.push(AppRoute.Search) },
        onDetailBack = { nav.pop() },
    )
}
