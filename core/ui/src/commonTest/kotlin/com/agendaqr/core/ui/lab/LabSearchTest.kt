package com.agendaqr.core.ui.lab

import com.agendaqr.core.ui.lab.model.LabCatalogQuery
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import com.agendaqr.core.ui.lab.model.categoryCounts
import com.agendaqr.core.ui.lab.model.filterCatalog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Search and category filtering over the real catalog inventory. */
class LabSearchTest {

    private val catalog = LabComponentCatalog.all

    @Test
    fun blank_query_returns_the_whole_catalog() {
        assertEquals(catalog, filterCatalog(catalog, LabCatalogQuery()))
    }

    @Test
    fun search_matches_component_name_case_insensitively() {
        val results = filterCatalog(catalog, LabCatalogQuery(text = "xauxatile"))
        // xauxa-tile matches by name; xauxa-hero-card matches because it
        // honestly documents that it composes XauxaTile.
        assertTrue(results.any { it.id == "xauxa-tile" })
        assertTrue(results.all {
            (it.id + it.name + it.purpose + it.description).contains("xauxatile", ignoreCase = true)
        })
    }

    @Test
    fun search_matches_purpose_description_usage_and_tags() {
        assertTrue(filterCatalog(catalog, LabCatalogQuery(text = "comprobante")).isNotEmpty())
        assertTrue(filterCatalog(catalog, LabCatalogQuery(text = "QR")).any { it.id == "xauxa-qr-preview" })
        assertTrue(filterCatalog(catalog, LabCatalogQuery(text = "favorito")).any { it.id == "xauxa-favorite-indicator" })
        assertTrue(filterCatalog(catalog, LabCatalogQuery(text = "carga")).any { it.id == "xauxa-loading" })
    }

    @Test
    fun search_matches_declared_state_names() {
        val disabled = filterCatalog(catalog, LabCatalogQuery(text = "deshabilitado"))
        // Components that declare a Disabled state, including the honest
        // NOT_SUPPORTED entry of the text action, must be findable by it.
        assertTrue(disabled.any { it.id == "xauxa-primary-button" })
        assertTrue(disabled.any { it.id == "xauxa-text-action" })
    }

    @Test
    fun search_without_results_returns_empty() {
        assertTrue(filterCatalog(catalog, LabCatalogQuery(text = "xxx-sin-resultados-xxx")).isEmpty())
    }

    @Test
    fun category_filter_keeps_only_that_category() {
        val results = filterCatalog(catalog, LabCatalogQuery(category = LabCategory.QR))
        assertEquals(listOf("xauxa-qr-preview", "xauxa-scanner-viewport", "xauxa-file-upload"), results.map { it.id })
    }

    @Test
    fun category_and_text_filters_combine() {
        val results = filterCatalog(catalog, LabCatalogQuery(text = "botón", category = LabCategory.ACTIONS))
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.category == LabCategory.ACTIONS })
    }

    @Test
    fun category_counts_sum_to_the_catalog_size() {
        val counts = categoryCounts(catalog)
        assertEquals(catalog.size, counts.values.sum())
        assertEquals(LabComponentCatalog.foundations.size, counts[LabCategory.FOUNDATIONS])
        assertEquals(6, counts[LabCategory.SURFACES])
        assertEquals(10, counts[LabCategory.ACTIONS])
        assertEquals(8, counts[LabCategory.FEEDBACK])
        assertEquals(5, counts[LabCategory.DATA])
        assertEquals(3, counts[LabCategory.QR])
    }
}
