package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.agendaqr.destinations.data.SyncQueueObserver
import com.agendaqr.destinations.data.createComprobanteFileStore
import com.agendaqr.destinations.data.createImportPayloadStore
import com.agendaqr.destinations.data.createDeletedOperationHistoryRepository
import com.agendaqr.destinations.data.createAuthRepository
import com.agendaqr.destinations.data.LocalSyncQueue
import com.agendaqr.destinations.data.SyncMutationEnqueuer
import com.agendaqr.destinations.data.SyncMutationProcessor
import com.agendaqr.destinations.data.SyncRecoveryCoordinator
import com.agendaqr.destinations.data.createRemoteContextRepository
import com.agendaqr.destinations.data.createRemoteDestinationRepository
import com.agendaqr.destinations.data.createRemoteOperationRepository
import com.agendaqr.destinations.data.createRemoteComprobanteRepository
import com.agendaqr.destinations.data.createSyncedContextRepository
import com.agendaqr.destinations.data.createSyncedComprobanteRepository
import com.agendaqr.destinations.data.createSyncedDestinationRepository
import com.agendaqr.destinations.data.createSyncedOperationRepository
import com.agendaqr.core.ui.components.XauxaStatusBanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.agendaqr.destinations.domain.*
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.theme.AgendaQrTheme

@Composable
fun AgendaQrApp() {
    val authRepository = remember { createAuthRepository() }
    val authViewModel = remember(authRepository) {
        AuthViewModel(
            observe = ObserveAuthStateUseCase(authRepository),
            signIn = SignInUseCase(authRepository),
            signUp = SignUpUseCase(authRepository),
            signOut = SignOutUseCase(authRepository),
        )
    }
    val authState by authViewModel.state.collectAsState()

    AgendaQrTheme {
        when (authState.authState) {
            AuthState.Loading -> XauxaLoading()
            AuthState.SignedOut -> AuthScreen(
                state = authState,
                onEmailChanged = authViewModel::setEmail,
                onPasswordChanged = authViewModel::setPassword,
                onSignIn = authViewModel::submitSignIn,
                onSignUp = authViewModel::submitSignUp,
            )
            is AuthState.SignedIn -> AgendaQrAuthenticatedApp(onSignOut = authViewModel::signOut)
        }
    }
}

@Composable
private fun AgendaQrAuthenticatedApp(onSignOut: () -> Unit) {
    val syncScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    val syncQueue = remember { LocalSyncQueue() }
    val syncEnqueuer = remember(syncQueue) { SyncMutationEnqueuer(syncQueue) }
    val contextRepository = remember(syncQueue) { createSyncedContextRepository(syncQueue) }
    val destinationRepository = remember(syncEnqueuer) { createSyncedDestinationRepository(syncEnqueuer) }
    val operationRepository = remember(syncEnqueuer) { createSyncedOperationRepository(syncEnqueuer) }
    val fileStore = remember { createComprobanteFileStore() }
    val importPayloadStore = remember { createImportPayloadStore() }
    val comprobanteRepository = remember(syncEnqueuer, fileStore) { createSyncedComprobanteRepository(fileStore, syncEnqueuer) }
    val syncProcessor = remember(syncQueue, contextRepository, destinationRepository, operationRepository, comprobanteRepository, fileStore) {
        SyncMutationProcessor(
            queue = syncQueue,
            contexts = contextRepository,
            destinations = destinationRepository,
            operations = operationRepository,
            comprobantes = comprobanteRepository,
            remoteContexts = createRemoteContextRepository(),
            remoteDestinations = createRemoteDestinationRepository(),
            remoteOperations = createRemoteOperationRepository(),
            remoteComprobantes = createRemoteComprobanteRepository(),
            fileStore = fileStore,
        )
    }
    val recovery = remember(syncProcessor, syncScope) {
        SyncRecoveryCoordinator(syncProcessor, syncScope)
    }
    DisposableEffect(recovery) {
        recovery.start()
        onDispose {
            recovery.stop()
            syncScope.coroutineContext.cancel()
        }
    }
    val queueObserver = remember(syncQueue) { SyncQueueObserver(syncQueue) }
    var pendingCount by remember { mutableStateOf(0) }
    var isOffline by remember { mutableStateOf(false) }
    var hasFailed by remember { mutableStateOf(false) }
    LaunchedEffect(queueObserver) {
        queueObserver.observeQueue().collect { items ->
            pendingCount = items.count { it.state != com.agendaqr.destinations.data.SyncMutationState.FAILED } + items.count { it.state == com.agendaqr.destinations.data.SyncMutationState.FAILED }
            hasFailed = items.any { it.state == com.agendaqr.destinations.data.SyncMutationState.FAILED }
            // failed count kept separate for banner
            pendingCount = items.size
        }
    }
    LaunchedEffect(queueObserver) {
        queueObserver.observeNetwork().collect { online ->
            isOffline = !online
        }
    }
    var showOperations by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var showContexts by remember { mutableStateOf(false) }
    var showImportBatch by remember { mutableStateOf(false) }
    var importBatchState by remember { mutableStateOf<ImportBatchUiState>(ImportBatchUiState.Idle) }
    val importBatchReducer = remember { ImportBatchReducer() }
    val saveImportBatch = remember(destinationRepository, comprobanteRepository, fileStore, importPayloadStore) {
        SaveImportBatchUseCase(destinationRepository, comprobanteRepository, fileStore, importPayloadStore)
    }
    val repository = destinationRepository
    val viewModel = remember(repository) { DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repository),
            get = GetDestinationUseCase(repository),
            save = SaveDestinationUseCase(repository),
            update = UpdateDestinationUseCase(repository),
            delete = DeleteDestinationUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
        ) }
    val state by viewModel.state.collectAsState()
    val historyRepository = remember { createDeletedOperationHistoryRepository() }
    val operationsViewModel = remember(operationRepository, comprobanteRepository, fileStore, historyRepository) {
        OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operationRepository),
            observeContexts = ObserveContextsUseCase(contextRepository),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(comprobanteRepository),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(comprobanteRepository),
            getOperation = GetOperationUseCase(operationRepository),
            saveOperation = SaveOperationUseCase(operationRepository),
            deleteOperation = DeleteOperationWithHistoryUseCase(operationRepository, comprobanteRepository, fileStore, historyRepository),
            associate = AssociateComprobanteToOperationUseCase(operationRepository, comprobanteRepository),
            saveComprobante = SaveComprobanteUseCase(comprobanteRepository, fileStore),
            findDuplicates = FindDuplicateComprobantesUseCase(comprobanteRepository, fileStore),
        )
    }
    val operationState by operationsViewModel.state.collectAsState()
    val contextViewModel = remember(contextRepository, destinationRepository, operationRepository, comprobanteRepository) {
        ContextsViewModel(
            observe = ObserveContextsUseCase(contextRepository),
            observeContents = ObserveContextContentsUseCase(
                contextRepository,
                destinationRepository,
                operationRepository,
                comprobanteRepository,
            ),
            get = GetContextUseCase(contextRepository),
        )
    }
    val contextState by contextViewModel.state.collectAsState()
    val globalSearchViewModel = remember(contextRepository, destinationRepository, operationRepository, comprobanteRepository) {
        GlobalSearchViewModel(
            search = SearchAgendaQrUseCase(
                contextRepository,
                destinationRepository,
                operationRepository,
                comprobanteRepository,
            ),
        )
    }
    val globalSearchState by globalSearchViewModel.state.collectAsState()

    ImportBatchControls { batch ->
        importBatchState = importBatchReducer.reduce(importBatchState, ImportBatchAction.Analyzed(batch))
        showImportBatch = true
    }

    androidx.compose.foundation.layout.Column {
        if (isOffline) {
            XauxaStatusBanner("Sin conexión — los cambios se guardan localmente y se sincronizarán al recuperar conectividad.", danger = false)
        }
        if (pendingCount > 0) {
            XauxaStatusBanner(
                if (hasFailed) "Sincronización pendiente: $pendingCount · reintentando…"
                else "Sincronización pendiente: $pendingCount",
                danger = hasFailed,
            )
            if (hasFailed) {
                androidx.compose.material3.TextButton(onClick = { syncScope.launch { syncProcessor.drain() } }) {
                    androidx.compose.material3.Text("Reintentar ahora")
                }
            }
        }
    }
    when {
        showImportBatch -> ImportBatchScreen(
            state = importBatchState,
            onAction = { action ->
                when (action) {
                    ImportBatchAction.Back -> {
                        val batch = when (val current = importBatchState) {
                            is ImportBatchUiState.Result -> current.batch
                            is ImportBatchUiState.Review -> current.batch
                            is ImportBatchUiState.Error -> current.batch
                            else -> null
                        }
                        batch?.let { pending ->
                            syncScope.launch {
                                pending.candidates.mapNotNull { it.payloadRef }
                                    .distinct()
                                    .forEach { importPayloadStore.delete(it) }
                            }
                        }
                        importBatchState = importBatchReducer.reduce(importBatchState, action)
                        showImportBatch = false
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
                            syncScope.launch {
                                runCatching { saveImportBatch(batch) }
                                    .onSuccess {
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            ImportBatchAction.Saved,
                                        )
                                    }
                                    .onFailure { error ->
                                        importBatchState = importBatchReducer.reduce(
                                            importBatchState,
                                            ImportBatchAction.Failed(
                                                error.message ?: "No se pudo guardar la importación",
                                            ),
                                        )
                                    }
                            }
                        }
                    }
                    else -> importBatchState = importBatchReducer.reduce(importBatchState, action)
                }
            },
        )
        showSearch -> GlobalSearchScreen(
            state = globalSearchState,
            onAction = globalSearchViewModel::onAction,
            onSelect = { result ->
                showSearch = false
                when (result.type) {
                    AgendaSearchResultType.CONTEXT -> result.contextId?.let { contextViewModel.onAction(ContextAction.Open(it)); showContexts = true }
                    AgendaSearchResultType.QR -> result.destinationId?.let { viewModel.onAction(DestinationAction.Open(it)) }
                    AgendaSearchResultType.ACTIVITY -> result.operationId?.let { operationsViewModel.onAction(OperationAction.Open(it)); showOperations = true }
                    AgendaSearchResultType.COMPROBANTE -> if (result.operationId != null) { operationsViewModel.onAction(OperationAction.Open(result.operationId)); showOperations = true } else { operationsViewModel.onAction(OperationAction.OpenUnassociated); showOperations = true }
                }
            },
            onBack = { showSearch = false },
        )
        showContexts -> ContextsScreen(contextState, contextViewModel::onAction)
        showOperations -> OperationsScreen(operationState, operationsViewModel, onBack = { showOperations = false })
        else -> when (val route = state.route) {
            DestinationRoute.List -> DestinationsScreen(
                state,
                viewModel::onAction,
                onOpenOperations = { showOperations = true },
                onOpenSearch = { showSearch = true },
                onOpenContexts = { showContexts = true },
                onSignOut = onSignOut,
            )
            is DestinationRoute.Edit -> DestinationEditorScreen(existing = route.id?.let(viewModel::destination), onSave = { destination -> viewModel.onAction(if (route.id == null) DestinationAction.Save(destination) else DestinationAction.Update(destination)) }, onImportMany = { assets -> viewModel.onAction(DestinationAction.ImportAssets(assets)) }, contexts = contextState.contexts, onBack = { viewModel.onAction(DestinationAction.Back) })
            is DestinationRoute.Detail -> route.id.let(viewModel::destination)?.let { destination -> DestinationDetailScreen(destination = destination, onShowQr = { viewModel.onAction(DestinationAction.ShowQr(destination.id)) }, onEdit = { viewModel.onAction(DestinationAction.Edit(destination.id)) }, onDelete = { viewModel.onAction(DestinationAction.Delete(destination.id)) }, onShare = { shareQr(destination.qr) }, onBack = { viewModel.onAction(DestinationAction.Back) }) }
            is DestinationRoute.FullscreenQr -> route.id.let(viewModel::destination)?.let { destination -> QrFullscreenPattern(destination.qr.encoded) { viewModel.onAction(DestinationAction.Back) } }
            DestinationRoute.ImportReview -> ImportReviewScreen(assets = viewModel.importedAssets(), onSaveAll = viewModel::saveImportedAssets, onBack = { viewModel.onAction(DestinationAction.Back) })
        }
    }
}
