package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Reproducción U-01: los chips "Todos" y "Favoritos" disparan la misma
 * acción `ToggleFavorites`. Tocar "Todos" estando ya activo cambia a
 * Favoritos. Estos tests fallan con el comportamiento actual y pasan
 * cuando los chips envían `SetFavoritesOnly` idempotente.
 */
class UxFilterReproductionTest {

    @Test
    fun todos_chip_tap_while_todos_active_keeps_todos() = runBlocking {
        val vm = viewModel()
        // Estado inicial: filtro Todos (favoriteOnly = false). El chip
        // "Todos" envía hoy ToggleFavorites.
        vm.onAction(DestinationAction.ToggleFavorites)
        assertEquals(
            false,
            vm.state.value.favoriteOnly,
            "U-01: tocar 'Todos' estando activo no debe cambiar el filtro",
        )
    }

    @Test
    fun favoritos_chip_tap_while_favoritos_active_keeps_favoritos() = runBlocking {
        val vm = viewModel()
        // Primer toque: ir a Favoritos. Segundo toque (chip activo): debe
        // quedarse en Favoritos.
        vm.onAction(DestinationAction.ToggleFavorites)
        vm.onAction(DestinationAction.ToggleFavorites)
        assertEquals(
            true,
            vm.state.value.favoriteOnly,
            "U-01: tocar 'Favoritos' estando activo no debe cambiar el filtro",
        )
    }

    private fun viewModel(): DestinationsViewModel {
        val repo = EmptyDestinations()
        return DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repo),
            get = GetDestinationUseCase(repo),
            save = SaveDestinationUseCase(repo),
            update = UpdateDestinationUseCase(repo),
            delete = DeleteDestinationUseCase(repo),
            toggleFavorite = ToggleFavoriteUseCase(repo),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
    }

    private class EmptyDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            state.value = state.value + destination
        }
        override suspend fun update(destination: Destination) {
            state.value = state.value.map { if (it.id == destination.id) destination else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }
}
