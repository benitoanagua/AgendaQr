package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.MarkDestinationUsedUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DestinationRoute {
    data object List : DestinationRoute
    data class Detail(val id: String) : DestinationRoute
    data class Edit(val id: String?) : DestinationRoute
    data class FullscreenQr(val id: String) : DestinationRoute
    data object ImportReview : DestinationRoute
}

data class DestinationsUiState(
    val destinations: List<Destination> = emptyList(),
    val query: String = "",
    val favoriteOnly: Boolean = false,
    val recentOnly: Boolean = false,
    val category: String? = null,
    val route: DestinationRoute = DestinationRoute.List,
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val visibleDestinations: List<Destination>
        get() = destinations
            .asSequence()
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) || it.category.orEmpty().contains(query, ignoreCase = true) }
            .filter { !favoriteOnly || it.favorite }
            .filter { !recentOnly || it.lastUsedAt != null }
            .filter { category == null || it.category == category }
            .sortedWith(compareByDescending<Destination> { it.lastUsedAt ?: Long.MIN_VALUE }.thenBy { it.name.lowercase() })
            .toList()
}

sealed interface DestinationAction {
    data class Search(val value: String) : DestinationAction
    data object ToggleFavorites : DestinationAction
    data object ToggleRecent : DestinationAction
    data class SelectCategory(val value: String?) : DestinationAction
    data class Open(val id: String) : DestinationAction
    data class Edit(val id: String?) : DestinationAction
    data class ShowQr(val id: String) : DestinationAction
    data class Save(val destination: Destination) : DestinationAction
    data class ImportAssets(val assets: List<com.agendaqr.destinations.domain.QrAsset>) : DestinationAction
    data class Update(val destination: Destination) : DestinationAction
    data class Delete(val id: String) : DestinationAction
    data class ToggleFavorite(val destination: Destination) : DestinationAction
    data object Back : DestinationAction
    data object ClearError : DestinationAction
}

class DestinationsViewModel(
    observe: ObserveDestinationsUseCase,
    private val get: GetDestinationUseCase,
    private val save: SaveDestinationUseCase,
    private val update: UpdateDestinationUseCase,
    private val delete: DeleteDestinationUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val markUsed: MarkDestinationUsedUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(DestinationsUiState())
    val state: StateFlow<DestinationsUiState> = _state.asStateFlow()
    private var importedAssets: List<com.agendaqr.destinations.domain.QrAsset> = emptyList()

    private var observation: Job = scope.launch {
        observe().collect { destinations ->
            _state.update { it.copy(destinations = destinations, isLoading = false) }
        }
    }

    fun onAction(action: DestinationAction) {
        when (action) {
            is DestinationAction.Search -> _state.update { it.copy(query = action.value) }
            DestinationAction.ToggleFavorites -> _state.update { it.copy(favoriteOnly = !it.favoriteOnly) }
            DestinationAction.ToggleRecent -> _state.update { it.copy(recentOnly = !it.recentOnly) }
            is DestinationAction.SelectCategory -> _state.update { it.copy(category = action.value) }
            is DestinationAction.Open -> scope.launch {
                get(action.id)?.let { _state.update { state -> state.copy(route = DestinationRoute.Detail(action.id)) } }
            }
            is DestinationAction.Edit -> _state.update { it.copy(route = DestinationRoute.Edit(action.id)) }
            is DestinationAction.ShowQr -> scope.launch {
                get(action.id)?.let { destination ->
                    runCatching { markUsed(destination) }.onFailure(::showError)
                    _state.update { state -> state.copy(route = DestinationRoute.FullscreenQr(action.id)) }
                }
            }
            is DestinationAction.Save -> scope.launch { runCatching { save(action.destination) }.onFailure(::showError).onSuccess { back() } }
            is DestinationAction.ImportAssets -> _state.update { it.copy(route = DestinationRoute.ImportReview, error = null) }.also { importedAssets = action.assets }
            is DestinationAction.Update -> scope.launch { runCatching { update(action.destination) }.onFailure(::showError).onSuccess { back() } }
            is DestinationAction.Delete -> scope.launch { runCatching { delete(action.id) }.onFailure(::showError).onSuccess { back() } }
            is DestinationAction.ToggleFavorite -> scope.launch { runCatching { toggleFavorite(action.destination) }.onFailure(::showError) }
            DestinationAction.Back -> back()
            DestinationAction.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    fun destination(id: String): Destination? = state.value.destinations.firstOrNull { it.id == id }

    fun importedAssets(): List<com.agendaqr.destinations.domain.QrAsset> = importedAssets

    fun saveImportedAssets() {
        val assets = importedAssets
        if (assets.isEmpty()) { back(); return }
        scope.launch {
            runCatching {
                assets.forEach { asset ->
                    val now = com.agendaqr.destinations.domain.nowMillis()
                    save(com.agendaqr.destinations.domain.Destination(
                        id = "destination-$now-${asset.hashCode()}",
                        name = "",
                        qr = asset,
                        createdAt = now,
                        updatedAt = now,
                    ))
                }
            }.onFailure(::showError).onSuccess { importedAssets = emptyList(); back() }
        }
    }

    private fun back() { _state.update { it.copy(route = DestinationRoute.List, error = null) } }
    private fun showError(error: Throwable) { _state.update { it.copy(error = error.message ?: "Operation failed") } }
}
