package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AgendaSearchQuery
import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.SearchAgendaQrUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GlobalSearchUiState(
    val query: String = "",
    val results: List<AgendaSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
)

sealed interface GlobalSearchAction {
    data class QueryChanged(val value: String) : GlobalSearchAction
    data object Clear : GlobalSearchAction
}

class GlobalSearchViewModel(
    private val search: SearchAgendaQrUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(GlobalSearchUiState())
    val state: StateFlow<GlobalSearchUiState> = _state.asStateFlow()

    fun onAction(action: GlobalSearchAction) {
        when (action) {
            is GlobalSearchAction.QueryChanged -> {
                _state.update { it.copy(query = action.value, isSearching = action.value.isNotBlank(), error = null) }
                scope.launch {
                    runCatching { search(AgendaSearchQuery(action.value)) }
                        .onSuccess { flow ->
                            flow.collect { results ->
                                _state.update { it.copy(results = results, isSearching = false) }
                            }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(isSearching = false, error = error.message ?: "No se pudo buscar") }
                        }
                }
            }
            GlobalSearchAction.Clear -> _state.value = GlobalSearchUiState()
        }
    }
}
