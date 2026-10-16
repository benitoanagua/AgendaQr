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
 * U-10/D8: Inicio acorde a la especificación. El FAB "Añadir" abre el hub
 * "¿Qué quieres añadir?" con las 3 creaciones (traer QR, registrar
 * actividad, crear contexto); sin acciones duplicadas en la pantalla.
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

    private fun openHub() {
        compose.onNodeWithText(AppStrings.Anadir, useUnmergedTree = true).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.QueQuieresAnadir).assertExists()
    }

    @Test
    fun hub_offers_the_three_creations() {
        render()
        // Sin acciones duplicadas en Inicio (las entradas viven en S02).
        compose.onNodeWithText("Escanear QR").assertDoesNotExist()
        compose.onNodeWithText("Importar imagen").assertDoesNotExist()
        openHub()
        compose.onNodeWithText(AppStrings.TraerQr).assertExists()
        compose.onNodeWithText(AppStrings.RegistrarOperacion).assertExists()
        compose.onNodeWithText(AppStrings.CrearContexto).assertExists()
    }

    @Test
    fun hub_traer_qr_goes_to_add() {
        val actions = mutableListOf<DestinationAction>()
        render(onAction = { actions += it })
        openHub()
        compose.onNodeWithText(AppStrings.TraerQr).performClick()
        assert(actions.contains(DestinationAction.Edit(null)))
    }

    @Test
    fun hub_registrar_actividad_calls_register() {
        var registered = 0
        render(onRegisterOperation = { registered++ })
        openHub()
        compose.onNodeWithText(AppStrings.RegistrarOperacion).performClick()
        compose.waitForIdle()
        assert(registered == 1)
    }

    @Test
    fun hub_crear_contexto_opens_the_creation_dialog() {
        var created: Pair<String, String?>? = null
        render(onCreateContext = { name, note -> created = name to note })
        openHub()
        compose.onNodeWithText(AppStrings.CrearContexto).performClick()
        compose.waitForIdle()
        // El diálogo de creación existente pide el nombre.
        compose.onNodeWithText(AppStrings.Nombre).assertExists()
        assert(created == null)
    }

    @Test
    fun hub_cancel_closes_without_navigating() {
        val actions = mutableListOf<DestinationAction>()
        var registered = 0
        render(onAction = { actions += it }, onRegisterOperation = { registered++ })
        openHub()
        compose.onNodeWithText(AppStrings.Cancelar).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.QueQuieresAnadir).assertDoesNotExist()
        assert(actions.isEmpty())
        assert(registered == 0)
    }
}
