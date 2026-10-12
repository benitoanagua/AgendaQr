package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.components.XauxaNavChromeState
import com.agendaqr.core.ui.components.XauxaNavDestination
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.xauxaReducedSurfaceEnter
import com.agendaqr.core.ui.components.xauxaReducedSurfaceExit
import com.agendaqr.core.ui.components.xauxaSurfaceEnter
import com.agendaqr.core.ui.components.xauxaSurfaceExit
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.domain.AgendaSearchResultType
import kotlinx.coroutines.launch

/**
 * Navegación de superficies de la app autenticada (§12 V1.2, ADR-0012).
 *
 * El back stack ([AppBackStack]) es la única autoridad de qué superficie
 * está visible; el Back del sistema pasa primero por el flujo interno de la
 * superficie (su ViewModel), después por el pop de superficie y en la raíz
 * lo deja al sistema (salir de la app). El predictive back de Android 14+
 * conduce la animación de la superficie con el progreso del gesto (en iOS
 * el gesto se comporta como back simple). Cambiar de destino raíz =
 * `popToRoot()` + `push(destino)`: Inicio sigue siendo la raíz y el
 * Back desde otro destino vuelve a Inicio (§4 S01).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticatedAppRoot(
    graph: AuthenticatedSessionGraph,
    onSignOut: () -> Unit,
) {
    // el back stack SOBREVIVE a rotación y a muerte de
    // proceso (rememberSaveable + Saver por nombre de ruta).
    val nav = rememberSaveable(saver = AppBackStackSaver) { AppBackStack() }
    val stack by nav.stack.collectAsStateWithLifecycle()
    val top = stack.last()

    val state by graph.destinationsViewModel.state.collectAsStateWithLifecycle()
    val operationState by graph.operationsViewModel.state.collectAsStateWithLifecycle()
    val contextState by graph.contextsViewModel.state.collectAsStateWithLifecycle()
    val globalSearchState by graph.globalSearchViewModel.state.collectAsStateWithLifecycle()

    // D1 — la lógica del lote vive en ImportBatchCoordinator (sin Compose).
    val importBatch = remember(graph) { ImportBatchStateHolder(graph) }
    val importBatchState by importBatch.state.collectAsStateWithLifecycle()
    val existingPreview by importBatch.existingPreview.collectAsStateWithLifecycle()

    // S05: un resultado QR de la Búsqueda abre su detalle en Inicio; al
    // volver se restaura la superficie de Búsqueda (la consulta sobrevive
    // en el ViewModel retenido). La restauración se ata al detalle
    // concreto: si el detalle se cerró por otra vía (eliminar, guardar la
    // edición — backs internos del VM) el id ya no coincide y no se
    // restaura nada ajeno.
    var searchReturnDestinationId by rememberSaveable { mutableStateOf<String?>(null) }
    fun backFromDestinationDetail() {
        val detailId = (state.route as? DestinationRoute.Detail)?.id
        graph.destinationsViewModel.onAction(DestinationAction.Back)
        val pending = searchReturnDestinationId
        searchReturnDestinationId = null
        if (pending != null && pending == detailId) {
            nav.push(AppRoute.Search)
        }
    }

    var pendingCount by remember { mutableStateOf(0) }
    var isOffline by remember { mutableStateOf(false) }
    var hasFailed by remember { mutableStateOf(false) }
    var syncQueueItems by remember { mutableStateOf<List<com.agendaqr.destinations.data.PendingSyncMutation>>(emptyList()) }
    LaunchedEffect(graph.queueObserver) {
        graph.queueObserver.observeQueue().collect { items ->
            syncQueueItems = items
            pendingCount = items.size
            hasFailed = items.any { it.state == SyncMutationState.FAILED }
        }
    }
    // anuncio de sincronización COMPLETADA por evento:
    // la cola pasa de >0 a 0 (no se repite en recomposiciones; el token
    // solo cambia en la transición). V1.2: se publica como Snackbar
    // transitorio (con háptico y anuncio accesible por evento).
    val feedbackHost = remember { com.agendaqr.core.ui.components.XauxaFeedbackHost() }
    var syncDoneEvent by remember { mutableStateOf<Any?>(null) }
    var previousPendingCount by remember { mutableStateOf(0) }
    LaunchedEffect(pendingCount) {
        if (previousPendingCount > 0 && pendingCount == 0) {
            syncDoneEvent = Any()
        }
        previousPendingCount = pendingCount
    }
    com.agendaqr.core.ui.components.XauxaFeedbackEvent(
        event = syncDoneEvent,
        message = AppStrings.SincronizacionCompletada,
        feedbackHost = feedbackHost,
    )
    // estado de sincronización por elemento (consulta indexada).
    val syncLookup = remember(syncQueueItems) { ElementSyncLookup(syncQueueItems) }
    val retrySync = remember(graph) {
        { graph.sessionScope.launch { graph.syncProcessor.drain() }; Unit }
    }
    LaunchedEffect(graph.queueObserver) {
        graph.queueObserver.observeNetwork().collect { online -> isOffline = !online }
    }

    // QR compartidos desde fuera de la app: pueden llegar en frío (antes de
    // que exista colector) y se retienen hasta que la app autenticada los
    // consume; el contrato S02/S09 lleva la importación compartida a la
    // revisión de QR.
    LaunchedEffect(graph.destinationsViewModel) {
        observeSharedQrImports().collect { result ->
            graph.destinationsViewModel.onAction(
                DestinationAction.ImportAssets(result.assets),
            )
        }
    }
    // Un comprobante recibido (share o selección única no-QR) se presenta
    // desde la superficie de operaciones; sin esto el diálogo quedaba
    // montado sobre una pantalla que no lo muestra y era invisible.
    LaunchedEffect(operationState.pendingIncoming) {
        if (operationState.pendingIncoming != null) nav.push(AppRoute.Operations)
    }
    // Errores de importación recuperables (URI compartida ilegible/revocada).
    var importError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        observeImportErrors().collect { importError = it }
    }

    ImportBatchControls { batch ->
        importBatch.onAnalyzed(batch)
        nav.push(AppRoute.ImportBatch)
    }

    // ------------------------------------------------------------------
    // Hosts compartidos de la sesión (§12 V1.2): el AppStatusHost (estados
    // persistentes bajo la barra superior de cada superficie) y el host
    // del Snackbar (eventos transitorios). La superficie activa los monta
    // en su Scaffold.
    // ------------------------------------------------------------------
    val retryNow: () -> Unit = remember(graph) {
        { graph.sessionScope.launch { graph.syncProcessor.drain() }; Unit }
    }
    val appHosts = AppHosts(
        status = {
            AppStatusHost(
                importError = importError,
                isOffline = isOffline,
                pendingCount = pendingCount,
                hasFailed = hasFailed,
                onDismissImportError = { importError = null },
                onRetryNow = retryNow,
            )
        },
        feedback = feedbackHost,
    )

    // ------------------------------------------------------------------
    // Navegación raíz (§4 S01 V1.2): Inicio · Buscar · Actividades ·
    // Contextos. Cambiar de destino = popToRoot + push: Inicio es la raíz
    // del back stack (Back desde otro destino vuelve a Inicio; desde
    // Inicio sale de la app).
    // ------------------------------------------------------------------
    val navChrome = remember(top) {
        XauxaNavChromeState(
            destinations = listOf(
                XauxaNavDestination(appRouteNameOf(AppRoute.Home), AppStrings.Inicio, XauxaIcons.Home),
                XauxaNavDestination(appRouteNameOf(AppRoute.Search), AppStrings.Buscar, XauxaIcons.Search),
                XauxaNavDestination(appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones, XauxaIcons.Register),
                XauxaNavDestination(appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos, XauxaIcons.Context),
            ),
            selectedId = appRouteNameOf(top),
            onSelect = { name ->
                val destination = appRouteForName(name) ?: return@XauxaNavChromeState
                nav.popToRoot()
                if (destination != AppRoute.Home) nav.push(destination)
            },
        )
    }

    // ------------------------------------------------------------------
    // Back del sistema : flujo interno → pop de superficie → sistema.
    // Predictive back (Android 14+): el progreso del gesto anima la
    // superficie saliente (escala + fundido); al confirmar corre la
    // transición estándar; al cancelar vuelve a su estado. En iOS y en
    // versiones sin gesto predictivo se comporta como back simple.
    // ------------------------------------------------------------------
    val needsInnerBack: (AppRoute) -> Boolean = { route ->
        when (route) {
            AppRoute.Home -> state.route != DestinationRoute.List
            AppRoute.Operations -> operationState.route != OperationRoute.List
            AppRoute.Contexts -> contextState.route != ContextRoute.List
            AppRoute.ImportBatch -> importBatchState is ImportBatchUiState.Review
            AppRoute.Search -> false
        }
    }
    var backProgress by remember { mutableFloatStateOf(0f) }
    PredictiveBackHandler(
        enabled = needsInnerBack(top) || nav.canPop(),
    ) { progress ->
        // Predictive back multiplataforma (ui-backhandler 1.8.2): el flujo
        // emite el progreso del gesto; COMPLETA al confirmar y se CANCELA
        // si el gesto se aborta. La superficie visible se encoge/funde
        // con el progreso; confirmar ejecuta la misma decisión
        // systemBackAction; cancelar la restaura.
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
                }
                SystemBackAction.PopSurface -> nav.pop()
                SystemBackAction.SystemExit -> Unit // handler deshabilitado en la raíz.
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            // Gesto cancelado: la superficie vuelve a su estado.
            backProgress = 0f
        }
    }

    val reducedMotion = LocalReducedMotion.current

    Box(Modifier.fillMaxSize()) {
        // Las SUPERFICIES (AppRoute) transicionan con la transición
        // estándar corta (fundido + desplazamiento pequeño). La dirección
        // se decide por la profundidad del back stack: si la nueva
        // superficie está MENOS profunda que la anterior, es un regreso
        // (desplazamiento inverso). Con reduced motion: cambio inmediato
        // en el sitio (§11).
        AnimatedContent(
            targetState = top,
            transitionSpec = {
                val from = stack.indexOf(initialState)
                val to = stack.indexOf(targetState)
                val reverse = to in 0..from
                if (reducedMotion) {
                    xauxaReducedSurfaceEnter() togetherWith xauxaReducedSurfaceExit()
                } else {
                    xauxaSurfaceEnter(reverse = reverse) togetherWith xauxaSurfaceExit(reverse = reverse)
                }
            },
            label = AppStrings.SurfaceRouteTurnstile,
        ) { route ->
            // Predictive back: la superficie visible se encoge y funde con
            // el progreso del gesto (cancelar la restaura; confirmar corre
            // la transición estándar). Con reduced motion el gesto no
            // añade animación (solo el estado del gesto cambia).
            val predictiveScale = 1f - (backProgress * PredictiveBackShrink)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - (backProgress * PredictiveBackFade)
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
                        onSelect = { result ->
                            when (result.type) {
                                AgendaSearchResultType.CONTEXT -> {
                                    // S04 → S06: la Búsqueda queda debajo del stack, así el
                                    // Back conserva la consulta (S05) sin estado extra.
                                    result.contextId?.let { graph.contextsViewModel.onAction(ContextAction.Open(it)) }
                                    nav.push(AppRoute.Contexts)
                                }
                                AgendaSearchResultType.QR -> {
                                    result.destinationId?.let {
                                        graph.destinationsViewModel.onAction(
                                            DestinationAction.Open(it),
                                        )
                                    }
                                    nav.pop()
                                    searchReturnDestinationId = result.destinationId
                                }
                                AgendaSearchResultType.ACTIVITY -> {
                                    result.operationId?.let {
                                        graph.operationsViewModel.onAction(OperationAction.Open(it))
                                        // Sin pop: Operaciones se apila SOBRE la Búsqueda;
                                        // Back vuelve a los resultados con la consulta (S05).
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
                        // Lista → pop: la superficie que queda debajo es el origen real
                        // (Inicio o Búsqueda), no un estado aparte (S05).
                        onBack = { nav.pop() },
                        // S06 utilizable: las filas reutilizan las rutas
                        // existentes (las mismas que los resultados de búsqueda):
                        // el QR abre su detalle en Inicio; la actividad y el
                        // comprobante abren su superficie en Operaciones.
                        onOpenDestination = { id ->
                            graph.destinationsViewModel.onAction(DestinationAction.Open(id))
                            // El detalle del QR es una ruta INTERNA de Inicio (la raíz):
                            // desde S06 (posiblemente abierta sobre la Búsqueda) hay que
                            // volver a la raíz, igual que al abrir un QR desde un
                            // resultado de búsqueda (S04 → S09/detalle).
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
                            nav.pop()
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
                        // U3/ADR-0003: el selector S08 puede crear contexto; el draft
                        // del editor nunca sale de composición.
                        onCreateContext = { name, note ->
                            graph.contextsViewModel.onAction(ContextAction.Create(name, note))
                        },
                        justCreatedContextId = contextState.justCreatedContextId,
                    )
                    AppRoute.Home -> HomeSurface(
                        // S01 (lista convencional): el estado de la pantalla
                        // necesita las actividades recientes; se fusionan aquí (la
                        // observación vive en la superficie de Operaciones).
                        state = state.copy(recentOperations = operationState.operations),
                        contextState = contextState,
                        navChrome = navChrome,
                        appHosts = appHosts,
                        onSearchOpened = { searchReturnDestinationId = null },
                        onDetailBack = ::backFromDestinationDetail,
                        graph = graph,
                        onSignOut = onSignOut,
                        nav = nav,
                        syncLookup = syncLookup,
                        onRetrySync = retrySync,
                    )
                }
            }
        }
    }
}

/** Cuánto se encoge la superficie con el predictive back (fracción 0–1). */
internal const val PredictiveBackShrink = 0.08f

/** Cuánto funde la superficie con el predictive back (fracción 0–1). */
internal const val PredictiveBackFade = 0.5f
