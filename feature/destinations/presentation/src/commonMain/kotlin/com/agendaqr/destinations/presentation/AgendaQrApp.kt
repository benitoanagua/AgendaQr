package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.agendaqr.destinations.data.createDestinationRepository
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.MarkDestinationUsedUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import com.agendaqr.core.ui.theme.AgendaQrTheme

@Composable
fun AgendaQrApp() {
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

    AgendaQrTheme {
        when (val route = state.route) {
            DestinationRoute.List -> DestinationsScreen(state, viewModel::onAction)
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
