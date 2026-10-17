package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.Destination
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * U-10/D8 + Anexo A: Inicio con rejilla "King Kong" de acciones rápidas.
 * Las 3 creaciones (traer QR, registrar actividad, crear contexto) viven
 * como celdas de la rejilla, sin FAB ni hub duplicado.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UxHomeHubTest {

    @get:Rule
    val compose = createComposeRule()

    private fun render(
        onAction: (DestinationAction) -> Unit = {},
        onRegisterOperation: () -> Unit = {},
        onCreateContext: (String, String?) -> Unit = { _, _ -> },
    ) {
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(
                            Destination(
                                id = "d-1",
                                name = "Carniceria",
                                qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                                createdAt = 1,
                                updatedAt = 1,
                            ),
                        ),
                        isLoading = false,
                    ),
                    onAction = onAction,
                    onRegisterOperation = onRegisterOperation,
                    onCreateContext = onCreateContext,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun grid_offers_the_three_creations() {
        render()
        // La rejilla muestra las 3 acciones existentes.
        compose.onNodeWithText(AppStrings.TraerQr).assertExists()
        compose.onNodeWithText(AppStrings.RegistrarOperacion).assertExists()
        compose.onNodeWithText(AppStrings.CrearContexto).assertExists()
    }

    @Test
    fun grid_traer_qr_goes_to_add() {
        val actions = mutableListOf<DestinationAction>()
        render(onAction = { actions += it })
        compose.onNodeWithText(AppStrings.TraerQr).performClick()
        compose.waitForIdle()
        assert(actions.contains(DestinationAction.Edit(null)))
    }

    @Test
    fun grid_registrar_actividad_calls_register() {
        var registered = 0
        render(onRegisterOperation = { registered++ })
        compose.onNodeWithText(AppStrings.RegistrarOperacion).performClick()
        compose.waitForIdle()
        assert(registered == 1)
    }

    @Test
    fun grid_crear_contexto_opens_the_creation_dialog() {
        var created: Pair<String, String?>? = null
        render(onCreateContext = { name, note -> created = name to note })
        compose.onNodeWithText(AppStrings.CrearContexto).performClick()
        compose.waitForIdle()
        // El diálogo de creación existente pide el nombre.
        compose.onNodeWithText(AppStrings.Nombre).assertExists()
        assert(created == null)
    }

    @Test
    fun grid_no_fab_present() {
        render()
        // El FAB fue retirado para evitar duplicidad con la rejilla.
        compose.onNodeWithText(AppStrings.Anadir).assertDoesNotExist()
    }
}
