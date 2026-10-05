package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T-e — los botones de error de las pantallas disparan la acción del
 * error: REINTENTAR re-ejecuta (RetryFailed / guardar de nuevo) y CERRAR
 * solo descarta.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ErrorRetryButtonsTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun destinations_list_retry_dispatches_retry_failed() {
        val received = mutableListOf<DestinationAction>()
        compose.setContent {
            XauxaTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(
                            Destination("d-1", "Carniceria", QrAsset("eA=="), createdAt = 1, updatedAt = 1),
                        ),
                        isLoading = false,
                        error = userFacingError(IllegalStateException("boom"), ErrorFlow.SaveQr),
                    ),
                    onAction = { received += it },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("REINTENTAR").performClick()
        assertTrue(received.contains(DestinationAction.RetryFailed))
    }

    @Test
    fun destinations_list_close_only_dismisses() {
        val received = mutableListOf<DestinationAction>()
        compose.setContent {
            XauxaTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = emptyList(),
                        isLoading = false,
                        error = comprobanteNotFound(),
                    ),
                    onAction = { received += it },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("CERRAR").performClick()
        assertTrue(received == listOf(DestinationAction.ClearError))
    }

    @Test
    fun import_review_retry_saves_again() {
        var saves = 0
        compose.setContent {
            XauxaTheme {
                ImportReviewScreen(
                    assets = listOf(QrAsset("eA==")),
                    onSaveAll = { saves++ },
                    onBack = {},
                    error = userFacingError(IllegalStateException("boom"), ErrorFlow.SaveQr),
                    onClearError = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("REINTENTAR").performClick()
        assertEquals(1, saves)
    }
}
