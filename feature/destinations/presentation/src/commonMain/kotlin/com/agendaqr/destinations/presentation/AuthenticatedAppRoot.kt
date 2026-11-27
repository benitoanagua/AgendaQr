package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.aqReducedSurfaceEnter
import com.agendaqr.core.ui.components.aqReducedSurfaceExit
import com.agendaqr.core.ui.components.aqSurfaceEnter
import com.agendaqr.core.ui.components.aqSurfaceExit
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.destinations.data.createAuthRepository
import com.agendaqr.destinations.domain.AgendaSearchResultType
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import com.agendaqr.destinations.domain.SignUpUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Raíz autenticada: host de superficies [AppRoute] sobre [AppBackStack].
 *
 * Cada [AppRoute] es una superficie completa; los flujos internos
 * (Detalle/Editor de destinos, rutas de Contextos y Operaciones) viven en
 * el ViewModel de su flujo. Este host es la única autoridad de qué
 * superficie está visible: el Component Lab, la galería y producción
 * consumen las mismas pantallas a través de aquí.
 *
 * - Navegación raíz: [AqNavChromeState] con los 4 destinos (Inicio,
 *   Buscar, Operaciones, Contextos) compartido por todas las superficies.
 * - Back del sistema: primero el flujo interno de la superficie visible
 *   ([systemBackAction]); si no tiene adónde volver, pop de superficie;
 *   en la raíz, el sistema decide (ADR-0006).
 * - Transición entre superficies: estándar corta de Moon; con movimiento
 *   reducido el cambio es inmediato.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticatedAppRoot(
    graph: AuthenticatedSessionGraph,
    appHosts: AppHosts?,
    onLock: () -> Unit,
    nav: AppBackStack,
) {
    val state by graph.destinationsViewModel.state.collectAsStateWithLifecycle()
    val contextState by graph.contextsViewModel.state.collectAsStateWithLifecycle()
    val operationState by graph.operationsViewModel.state.collectAsStateWithLifecycle()
    val globalSearchState by graph.globalSearchViewModel.state.collectAsStateWithLifecycle()
    val importBatchHolder = remember(graph) { ImportBatchStateHolder(graph) }
    val importBatchState by importBatchHolder.state.collectAsStateWithLifecycle()
    val existingPreview by importBatchHolder.existingPreview.collectAsStateWithLifecycle()
    val stack by nav.stack.collectAsStateWithLifecycle()
    val top = stack.lastOrNull() ?: AppRoute.Home
    val scope = rememberCoroutineScope()

    // Estado de sincronización por elemento (§5): la cola deduplica por
    // (resource, entityId); el observer emite la instantánea cada 2 s.
    val syncQueue by graph.queueObserver.observeQueue().collectAsStateWithLifecycle(initialValue = emptyList())
    val syncLookup = remember(syncQueue) { ElementSyncLookup(syncQueue) }
    val retrySync: () -> Unit = { scope.launch { graph.syncProcessor.drain() } }

    // Retorno Búsqueda → Detalle QR: al abrir un QR desde resultados se
    // marca el id; el Back desde ese detalle restaura la consulta.
    var searchReturnDestinationId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchAutoFocus by rememberSaveable { mutableStateOf(true) }

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
            AppRoute.Search, AppRoute.Account -> false
        }
    }

    fun backFromDestinationDetail() {
        val detailId = when (val route = state.route) {
            is DestinationRoute.Detail -> route.id
            is DestinationRoute.FullscreenQr -> route.id
            else -> null
        }
        graph.destinationsViewModel.onAction(DestinationAction.Back)
        val pending = searchReturnDestinationId
        searchReturnDestinationId = null
        if (pending != null && pending == detailId) nav.push(AppRoute.Search)
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
                    AppRoute.ImportBatch -> importBatchHolder.onAction(ImportBatchAction.Back)
                    AppRoute.Search, AppRoute.Account -> Unit
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
                    AppRoute.Account -> AccountSurface(onBack = { nav.pop() })
                    AppRoute.ImportBatch -> ImportBatchScreen(
                        state = importBatchState,
                        existing = existingPreview,
                        appHosts = appHosts,
                        onAction = importBatchHolder::onAction,
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
                                    result.contextId?.let {
                                        graph.contextsViewModel.onAction(ContextAction.Open(it))
                                    }
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
                        onOpenOperations = { nav.push(AppRoute.Operations) },
                    )
                    AppRoute.Operations -> OperationsSurface(
                        state = operationState,
                        viewModel = graph.operationsViewModel,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        syncLookup = syncLookup,
                        onRetrySync = retrySync,
                        destinationsViewModel = graph.destinationsViewModel,
                        contextState = contextState,
                        onCreateContext = { name, note ->
                            graph.contextsViewModel.onAction(ContextAction.Create(name, note))
                        },
                    )
                    AppRoute.Home -> HomeSurface(
                        state = state,
                        contextState = contextState,
                        graph = graph,
                        onLock = onLock,
                        nav = nav,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        syncLookup = syncLookup,
                        onRetrySync = retrySync,
                        onSearchOpened = {
                            searchReturnDestinationId = null
                            searchAutoFocus = true
                        },
                        onDetailBack = ::backFromDestinationDetail,
                    )
                }
            }
        }
    }
}

/**
 * Cuenta y respaldo (ADR-0016): acceso remoto OPCIONAL con correo y
 * contraseña. El [AuthViewModel] y el [AuthRepository] se crean de forma
 * perezosa — solo cuando la superficie se compone; si el usuario nunca
 * entra, no se instancia nada remoto.
 */
@Composable
private fun AccountSurface(onBack: () -> Unit) {
    val repository = remember { createAuthRepository() }
    val viewModel = remember(repository) {
        AuthViewModel(
            ObserveAuthStateUseCase(repository),
            SignInUseCase(repository),
            SignUpUseCase(repository),
            SignOutUseCase(repository),
        )
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AuthScreen(
        state = state,
        onEmailChanged = viewModel::setEmail,
        onPasswordChanged = viewModel::setPassword,
        onSignIn = viewModel::submitSignIn,
        onSignUp = viewModel::submitSignUp,
        title = AppStrings.CuentaYRespaldo,
        onBack = onBack,
    )
}

/**
 * S07 — Operaciones: host de las rutas internas
 * ([OperationRoute.List]/[New]/[Edit]/[Detail]/[Unassociated]) sobre el
 * mismo [OperationsViewModel]. Es la única composición de estas
 * pantallas: Lab y producción ven exactamente lo mismo.
 */
@Composable
private fun OperationsSurface(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    navChrome: AqNavChromeState?,
    appHosts: AppHosts?,
    syncLookup: ElementSyncLookup,
    onRetrySync: () -> Unit,
    destinationsViewModel: DestinationsViewModel,
    contextState: ContextsUiState,
    onCreateContext: (name: String, note: String?) -> Unit,
) {
    when (val route = state.route) {
        OperationRoute.List -> OperationListScreen(
            state = state,
            viewModel = viewModel,
            navChrome = navChrome,
            appHosts = appHosts,
            syncLookup = syncLookup,
        )
        OperationRoute.New -> OperationEditorScreen(
            state = state,
            viewModel = viewModel,
            existing = null,
            appHosts = appHosts,
            onCreateContext = onCreateContext,
            justCreatedContextId = contextState.justCreatedContextId,
        )
        is OperationRoute.Edit -> OperationEditorScreen(
            state = state,
            viewModel = viewModel,
            existing = state.operations.firstOrNull { it.id == route.id },
            appHosts = appHosts,
            onCreateContext = onCreateContext,
            justCreatedContextId = contextState.justCreatedContextId,
        )
        is OperationRoute.Detail -> OperationDetailScreen(
            state = state,
            viewModel = viewModel,
            appHosts = appHosts,
            syncLookup = syncLookup,
            onRetrySync = onRetrySync,
            destinationsViewModel = destinationsViewModel,
        )
        OperationRoute.Unassociated -> UnassociatedScreen(
            state = state,
            viewModel = viewModel,
            appHosts = appHosts,
            syncLookup = syncLookup,
        )
    }
}
