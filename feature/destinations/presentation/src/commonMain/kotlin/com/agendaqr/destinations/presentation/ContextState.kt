package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContextRoute {
    data object List : ContextRoute
    data class Detail(val id: String) : ContextRoute
}

data class ContextsUiState(
    val contexts: List<Context> = emptyList(),
    val contents: ContextContents? = null,
    val route: ContextRoute = ContextRoute.List,
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface ContextAction {
    data class Open(val id: String) : ContextAction
    data object Back : ContextAction
    data object ClearError : ContextAction
}

class ContextsViewModel(
    observe: ObserveContextsUseCase,
    private val observeContents: ObserveContextContentsUseCase,
    private val get: GetContextUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(ContextsUiState())
    val state: StateFlow<ContextsUiState> = _state.asStateFlow()
    private var selectedId: String? = null

    init {
        scope.launch {
            observe().collect { contexts ->
                _state.value = _state.value.copy(contexts = contexts, isLoading = false)
            }
        }
    }

    fun onAction(action: ContextAction) {
        when (action) {
            is ContextAction.Open -> open(action.id)
            ContextAction.Back -> {
                selectedId = null
                _state.value = _state.value.copy(route = ContextRoute.List, contents = null, error = null)
            }
            ContextAction.ClearError -> _state.value = _state.value.copy(error = null)
        }
    }

    private fun open(id: String) {
        selectedId = id
        scope.launch {
            if (get(id) == null) {
                _state.value = _state.value.copy(error = "Context not found: $id")
                return@launch
            }
            _state.value = _state.value.copy(route = ContextRoute.Detail(id), error = null)
            observeContents(id).collect { contents ->
                _state.value = _state.value.copy(contents = contents)
            }
        }
    }
}
