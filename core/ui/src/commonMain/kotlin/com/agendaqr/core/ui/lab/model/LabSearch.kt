package com.agendaqr.core.ui.lab.model

/**
 * Pure catalog query used by the lab search field and category filter. Keeping
 * this logic here (instead of inside composables) allows unit-testing search
 * and filtering without a UI runtime.
 */
data class LabCatalogQuery(
    val text: String = "",
    val category: LabCategory? = null,
)

private fun LabComponentContract.matches(text: String): Boolean {
    if (text.isBlank()) return true
    val haystack = buildList {
        add(id)
        add(name)
        add(purpose)
        add(description)
        addAll(usage)
        addAll(tags)
        addAll(states.map { it.name })
        addAll(events.map { it.name })
    }
    return haystack.any { it.contains(text, ignoreCase = true) }
}

/** Components matching the query: category first, then free text. */
fun filterCatalog(
    catalog: List<LabComponentContract>,
    query: LabCatalogQuery,
): List<LabComponentContract> = catalog.filter { component ->
    (query.category == null || component.category == query.category) &&
        component.matches(query.text.trim())
}

/** Component count per category, preserving the category declaration order. */
fun categoryCounts(catalog: List<LabComponentContract>): Map<LabCategory, Int> =
    LabCategory.entries.associateWith { category -> catalog.count { it.category == category } }

/** Grouping used by the catalog list: categories in order with their entries. */
fun groupByCategory(catalog: List<LabComponentContract>): Map<LabCategory, List<LabComponentContract>> =
    LabCategory.entries.associateWith { category -> catalog.filter { it.category == category } }
