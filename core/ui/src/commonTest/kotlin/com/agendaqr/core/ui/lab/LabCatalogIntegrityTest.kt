package com.agendaqr.core.ui.lab

import com.agendaqr.core.ui.lab.model.LabCatalogIntegrity
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import com.agendaqr.core.ui.lab.model.LabDarkThemeSupport
import com.agendaqr.core.ui.lab.model.LabReviewStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Inventory integrity tests. They mirror the rules enforced by
 * LabCatalogIntegrity and add invariants that keep the catalog synchronized
 * with the components that really exist in core:ui.
 */
class LabCatalogIntegrityTest {

    @Test
    fun catalog_has_no_integrity_problems() {
        assertTrue(LabCatalogIntegrity.validate().isEmpty(), LabCatalogIntegrity.validate().joinToString("\n"))
    }

    @Test
    fun identifiers_are_unique_and_stable() {
        val ids = LabComponentCatalog.all.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
        assertTrue(ids.all { it.isNotBlank() })
    }

    @Test
    fun every_entry_declares_required_metadata() {
        LabComponentCatalog.all.forEach { contract ->
            assertTrue(contract.name.isNotBlank(), "${contract.id}: name")
            assertTrue(contract.purpose.isNotBlank(), "${contract.id}: purpose")
            assertTrue(contract.description.isNotBlank(), "${contract.id}: description")
            assertTrue(contract.usage.isNotEmpty(), "${contract.id}: usage")
            assertTrue(contract.states.isNotEmpty(), "${contract.id}: states")
            assertTrue(contract.darkThemeNote.isNotBlank(), "${contract.id}: dark note")
        }
    }

    @Test
    fun every_state_is_classified_and_described() {
        LabComponentCatalog.all.forEach { contract ->
            contract.states.forEach { state ->
                assertTrue(state.name.isNotBlank(), "${contract.id}: state name")
                assertTrue(state.description.isNotBlank(), "${contract.id}/${state.name}: description")
            }
        }
    }

    @Test
    fun every_component_has_at_least_one_verified_state() {
        LabComponentCatalog.components.forEach { contract ->
            assertTrue(
                contract.states.any { it.status == LabReviewStatus.VERIFIED },
                "${contract.id} must expose at least one state the API really offers",
            )
        }
    }

    @Test
    fun referenced_tokens_all_exist_in_the_token_index() {
        LabComponentCatalog.all.forEach { contract ->
            contract.tokens.forEach { token ->
                assertTrue(
                    com.agendaqr.core.ui.lab.model.XauxaTokenIndex.isValid(token.token),
                    "${contract.id} references missing token ${token.token}",
                )
            }
        }
    }

    @Test
    fun catalog_covers_every_real_component_in_core_ui() {
        val expected = setOf(
            "xauxa-screen",
            "xauxa-section",
            "xauxa-tile",
            "xauxa-hero-card",
            "xauxa-dialog",
            "xauxa-primary-button",
            "xauxa-secondary-button",
            "xauxa-danger-button",
            "xauxa-text-action",
            "xauxa-icon-button",
            "xauxa-filter-chip",
            "xauxa-text-input",
            "xauxa-search-bar",
            "xauxa-setting-row",
            "xauxa-favorite-toggle",
            "xauxa-toast",
            "xauxa-status-banner",
            "xauxa-inline-result",
            "xauxa-loading",
            "xauxa-skeleton",
            "xauxa-empty-state",
            "xauxa-error-page",
            "xauxa-load-more",
            "xauxa-list-row",
            "xauxa-stat-block",
            "xauxa-badge",
            "xauxa-category-chip",
            "xauxa-favorite-indicator",
            "xauxa-qr-preview",
            "xauxa-scanner-viewport",
            "xauxa-file-upload",
        )
        assertEquals(expected, LabComponentCatalog.components.map { it.id }.toSet())
    }

    @Test
    fun catalog_covers_every_real_foundation() {
        val expected = setOf("xauxa-color", "xauxa-spacing", "xauxa-metrics", "xauxa-type", "xauxa-motion", "xauxa-focus")
        assertEquals(expected, LabComponentCatalog.foundations.map { it.id }.toSet())
    }

    @Test
    fun interactive_components_declare_a_callback_prop() {
        LabComponentCatalog.components.filter { it.isInteractive }.forEach { contract ->
            assertTrue(
                contract.props.any { it.name.startsWith("on") },
                "${contract.id} declares events but no callback prop",
            )
        }
    }

    @Test
    fun dark_theme_support_is_declared_honestly() {
        // Today no component adapts to dark theme: all use fixed light tokens.
        LabComponentCatalog.components.forEach { contract ->
            assertEquals(
                LabDarkThemeSupport.NOT_SUPPORTED,
                contract.darkThemeSupport,
                "${contract.id} claims dark support that the tokens do not provide",
            )
        }
        // Non-chromatic foundations legitimately answer "no aplica".
        val nonChromatic = LabComponentCatalog.foundations.filter { it.id != "xauxa-color" }
        nonChromatic.forEach { contract ->
            assertEquals(LabDarkThemeSupport.NOT_APPLICABLE, contract.darkThemeSupport, contract.id)
        }
    }

    @Test
    fun categories_used_by_the_catalog_are_real() {
        val used = LabComponentCatalog.all.map { it.category }.toSet()
        assertTrue(used.all { it in LabCategory.entries })
        // The guidance list includes input/navigation components that do not
        // exist yet in core:ui; the catalog must not invent them.
        assertTrue(LabCategory.FOUNDATIONS in used)
        assertTrue(LabCategory.QR in used)
    }

    @Test
    fun web_wasm_render_stays_pending_until_browser_decoding_exists() {
        // The Wasm migration made the platform assumption of XauxaQrPreview
        // explicit: only Android decodes. The web state must stay PENDING —
        // never VERIFIED — until a browser decoding path really exists.
        val qrPreview = requireNotNull(LabComponentCatalog.find("xauxa-qr-preview"))
        val webRender = qrPreview.states.firstOrNull { it.name == "Render en web (Wasm)" }
        assertNotNull(webRender, "XauxaQrPreview must declare its web (Wasm) render state")
        assertEquals(LabReviewStatus.PENDING, webRender.status)
        assertTrue(webRender.description.isNotBlank())
    }
}
