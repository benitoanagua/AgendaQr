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
 * U-01: los chips "Todos" y "Favoritos" envían `SetFavoritesOnly`
 * idempotente. Tocar el chip activo no cambia el filtro (antes ambos
 * disparaban `ToggleFavorites` y el filtro conmutaba).
 */
class UxFilterReproductionTest {

    @Test
    fun todos_chip_tap_while_todos_active_keeps_todos() = runBlocking {
        val vm = viewModel()
        vm.onAction(DestinationAction.SetFavoritesOnly(false))
        vm.onAction(DestinationAction.SetFavoritesOnly(false))
        assertEquals(false, vm.state.value.favoriteOnly)
    }

    @Test
    fun favoritos_chip_tap_while_favoritos_active_keeps_favoritos() = runBlocking {
        val vm = viewModel()
        vm.onAction(DestinationAction.SetFavoritesOnly(true))
        vm.onAction(DestinationAction.SetFavoritesOnly(true))
        assertEquals(true, vm.state.value.favoriteOnly)
    }

    @Test
    fun filter_transitions_both_ways() = runBlocking {
        val vm = viewModel()
        vm.onAction(DestinationAction.SetFavoritesOnly(true))
        assertEquals(true, vm.state.value.favoriteOnly)
        vm.onAction(DestinationAction.SetFavoritesOnly(false))
        assertEquals(false, vm.state.value.favoriteOnly)
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
