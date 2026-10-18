package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Secciones de Inicio: el carrusel muestra los favoritos y la lista inferior
 * "Otros destinos"; el estado derivado decide qué secciones existen para no
 * mostrar nunca un estado vacío falso cuando el carrusel ya muestra todo.
 */
class DestinationsSectionStateTest {

    private fun destination(id: String, favorite: Boolean = false, name: String = id) = Destination(
        id = id,
        name = name,
        qr = QrAsset(encoded = "eA=="),
        favorite = favorite,
        createdAt = 1,
        updatedAt = 1,
    )

    @Test
    fun all_favorites_show_the_carousel_without_other_destinations() {
        val state = DestinationsUiState(
            destinations = listOf(
                destination("d-1", favorite = true),
                destination("d-2", favorite = true),
            ),
            isLoading = false,
        )
        assertEquals(2, state.favorites.size)
        assertTrue(state.showsFavoritesCarousel)
        assertFalse(state.hasOtherDestinations)
        assertTrue(state.visibleDestinations.isEmpty())
    }

    @Test
    fun no_favorites_show_the_full_list_without_a_carousel() {
        val state = DestinationsUiState(
            destinations = listOf(destination("d-1"), destination("d-2")),
            isLoading = false,
        )
        assertTrue(state.favorites.isEmpty())
        assertFalse(state.showsFavoritesCarousel)
        assertTrue(state.hasOtherDestinations)
        assertEquals(2, state.visibleDestinations.size)
    }

    @Test
    fun mixed_destinations_show_the_carousel_and_other_destinations() {
        val state = DestinationsUiState(
            destinations = listOf(
                destination("d-1", favorite = true),
                destination("d-2"),
            ),
            isLoading = false,
        )
        assertEquals(1, state.favorites.size)
        assertTrue(state.showsFavoritesCarousel)
        assertTrue(state.hasOtherDestinations)
        assertEquals(listOf("d-2"), state.visibleDestinations.map { it.id })
    }

    @Test
    fun favorites_filter_lists_favorites_and_hides_the_carousel() {
        val state = DestinationsUiState(
            destinations = listOf(
                destination("d-1", favorite = true),
                destination("d-2"),
            ),
            favoriteOnly = true,
            isLoading = false,
        )
        assertFalse(state.showsFavoritesCarousel)
        assertEquals(listOf("d-1"), state.visibleDestinations.map { it.id })
    }

    @Test
    fun todos_chip_count_is_carousel_favorites_plus_list_rows() {
        // El chip "Todos" cuenta el total real: favoritos del carrusel + filas
        // de la lista inferior (sin duplicar ni perder ninguno).
        val state = DestinationsUiState(
            destinations = listOf(
                destination("d-1", favorite = true),
                destination("d-2", favorite = true),
                destination("d-3"),
            ),
            isLoading = false,
        )
        val carousel = state.favorites.size
        val rows = state.visibleDestinations.size
        assertEquals(state.destinations.size, carousel + rows)
    }

    @Test
    fun text_query_filters_both_the_carousel_and_the_list() {
        val state = DestinationsUiState(
            destinations = listOf(
                destination("d-1", favorite = true, name = "Mercado"),
                destination("d-2", favorite = true, name = "Ferretería"),
                destination("d-3", name = "Mercado"),
            ),
            query = "mercado",
            isLoading = false,
        )
        assertEquals(listOf("d-1"), state.favorites.map { it.id })
        assertEquals(listOf("d-3"), state.visibleDestinations.map { it.id })
    }
}
