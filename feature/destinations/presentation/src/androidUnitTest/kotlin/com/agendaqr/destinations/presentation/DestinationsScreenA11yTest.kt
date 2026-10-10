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
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * T10 — Accesibilidad verificable (spec §11) sobre Robolectric: se ejecuta
 * en el gate existente (testDebugUnitTest) sin emulador.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
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
            XauxaTheme {
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
    fun main_buttons_have_48dp_touch_targets() {
        setContent()
        compose.onNodeWithText("Añadir").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Registrar").assertHeightIsAtLeast(48.dp)
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
            XauxaTheme {
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
    fun favorites_tile_state_is_text_and_semantics_not_color() {
        // Contrato §5.1/§11: el filtro Favoritos NO cambia el acento del
        // tile (lime era un acento de contexto con otro significado); el
        // estado se expresa con TEXTO visible y semántica selected/
        // toggleableState.
        compose.setContent {
            XauxaTheme {
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
        compose.onNodeWithText("Favoritos: activado")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ToggleableState,
                    androidx.compose.ui.state.ToggleableState.On,
                ),
            )
    }

    @Test
    fun favorites_tile_without_filter_returns_to_textual_rest_state() {
        compose.setContent {
            XauxaTheme {
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
    fun live_tile_shows_the_most_recent_activity_from_unfiltered_data() {
        // S01 (tile vivo): la actividad MÁS RECIENTE aparece aunque la
        // lista de QR no esté vacía (antes el tile solo mostraba QR y solo
        // el primero del filtro visible).
        compose.setContent {
            XauxaTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(sample),
                        recentOperations = listOf(
                            com.agendaqr.destinations.domain.Operation(
                                id = "o-1",
                                type = com.agendaqr.destinations.domain.OperationType.COBRO,
                                occurredAt = 900,
                                createdAt = 900,
                                updatedAt = 900,
                                amount = "500",
                            ),
                        ),
                        isLoading = false,
                    ),
                    onAction = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Cobro · 500").assertExists()
        compose.onNodeWithText("Último QR o actividad reciente").assertExists()
    }
}
