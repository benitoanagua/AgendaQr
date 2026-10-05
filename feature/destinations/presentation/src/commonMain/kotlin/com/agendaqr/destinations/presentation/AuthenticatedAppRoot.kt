package com.agendaqr.destinations.presentation

import androidx.compose.animation.AnimatedContent
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val nav = remember { AppBackStack() }
    val stack by nav.stack.collectAsState()
    val top = stack.last()

    val state by graph.destinationsViewModel.state.collectAsState()
    val operationState by graph.operationsViewModel.state.collectAsState()
    val contextState by graph.contextsViewModel.state.collectAsState()
    val globalSearchState by graph.globalSearchViewModel.state.collectAsState()

    var importBatchState by remember { mutableStateOf<ImportBatchUiState>(ImportBatchUiState.Idle) }
    val importBatchReducer = remember { ImportBatchReducer() }
    // T7 — S12: efectos por elemento (copias deliberadas, payloads, existentes).
    val importBatchHost = remember(
        graph.destinationRepository,
        graph.comprobanteRepository,
        graph.importPayloadStore,
    ) {
        ImportBatchItemHost(
            destinations = graph.destinationRepository,
            comprobantes = graph.comprobanteRepository,
            comprobanteFiles = graph.comprobanteFiles,
            payloads = graph.importPayloadStore,
        )
    }
    var existingPreview by remember { mutableStateOf<ExistingImportPreview?>(null) }
    /** Lote actual (el que la UI está viendo), si hay alguno. */
    fun currentBatch() = when (val current = importBatchState) {
        is ImportBatchUiState.Result -> current.batch
        is ImportBatchUiState.Review -> current.batch
        is ImportBatchUiState.Saving -> current.batch
        is ImportBatchUiState.Error -> current.batch
        is ImportBatchUiState.Saved -> current.batch
        else -> null
    }
    fun currentCandidate(id: String): com.agendaqr.destinations.domain.ImportCandidate? =
        currentBatch()?.candidates?.firstOrNull { it.id == id }

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
        importBatchState = importBatchReducer.reduce(importBatchState, ImportBatchAction.Analyzed(batch))
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
                AppRoute.Home -> graph.destinationsViewModel.onAction(DestinationAction.Back)
                AppRoute.Operations -> graph.operationsViewModel.onAction(OperationAction.Back)
                AppRoute.Contexts -> graph.contextsViewModel.onAction(ContextAction.Back)
                AppRoute.ImportBatch ->
                    importBatchState = importBatchReducer.reduce(importBatchState, ImportBatchAction.Back)
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

    when (top) {
        AppRoute.ImportBatch -> ImportBatchScreen(
            state = importBatchState,
            existing = existingPreview,
            onAction = { action ->
                when (action) {
                    ImportBatchAction.Back -> {
                        if (importBatchState is ImportBatchUiState.Review) {
                            importBatchState = importBatchReducer.reduce(importBatchState, action)
                        } else {
                            val batch = when (val current = importBatchState) {
                                is ImportBatchUiState.Result -> current.batch
                                is ImportBatchUiState.Error -> current.batch
                                is ImportBatchUiState.Saved -> current.batch
                                else -> null
                            }
                            batch?.let { pending ->
                                graph.sessionScope.launch {
                                    pending.candidates.mapNotNull { it.payloadRef }
                                        .distinct()
                                        .forEach { graph.importPayloadStore.delete(it) }
                                }
                            }
                            importBatchState = importBatchReducer.reduce(importBatchState, action)
                            nav.pop()
                        }
                    }
                    ImportBatchAction.SaveRecognized -> {
                        val batch = (importBatchState as? ImportBatchUiState.Result)?.batch
                            ?: (importBatchState as? ImportBatchUiState.Review)?.batch
                        if (batch == null) {
                            importBatchState = importBatchReducer.reduce(
                                importBatchState,
                                ImportBatchAction.Failed("No hay lote para guardar"),
                            )
                        } else {
                            importBatchState = importBatchReducer.reduce(importBatchState, action)
                            graph.sessionScope.launch {
                                runCatching { graph.saveImportBatch(batch) }
                                    .onSuccess {
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            ImportBatchAction.Saved,
                                        )
                                    }
                                    .onFailure { error ->
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            // T5: mapeo centralizado; nunca
                                            // `error.message` crudo.
                                            ImportBatchAction.Failed(
                                                userFacingError(error, ErrorFlow.ImportBatchSave).display(),
                                            ),
                                        )
                                    }
                            }
                        }
                    }
                    // ------------------------------------------------------
                    // T7 — resolución de pendientes por elemento.
                    // ------------------------------------------------------
                    is ImportBatchAction.RetryCandidate -> {
                        val candidate = currentCandidate(action.candidateId)
                        val payloadRef = candidate?.payloadRef
                        if (candidate != null && payloadRef != null) {
                            graph.sessionScope.launch {
                                val bytes = runCatching { graph.importPayloadStore.read(payloadRef) }.getOrNull()
                                val reclassified = if (bytes != null) {
                                    reclassifyImportCandidate(candidate, bytes)
                                } else {
                                    candidate
                                }
                                importBatchState = importBatchReducer.reduce(
                                    importBatchState,
                                    ImportBatchAction.CandidateReclassified(reclassified),
                                )
                            }
                        }
                    }
                    is ImportBatchAction.DiscardCandidate -> {
                        val candidate = currentCandidate(action.candidateId)
                        // El payload temporal se elimina al descartar.
                        graph.sessionScope.launch { candidate?.let { importBatchHost.discardPayload(it) } }
                        importBatchState = importBatchReducer.reduce(
                            importBatchState,
                            ImportBatchAction.CandidateSaved(action.candidateId),
                        )
                    }
                    is ImportBatchAction.SaveDuplicateAnyway -> {
                        val candidate = currentCandidate(action.candidateId)
                        if (candidate != null) {
                            graph.sessionScope.launch {
                                runCatching { importBatchHost.saveDuplicateAnyway(candidate) }
                                    .onSuccess {
                                        // Sin doble guardado: el elemento sale del lote.
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            ImportBatchAction.CandidateSaved(action.candidateId),
                                        )
                                    }
                                    .onFailure {
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            ImportBatchAction.Failed("No pudimos guardar el elemento. Inténtalo de nuevo."),
                                        )
                                    }
                            }
                        }
                    }
                    is ImportBatchAction.ViewExisting -> {
                        val candidate = currentCandidate(action.candidateId)
                        val batch = currentBatch()
                        if (candidate != null && batch != null) {
                            graph.sessionScope.launch {
                                existingPreview = importBatchHost.resolveExisting(candidate, batch)
                            }
                        }
                    }
                    ImportBatchAction.CloseExisting -> existingPreview = null
                    else -> importBatchState = importBatchReducer.reduce(importBatchState, action)
                }
            },
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
                    }
                    AgendaSearchResultType.ACTIVITY -> {
                        result.operationId?.let {
                            graph.operationsViewModel.onAction(OperationAction.Open(it))
                            nav.pop()
                            nav.push(AppRoute.Operations)
                        }
                    }
                    AgendaSearchResultType.COMPROBANTE -> {
                        graph.operationsViewModel.onAction(OperationAction.OpenComprobante(result.id))
                        nav.pop()
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
            graph = graph,
            onSignOut = onSignOut,
            nav = nav,
            syncLookup = syncLookup,
            onRetrySync = retrySync,
        )
    }
}

/**
 * S01 — Inicio y el flujo de destinos completo (S02/S09). Las rutas internas
 * de destinos siguen viviendo en [DestinationsUiState.route] con su
 * transición turnstile, que respeta el movimiento reducido del sistema.
 */
@Composable
private fun HomeSurface(
    state: DestinationsUiState,
    contextState: ContextsUiState,
    graph: AuthenticatedSessionGraph,
    onSignOut: () -> Unit,
    nav: AppBackStack,
    syncLookup: ElementSyncLookup,
    onRetrySync: () -> Unit,
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
                onOpenSearch = { nav.push(AppRoute.Search) },
                onSignOut = onSignOut,
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
            is DestinationRoute.Edit -> DestinationEditorScreen(
                existing = route.id?.let(graph.destinationsViewModel::destination),
                onSave = { destination ->
                    graph.destinationsViewModel.onAction(
                        if (route.id == null) {
                            DestinationAction.Save(destination)
                        } else {
                            DestinationAction.Update(destination)
                        },
                    )
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
            )
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
                        onBack = { graph.destinationsViewModel.onAction(DestinationAction.Back) },
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
