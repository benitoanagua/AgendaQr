package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AgendaSearchQuery
import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.SearchAgendaQrUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GlobalSearchUiState(
    val query: String = "",
    val results: List<AgendaSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: UserFacingError? = null,
)

sealed interface GlobalSearchAction {
    data class QueryChanged(val value: String) : GlobalSearchAction
    data object Clear : GlobalSearchAction
    data object ClearError : GlobalSearchAction
}

class GlobalSearchViewModel(
    private val search: SearchAgendaQrUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(GlobalSearchUiState())
    private var searchJob: Job? = null
    val state: StateFlow<GlobalSearchUiState> = _state.asStateFlow()

    fun onAction(action: GlobalSearchAction) {
        when (action) {
            is GlobalSearchAction.QueryChanged -> {
                searchJob?.cancel()
                _state.update {
                    it.copy(
                        query = action.value,
                        results = emptyList(),
                        isSearching = action.value.isNotBlank(),
                        error = null,
                    )
                }
                if (action.value.isBlank()) return
                searchJob = scope.launch {
                    try {
                        search(AgendaSearchQuery(action.value)).collect { results ->
                            _state.update { current ->
                                if (current.query == action.value) {
                                    current.copy(results = results, isSearching = false)
                                } else {
                                    current
                                }
                            }
                        }
                    } catch (error: Throwable) {
                        if (currentCoroutineContext().isActive) {
                            _state.update { current ->
                                if (current.query == action.value) {
                                    // T5: nunca `error.message` crudo; el
                                    // error ofrece REINTENTAR (spec §10).
                                    current.copy(
                                        isSearching = false,
                                        error = userFacingError(error, ErrorFlow.Search),
                                    )
                                } else {
                                    current
                                }
                            }
                        }
                    }
                }
            }
            GlobalSearchAction.Clear -> {
                searchJob?.cancel()
                _state.value = GlobalSearchUiState()
            }
            GlobalSearchAction.ClearError -> _state.update { it.copy(error = null) }
        }
    }
}
