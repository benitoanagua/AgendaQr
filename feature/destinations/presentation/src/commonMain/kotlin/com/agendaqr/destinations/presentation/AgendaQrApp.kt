package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.agendaqr.destinations.data.createDestinationRepository
import com.agendaqr.destinations.data.createOperationRepository
import com.agendaqr.destinations.data.createComprobanteRepository
import com.agendaqr.destinations.data.createComprobanteFileStore
import com.agendaqr.destinations.data.createDeletedOperationHistoryRepository
import com.agendaqr.destinations.domain.*
import com.agendaqr.core.ui.theme.AgendaQrTheme

@Composable
fun AgendaQrApp() {
    var showOperations by remember { mutableStateOf(false) }
    val repository = remember { createDestinationRepository() }
    val viewModel = remember(repository) {
        DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repository),
            get = GetDestinationUseCase(repository),
            save = SaveDestinationUseCase(repository),
            update = UpdateDestinationUseCase(repository),
            delete = DeleteDestinationUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            markUsed = MarkDestinationUsedUseCase(repository),
        )
    }
    val state by viewModel.state.collectAsState()
    val operationRepository = remember { createOperationRepository() }
    val comprobanteRepository = remember { createComprobanteRepository() }
    val fileStore = remember { createComprobanteFileStore() }
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

    AgendaQrTheme {
        if (showOperations) {
            OperationsScreen(operationState, operationsViewModel, onBack = { showOperations = false })
        } else when (val route = state.route) {
            DestinationRoute.List -> DestinationsScreen(state, viewModel::onAction, onOpenOperations = { showOperations = true })
            is DestinationRoute.Edit -> DestinationEditorScreen(
                existing = route.id?.let(viewModel::destination),
                onSave = { destination ->
                    viewModel.onAction(if (route.id == null) DestinationAction.Save(destination) else DestinationAction.Update(destination))
                },
                onImportMany = { assets -> viewModel.onAction(DestinationAction.ImportAssets(assets)) },
                onBack = { viewModel.onAction(DestinationAction.Back) },
            )
            is DestinationRoute.Detail -> route.id.let(viewModel::destination)?.let { destination ->
                DestinationDetailScreen(
                    destination = destination,
                    onShowQr = { viewModel.onAction(DestinationAction.ShowQr(destination.id)) },
                    onEdit = { viewModel.onAction(DestinationAction.Edit(destination.id)) },
                    onDelete = { viewModel.onAction(DestinationAction.Delete(destination.id)) },
                    onShare = { shareQr(destination.qr) },
                    onBack = { viewModel.onAction(DestinationAction.Back) },
                )
            }
            is DestinationRoute.FullscreenQr -> route.id.let(viewModel::destination)?.let { destination ->
                QrFullscreenPattern(destination.qr.encoded) { viewModel.onAction(DestinationAction.Back) }
            }
            DestinationRoute.ImportReview -> ImportReviewScreen(
                assets = viewModel.importedAssets(),
                onSaveAll = viewModel::saveImportedAssets,
                onBack = { viewModel.onAction(DestinationAction.Back) },
            )
        }
    }
}
