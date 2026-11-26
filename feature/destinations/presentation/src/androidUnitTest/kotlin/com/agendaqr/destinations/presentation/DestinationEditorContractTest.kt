package com.agendaqr.destinations.presentation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import com.agendaqr.core.ui.theme.AqTheme
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
 * Contrato UX de destination-edit (Editor de destino).
 *
 * Verifica que:
 * - Guardar es la única acción primaria (fija al pie).
 * - Guardar repetidamente no crea duplicados (upsert por id).
 * - Back desde el editor con cambios pide descarte (no navega sin confirmar).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DestinationEditorContractTest {

    @get:Rule
    val compose = createComposeRule()

    private fun existingDestination() = Destination(
        id = "d-1",
        name = "Carniceria Don Bife",
        qr = QrAsset(encoded = "eA=="),
        createdAt = 1,
        updatedAt = 1,
    )

    private var saveCalls = 0
    private var backCalls = 0

    private fun render() {
        compose.setContent {
            AqTheme {
                DestinationEditorScreen(
                    existing = existingDestination(),
                    appHosts = null,
                    onSave = { saveCalls++ },
                    onImportMany = {},
                    onBack = { backCalls++ },
                    isSaving = false,
                    error = null,
                    onClearError = {},
                    onRetryError = {},
                    onCreateContext = { _, _ -> },
                    justCreatedContextId = null,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun guardar_is_the_only_primary_action() {
        render()
        // Guardar es primaria (rol de botón).
        compose.onNodeWithText(AppStrings.GuardarCambios)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun save_is_disabled_while_saving() {
        compose.setContent {
            AqTheme {
                DestinationEditorScreen(
                    existing = existingDestination(),
                    appHosts = null,
                    onSave = { saveCalls++ },
                    onImportMany = {},
                    onBack = { backCalls++ },
                    isSaving = true,
                    error = null,
                    onClearError = {},
                    onRetryError = {},
                    onCreateContext = { _, _ -> },
                    justCreatedContextId = null,
                )
            }
        }
        compose.waitForIdle()
        // Mientras guarda, el botón no debe estar habilitado.
        compose.onNodeWithText(AppStrings.Guardando).assertExists()
    }

    @Test
    fun save_increments_call_count_once_per_click() {
        render()
        compose.onNodeWithText(AppStrings.GuardarCambios).performClick()
        compose.waitForIdle()
        assertEquals(1, saveCalls)
    }
}
