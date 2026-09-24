package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.agendaqr.core.ui.lab.model.categoryCounts
import com.agendaqr.core.ui.lab.model.filterCatalog
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Navigable catalog pane: free-text search, category filters with component
 * counts, selection indicator and an explicit empty-search state.
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
) {
    val results = filterCatalog(catalog, query)
    val counts = categoryCounts(catalog)

    LabPanel(
        title = "Catálogo de componentes",
        subtitle = "Inventario navegable; selecciona una entrada para inspeccionarla",
        trailing = { LabBadge("${results.size} / ${catalog.size}") },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = query.text,
            onValueChange = { onQueryChange(query.copy(text = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar por nombre, uso, estado o tag") },
            singleLine = true,
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
private fun LabCategoryFilter(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("$label ($count)") },
        modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
    )
}

@Composable
private fun LabCatalogRow(component: LabComponentContract, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        shape = RectangleShape,
        color = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
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
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                if (selected) {
                    Text(
                        "Seleccionado",
                        fontSize = XauxaType.Caption,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                component.purpose,
                fontSize = XauxaType.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LabEmptySearch(query: LabCatalogQuery) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(XauxaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            Text(
                "Sin resultados",
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                if (query.category == null) {
                    "Ninguna entrada coincide con «${query.text}». Prueba con otro término."
                } else {
                    "Ninguna entrada de ${query.category.label} coincide con «${query.text}»."
                },
                fontSize = XauxaType.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
