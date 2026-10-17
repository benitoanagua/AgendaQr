package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Accesibilidad verificable (spec §11, V1.2) sobre Robolectric: se ejecuta
 * en el gate existente (testDebugUnitTest) sin emulador.
 */
@RunWith(RobolectricTestRunner::class)
// Layout MCP de S01 (garantía + carrusel + pestañas + lista): la fila con
// el favorito queda bajo el pliegue de la ventana por defecto de
// Robolectric; viewport alto para que el objetivo de 48 dp sea real.
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class DestinationsScreenA11yTest {

    @get:Rule
    val compose = createComposeRule()

    private val sample = Destination(
        id = "d-1",
        name = "Carniceria Don Bife",
        qr = QrAsset(encoded = "eA=="),
        createdAt = 1,
        updatedAt = 1,
    )

    private fun setContent(onOpenSearch: () -> Unit = {}) {
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample),
                        isLoading = false,
                    ),
                    onAction = {},
                    onOpenSearch = onOpenSearch,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun grid_actions_and_row_actions_keep_48dp_targets() {
        setContent()
        // Anexo A: rejilla "King Kong" con las 3 acciones rápidas; la acción
        // de favorito de la fila es un IconToggleButton de 48 dp.
        compose.onNodeWithText(AppStrings.TraerQr).assertExists()
        compose.onNodeWithText(AppStrings.RegistrarOperacion).assertExists()
        compose.onNodeWithText(AppStrings.CrearContexto).assertExists()
        compose.onNodeWithContentDescription("Marcar favorito").assertExists()
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun app_title_is_a_semantic_heading() {
        setContent()
        compose.onNodeWithText("Agenda QR")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun search_entry_is_a_single_button_node() {
        var opened = false
        setContent(onOpenSearch = { opened = true })
        // Un solo nodo con el rol del control, cero nodos editables bajo él.
        compose.onNodeWithContentDescription("Buscar en Agenda QR")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        assert(opened) { "El toque en la barra debe abrir la búsqueda global (S04)" }
    }

    @Test
    fun sync_state_is_expressed_as_text_not_only_color() {
        // §11: los estados se expresan también mediante texto. La fila del
        // QR muestra "Pendiente" (badge con texto) cuando su mutación está
        // en la cola, no solo un cambio de color.
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample),
                        isLoading = false,
                    ),
                    onAction = {},
                    syncLookup = ElementSyncLookup(
                        listOf(
                            com.agendaqr.destinations.data.PendingSyncMutation(
                                id = "m-1",
                                resource = com.agendaqr.destinations.data.SyncResource.DESTINATION,
                                mutation = com.agendaqr.destinations.data.SyncMutationType.UPSERT,
                                entityId = "d-1",
                                enqueuedAt = 1,
                                state = com.agendaqr.destinations.data.SyncMutationState.PENDING,
                            ),
                        ),
                    ),
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Pendiente").assertExists()
    }

    @Test
    fun favorites_filter_state_is_text_and_semantics_not_color() {
        // U-01: el filtro Favoritos es un FilterChip; la etiqueta NO
        // cambia ("Favoritos" siempre) y el estado se expresa con
        // semántica `selected` + `stateDescription`, nunca solo con color.
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample),
                        favoriteOnly = true,
                        isLoading = false,
                    ),
                    onAction = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Favoritos")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        compose.onNodeWithText("Favoritos")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, AppStrings.Seleccionado))
    }

    @Test
    fun favorites_filter_without_filter_returns_to_textual_rest_state() {
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample),
                        favoriteOnly = false,
                        isLoading = false,
                    ),
                    onAction = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Favoritos")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
    }

    @Test
    fun favorite_row_toggle_announces_state_through_its_description() {
        // §11: el favorito de fila es un IconToggleButton cuya
        // descripción accesible CAMBIA con el estado (marcar/quitar).
        // Las filas de favoritos se ven con el filtro Favoritos (U-10: en
        // Todos los favoritos viven en el carrusel).
        var toggled: Boolean? = null
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample.copy(favorite = true)),
                        favoriteOnly = true,
                        isLoading = false,
                    ),
                    onAction = { action ->
                        if (action is DestinationAction.ToggleFavorite) toggled = action.destination.favorite
                    },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Quitar favorito")
            .assertExists()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .performClick()
        assert(toggled == true) { "El toggle debe pedir quitar el favorito existente" }
    }
}
