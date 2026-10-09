package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.components.xauxaReducedMotionEnter
import com.agendaqr.core.ui.components.xauxaReducedMotionExit
import com.agendaqr.core.ui.components.xauxaTurnstileEnter
import com.agendaqr.core.ui.components.xauxaTurnstileExit
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.domain.AgendaSearchResultType
import kotlinx.coroutines.launch

/**
 * Navegación de superficies de la app autenticada (T2).
 *
 * El back stack ([AppBackStack]) es la única autoridad de qué superficie
 * está visible; el Back del sistema pasa primero por el flujo interno de la
 * superficie (su ViewModel), después por el pop de superficie y en la raíz
 * lo deja al sistema (salir de la app). Los botones "Volver" existentes se
 * conservan: llaman al mismo pop/back.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticatedAppRoot(
    graph: AuthenticatedSessionGraph,
    onSignOut: () -> Unit,
) {
    // Ronda 2 (Área C): el back stack SOBREVIVE a rotación y a muerte de
    // proceso (rememberSaveable + Saver por nombre de ruta).
    val nav = rememberSaveable(saver = AppBackStackSaver) { AppBackStack() }
    val stack by nav.stack.collectAsState()
    val top = stack.last()

    val state by graph.destinationsViewModel.state.collectAsState()
    val operationState by graph.operationsViewModel.state.collectAsState()
    val contextState by graph.contextsViewModel.state.collectAsState()
    val globalSearchState by graph.globalSearchViewModel.state.collectAsState()

    // D1 — la lógica del lote vive en ImportBatchCoordinator (sin Compose).
    val importBatch = remember(graph) { ImportBatchStateHolder(graph) }
    val importBatchState by importBatch.state.collectAsState()
    val existingPreview by importBatch.existingPreview.collectAsState()

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
    // T6: estado de sincronización por elemento (consulta indexada).
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
    // Back del sistema (T2): flujo interno → pop de superficie → sistema.
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
    BackHandler(enabled = needsInnerBack(top) || nav.canPop()) {
        when (val action = systemBackAction(top, needsInnerBack, nav.canPop())) {
            is SystemBackAction.InnerFlow -> when (action.route) {
                AppRoute.Home -> backFromDestinationDetail()
                AppRoute.Operations -> graph.operationsViewModel.onAction(OperationAction.Back)
                AppRoute.Contexts -> graph.contextsViewModel.onAction(ContextAction.Back)
                AppRoute.ImportBatch -> importBatch.onAction(ImportBatchAction.Back)
                AppRoute.Search -> Unit
            }
            SystemBackAction.PopSurface -> nav.pop()
            SystemBackAction.SystemExit -> Unit // BackHandler deshabilitado en la raíz.
        }
    }

    Column {
        importError?.let { message ->
            // T5: error recuperable con acción (spec §10): ELEGIR OTRA
            // IMAGEN despeja el error para volver a intentarlo desde la
            // pantalla de origen (Añadir/Galería).
            val error = importReadError(message)
            XauxaStatusBanner(
                error.display(),
                tone = XauxaTone.Danger,
                actionLabel = error.action.label,
                onAction = { importError = null },
                onDismiss = { importError = null },
                dismissLabel = AppStrings.Descartar,
            )
        }
        if (isOffline) {
            XauxaStatusBanner(
                AppStrings.SinConexionLosCambiosSe,
                tone = XauxaTone.Neutral,
            )
        }
        if (pendingCount > 0) {
            XauxaStatusBanner(
                syncPendingLabel(pendingCount, retrying = hasFailed),
                tone = if (hasFailed) XauxaTone.Danger else XauxaTone.Neutral,
            )
            if (hasFailed) {
                XauxaTextAction(
                    label = AppStrings.ReintentarAhora,
                    onClick = { graph.sessionScope.launch { graph.syncProcessor.drain() } },
                )
            }
        }
    }

    // Fase 3 (auditoría): las SUPERFICIES (AppRoute) también transicionan
    // con el turnstile — igual que las rutas internas de HomeSurface. La
    // dirección se decide por la profundidad del back stack: si la nueva
    // superficie está MENOS profunda que la anterior, es un regreso
    // (turnstile inverso). Con reduced motion: cambio inmediato en el
    // sitio (§11). NOTA (ts3): el predictive back animado de Android 14+
    // requiere verificación manual en dispositivo — ver changelog.
    val reducedMotion = com.agendaqr.core.ui.motion.LocalReducedMotion.current
    AnimatedContent(
        targetState = top,
        transitionSpec = {
            // Profundidad en el stack: si la nueva superficie es menos
            // profunda (o igual), es un regreso visual.
            val from = stack.indexOf(initialState)
            val to = stack.indexOf(targetState)
            val reverse = to in 0..from
            if (reducedMotion) {
                xauxaReducedMotionEnter() togetherWith xauxaReducedMotionExit()
            } else {
                xauxaTurnstileEnter(reverse = reverse) togetherWith
                    xauxaTurnstileExit(reverse = reverse)
            }
        },
        label = AppStrings.SurfaceRouteTurnstile,
    ) { route ->
        when (route) {
        AppRoute.ImportBatch -> ImportBatchScreen(
            state = importBatchState,
            existing = existingPreview,
            onAction = { action -> importBatch.onAction(action) },
        )
        AppRoute.Search -> GlobalSearchScreen(
            state = globalSearchState,
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
            onBack = { nav.pop() },
        )
        AppRoute.Contexts -> ContextsScreen(
            state = contextState,
            onAction = graph.contextsViewModel::onAction,
            // Lista → pop: la superficie que queda debajo es el origen real
            // (Inicio o Búsqueda), no un estado aparte (S05).
            onBack = { nav.pop() },
            // T11 — S06 utilizable: las filas reutilizan las rutas
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
            onBack = { nav.pop() },
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
            state = state,
            contextState = contextState,
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
