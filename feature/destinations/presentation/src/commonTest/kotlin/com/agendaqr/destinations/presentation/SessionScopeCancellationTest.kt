package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T12 — un scope cancelable ligado a la sesión: los ViewModels reciben el
 * scope del grafo (`remember(userId)` + `DisposableEffect`), así que al
 * cerrar sesión TODO se cancela y ningún collector sobrevive a la sesión
 * que lo creó (aislamiento por usuario).
 */
class SessionScopeCancellationTest {

    @Test
    fun cancelling_the_session_scope_stops_viewmodel_collectors() = runBlocking {
        val repository = GrowingDestinations()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repository),
            get = GetDestinationUseCase(repository),
            save = SaveDestinationUseCase(repository),
            update = UpdateDestinationUseCase(repository),
            delete = DeleteDestinationUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            scope = scope,
        )
        waitUntil { viewModel.state.value.destinations.isNotEmpty() }
        assertEquals(1, viewModel.state.value.destinations.size)

        // Cierre de sesión: el DisposableEffect de la sesión cancela el
        // scope compartido. Los collectors mueren con ella.
        scope.cancel()

        // Datos que llegan DESPUÉS de la cancelación no deben tocar el
        // estado del VM (la sesión ya no existe).
        repository.add(Destination("d-late", "Tardío", com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="), createdAt = 2, updatedAt = 2))
        delay(100)

        assertEquals(1, viewModel.state.value.destinations.size)
        assertTrue(viewModel.state.value.destinations.none { it.id == "d-late" })
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    private class GrowingDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())

        init {
            state.value = listOf(
                Destination("d-1", "Carniceria", com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="), createdAt = 1, updatedAt = 1),
            )
        }

        fun add(destination: Destination) {
            state.value = state.value + destination
        }

        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {}
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }
}
