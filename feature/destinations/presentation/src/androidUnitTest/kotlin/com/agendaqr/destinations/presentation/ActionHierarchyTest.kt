package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Jerarquía de acciones por nivel (§1 del contrato de acciones):
 *
 * - Nivel 3 (tarjeta de pendiente): la acción principal por tipo de
 *   incidencia («Ver existente» en duplicados, «Reintentar clasificación»
 *   en desconocidos) convive con las secundarias/terciarias.
 * - Nivel 4 (destructiva): «Descartar» sobre un elemento SIN clasificar
 *   pide confirmación (pérdida de contenido retenido, no recuperable en
 *   el flujo); sobre un DUPLICADO se ejecuta directo (el original existe,
 *   no se pierde información).
 * - No se exponen acciones incompatibles con el estado: «Guardar de todos
 *   modos» solo existe para duplicados.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ActionHierarchyTest {

    @get:Rule
    val compose = createComposeRule()

    private val unknown = ImportCandidate(
        id = "u-1",
        kind = ImportKind.DESCONOCIDO,
        fingerprint = "fp-u",
        payloadRef = "payload-u",
    )
    private val duplicate = ImportCandidate(
        id = "d-1",
        kind = ImportKind.QR,
        fingerprint = "fp-dup",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val original = ImportCandidate(
        id = "o-1",
        kind = ImportKind.QR,
        fingerprint = "fp-dup",
        qrAsset = QrAsset(encoded = "eA=="),
    )

    private fun render(batch: ImportBatch, onAction: (ImportBatchAction) -> Unit) {
        compose.setContent {
            AmTheme {
                ImportBatchScreen(
                    state = ImportBatchUiState.Review(batch),
                    onAction = onAction,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun unknown_card_offers_retry_as_main_action_and_discard_with_confirmation() {
        val batch = ImportBatch(listOf(unknown))
        val actions = mutableListOf<ImportBatchAction>()
        render(batch, { actions += it })

        // Nivel 3: acción principal del elemento sin clasificar.
        compose.onNodeWithText(AppStrings.ReintentarClasificacion).assertExists()
        compose.onNodeWithText(AppStrings.Descartar).assertExists()
        assertTouchTarget(AppStrings.ReintentarClasificacion)
        assertTouchTarget(AppStrings.Descartar)
        // Nivel 4: sin acción incompatible con el estado.
        compose.onNodeWithText(AppStrings.GuardarDeTodosModos).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.VerExistente).assertDoesNotExist()

        // El primer toque en Descartar NO descarta: abre la confirmación.
        compose.onNodeWithText(AppStrings.Descartar).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.descartarElementoWarning).assertExists()
        compose.onNodeWithText(AppStrings.Cancelar).assertExists()
        assertTrue("No debe emitirse DiscardCandidate antes de confirmar") {
            actions.none { it is ImportBatchAction.DiscardCandidate }
        }

        // Confirmar ejecuta el descarte una sola vez.
        compose.onNodeWithText(AppStrings.descartarElementoConfirmar).performClick()
        compose.waitForIdle()
        assertEquals(1, actions.count { it is ImportBatchAction.DiscardCandidate })
    }

    @Test
    fun duplicate_card_offers_view_existing_as_main_and_saves_anyway_as_secondary() {
        val batch = ImportBatch(listOf(original, duplicate))
        val actions = mutableListOf<ImportBatchAction>()
        render(batch, { actions += it })

        // Nivel 3 contextual: primaria "Ver existente", secundaria "Guardar de todos modos".
        compose.onNodeWithText(AppStrings.VerExistente).assertExists()
        compose.onNodeWithText(AppStrings.GuardarDeTodosModos).assertExists()
        compose.onNodeWithText(AppStrings.ReintentarClasificacion).assertDoesNotExist()
        assertTouchTarget(AppStrings.VerExistente)
        assertTouchTarget(AppStrings.GuardarDeTodosModos)
        assertTouchTarget(AppStrings.Descartar)

        val saveBounds = compose.onNodeWithText(AppStrings.GuardarDeTodosModos)
            .getUnclippedBoundsInRoot()
        val discardBounds = compose.onNodeWithText(AppStrings.Descartar)
            .getUnclippedBoundsInRoot()
        assertTrue("Las acciones de una tarjeta pendiente deben tener espacio visible entre ellas") {
            discardBounds.left > saveBounds.right
        }

        // Nivel 4 sobre duplicado: descartar directo (el original existe;
        // no se pierde información) — sin diálogo de confirmación.
        compose.onNodeWithText(AppStrings.Descartar).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.descartarElementoWarning).assertDoesNotExist()
        assertEquals(1, actions.count { it is ImportBatchAction.DiscardCandidate })
    }

    @Test
    fun cancel_confirmation_keeps_the_unclassified_candidate() {
        val batch = ImportBatch(listOf(unknown))
        val actions = mutableListOf<ImportBatchAction>()
        render(batch, { actions += it })

        compose.onNodeWithText(AppStrings.Descartar).performClick()
        compose.waitForIdle()
        // Cancelar mantiene el diálogo cerrado y el elemento intacto:
        // ningún evento de descarte ni de guardado emitido.
        compose.onNodeWithText(AppStrings.Cancelar).performClick()
        compose.waitForIdle()
        assertTrue("Cancelar no debe emitir ninguna acción destructiva") {
            actions.none { it is ImportBatchAction.DiscardCandidate }
        }
    }

    private fun assertTrue(message: String, condition: () -> Boolean) {
        check(condition()) { message }
    }

    private fun assertTouchTarget(label: String) {
        val bounds = compose.onNodeWithText(label).getUnclippedBoundsInRoot()
        val height = bounds.bottom - bounds.top
        assertTrue("$label debe conservar un objetivo táctil de 48 dp") {
            height >= AmMetrics.ControlMinSize
        }
    }
}
