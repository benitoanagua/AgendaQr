package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.amReducedSurfaceEnter
import com.agendaqr.core.ui.components.amReducedSurfaceExit
import com.agendaqr.core.ui.components.amSurfaceEnter
import com.agendaqr.core.ui.components.amSurfaceExit
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
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup,
    onRetrySync: () -> Unit,
    onSearchOpened: () -> Unit = {},
    /** Back desde el detalle del QR (S05: puede restaurar la Búsqueda). */
    onDetailBack: () -> Unit = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
) {
    val reducedMotion = LocalReducedMotion.current
    AnimatedContent(
        targetState = state.route,
        transitionSpec = {
            val reverse = destinationNavigationIsBack(initialState, targetState)
            if (reducedMotion) {
                // ReducedMotion (§11): sin desplazamiento; la pantalla cambia
                // en el sitio y la comprensión no depende de la animación.
                amReducedSurfaceEnter() togetherWith amReducedSurfaceExit()
            } else {
                amSurfaceEnter(reverse = reverse) togetherWith amSurfaceExit(reverse = reverse)
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
                // Cuenta y respaldo es OPCIONAL: se abre como superficie más
                // en el stack; la app local no depende de ella.
                onOpenAccount = { nav.push(AppRoute.Account) },
                syncLookup = syncLookup,
                // Acción rápida / FAB: traer un QR → S02,
                // con cámara y galería dentro del flujo real.
                onScanQr = { graph.destinationsViewModel.onAction(DestinationAction.Edit(null)) },
                // Hub (U-10, D8): registrar actividad (S07, sin
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
                appHosts = appHosts,
                onImport = { assets ->
                    graph.destinationsViewModel.onAction(
                        DestinationAction.ImportAssets(assets),
                    )
                },
                onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
            )
            is DestinationRoute.Edit -> {
                val target = route.id?.let(graph.destinationsViewModel::destination)
                if (target == null) {
                    DestinationNotFound(onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) })
                } else {
                    DestinationEditorScreen(
                        existing = target,
                appHosts = appHosts,
                onSave = { destination ->
                    graph.destinationsViewModel.onAction(DestinationAction.Update(destination))
                },
                onImportMany = { assets ->
                    graph.destinationsViewModel.onAction(
                        DestinationAction.ImportAssets(assets),
                    )
                },
                contexts = contextState.contexts,
                onCreateContext = { name, note ->
                    graph.contextsViewModel.onAction(ContextAction.Create(name, note))
                },
                justCreatedContextId = contextState.justCreatedContextId,
                onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
                isSaving = state.isSaving,
                error = state.error,
                onClearError = { graph.destinationsViewModel.onAction(DestinationAction.ClearError) },
                onRetryError = { graph.destinationsViewModel.onAction(DestinationAction.RetryFailed) },
                    )
                }
            }
            is DestinationRoute.Detail -> {
                val destination = graph.destinationsViewModel.destination(route.id)
                if (destination == null) {
                    DestinationNotFound(onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) })
                } else {
                    DestinationDetailScreen(
                        appHosts = appHosts,
                        destination = destination,
                        onShowQr = {
                            graph.destinationsViewModel.onAction(DestinationAction.ShowQr(destination.id))
                        },
                        onEdit = {
                            graph.destinationsViewModel.onAction(DestinationAction.Edit(destination.id))
                        },
                        onDelete = {
                            graph.destinationsViewModel.onAction(DestinationAction.Delete(destination.id))
                        },
                        onShare = { shareQr(destination.qr) },
                        onBack = onDetailBack,
                        // Dock: registrar actividad — S07
                        // real vía el VM central; sin preasociación de
                        // destino (regla que no existe en el dominio).
                        onRegisterOperation = {
                            graph.operationsViewModel.onAction(OperationAction.New)
                            nav.push(AppRoute.Operations)
                        },
                        syncStatus = syncLookup.status(
                            com.agendaqr.destinations.data.SyncResource.DESTINATION,
                            destination.id,
                        ),
                        onRetrySync = onRetrySync,
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
            DestinationRoute.ImportReview -> ImportReviewScreen(
                appHosts = appHosts,
                assets = graph.destinationsViewModel.importedAssets(),
                onSaveAll = graph.destinationsViewModel::saveImportedAssets,
                onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
                isSaving = state.isSaving,
                error = state.error,
                onClearError = { graph.destinationsViewModel.onAction(DestinationAction.ClearError) },
            )
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
