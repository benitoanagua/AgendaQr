package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.agendaqr.core.ui.components.xauxaReducedMotionEnter
import com.agendaqr.core.ui.components.xauxaReducedMotionExit
import com.agendaqr.core.ui.components.xauxaTurnstileEnter
import com.agendaqr.core.ui.components.xauxaTurnstileExit
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.destinations.data.SyncResource


/**
 * S01 — Inicio y el flujo de destinos completo (S02/S09). Las rutas internas
 * de destinos siguen viviendo en [DestinationsUiState.route] con su
 * transición turnstile, que respeta el movimiento reducido del sistema.
 */
@Composable
internal fun HomeSurface(
    state: DestinationsUiState,
    contextState: ContextsUiState,
    graph: AuthenticatedSessionGraph,
    onSignOut: () -> Unit,
    nav: AppBackStack,
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
                xauxaReducedMotionEnter() togetherWith xauxaReducedMotionExit()
            } else {
                xauxaTurnstileEnter(reverse = reverse) togetherWith
                    xauxaTurnstileExit(reverse = reverse)
            }
        },
        label = AppStrings.DestinationRouteTurnstile,
    ) { route ->
        when (route) {
            DestinationRoute.List -> DestinationsScreen(
                state,
                graph.destinationsViewModel::onAction,
                onOpenOperations = {
                    graph.operationsViewModel.onAction(OperationAction.New)
                    nav.push(AppRoute.Operations)
                },
                onOpenSearch = { onSearchOpened(); nav.push(AppRoute.Search) },
                onOpenContexts = { nav.push(AppRoute.Contexts) },
                onSignOut = onSignOut,
                // S01 (tile vivo): la actividad reciente abre su detalle en
                // la superficie de Operaciones (misma ruta que una fila de
                // resultados de búsqueda).
                onOpenOperation = { id ->
                    graph.operationsViewModel.onAction(OperationAction.Open(id))
                    nav.push(AppRoute.Operations)
                },
                syncLookup = syncLookup,
            )
            DestinationRoute.Add -> AddDestinationScreen(
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
                    QrFullscreenPattern(destination.qr.encoded) {
                        graph.destinationsViewModel.onAction(DestinationAction.Back)
                    }
                }
            }
            DestinationRoute.ImportReview -> ImportReviewScreen(
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

/** Rutas de destinos: ¿este cambio de ruta es un regreso (turnstile inverso)? */
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
