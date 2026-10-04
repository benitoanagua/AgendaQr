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
}
