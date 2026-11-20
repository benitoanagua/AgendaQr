package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.AgendaSearchResultType
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * ADR-0028 — patrones móviles (Moon como referencia, Polaris autoridad):
 *
 * - S04: con resultados de tipos mezclados, chips de filtro por tipo
 *   (AqChipGroup); con un solo tipo no se ofrece filtro.
 * - S12 revisión: con duplicados Y sin clasificar a la vez, chips
 *   Todos / Posibles duplicados / Pendientes de revisión que filtran la
 *   lista y el conteo del encabezado.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class MobilePatternScreensContractTest {

    @get:Rule
    val compose = createComposeRule()

    // ------------------------------------------------------------------
    // S04 — Buscar: chips de tipo solo con resultados mixtos
    // ------------------------------------------------------------------

    private fun renderSearch(results: List<AgendaSearchResult>) {
        compose.setContent {
            AqTheme {
                GlobalSearchScreen(
                    state = GlobalSearchUiState(query = "agenda", results = results),
                    onAction = {},
                    onSelect = {},
                    autoFocus = false,
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun s04_shows_type_filter_chips_when_results_mix_types() {
        renderSearch(
            listOf(
                AgendaSearchResult(
                    type = AgendaSearchResultType.QR,
                    id = "qr-1",
                    title = "Farmacia Central",
                ),
                AgendaSearchResult(
                    type = AgendaSearchResultType.ACTIVITY,
                    id = "op-1",
                    title = "Cliente ejemplo",
                ),
            ),
        )
        compose.onNodeWithText(AppStrings.Todos).assertExists()
        compose.onNodeWithText(AppStrings.TipoQr).assertExists()
        compose.onNodeWithText(AppStrings.TipoActividad).assertExists()
    }

    @Test
    fun s04_hides_type_filter_when_single_type() {
        renderSearch(
            listOf(
                AgendaSearchResult(
                    type = AgendaSearchResultType.QR,
                    id = "qr-1",
                    title = "Farmacia Central",
                ),
                AgendaSearchResult(
                    type = AgendaSearchResultType.QR,
                    id = "qr-2",
                    title = "Ferretería Norte",
                ),
            ),
        )
        // Sin chips de filtro: el único grupo se lista con su encabezado.
        compose.onNodeWithText(AppStrings.TipoQr + " (2)").assertExists()
        compose.onNodeWithText(AppStrings.TipoActividad).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.TipoComprobante).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.TipoContexto).assertDoesNotExist()
    }

    // ------------------------------------------------------------------
    // S12 — Revisión de importación: chips cuando hay duplicados + unknown
    // ------------------------------------------------------------------

    private val original = ImportCandidate(
        id = "o-1",
        kind = ImportKind.QR,
        fingerprint = "fp-dup",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val duplicate = ImportCandidate(
        id = "d-1",
        kind = ImportKind.QR,
        fingerprint = "fp-dup",
        qrAsset = QrAsset(encoded = "eA=="),
    )
    private val unknown = ImportCandidate(
        id = "u-1",
        kind = ImportKind.DESCONOCIDO,
        fingerprint = "fp-u",
        payloadRef = "payload-u",
    )

    private fun renderReview(batch: ImportBatch) {
        compose.setContent {
            AqTheme {
                ImportBatchScreen(
                    state = ImportBatchUiState.Review(batch),
                    onAction = {},
                )
            }
        }
        compose.waitForIdle()
    }

    private val reviewHeaderAll = AppStrings.ElementosQueNecesitanRevision + " (2)"
    private val reviewHeaderDuplicates = AppStrings.ElementosQueNecesitanRevision + " (1)"

    @Test
    fun s12_review_shows_filter_chips_when_duplicates_and_unknown() {
        renderReview(ImportBatch(listOf(original, duplicate, unknown)))
        compose.onNodeWithText(AppStrings.Todos).assertExists()
        compose.onNodeWithText(AppStrings.PosiblesDuplicados).assertExists()
        compose.onNodeWithText(AppStrings.PendientesDeRevision).assertExists()
        compose.onNodeWithText(reviewHeaderAll).assertExists()
    }

    @Test
    fun s12_filter_duplicates_reduces_visible_pending_header() {
        renderReview(ImportBatch(listOf(original, duplicate, unknown)))
        compose.onNodeWithText(reviewHeaderAll).assertExists()

        compose.onNodeWithText(AppStrings.PosiblesDuplicados).performClick()
        compose.waitForIdle()

        compose.onNodeWithText(reviewHeaderDuplicates).assertExists()
    }
}
