package com.agendaqr.core.ui.lab.model

/**
 * Congelado explícito del inventario, adaptado del proyecto de referencia
 * (`WwApiFreeze`). Añadir o retirar una entrada del catálogo exige actualizar
 * esta lista deliberadamente: el test falla ante cambios no declarados.
 */
object LabApiFreeze {

    val canonicalIds: Set<String> = setOf(
        "xauxa-color",
        "xauxa-spacing",
        "xauxa-metrics",
        "xauxa-type",
        "xauxa-motion",
        "xauxa-focus",
        "xauxa-section",
        "xauxa-tile",
        "xauxa-page-title",
        "xauxa-section-header",
        "xauxa-metro-tile",
        "xauxa-tile-grid",
        "xauxa-pivot",
        "xauxa-primary-button",
        "xauxa-secondary-button",
        "xauxa-danger-button",
        "xauxa-text-action",
        "xauxa-icon",
        "xauxa-icon-button",
        "xauxa-app-bar",
        "xauxa-filter-chip",
        "xauxa-category-chip",
        "xauxa-text-input",
        "xauxa-search-bar",
        "xauxa-dialog",
        "xauxa-status-banner",
        "xauxa-list-row",
        "xauxa-badge",
        "xauxa-loading",
        "xauxa-skeleton",
        "xauxa-empty-state",
        "xauxa-load-more",
        "xauxa-favorite-toggle",
        "xauxa-qr-preview",
        "xauxa-scanner-viewport",
    )

    fun validate(catalog: List<LabComponentContract> = LabComponentCatalog.all): List<String> {
        val actual = catalog.map { it.id }.toSet()
        return buildList {
            if (actual != canonicalIds) {
                val missing = canonicalIds - actual
                val added = actual - canonicalIds
                if (missing.isNotEmpty()) add("faltan: ${missing.sorted().joinToString()}")
                if (added.isNotEmpty()) add("añadidos sin congelar: ${added.sorted().joinToString()}")
            }
            if (actual.size != catalog.size) add("identificadores de contrato duplicados")
        }
    }
}
