package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.agendaqr.destinations.data.createComprobanteFileStore
import com.agendaqr.destinations.data.createDeletedOperationHistoryRepository
import com.agendaqr.destinations.data.createAuthRepository
import com.agendaqr.destinations.data.LocalSyncQueue
import com.agendaqr.destinations.data.SyncMutationEnqueuer
import com.agendaqr.destinations.data.SyncMutationProcessor
import com.agendaqr.destinations.data.SyncRecoveryCoordinator
import com.agendaqr.destinations.data.createRemoteDestinationRepository
import com.agendaqr.destinations.data.createRemoteOperationRepository
import com.agendaqr.destinations.data.createRemoteComprobanteRepository
import com.agendaqr.destinations.data.createSyncedComprobanteRepository
import com.agendaqr.destinations.data.createSyncedDestinationRepository
import com.agendaqr.destinations.data.createSyncedOperationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
    val destinationRepository = remember(syncEnqueuer) { createSyncedDestinationRepository(syncEnqueuer) }
    val operationRepository = remember(syncEnqueuer) { createSyncedOperationRepository(syncEnqueuer) }
    val fileStore = remember { createComprobanteFileStore() }
    val comprobanteRepository = remember(syncEnqueuer, fileStore) { createSyncedComprobanteRepository(fileStore, syncEnqueuer) }
    val syncProcessor = remember(syncQueue, destinationRepository, operationRepository, comprobanteRepository, fileStore) {
        SyncMutationProcessor(
            queue = syncQueue,
            destinations = destinationRepository,
            operations = operationRepository,
            comprobantes = comprobanteRepository,
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
    var showOperations by remember { mutableStateOf(false) }
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
    if (showOperations) {
        OperationsScreen(operationState, operationsViewModel, onBack = { showOperations = false })
    } else when (val route = state.route) {
        DestinationRoute.List -> DestinationsScreen(state, viewModel::onAction, onOpenOperations = { showOperations = true }, onSignOut = onSignOut)
        is DestinationRoute.Edit -> DestinationEditorScreen(existing = route.id?.let(viewModel::destination), onSave = { destination -> viewModel.onAction(if (route.id == null) DestinationAction.Save(destination) else DestinationAction.Update(destination)) }, onImportMany = { assets -> viewModel.onAction(DestinationAction.ImportAssets(assets)) }, onBack = { viewModel.onAction(DestinationAction.Back) })
        is DestinationRoute.Detail -> route.id.let(viewModel::destination)?.let { destination -> DestinationDetailScreen(destination = destination, onShowQr = { viewModel.onAction(DestinationAction.ShowQr(destination.id)) }, onEdit = { viewModel.onAction(DestinationAction.Edit(destination.id)) }, onDelete = { viewModel.onAction(DestinationAction.Delete(destination.id)) }, onShare = { shareQr(destination.qr) }, onBack = { viewModel.onAction(DestinationAction.Back) }) }
        is DestinationRoute.FullscreenQr -> route.id.let(viewModel::destination)?.let { destination -> QrFullscreenPattern(destination.qr.encoded) { viewModel.onAction(DestinationAction.Back) } }
        DestinationRoute.ImportReview -> ImportReviewScreen(assets = viewModel.importedAssets(), onSaveAll = viewModel::saveImportedAssets, onBack = { viewModel.onAction(DestinationAction.Back) })
    }
}
