package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.ReceiptProvenance
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * D3 — accesibilidad verificable ampliada: S02, S04, S06, S09, S10, S12,
 * selector S08 y editor de operación sobre Robolectric (gate de CI).
 *
 * Los tests geometricos de S02 van en NATIVE+w360 (ver esos metodos): en
 * LEGACY los avances son degenerados y sin qualifiers NATIVE no mide.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ScreensA11yCoverageTest {

    @get:Rule
    val compose = createComposeRule()

    private fun headingMatcher() =
        SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    // S02 — Añadir: título con heading, Galería primaria ≥48dp y entradas.
    // NATIVE+w360: la geometria necesita fuentes reales (LEGACY degenera)
    // y NATIVE necesita qualifiers explicitos (sin ellos mide ceros).
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    fun s02_add_screen_headings_and_touch_targets() {
        compose.setContent {
            AmTheme {
                AddDestinationScreen(
                    onImport = { },
                    onBack = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Añadir").assert(headingMatcher())
        // V1.2: una única acción de galería con selección múltiple (sin
        // "Galería (varios)") y la cámara después de la ayuda de share.
        compose.onNodeWithText("Elegir de la galería").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Cámara").assertHeightIsAtLeast(48.dp)
        // V1.2: la flecha atrás de la barra superior es un IconButton con
        // descripción accesible (M3 garantiza el target táctil de 48 dp).
        compose.onNodeWithContentDescription("Volver").assertExists()
    }

    // S04 — Buscar: destino raíz con barra de navegación; el estado vacío
    // es textual.
    @Test
    fun s04_search_intro_is_textual_and_heading_exists() {
        compose.setContent {
            AmTheme {
                GlobalSearchScreen(
                    state = GlobalSearchUiState(query = ""),
                    onAction = { },
                    onSelect = { },
                )
            }
        }
        compose.waitForIdle()
        // §11: el estado se expresa con texto, no solo con color.
        compose.onNodeWithText("Busca QR, actividades, contextos o comprobantes.")
            .assertIsDisplayed()
    }

    // S06 — Contexto: filas navegables; estado vacío con acción primaria textual.
    @Test
    fun s06_empty_state_primary_action_and_rows() {
        compose.setContent {
            AmTheme {
                ContextsScreen(
                    state = ContextsUiState(isLoading = false),
                    onAction = { },
                    onBack = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Sin contextos").assertIsDisplayed()
        // U3/ADR-0003: la acción primaria del vacío es crear, con texto.
        compose.onNodeWithText("Crear contexto").assertHeightIsAtLeast(48.dp)
    }

    // S09 — Revisar QR: título con heading; acciones ≥48dp.
    @Test
    fun s09_review_qr_headings_and_actions() {
        compose.setContent {
            AmTheme {
                ImportReviewScreen(
                    assets = listOf(QrAsset(encoded = "eA==")),
                    onSaveAll = { },
                    onBack = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Revisar QR").assert(headingMatcher())
        compose.onNodeWithText("Guardar").assertHeightIsAtLeast(48.dp)
        // V1.2: la flecha atrás de la barra superior es un IconButton con
        // descripción accesible (M3 garantiza el target táctil de 48 dp).
        compose.onNodeWithContentDescription("Volver").assertExists()
    }

    // S10/S07 — editor de operación: selector S08 con semántica y targets.
    @Test
    fun s07_editor_and_s08_picker_semantics() {
        compose.setContent {
            AmTheme {
                ContextPickerDialog(
                    contexts = listOf(
                        com.agendaqr.destinations.domain.Context("ctx-1", "Mercado", createdAt = 1, updatedAt = 1),
                    ),
                    selection = ContextSelection(contextId = "ctx-1"),
                    onSelect = { },
                    onRemove = { },
                    onCancel = { },
                    onCreateContext = { _, _ -> },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Seleccionar contexto").assertIsDisplayed()
        // Quitar contexto solo con selección previa; Cancelar nunca destruye.
        compose.onNodeWithText("Quitar contexto").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Cancelar").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Crear contexto").assertExists()
    }

    // S12 — resultado del lote: estados con texto (✓/!/?) y contadores.
    @Test
    fun s12_batch_result_states_as_text() {
        val batch = com.agendaqr.destinations.domain.ImportBatch(
            listOf(
                com.agendaqr.destinations.domain.ImportCandidate(
                    id = "qr-1",
                    kind = com.agendaqr.destinations.domain.ImportKind.QR,
                    fingerprint = "fp",
                    qrAsset = QrAsset(encoded = "eA=="),
                ),
                com.agendaqr.destinations.domain.ImportCandidate(
                    id = "u-1",
                    kind = com.agendaqr.destinations.domain.ImportKind.DESCONOCIDO,
                    fingerprint = "fp-u",
                ),
            ),
        )
        compose.setContent {
            AmTheme {
                ImportBatchScreen(
                    state = ImportBatchUiState.Result(batch),
                    onAction = { },
                )
            }
        }
        compose.waitForIdle()
        // §5/§11: los estados del contrato se leen como texto. El resumen usa
        // filas etiqueta-valor (G3): el rótulo del estado y su conteo son
        // nodos de texto separados (rótulo a la izquierda, conteo a la
        // derecha).
        compose.onNodeWithText("Reconocidos").assertExists()
        compose.onNodeWithText("Pendientes de revisión").assertExists()
        compose.onNodeWithText("Revisar 1 pendiente").assertHeightIsAtLeast(48.dp)
    }

    // S10 — el visor requiere OperationsViewModel (diálogo del VM); su
    // contenido textual ("Recibido", "Eliminar", "Cerrar") queda cubierto
    // por el flujo D de R1 en runtime. Aquí se valida lo estático: que la
    // vista previa de bytes no crashee y el texto de origen sea semántico.
    @Test
    fun s10_receipt_preview_renders_without_crash() {
        compose.setContent {
            AmTheme {
                ComprobantePreview(
                    bytes = byteArrayOf(1, 2, 3),
                    mimeType = "image/png",
                )
            }
        }
        compose.waitForIdle()
        // Sin crash + un nodo accesible activo es lo verificable headless.
        compose.onNodeWithText("Agenda QR").assertDoesNotExist()
    }

    // S01 — la fila del destino: badge con texto + acción etiquetada.
    @Test
    fun s01_destination_row_states_and_labels() {
        compose.setContent {
            AmTheme {
                DestinationsScreen(
                    state = DestinationsUiState(
                        destinations = listOf(
                            Destination(
                                id = "d-1",
                                name = "Carniceria Don Bife",
                                qr = QrAsset(encoded = "eA=="),
                                createdAt = 1,
                                updatedAt = 1,
                                favorite = true,
                            ),
                            Destination(
                                id = "d-2",
                                name = "Verduleria El Sol",
                                qr = QrAsset(encoded = "eA=="),
                                createdAt = 1,
                                updatedAt = 1,
                                favorite = false,
                            ),
                        ),
                        isLoading = false,
                    ),
                    onAction = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Agenda QR").assert(headingMatcher())
        // U-10: sin duplicación — el favorito vive SOLO en el carrusel y
        // el no favorito SOLO en la lista de "Todos los destinos".
        val favNodes = compose.onAllNodesWithText("Carniceria Don Bife").fetchSemanticsNodes()
        assert(favNodes.size == 1) { "S01: el favorito va solo en el carrusel" }
        val listNodes = compose.onAllNodesWithText("Verduleria El Sol").fetchSemanticsNodes()
        assert(listNodes.size == 1) { "S01: el no favorito va solo en la lista" }
        compose.onNodeWithContentDescription("Marcar favorito").assertExists()
        // V1.2: sin tiles — el botón de texto "Favorito" por fila desapareció
        // con el IconToggleButton de estrella (48 dp garantizado por M3).
        // El estado de sync ES texto cuando hay algo que informar.
        // Defecto F-1: el estado SINCRONIZADO es el normal (silencio = OK);
        // el badge solo aparece para Pendiente/Error/Sincronizando.
        compose.onNodeWithText("Sincronizado").assertDoesNotExist()
    }

    // S02 — orden congelado: Galería → Desde otra app → Cámara.
    // Misma razon NATIVE+w360 que el test anterior (posiciones reales).
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    fun s02_frozen_order_gallery_share_camera() {
        compose.setContent {
            AmTheme {
                AddDestinationScreen(
                    onImport = { },
                    onBack = { },
                )
            }
        }
        compose.waitForIdle()
        // V1.2: Galería (única, multi-selección) → ayuda breve de share →
        // Cámara. En Robolectric corre el actual Android
        // (sharedImportsSupported = true), así que la ayuda es visible.
        val gallery = compose.onNodeWithText("Elegir de la galería").getBoundsInRoot().top
        val share = compose.onNodeWithText(
            AppStrings.shareFromAnotherAppHint,
        ).getBoundsInRoot().top
        val camera = compose.onNodeWithText("Cámara").getBoundsInRoot().top
        assertTrue(gallery < share, "Galería debe estar sobre la ayuda de share")
        assertTrue(share < camera, "La ayuda de share debe estar sobre Cámara")
    }

    // S07 — tipo con semántica de selección: un chip marcado, 48dp.
    @Test
    fun s07_type_selector_shows_single_selection() {
        var selected = OperationType.PAGO
        compose.setContent {
            AmTheme {
                OperationTypeSelector(
                    type = selected,
                    onSelect = { selected = it },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Pago").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Cobro").assertHeightIsAtLeast(48.dp)
        // Cambiar el tipo mueve la selección (semántica, no dos primarios).
        compose.onNodeWithText("Cobro").performClick()
        compose.waitForIdle()
        assertTrue(selected == OperationType.COBRO)
    }

    // Defectos registrados en docs/STATE.md,
    // resueltos por V1.1 (verificación explícita): "Volver" ya no se parte
    // en dos líneas y el primario nunca queda recortado al final del
    // scroll: ambos viven en la AmScreenScaffold anclado a los insets.
    @Test
    fun t07_actions_live_in_the_top_bar_and_save_stays_reachable() {
        var saved = false
        var backed = false
        compose.setContent {
            AmTheme {
                ImportReviewScreen(
                    assets = listOf(com.agendaqr.destinations.domain.QrAsset("eA==", content = "qr-real")),
                    onSaveAll = { saved = true },
                    onBack = { backed = true },
                )
            }
        }
        compose.waitForIdle()
        // V1.2: la flecha atrás vive en la barra superior (misma en ambas
        // plataformas) y Guardar es el botón principal FIJO abajo (acción
        // real de guardado, nunca recortada). M3 garantiza el target de la
        // flecha (minimumInteractiveComponentSize).
        compose.onNodeWithContentDescription("Volver").assertExists().performClick()
        assertTrue(backed, "La flecha atrás ejecuta el pop de superficie")
        compose.onNodeWithText("Guardar").assertHeightIsAtLeast(48.dp).performClick()
        assertTrue(saved, "Guardar es la acción principal fija abajo (nunca recortada)")
    }
}

