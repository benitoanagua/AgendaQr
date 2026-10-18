package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Inicio (S01): la lista inferior nunca muestra un mensaje falso. Con todos
 * los destinos favoritos, el carrusel ya lo muestra todo y no hay sección
 * inferior; con el filtro Favoritos vacío hay un mensaje propio con salida
 * real al filtro Todos; con mezcla, el encabezado es "Otros destinos".
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class UxHomeFavoritesTest {

    @get:Rule
    val compose = createComposeRule()

    private fun destination(id: String, favorite: Boolean, name: String = id) = Destination(
        id = id,
        name = name,
        qr = QrAsset(encoded = "eA=="),
        favorite = favorite,
        createdAt = 1,
        updatedAt = 1,
    )

    private fun render(
        state: DestinationsUiState,
        onAction: (DestinationAction) -> Unit = {},
    ) {
        compose.setContent {
            AmTheme {
                DestinationsScreen(state = state, onAction = onAction)
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun all_favorites_show_no_empty_message_and_no_lower_heading() {
        render(
            DestinationsUiState(
                destinations = listOf(
                    destination("d-1", favorite = true, name = "Mercado"),
                    destination("d-2", favorite = true, name = "Ferretería"),
                ),
                isLoading = false,
            ),
        )
        // El carrusel muestra todo: nada de "no se encontraron" ni encabezado
        // inferior ("Todos" u "Otros").
        compose.onNodeWithText(AppStrings.NoSeEncontraronDestinos).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.AunNoHayFavoritos).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.OtrosDestinos).assertDoesNotExist()
        compose.onNodeWithText("Mercado").assertExists()
    }

    @Test
    fun no_favorites_show_the_full_list_with_the_default_heading() {
        render(
            DestinationsUiState(
                destinations = listOf(destination("d-1", favorite = false, name = "Mercado")),
                isLoading = false,
            ),
        )
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assertExists()
        compose.onNodeWithText(AppStrings.OtrosDestinos).assertDoesNotExist()
    }

    @Test
    fun mixed_destinations_label_the_lower_section_as_other_destinations() {
        render(
            DestinationsUiState(
                destinations = listOf(
                    destination("d-1", favorite = true, name = "Mercado"),
                    destination("d-2", favorite = false, name = "Ferretería"),
                ),
                isLoading = false,
            ),
        )
        compose.onNodeWithText(AppStrings.OtrosDestinos).assertExists()
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assertDoesNotExist()
        compose.onNodeWithText("Mercado").assertExists()
        compose.onNodeWithText("Ferretería").assertExists()
    }

    @Test
    fun empty_favorites_filter_shows_its_own_message_and_ver_todos_leaves_the_filter() {
        val actions = mutableListOf<DestinationAction>()
        render(
            DestinationsUiState(
                destinations = listOf(destination("d-1", favorite = false, name = "Mercado")),
                favoriteOnly = true,
                isLoading = false,
            ),
            onAction = { actions += it },
        )
        compose.onNodeWithText(AppStrings.AunNoHayFavoritos).assertExists()
        compose.onNodeWithText(AppStrings.NoSeEncontraronDestinos).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.VerTodos).performClick()
        compose.waitForIdle()
        assert(actions.contains(DestinationAction.SetFavoritesOnly(false))) {
            "Ver todos debe salir del filtro Favoritos"
        }
    }

    @Test
    fun query_without_results_shows_the_search_empty_state() {
        render(
            DestinationsUiState(
                destinations = listOf(destination("d-1", favorite = false, name = "Mercado")),
                query = "inexistente",
                isLoading = false,
            ),
        )
        compose.onNodeWithText(AppStrings.NoSeEncontraronDestinos).assertExists()
        compose.onNodeWithText(AppStrings.AunNoHayFavoritos).assertDoesNotExist()
    }
}
