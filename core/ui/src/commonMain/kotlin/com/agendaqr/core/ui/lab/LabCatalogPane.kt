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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.components.XauxaSearchBar
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

enum class LabSection(val label: String) {
    FOUNDATIONS("Fundamentos"),
    COMPONENTS("Componentes"),
    PATTERNS("Patrones"),
}

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
        title = "Componentes",
        subtitle = "Explora y prueba el sistema.",
        trailing = { LabBadge("${visible.size} elementos") },
        modifier = modifier,
    ) {
        if (scrollable) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
            ) {
                CatalogNavigation(
                    section = section,
                    onSectionChange = onSectionChange,
                    counts = counts,
                    catalog = catalog,
                    modifier = Modifier.fillMaxWidth(0.26f),
                )
                CatalogContent(
                    section = section,
                    query = query,
                    onQueryChange = onQueryChange,
                    results = results,
                    selectedId = selectedId,
                    onComponentSelected = onComponentSelected,
                    selectedPatternId = selectedPatternId,
                    onPatternSelected = onPatternSelected,
                    scrollable = true,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                    LabSection.entries.forEach { item ->
                        LabCategoryFilter(
                            label = item.label,
                            count = sectionCount(item, catalog),
                            selected = section == item,
                            onClick = { onSectionChange(item) },
                        )
                    }
                }
                CatalogContent(
                    section = section,
                    query = query,
                    onQueryChange = onQueryChange,
                    results = results,
                    selectedId = selectedId,
                    onComponentSelected = onComponentSelected,
                    selectedPatternId = selectedPatternId,
                    onPatternSelected = onPatternSelected,
                    scrollable = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CatalogNavigation(
    section: LabSection,
    onSectionChange: (LabSection) -> Unit,
    counts: Map<LabCategory, Int>,
    catalog: List<LabComponentContract>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        LabSection.entries.forEach { item ->
            LabCatalogNavItem(
                label = item.label,
                count = sectionCount(item, catalog),
                selected = section == item,
                onClick = { onSectionChange(item) },
            )
        }
        Text("CATEGORÍAS", fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
        LabCatalogNavItem(
            label = "Todos",
            count = catalog.size,
            selected = false,
            onClick = { onSectionChange(LabSection.COMPONENTS) },
        )
        LabCategory.entries.forEach { category ->
            CategoryCountRow(category.label, counts[category] ?: 0)
        }
    }
}

@Composable
private fun CatalogContent(
    section: LabSection,
    query: LabCatalogQuery,
    onQueryChange: (LabCatalogQuery) -> Unit,
    results: List<LabComponentContract>,
    selectedId: String,
    onComponentSelected: (String) -> Unit,
    selectedPatternId: String,
    onPatternSelected: (String) -> Unit,
    scrollable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        if (section == LabSection.PATTERNS) {
            LabPatternList(selectedPatternId, onPatternSelected, scrollable)
            return@Column
        }
        XauxaSearchBar(
            value = query.text,
            onValueChange = { onQueryChange(query.copy(text = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = "Buscar componentes",
            placeholder = "Buscar por nombre o uso",
            onClear = { onQueryChange(query.copy(text = "")) },
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            LabCategoryFilter(
                label = "Todos",
                count = results.size,
                selected = query.category == null,
                onClick = { onQueryChange(query.copy(category = null)) },
            )
            LabCategory.entries.forEach { category ->
                LabCategoryFilter(
                    label = category.label,
                    count = categoryCountInSection(category, section, results),
                    selected = query.category == category,
                    onClick = { onQueryChange(query.copy(category = category)) },
                )
            }
        }
        if (results.isEmpty()) {
            LabEmptySearch(query)
        } else if (scrollable) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 168.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                items(results, key = { it.id }) { component ->
                    LabCatalogRow(component, component.id == selectedId) {
                        onComponentSelected(component.id)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                results.forEach { component ->
                    LabCatalogRow(component, component.id == selectedId) {
                        onComponentSelected(component.id)
                    }
                }
            }
        }
    }
}

private fun sectionCount(section: LabSection, catalog: List<LabComponentContract>): Int =
    when (section) {
        LabSection.FOUNDATIONS -> catalog.count { it.category == LabCategory.FOUNDATIONS }
        LabSection.COMPONENTS -> catalog.count { it.category != LabCategory.FOUNDATIONS }
        LabSection.PATTERNS -> LabPatterns.all.size
    }

private fun categoryCountInSection(
    category: LabCategory,
    section: LabSection,
    results: List<LabComponentContract>,
): Int = results.count { it.category == category }

@Composable
private fun ColumnScope.LabPatternList(
    selectedId: String,
    onSelected: (String) -> Unit,
    scrollable: Boolean,
) {
    if (scrollable) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            items(LabPatterns.all, key = { it.id }) { pattern ->
                LabPatternRow(pattern, pattern.id == selectedId) { onSelected(pattern.id) }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            LabPatterns.all.forEach { pattern ->
                LabPatternRow(pattern, pattern.id == selectedId) { onSelected(pattern.id) }
            }
        }
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
        Column(Modifier.padding(XauxaSpacing.Sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                Text(pattern.title, fontSize = XauxaType.Label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary)
                if (pattern.demo) Text("Demo", fontSize = XauxaType.Caption, color = XauxaColor.Brand)
            }
            Text(pattern.description, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun LabCatalogNavItem(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        color = if (selected) XauxaColor.Surface2 else XauxaColor.Surface,
        shape = RectangleShape,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, fontSize = XauxaType.Label, color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary)
            Text(count.toString(), fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
        }
    }
}

@Composable
private fun CategoryCountRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
        Text(count.toString(), fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
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
            modifier = Modifier.padding(XauxaSpacing.Sm)
                .semantics { contentDescription = "${component.name}, ${component.category.label}" },
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            Text(
                component.name,
                fontSize = XauxaType.Label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            LabBadge(component.category.label)
            Text(
                component.purpose,
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LabEmptySearch(query: LabCatalogQuery) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RectangleShape, color = XauxaColor.SurfaceVariant) {
        Column(Modifier.padding(XauxaSpacing.Md), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            Text("Sin resultados", fontSize = XauxaType.Label, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
            Text(
                if (query.category == null) "No hay coincidencias para «${query.text}»." else "Sin coincidencias en ${query.category.label}.",
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}
