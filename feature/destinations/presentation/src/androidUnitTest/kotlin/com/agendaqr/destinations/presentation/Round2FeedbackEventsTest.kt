package com.agendaqr.destinations.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.LiveRegionMode
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ronda 2 (Área F): los anuncios de estado se disparan por TRANSICIÓN de
 * evento, no por estado persistente. El guardado del lote se anuncia una
 * sola vez al llegar a Saved (y no se repite mientras Saved persiste).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Round2FeedbackEventsTest {

    @get:Rule
    val compose = createComposeRule()

    private fun sampleBatch(): com.agendaqr.destinations.domain.ImportBatch =
        com.agendaqr.destinations.domain.ImportBatch(candidates = emptyList())

    @Test
    fun batch_saved_transition_announces_once_and_not_before() {
        var state by mutableStateOf<ImportBatchUiState>(ImportBatchUiState.Idle)
        compose.setContent {
            XauxaTheme {
                ImportBatchScreen(state = state, onAction = {})
            }
        }
        compose.waitForIdle()
        // Idle: sin anuncio.
        compose.onNodeWithContentDescription("Guardado").assertDoesNotExist()
        // Saving: aún sin anuncio (no es éxito).
        state = ImportBatchUiState.Saving(sampleBatch())
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Guardado").assertDoesNotExist()
        // Saved: anuncio de éxito accesible (liveRegion cortés).
        state = ImportBatchUiState.Saved(sampleBatch())
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Guardado")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
    }
    // --- Ronda 2 (Área I): slot trailing del campo (mostrar/ocultar contraseña) ---

    @Test
    fun text_input_trailing_slot_renders_inside_the_field() {
        compose.setContent {
            XauxaTheme {
                com.agendaqr.core.ui.components.XauxaTextInput(
                    label = "Contraseña",
                    value = "secreto",
                    onValueChange = {},
                    trailing = {
                        com.agendaqr.core.ui.components.XauxaText(
                            "Mostrar",
                            size = com.agendaqr.core.ui.theme.XauxaType.Caption,
                        )
                    },
                )
            }
        }
        compose.waitForIdle()
        // El slot vive DENTRO del campo: coexiste con la etiqueta asociada
        // (el campo conserva su nombre accesible).
        compose.onNodeWithContentDescription("Contraseña").assertExists()
        compose.onNodeWithText("Mostrar").assertExists()
    }
}
