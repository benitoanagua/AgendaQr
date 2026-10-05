package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DestinationRoute {
    data object List : DestinationRoute
    data object Add : DestinationRoute
    data class Detail(val id: String) : DestinationRoute
    data class Edit(val id: String?) : DestinationRoute
    data class FullscreenQr(val id: String) : DestinationRoute
    data object ImportReview : DestinationRoute
}

data class DestinationsUiState(
    val destinations: List<Destination> = emptyList(),
    val query: String = "",
    val favoriteOnly: Boolean = false,
    val category: String? = null,
    val route: DestinationRoute = DestinationRoute.List,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: UserFacingError? = null,
) {
    val visibleDestinations: List<Destination>
        get() = destinations
            .asSequence()
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) || it.category.orEmpty().contains(query, ignoreCase = true) }
            .filter { !favoriteOnly || it.favorite }
            .filter { category == null || it.category == category }
            .sortedWith(compareBy<Destination> { it.name.lowercase() })
            .toList()
}

sealed interface DestinationAction {
    data class Search(val value: String) : DestinationAction
    data object ToggleFavorites : DestinationAction
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
    /**
     * Re-ejecuta la última operación fallida (guardar, actualizar,
     * eliminar, favorito, guardado masivo): el banner REINTENTAR reintenta
     * de verdad en vez de solo cerrar (§10).
     */
    data object RetryFailed : DestinationAction
}

class DestinationsViewModel(
    observe: ObserveDestinationsUseCase,
    private val get: GetDestinationUseCase,
    private val save: SaveDestinationUseCase,
    private val update: UpdateDestinationUseCase,
    private val delete: DeleteDestinationUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    /**
     * Scope de la sesión (T12): lo aporta el grafo de la app; se cancela
     * al cerrar sesión, junto con todos los colecciones del ViewModel.
     */
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(DestinationsUiState())
    val state: StateFlow<DestinationsUiState> = _state.asStateFlow()
    private var importedAssets: List<com.agendaqr.destinations.domain.QrAsset> = emptyList()
    /** Última operación fallida, para re-ejecutarla con RetryFailed. */
    private var retryBlock: (() -> Unit)? = null

    private var observation: Job = scope.launch {
        observe().collect { destinations ->
            _state.update { it.copy(destinations = destinations, isLoading = false) }
        }
    }

    fun onAction(action: DestinationAction) {
        // Un reintento consume el bloque; cualquier otra acción lo invalida
        // (una intención nueva reemplaza a la fallida).
        if (action != DestinationAction.RetryFailed) retryBlock = null
        when (action) {
            is DestinationAction.Search -> _state.update { it.copy(query = action.value) }
            DestinationAction.ToggleFavorites -> _state.update { it.copy(favoriteOnly = !it.favoriteOnly) }
            is DestinationAction.SelectCategory -> _state.update { it.copy(category = action.value) }
            is DestinationAction.Open -> scope.launch {
                get(action.id)?.let { _state.update { state -> state.copy(route = DestinationRoute.Detail(action.id)) } }
            }
            is DestinationAction.Edit -> _state.update { it.copy(route = if (action.id == null) DestinationRoute.Add else DestinationRoute.Edit(action.id)) }
            is DestinationAction.ShowQr -> scope.launch {
                get(action.id)?.let { destination ->
                    _state.update { state -> state.copy(route = DestinationRoute.FullscreenQr(action.id)) }
                }
            }
            is DestinationAction.Save -> { if (!state.value.isSaving) scope.launch { _state.update { it.copy(isSaving = true, error = null) }; retryBlock = { onAction(action) }; runCatching { save(action.destination) }.onFailure { showError(it, ErrorFlow.SaveQr) }.onSuccess { retryBlock = null; back() }; _state.update { it.copy(isSaving = false) } } }
            is DestinationAction.ImportAssets -> { importedAssets = action.assets; _state.update { it.copy(route = DestinationRoute.ImportReview, error = null) } }
            is DestinationAction.Update -> { if (!state.value.isSaving) scope.launch { _state.update { it.copy(isSaving = true, error = null) }; retryBlock = { onAction(action) }; runCatching { update(action.destination) }.onFailure { showError(it, ErrorFlow.SaveQr) }.onSuccess { retryBlock = null; back() }; _state.update { it.copy(isSaving = false) } } }
            is DestinationAction.Delete -> scope.launch { retryBlock = { onAction(action) }; runCatching { delete(action.id) }.onFailure { showError(it, ErrorFlow.DeleteQr) }.onSuccess { retryBlock = null; back() } }
            is DestinationAction.ToggleFavorite -> scope.launch { retryBlock = { onAction(action) }; runCatching { toggleFavorite(action.destination) }.onFailure { showError(it, ErrorFlow.SaveQr) }.onSuccess { retryBlock = null } }
            DestinationAction.Back -> back()
            DestinationAction.ClearError -> _state.update { it.copy(error = null) }
            DestinationAction.RetryFailed -> retryBlock?.invoke()
        }
    }

    fun destination(id: String): Destination? = state.value.destinations.firstOrNull { it.id == id }

    fun importedAssets(): List<com.agendaqr.destinations.domain.QrAsset> = importedAssets

    fun saveImportedAssets() {
        val assets = importedAssets
        if (assets.isEmpty()) { back(); return }
        scope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            retryBlock = { saveImportedAssets() }
            runCatching {
                assets.forEachIndexed { index, asset ->
                    val now = com.agendaqr.destinations.domain.nowMillis()
                    save(com.agendaqr.destinations.domain.Destination(
                        id = "destination-$now-${asset.hashCode()}",
                        // La vía masiva no pasa por el editor (que exige nombre):
                        // valor por defecto editable luego, nunca vacío.
                        name = AppStrings.QrImportado + if (assets.size > 1) " ${index + 1}" else "",
                        qr = asset,
                        createdAt = now,
                        updatedAt = now,
                    ))
                }
            }.onFailure { showError(it, ErrorFlow.SaveQr) }.onSuccess { importedAssets = emptyList(); retryBlock = null; back() }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun back() {
        _state.update {
            it.copy(
                route = when (it.route) {
                    DestinationRoute.ImportReview -> DestinationRoute.Add
                    else -> DestinationRoute.List
                },
                error = null,
            )
        }
    }
    /**
     * Mapeo centralizado (T5): nunca se expone `error.message` crudo; el
     * estado lleva un error mostrable con qué/data/acción (spec §10).
     */
    private fun showError(error: Throwable, flow: ErrorFlow) {
        _state.update { it.copy(error = userFacingError(error, flow)) }
    }
}
