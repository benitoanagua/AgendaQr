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
        "xauxa-screen",
        "xauxa-section",
        "xauxa-tile",
        "xauxa-tile-header",
        "xauxa-hero-card",
        "xauxa-primary-button",
        "xauxa-secondary-button",
        "xauxa-danger-button",
        "xauxa-text-action",
        "xauxa-icon-button",
        "xauxa-filter-chip",
        "xauxa-category-chip",
        "xauxa-text-input",
        "xauxa-search-bar",
        "xauxa-setting-row",
        "xauxa-dialog",
        "xauxa-toast",
        "xauxa-status-banner",
        "xauxa-inline-result",
        "xauxa-list-row",
        "xauxa-stat-block",
        "xauxa-badge",
        "xauxa-loading",
        "xauxa-skeleton",
        "xauxa-empty-state",
        "xauxa-error-page",
        "xauxa-load-more",
        "xauxa-favorite-indicator",
        "xauxa-favorite-toggle",
        "xauxa-qr-preview",
        "xauxa-scanner-viewport",
        "xauxa-file-upload",
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
