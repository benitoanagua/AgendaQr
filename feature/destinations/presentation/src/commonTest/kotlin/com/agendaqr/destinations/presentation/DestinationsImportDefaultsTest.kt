package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DestinationsImportDefaultsTest {
    @Test
    fun saveImportedAssets_never_stores_blank_names() {
        runBlocking {
            val repository = FakeDestinations()
            val vm = DestinationsViewModel(
                observe = ObserveDestinationsUseCase(repository),
                get = GetDestinationUseCase(repository),
                save = SaveDestinationUseCase(repository),
                update = UpdateDestinationUseCase(repository),
                delete = DeleteDestinationUseCase(repository),
                toggleFavorite = ToggleFavoriteUseCase(repository),
                scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
            )
            vm.onAction(DestinationAction.ImportAssets(listOf(QrAsset("a"), QrAsset("b"))))
            vm.saveImportedAssets()
            repeat(100) {
                if (repository.saved.size == 2) return@repeat
                delay(20)
            }
            assertEquals(2, repository.saved.size)
            assertTrue(repository.saved.all { it.name.isNotBlank() })
        }
    }

    private class FakeDestinations : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        val saved = mutableListOf<Destination>()
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(destination: Destination) {
            saved += destination
            state.value = state.value.filterNot { it.id == destination.id } + destination
        }
        override suspend fun update(destination: Destination) {
            state.value = state.value.map { if (it.id == destination.id) destination else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }
}
