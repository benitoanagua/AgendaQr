package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.lab.model.LabCatalogQuery
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentContract
import com.agendaqr.core.ui.lab.model.LabPattern
import com.agendaqr.core.ui.lab.model.LabPatterns
import com.agendaqr.core.ui.lab.model.categoryCounts
import com.agendaqr.core.ui.lab.model.filterCatalog
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Top-level lab sections. Foundations and Components browse the central
 * inventory; Patterns browse the usage compositions (pure model, no UI).
 */
enum class LabSection(val label: String) {
    FOUNDATIONS("Fundamentos"),
    COMPONENTS("Componentes"),
    PATTERNS("Patrones"),
}

/**
 * Navigable catalog pane: section tabs, free-text search, category filters
 * with component counts, selection indicator and an explicit empty state.
 *
 * [scrollable] keeps a single scroll owner in compact layouts: the pane is
 * content-sized there and the page scrolls as one unit, while in wide
 * layouts the list owns its scroll.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LabCatalogPane(
    catalog: List<LabComponentContract>,
    query: LabCatalogQuery,
    onQueryChange: (LabCatalogQuery) -> Unit,
    selectedId: String,
    onComponentSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    section: LabSection = LabSection.COMPONENTS,
    onSectionChange: (LabSection) -> Unit = {},
    selectedPatternId: String = LabPatterns.all.first().id,
    onPatternSelected: (String) -> Unit = {},
) {
    val visible = when (section) {
        LabSection.FOUNDATIONS -> catalog.filter { it.category == LabCategory.FOUNDATIONS }
        LabSection.COMPONENTS -> catalog.filter { it.category != LabCategory.FOUNDATIONS }
        LabSection.PATTERNS -> catalog
    }
    val results = filterCatalog(visible, query)
    val counts = categoryCounts(catalog)

    LabPanel(
        title = "Catálogo",
        subtitle = "Fundamentos, componentes y patrones; selecciona una entrada para inspeccionarla",
        trailing = { LabBadge("${results.size} / ${visible.size}") },
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            LabSection.entries.forEach { entry ->
                LabCategoryFilter(
                    label = entry.label,
                    count = when (entry) {
                        LabSection.FOUNDATIONS -> catalog.count { it.category == LabCategory.FOUNDATIONS }
                        LabSection.COMPONENTS -> catalog.count { it.category != LabCategory.FOUNDATIONS }
                        LabSection.PATTERNS -> LabPatterns.all.size
                    },
                    selected = section == entry,
                    onClick = { onSectionChange(entry) },
                )
            }
        }
        if (section == LabSection.PATTERNS) {
            LabPatternList(
                selectedId = selectedPatternId,
                onSelected = onPatternSelected,
                scrollable = scrollable,
            )
            return@LabPanel
        }
        XauxaSearchBar(
            value = query.text,
            onValueChange = { onQueryChange(query.copy(text = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = "Buscar por nombre, uso, estado o tag",
            placeholder = "Buscar",
            onClear = { onQueryChange(query.copy(text = "")) },
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            LabCategoryFilter(
                label = "Todos",
                count = catalog.size,
                selected = query.category == null,
                onClick = { onQueryChange(query.copy(category = null)) },
            )
            LabCategory.entries.forEach { category ->
                LabCategoryFilter(
                    label = category.label,
                    count = counts[category] ?: 0,
                    selected = query.category == category,
                    onClick = { onQueryChange(query.copy(category = category)) },
                )
            }
        }
        if (results.isEmpty()) {
            LabEmptySearch(query)
        } else if (scrollable) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            ) {
                items(results, key = { it.id }) { component ->
                    LabCatalogRow(component, selected = component.id == selectedId) {
                        onComponentSelected(component.id)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            ) {
                results.forEach { component ->
                    LabCatalogRow(component, selected = component.id == selectedId) {
                        onComponentSelected(component.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.LabPatternList(selectedId: String, onSelected: (String) -> Unit, scrollable: Boolean) {
    val list: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            LabPatterns.all.forEach { pattern ->
                LabPatternRow(pattern, selected = pattern.id == selectedId) { onSelected(pattern.id) }
            }
        }
    }
    if (scrollable) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            items(LabPatterns.all.size) { index ->
                val pattern = LabPatterns.all[index]
                LabPatternRow(pattern, selected = pattern.id == selectedId) { onSelected(pattern.id) }
            }
        }
    } else {
        list()
    }
}

@Composable
private fun LabPatternRow(pattern: LabPattern, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        shape = RectangleShape,
        color = if (selected) XauxaColor.SurfaceVariant else XauxaColor.Surface,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(XauxaSpacing.Sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                Text(
                    pattern.title,
                    fontSize = XauxaType.Label,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary,
                )
                if (pattern.demo) {
                    Text("Demo", fontSize = XauxaType.Caption, color = XauxaColor.Brand)
                }
            }
            Text(
                pattern.description,
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}

@Composable
private fun LabCategoryFilter(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    LabChoiceChip(label = "$label ($count)", selected = selected, onClick = onClick)
}

@Composable
internal fun LabCatalogRow(component: LabComponentContract, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        shape = RectangleShape,
        color = if (selected) XauxaColor.Surface2 else XauxaColor.Surface,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(XauxaSpacing.Sm)
                .semantics { contentDescription = "${component.name}, ${component.category.label}" },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                Text(
                    component.name,
                    fontSize = XauxaType.Label,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) {
                        XauxaColor.Brand
                    } else {
                        XauxaColor.TextPrimary
                    },
                )
                if (selected) {
                    Text(
                        "Seleccionado",
                        fontSize = XauxaType.Caption,
                        color = XauxaColor.Brand,
                    )
                }
            }
            Text(
                component.purpose,
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}

@Composable
private fun LabEmptySearch(query: LabCatalogQuery) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        color = XauxaColor.SurfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(XauxaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            Text(
                "Sin resultados",
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.SemiBold,
                color = XauxaColor.TextPrimary,
            )
            Text(
                if (query.category == null) {
                    "Ninguna entrada coincide con «${query.text}». Prueba con otro término."
                } else {
                    "Ninguna entrada de ${query.category.label} coincide con «${query.text}»."
                },
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}
