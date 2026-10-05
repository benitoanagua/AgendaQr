package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    val error: UserFacingError? = null,
    /** U3/ADR-0003: id del contexto recién creado (para seleccionarlo). */
    val justCreatedContextId: String? = null,
)

sealed interface ContextAction {
    data class Open(val id: String) : ContextAction
    data object Back : ContextAction
    data object ClearError : ContextAction
    /** U3/ADR-0003 (opción C): crear contexto (Nombre obligatorio). */
    data class Create(val name: String, val note: String?) : ContextAction
    data object ClearJustCreated : ContextAction
    /** Re-ejecuta la creación fallida (el banner REINTENTAR reintenta). */
    data object RetryFailed : ContextAction
}

class ContextsViewModel(
    observe: ObserveContextsUseCase,
    private val observeContents: ObserveContextContentsUseCase,
    private val get: GetContextUseCase,
    /**
     * U3/ADR-0003 (opción C): persistencia de contextos nuevos. Opcional
     * para los tests que no ejercen creación.
     */
    private val save: SaveContextUseCase? = null,
    /**
     * Scope de la sesión (T12): lo aporta el grafo de la app; se cancela
     * al cerrar sesión, junto con todos los colecciones del ViewModel.
     */
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ContextsUiState())
    val state: StateFlow<ContextsUiState> = _state.asStateFlow()
    private var selectedId: String? = null
    private var contentsJob: Job? = null

    init {
        scope.launch {
            observe().collect { contexts ->
                _state.value = _state.value.copy(contexts = contexts, isLoading = false)
            }
        }
    }

    /** Última creación fallida, para re-ejecutarla con RetryFailed. */
    private var retryBlock: (() -> Unit)? = null

    fun onAction(action: ContextAction) {
        if (action != ContextAction.RetryFailed) retryBlock = null
        when (action) {
            is ContextAction.Create -> create(action)
            ContextAction.ClearJustCreated ->
                _state.value = _state.value.copy(justCreatedContextId = null)
            ContextAction.RetryFailed -> retryBlock?.invoke()
            is ContextAction.Open -> open(action.id)
            ContextAction.Back -> {
                selectedId = null
                contentsJob?.cancel()
                contentsJob = null
                _state.value = _state.value.copy(
                    route = ContextRoute.List,
                    contents = null,
                    error = null,
                )
            }
            ContextAction.ClearError -> _state.value = _state.value.copy(error = null)
        }
    }

    /**
     * U3/ADR-0003 (opción C): crear contexto con el formulario mínimo
     * (Nombre obligatorio + Nota). Persiste vía SaveContextUseCase (la
     * cola de sync encola el UPSERT) y expone el id para que la superficie
     * que pidió la creación lo seleccione. El draft del flujo padre nunca
     * se toca: solo cambia la selección de contexto.
     */
    private fun create(action: ContextAction.Create) {
        val name = action.name.trim()
        if (name.isBlank()) {
            _state.value = _state.value.copy(
                error = userFacingError(IllegalArgumentException("nombre vacío"), ErrorFlow.ContextOpen),
            )
            return
        }
        val useCase = save ?: run {
            _state.value = _state.value.copy(
                error = userFacingError(IllegalStateException("sin repositorio de guardado"), ErrorFlow.ContextSave),
            )
            return
        }
        val now = nowMillis()
        val context = Context(
            id = newEntityId("context"),
            name = name,
            note = action.note?.trim()?.takeIf { it.isNotBlank() },
            createdAt = now,
            updatedAt = now,
        )
        scope.launch {
            retryBlock = { onAction(action) }
            runCatching { useCase(context) }
                .onSuccess {
                    retryBlock = null
                    _state.value = _state.value.copy(
                        justCreatedContextId = context.id,
                        error = null,
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = userFacingError(error, ErrorFlow.ContextSave),
                    )
                }
        }
    }

    private fun open(id: String) {
        selectedId = id
        contentsJob?.cancel()
        contentsJob = scope.launch {
            if (get(id) == null) {
                // T5: error mostrable con qué/data/acción (spec §10).
                _state.value = _state.value.copy(error = userFacingError(IllegalStateException(), ErrorFlow.ContextOpen))
                return@launch
            }
            _state.value = _state.value.copy(
                route = ContextRoute.Detail(id),
                contents = null,
                error = null,
            )
            observeContents(id).collect { contents ->
                if (selectedId == id) {
                    _state.value = _state.value.copy(contents = contents)
                }
            }
        }
    }
}
