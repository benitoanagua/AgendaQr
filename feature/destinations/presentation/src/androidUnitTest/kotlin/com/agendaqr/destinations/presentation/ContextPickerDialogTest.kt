package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.destinations.domain.Context
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * U3/ADR-0003 — el selector S08 ofrece crear contexto sin salir del draft.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ContextPickerDialogTest {

    @get:Rule
    val compose = createComposeRule()

    private val contexts = listOf(
        Context("ctx-1", "Mercado Central", createdAt = 1, updatedAt = 1),
        Context("ctx-2", "Ferreteria", createdAt = 1, updatedAt = 1),
    )

    @Test
    fun picker_list_offers_create_and_selection_semantics() {
        var selection = ContextSelection()
        compose.setContent {
            XauxaTheme {
                ContextPickerDialog(
                    contexts = contexts,
                    selection = selection,
                    onSelect = { selection = selection.select(it) },
                    onRemove = { selection = selection.removeContext() },
                    onCancel = { selection = selection.cancelPicker() },
                    onCreateContext = { _, _ -> },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Seleccionar contexto").assertExists()
        compose.onNodeWithText("Mercado Central").assertExists()
        // U3: la acción de crear está en la lista del selector.
        compose.onNodeWithText("Crear contexto").assertExists()
        // Sin selección previa no se ofrece Quitar contexto (T4).
        compose.onNodeWithText("Quitar contexto").assertDoesNotExist()
    }

    @Test
    fun create_step_opens_with_the_minimal_form_and_back_to_list() {
        var selection = ContextSelection(contextId = "ctx-1")
        compose.setContent {
            XauxaTheme {
                ContextPickerDialog(
                    contexts = contexts,
                    selection = selection,
                    onSelect = { selection = selection.select(it) },
                    onRemove = { selection = selection.removeContext() },
                    onCancel = { selection = selection.cancelPicker() },
                    onCreateContext = { _, _ -> },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Crear contexto").performClick()
        compose.waitForIdle()
        // Formulario mínimo: Nombre obligatorio + Nota.
        compose.onNodeWithText("Nombre *").assertExists()
        compose.onNodeWithText("Nota").assertExists()
        compose.onNodeWithText("Crear").assertExists()
        // Cancelar vuelve a la lista sin tocar la selección.
        compose.onNodeWithText("Cancelar").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Seleccionar contexto").assertExists()
    }
}
