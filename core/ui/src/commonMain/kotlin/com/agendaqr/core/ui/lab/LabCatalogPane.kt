package com.agendaqr.core.ui.lab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.components.xauxaFocusRing
import com.agendaqr.core.ui.components.XauxaSearchBar
import com.agendaqr.core.ui.lab.model.LabCatalogQuery
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentContract
import com.agendaqr.core.ui.lab.model.LabPattern
import com.agendaqr.core.ui.lab.model.LabPatterns
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

/**
 * Indica si una categoría puede filtrar dentro de una sección. Fundamentos
 * solo admite su propia categoría; Componentes admite todas menos
 * Fundamentos; Patrones ignora el filtro de categoría porque su listado es
 * independiente del catálogo de componentes.
 */
internal fun LabSection.allows(category: LabCategory?): Boolean = when (this) {
    LabSection.FOUNDATIONS -> category == null || category == LabCategory.FOUNDATIONS
    LabSection.COMPONENTS -> category == null || category != LabCategory.FOUNDATIONS
    LabSection.PATTERNS -> true
}

private fun sectionScope(section: LabSection, catalog: List<LabComponentContract>): List<LabComponentContract> =
    when (section) {
        LabSection.FOUNDATIONS -> catalog.filter { it.category == LabCategory.FOUNDATIONS }
        LabSection.COMPONENTS -> catalog.filter { it.category != LabCategory.FOUNDATIONS }
        LabSection.PATTERNS -> catalog
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
    val scope = sectionScope(section, catalog)
    // Defensa ante un filtro de categoría que quedó obsoleto tras cambiar de
    // sección: se ignora en lugar de mostrar un vacío confuso. El propietario
    // del estado además lo limpia en onSectionChange.
    val effectiveQuery = if (section.allows(query.category)) query else query.copy(category = null)
    val results = filterCatalog(scope, effectiveQuery)
    // Conteos informativos y estables: dependen solo de la sección, nunca del
    // texto buscado ni de la categoría activa, para no fluctuar al escribir.
    val countsInScope = scope.groupingBy { it.category }.eachCount()
    // Las categorías vacías en la sección actual se ocultan (en ambos modos)
    // en vez de ofrecer un filtro que solo produce "sin resultados".
    val availableCategories = LabCategory.entries.filter { (countsInScope[it] ?: 0) > 0 }

    LabPanel(
        title = section.label,
        subtitle = "Explora y prueba el sistema.",
        trailing = {
            if (section == LabSection.PATTERNS) {
                LabBadge("${LabPatterns.all.size} patrones")
            } else {
                LabBadge("${results.size} de ${scope.size}")
            }
        },
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
                    catalog = catalog,
                    scopeSize = scope.size,
                    query = effectiveQuery,
                    onQueryChange = onQueryChange,
                    availableCategories = availableCategories,
                    countsInScope = countsInScope,
                    modifier = Modifier.fillMaxWidth(0.26f),
                )
                CatalogContent(
                    section = section,
                    query = effectiveQuery,
                    onQueryChange = onQueryChange,
                    scopeSize = scope.size,
                    results = results,
                    availableCategories = availableCategories,
                    countsInScope = countsInScope,
                    selectedId = selectedId,
                    onComponentSelected = onComponentSelected,
                    selectedPatternId = selectedPatternId,
                    onPatternSelected = onPatternSelected,
                    scrollable = true,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                    verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
                ) {
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
                    query = effectiveQuery,
                    onQueryChange = onQueryChange,
                    scopeSize = scope.size,
                    results = results,
                    availableCategories = availableCategories,
                    countsInScope = countsInScope,
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
    catalog: List<LabComponentContract>,
    scopeSize: Int,
    query: LabCatalogQuery,
    onQueryChange: (LabCatalogQuery) -> Unit,
    availableCategories: List<LabCategory>,
    countsInScope: Map<LabCategory, Int>,
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
            count = scopeSize,
            selected = query.category == null,
            onClick = { onQueryChange(query.copy(category = null)) },
        )
        availableCategories.forEach { category ->
            LabCatalogNavItem(
                label = category.label,
                count = countsInScope[category] ?: 0,
                selected = query.category == category,
                onClick = { onQueryChange(query.copy(category = category)) },
            )
        }
    }
}

@Composable
private fun CatalogContent(
    section: LabSection,
    query: LabCatalogQuery,
    onQueryChange: (LabCatalogQuery) -> Unit,
    scopeSize: Int,
    results: List<LabComponentContract>,
    availableCategories: List<LabCategory>,
    countsInScope: Map<LabCategory, Int>,
    selectedId: String,
    onComponentSelected: (String) -> Unit,
    selectedPatternId: String,
    onPatternSelected: (String) -> Unit,
    scrollable: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            LabCategoryFilter(
                label = "Todos",
                count = scopeSize,
                selected = query.category == null,
                onClick = { onQueryChange(query.copy(category = null)) },
            )
            availableCategories.forEach { category ->
                LabCategoryFilter(
                    label = category.label,
                    count = countsInScope[category] ?: 0,
                    selected = query.category == category,
                    onClick = { onQueryChange(query.copy(category = category)) },
                )
            }
        }
        if (results.isEmpty()) {
            LabEmptySearch(query)
        } else if (scrollable) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = XauxaMetrics.CatalogCardMinWidth),
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
            ) {
                items(results, key = { it.id }) { component ->
                    LabCatalogTile(component, component.id == selectedId) {
                        onComponentSelected(component.id)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                results.forEach { component ->
                    LabCatalogTile(component, component.id == selectedId) {
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
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .semantics { this.selected = selected },
        shape = RectangleShape,
        color = if (selected) XauxaColor.SurfaceVariant else XauxaColor.Surface,
        border = if (selected) {
            BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand)
        } else {
            null
        },
        onClick = onClick,
    ) {
        Column(
            Modifier.padding(horizontal = XauxaSpacing.Md, vertical = XauxaSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                Text(pattern.title, fontSize = XauxaType.Label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary)
                if (pattern.demo) Text("Demo", fontSize = XauxaType.Caption, color = XauxaColor.Brand)
            }
            Text(pattern.description, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun LabCatalogNavItem(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .semantics { this.selected = selected },
        color = if (selected) XauxaColor.Surface2 else XauxaColor.Surface,
        shape = RectangleShape,
        border = if (selected) {
            BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand)
        } else {
            null
        },
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
private fun LabCategoryFilter(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    LabChoiceChip(label = "$label ($count)", selected = selected, onClick = onClick)
}

/**
 * Tile Metro del catálogo del laboratorio: superficie plana y rectangular
 * para explorar componentes. Adapta el lenguaje Metro (tile plana,
 * tipografía como jerarquía, sin sombras ni gradientes) al contrato Xauxa:
 * paleta monocromática + acento único de marca, bordes de 1-2dp, espaciado
 * en múltiplos de 4dp y anillo de foco visible.
 *
 * Jerarquía del contenido: nombre (principal) → categoría (metadato
 * secundario en texto, sin badge) → descripción (tres líneas como máximo).
 * La selección combina borde fuerte de marca, fondo Surface2 y etiqueta
 * textual "Seleccionado": nunca depende solo del color. El foco de teclado
 * usa el anillo de foco Xauxa dedicado, estructuralmente distinto de la
 * selección persistente. El estado se expone en semántica para que las
 * tecnologías de asistencia anuncien la selección.
 *
 * Es una pieza exclusiva del laboratorio con API mínima; no es un segundo
 * sistema de diseño y no debe usarse en pantallas de producción.
 */
@Composable
internal fun LabCatalogTile(
    component: LabComponentContract,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = XauxaMetrics.CatalogTileMinHeight)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .focusable(interactionSource = interaction)
            .xauxaFocusRing(interaction)
            .semantics { this.selected = selected },
        shape = RectangleShape,
        color = if (selected) XauxaColor.Surface2 else XauxaColor.Surface,
        border = if (selected) {
            BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand)
        } else {
            BorderStroke(XauxaMetrics.Border, XauxaColor.Border)
        },
        tonalElevation = XauxaSpacing.None,
        shadowElevation = XauxaSpacing.None,
    ) {
        Column(
            modifier = Modifier.padding(XauxaSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            Text(
                component.name,
                fontSize = XauxaType.Body,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) XauxaColor.Brand else XauxaColor.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                component.category.label,
                fontSize = XauxaType.Caption,
                letterSpacing = XauxaType.LetterSpacingWide,
                color = XauxaColor.TextTertiary,
            )
            Text(
                component.purpose,
                fontSize = XauxaType.Label,
                color = XauxaColor.TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (selected) {
                Text(
                    "Seleccionado",
                    fontSize = XauxaType.Caption,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = XauxaType.LetterSpacingWide,
                    color = XauxaColor.Brand,
                )
            }
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
