package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.aqReducedSurfaceEnter
import com.agendaqr.core.ui.components.aqReducedSurfaceExit
import com.agendaqr.core.ui.components.aqSurfaceEnter
import com.agendaqr.core.ui.components.aqSurfaceExit
import com.agendaqr.core.ui.motion.LocalReducedMotion


/**
 * S01 — Inicio y el flujo de destinos completo (S02/S09). Las rutas internas
 * de destinos siguen viviendo en [DestinationsUiState.route] con la
 * transición estándar corta, que respeta el movimiento reducido del sistema.
 */
@Composable
internal fun HomeSurface(
    state: DestinationsUiState,
    contextState: ContextsUiState,
    graph: AuthenticatedSessionGraph,
    onLock: () -> Unit,
    nav: AppBackStack,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup,
    onRetrySync: () -> Unit,
    onSearchOpened: () -> Unit = {},
    /** Back desde el detalle del QR (S05: puede restaurar la Búsqueda). */
    onDetailBack: () -> Unit = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
) {
    val importBatchHolder = remember(graph) { ImportBatchStateHolder(graph) }
    val importBatchState by importBatchHolder.state.collectAsState()
    val operationsState by graph.operationsViewModel.state.collectAsState()
    val reducedMotion = LocalReducedMotion.current
    AnimatedContent(
        targetState = state.route,
        transitionSpec = {
            val reverse = destinationNavigationIsBack(initialState, targetState)
            if (reducedMotion) {
                // ReducedMotion (§11): sin desplazamiento; la pantalla cambia
                // en el sitio y la comprensión no depende de la animación.
                aqReducedSurfaceEnter() togetherWith aqReducedSurfaceExit()
            } else {
                aqSurfaceEnter(reverse = reverse) togetherWith aqSurfaceExit(reverse = reverse)
            }
        },
        label = AppStrings.DestinationRouteTransition,
    ) { route ->
        when (route) {
            DestinationRoute.List -> DestinationsScreen(
                state,
                graph.destinationsViewModel::onAction,
                navChrome = navChrome,
                appHosts = appHosts,
                onOpenSearch = { onSearchOpened(); nav.push(AppRoute.Search) },
                onOpenContexts = { nav.push(AppRoute.Contexts) },
                onLock = onLock,
                // Cuenta y respaldo no es una superficie navegable en V1
                // (flujo local-first); se mantiene el gancho como no-op.
                onOpenAccount = { },
                syncLookup = syncLookup,
                // Acción rápida / FAB: traer un QR → S02,
                // con cámara y galería dentro del flujo real.
                onScanQr = { graph.destinationsViewModel.onAction(DestinationAction.Edit(null)) },
                // Hub: registrar actividad (S07, sin
                // preasociación) y crear contexto (diálogo existente).
                onRegisterOperation = {
                    graph.operationsViewModel.onAction(OperationAction.New)
                    nav.push(AppRoute.Operations)
                },
                onCreateContext = { name, note ->
                    graph.contextsViewModel.onAction(ContextAction.Create(name, note))
                },
            )
            DestinationRoute.Add -> AddDestinationScreen(
                onImport = { },
                onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
            )
            is DestinationRoute.Edit -> {
                val target = route.id?.let(graph.destinationsViewModel::destination)
                if (target == null) {
                    DestinationNotFound(onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) })
                } else {
                    DestinationEditorScreen(
                        existing = target,
                        contexts = contextState.contexts,
                        appHosts = appHosts,
                        onSave = { updated ->
                            graph.destinationsViewModel.onAction(DestinationAction.Save(updated))
                        },
                        onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
                    )
                }
            }
            is DestinationRoute.Detail -> {
                val destination = graph.destinationsViewModel.destination(route.id)
                if (destination == null) {
                    DestinationNotFound(onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) })
                } else {
                    OperationDetailScreen(
                        state = operationsState,
                        viewModel = graph.operationsViewModel,
                        appHosts = appHosts,
                        syncLookup = syncLookup,
                        onRetrySync = onRetrySync,
                        destinationsViewModel = graph.destinationsViewModel,
                    )
                }
            }
            is DestinationRoute.FullscreenQr -> {
                val destination = graph.destinationsViewModel.destination(route.id)
                if (destination == null || destination.qr.encoded.isBlank()) {
                    DestinationNotFound(onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) })
                } else {
                    QrFullscreenPattern(
                        destination.qr.encoded,
                        destinationName = destination.name.ifBlank { AppStrings.SinNombre },
                        onBack = {
                            graph.destinationsViewModel.onAction(DestinationAction.Back)
                        },
                    )
                }
            }
            DestinationRoute.ImportReview -> {
                val assets = when (val s = importBatchState) {
                    is ImportBatchUiState.Review -> s.batch.candidates.mapNotNull { it.qrAsset }
                    is ImportBatchUiState.Result -> s.batch.candidates.mapNotNull { it.qrAsset }
                    else -> emptyList()
                }
                ImportReviewScreen(
                    assets = assets,
                    onSaveAll = { graph.operationsViewModel.onAction(OperationAction.SaveIncoming(allowDuplicate = true)) },
                    onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
                )
            }
        }
    }
}

/** Rutas de destinos: ¿este cambio de ruta es un regreso (transición inversa)? */
internal fun destinationNavigationIsBack(
    from: DestinationRoute,
    to: DestinationRoute,
): Boolean = when {
    from is DestinationRoute.Edit && to is DestinationRoute.List -> true
    from is DestinationRoute.Add && to is DestinationRoute.List -> true
    from is DestinationRoute.ImportReview && to is DestinationRoute.Add -> true
    from is DestinationRoute.Detail && to is DestinationRoute.List -> true
    from is DestinationRoute.FullscreenQr && to is DestinationRoute.Detail -> true
    from is DestinationRoute.ImportReview && to is DestinationRoute.Edit -> true
    else -> false
}
