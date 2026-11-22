package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.destinations.domain.OperationAction
import com.agendaqr.destinations.domain.OperationRoute
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.SyncResource
import kotlinx.coroutines.CancellationException

@Composable
fun AuthenticatedAppRoot(
    graph: AppGraph,
    appHosts: AppHosts?,
    onLock: () -> Unit,
    nav: AppNavigation,
) {
    val toastHost = appHosts?.toastHost
    val state by graph.uiState.collectAsStateWithLifecycle()
    val contextState by graph.contextsViewModel.state.collectAsStateWithLifecycle()
    val importBatchState by graph.importBatchViewModel.state.collectAsStateWithLifecycle()
    val globalSearchState by graph.globalSearchViewModel.state.collectAsStateWithLifecycle()
    val operationState by graph.operationsViewModel.state.collectAsStateWithLifecycle()
    val existingPreview by remember { mutableStateOf<ExistingImportPreview?>(null) }
    val searchAutoFocus by rememberSaveable { mutableStateOf(true) }
    var top by remember { mutableStateOf(AppRoute.Home) }
    var stack by rememberSaveable { mutableStateOf(listOf(AppRoute.Home)) }
    val navChrome = remember(top) {
        AqNavChromeState(
            destinations = listOf(
                AqNavDestination(appRouteNameOf(AppRoute.Home), AppStrings.Inicio, AqIcons.Home),
                AqNavDestination(appRouteNameOf(AppRoute.Search), AppStrings.Buscar, AqIcons.Search),
                AqNavDestination(appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones, AqIcons.Register),
                AqNavDestination(appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos, AqIcons.Context),
            ),
            selectedId = appRouteNameOf(top),
            onSelect = { name ->
                val destination = appRouteForName(name) ?: return@AqNavChromeState
                searchAutoFocus = false
                nav.popToRoot()
                if (destination != AppRoute.Home) nav.push(destination)
            },
        )
    }

    val needsInnerBack: (AppRoute) -> Boolean = { route ->
        when (route) {
            AppRoute.Home -> state.route != DestinationRoute.List
            AppRoute.Operations -> operationState.route != OperationRoute.List
            AppRoute.Contexts -> contextState.route != ContextRoute.List
            AppRoute.ImportBatch -> importBatchState is ImportBatchUiState.Review
            AppRoute.Search -> false
            AppRoute.Account -> false
        }
    }
    var backProgress by remember { mutableFloatStateOf(0f) }
    PredictiveBackHandler(
        enabled = needsInnerBack(top) || nav.canPop(),
    ) { progress ->
        try {
            progress.collect { event -> backProgress = event.progress }
            backProgress = 0f
            when (val action = systemBackAction(top, needsInnerBack, nav.canPop())) {
                is SystemBackAction.InnerFlow -> when (action.route) {
                    AppRoute.Home -> backFromDestinationDetail()
                    AppRoute.Operations -> graph.operationsViewModel.onAction(OperationAction.Back)
                    AppRoute.Contexts -> graph.contextsViewModel.onAction(ContextAction.Back)
                    AppRoute.ImportBatch -> importBatch.onAction(ImportBatchAction.Back)
                    AppRoute.Search -> Unit
                    AppRoute.Account -> Unit
                }
                SystemBackAction.PopSurface -> nav.pop()
                SystemBackAction.SystemExit -> Unit
            }
        } catch (e: CancellationException) {
            backProgress = 0f
        }
    }

    val reducedMotion = LocalReducedMotion.current

    Box(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = top,
            transitionSpec = {
                val from = stack.indexOf(initialState)
                val to = stack.indexOf(targetState)
                val reverse = to in 0..from
                if (reducedMotion) {
                    aqReducedSurfaceEnter() togetherWith aqReducedSurfaceExit()
                } else {
                    aqSurfaceEnter(reverse = reverse) togetherWith aqSurfaceExit(reverse = reverse)
                }
            },
            label = AppStrings.SurfaceRouteTransition,
        ) { route ->
            val predictiveScale = 1f - (backProgress * AqMotion.PredictiveBackShrink)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - (backProgress * AqMotion.PredictiveBackFade)
                        scaleX = predictiveScale
                        scaleY = predictiveScale
                    },
            ) {
                when (route) {
                    AppRoute.ImportBatch -> ImportBatchScreen(
                        state = importBatchState,
                        existing = existingPreview,
                        appHosts = appHosts,
                        onAction = { action -> importBatch.onAction(action) },
                    )
                    AppRoute.Search -> GlobalSearchScreen(
                        state = globalSearchState,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        onAction = graph.globalSearchViewModel::onAction,
                        autoFocus = searchAutoFocus,
                        onSelect = { result ->
                            when (result.type) {
                                AgendaSearchResultType.CONTEXT -> {
                                    result.contextId?.let { graph.contextsViewModel.onAction(ContextAction.Open(it)) }
                                    nav.push(AppRoute.Contexts)
                                }
                                AgendaSearchResultType.QR -> {
                                    result.destinationId?.let {
                                        graph.destinationsViewModel.onAction(DestinationAction.Open(it))
                                    }
                                    nav.pop()
                                    searchReturnDestinationId = result.destinationId
                                }
                                AgendaSearchResultType.ACTIVITY -> {
                                    result.operationId?.let {
                                        graph.operationsViewModel.onAction(OperationAction.Open(it))
                                        nav.push(AppRoute.Operations)
                                    }
                                }
                                AgendaSearchResultType.COMPROBANTE -> {
                                    graph.operationsViewModel.onAction(OperationAction.OpenComprobante(result.id))
                                    nav.push(AppRoute.Operations)
                                }
                            }
                        },
                    )
                    AppRoute.Contexts -> ContextsScreen(
                        state = contextState,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        onAction = graph.contextsViewModel::onAction,
                        onBack = { nav.pop() },
                        onOpenDestination = { id ->
                            graph.destinationsViewModel.onAction(DestinationAction.Open(id))
                            nav.popToRoot()
                        },
                        onOpenOperation = { id ->
                            graph.operationsViewModel.onAction(OperationAction.Open(id))
                            nav.pop()
                            nav.push(AppRoute.Operations)
                        },
                        onOpenComprobante = { id ->
                            graph.operationsViewModel.onAction(OperationAction.OpenComprobante(id))
                            nav.pop()
                            nav.push(AppRoute.Operations)
                        },
                        onOpenOperations = {
                            nav.push(AppRoute.Operations)
                        },
                    )
                    AppRoute.Operations -> OperationsScreen(
                        state = operationState,
                        viewModel = graph.operationsViewModel,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        syncLookup = syncLookup,
                        onRetrySync = retrySync,
                        destinationsViewModel = graph.destinationsViewModel,
                        onCreateContext = { name, note ->
                            graph.contextsViewModel.onAction(ContextAction.Create(name, note))
                        },
                        justCreatedContextId = contextState.justCreatedContextId,
                    )
                    AppRoute.Home -> HomeSurface(
                        state = state,
                        contextState = contextState,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        onSearchOpened = {
                            searchReturnDestinationId = null
                            searchAutoFocus = true
                        },
                        onDetailBack = ::backFromDestinationDetail,
                        graph = graph,
                        onLock = onLock,
                        nav = nav,
                        syncLookup = syncLookup,
                        onRetrySync = retrySync,
                    )
                    AppRoute.Account -> AccountScreen(
                        navChrome = navChrome,
                        appHosts = appHosts,
                        onBack = { nav.pop() },
                    )
                }
            }
        }
    }
}
